package ureca.team5.handicine.service;

import ureca.team5.handicine.dto.LoginRequestDTO;
import ureca.team5.handicine.dto.UserDTO;
import ureca.team5.handicine.entity.Role;
import ureca.team5.handicine.entity.User;
import ureca.team5.handicine.repository.UserRepository;
import ureca.team5.handicine.repository.RoleRepository;
import ureca.team5.handicine.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 로컬 로그인 처리
    public String login(LoginRequestDTO loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 로컬 사용자일 경우 비밀번호를 검증, 소셜 로그인 사용자는 비밀번호 검증 생략
        if (user.getPassword() != null && !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // JWT 토큰 생성
        return jwtTokenProvider.createToken(user.getUsername(), user.getRole().getRoleName(), user.getUserId());
    }

    // 로그아웃 처리 (JWT 삭제 방식은 클라이언트에서 처리)
    public void logout() {
        // 클라이언트에서 토큰을 삭제하는 방식으로 처리
    }

    // 사용자 정보 조회 (username으로 조회)
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found."));
        return new UserDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getRole().getRoleName());
    }

    // 사용자 생성 (소셜 로그인과 로컬 로그인 사용자 모두 처리)
    public UserDTO createUser(UserDTO userDTO) {
        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());

        // 이메일 설정
        if (userDTO.getEmail() == null || userDTO.getEmail().isEmpty()) {
            newUser.setEmail(userDTO.getUsername() + "@handicine.com");  // 임시 이메일 설정
        } else {
            newUser.setEmail(userDTO.getEmail());
        }

        // 소셜 로그인 사용자는 비밀번호가 없을 수 있으므로 비밀번호 설정 조건 추가
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        // 역할 설정 (소셜 로그인과 로컬 로그인 모두 기본 역할 MEMBER 부여)
        String roleName = (userDTO.getRoleName() != null && !userDTO.getRoleName().isEmpty()) ? userDTO.getRoleName() : "MEMBER";
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        newUser.setRole(role);

        // 사용자 저장
        userRepository.save(newUser);

        return new UserDTO(newUser.getUserId(), newUser.getUsername(), newUser.getEmail(), newUser.getRole().getRoleName());
    }

    // 소셜 로그인 사용자 정보 저장
    public UserDTO saveSocialUser(String username, String email) {
        // 사용자 정보가 있으면 기존 사용자 반환
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            return new UserDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getRole().getRoleName());
        }

        // 새로운 사용자 생성
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setEmail(email);
        return createUser(userDTO);  // 소셜 사용자도 같은 방식으로 저장
    }

    // 사용자 정보 수정 (username 기반으로)
    public UserDTO updateUserByUsername(String username, UserDTO userDTO) {
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // 사용자 정보 업데이트
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());

        // 역할 업데이트
        Optional<Role> roleOptional = roleRepository.findByRoleName(userDTO.getRoleName());
        if (roleOptional.isPresent()) {
            existingUser.setRole(roleOptional.get());
        } else {
            throw new RuntimeException("Role not found: " + userDTO.getRoleName());
        }

        // 비밀번호는 null 또는 빈 값이 아닌 경우만 업데이트 (소셜 로그인 사용자는 비밀번호 없이 로그인 가능)
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        // 사용자 정보 저장
        userRepository.save(existingUser);

        return new UserDTO(existingUser.getUserId(), existingUser.getUsername(), existingUser.getEmail(), existingUser.getRole().getRoleName());
    }

    // 사용자 삭제
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}