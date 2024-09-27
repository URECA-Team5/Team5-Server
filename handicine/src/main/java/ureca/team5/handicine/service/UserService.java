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

    public String login(LoginRequestDTO loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // JWT 토큰 생성
        return jwtTokenProvider.createToken(user.getUsername(), user.getRole().getRoleName(), user.getUserId());
    }

    public void logout() {
        // 클라이언트 측에서 JWT 토큰을 삭제하는 방식으로 로그아웃 처리
        // 서버 측에서는 특별한 처리가 필요하지 않음 (서버는 JWT 상태를 유지하지 않기 때문)
        // 토큰 무효화 기능이 필요할 경우 Redis 등을 활용하여 블랙리스트 방식으로 구현할 수 있음
    }

    // username을 사용하여 사용자 정보 조회
    public UserDTO getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            User foundUser = user.get();
            return new UserDTO(foundUser.getUserId(), foundUser.getUsername(), foundUser.getEmail(), foundUser.getRole().getRoleName());
        } else {
            throw new RuntimeException("User not found.");
        }
    }

    public UserDTO createUser(UserDTO userDTO) {
        // 새로운 사용자 생성
        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());

        // 이메일이 null이거나 빈 문자열인 경우 기본 이메일 할당
        if (userDTO.getEmail() == null || userDTO.getEmail().isEmpty()) {
            newUser.setEmail(userDTO.getUsername() + "@handicine.com");  // 임시 이메일 설정
            System.out.println("Email is null or empty. Assigning default email: " + newUser.getEmail());
        } else {
            newUser.setEmail(userDTO.getEmail());
        }

        // 비밀번호 설정
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            throw new RuntimeException("Password cannot be null or empty");
        }

        // 역할 설정
        String roleName = userDTO.getRoleName();
        if (roleName == null || roleName.isEmpty()) {
            roleName = "MEMBER";  // 기본 역할 설정
            System.out.println("Role name is null or empty. Assigning default role: " + roleName);
        }

        Optional<Role> roleOptional = roleRepository.findByRoleName(roleName);
        if (roleOptional.isPresent()) {
            newUser.setRole(roleOptional.get());
        } else {
            throw new RuntimeException("Role not found: " + roleName);
        }

        // 사용자 저장
        userRepository.save(newUser);

        // 생성된 사용자 정보를 DTO로 반환
        return new UserDTO(newUser.getUserId(), newUser.getUsername(), newUser.getEmail(), newUser.getRole().getRoleName(), newUser.getPassword());
    }

    public UserDTO saveSocialUser(String username, String email) {
        // 기존 사용자 검색
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            return convertToDTO(existingUser.get()); // 기존 사용자가 있으면 해당 사용자 반환
        }

        // 새로운 사용자 생성
        User newUser = new User();
        newUser.setUsername(username);  // 카카오에서 가져온 닉네임
        newUser.setEmail(email != null ? email : "no-email@" + username + ".kakao.com"); // 이메일이 없으면 대체 이메일 설정

        // 기본 역할 설정
        Role role = roleRepository.findByRoleName("MEMBER").orElseThrow(() -> new RuntimeException("Role not found: MEMBER"));
        newUser.setRole(role);

        // 사용자 저장
        User savedUser = userRepository.save(newUser);

        // 저장된 사용자 엔티티를 DTO로 변환 후 반환
        return convertToDTO(savedUser);
    }

    // 엔티티 -> DTO 변환 메서드
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getRoleName()
        );
    }

    // DTO -> 엔티티 변환 메서드 (필요할 경우 추가)
    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        if (userDTO.getRoleName() != null) {
            Role role = roleRepository.findByRoleName(userDTO.getRoleName())
                    .orElseThrow(() -> new RuntimeException("Role not found: " + userDTO.getRoleName()));
            user.setRole(role);
        }

        return user;
    }

    // username을 사용하여 사용자 정보 수정
    public UserDTO updateUserByUsername(String username, UserDTO userDTO) {
        // 사용자 조회
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // 사용자 정보 업데이트
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());

        // role_name 로그 출력
        System.out.println("Received role_name: " + userDTO.getRoleName());

        // 역할(Role) 업데이트
        Optional<Role> roleOptional = roleRepository.findByRoleName(userDTO.getRoleName());
        if (roleOptional.isPresent()) {
            existingUser.setRole(roleOptional.get());
        } else {
            throw new RuntimeException("Role not found: " + userDTO.getRoleName());
        }

        // 비밀번호가 null 또는 빈 값이 아닐 때만 업데이트
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        // 사용자 저장
        userRepository.save(existingUser);

        // 업데이트된 사용자 정보를 DTO로 반환
        return new UserDTO(
                existingUser.getUserId(),
                existingUser.getUsername(),
                existingUser.getEmail(),
                existingUser.getRole().getRoleName(),
                existingUser.getPassword()
        );
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}