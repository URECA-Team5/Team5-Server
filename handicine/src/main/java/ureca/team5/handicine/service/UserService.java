package ureca.team5.handicine.service;

import ureca.team5.handicine.dto.LoginRequestDTO;
import ureca.team5.handicine.dto.UserDTO;
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
        return jwtTokenProvider.createToken(user.getUsername(), user.getRole().getRoleName());
    }

    public void logout() {
        // 클라이언트 측에서 JWT 토큰을 삭제하는 방식으로 로그아웃 처리
        // 서버 측에서는 특별한 처리가 필요하지 않음 (서버는 JWT 상태를 유지하지 않기 때문)
        // 토큰 무효화 기능이 필요할 경우 Redis 등을 활용하여 블랙리스트 방식으로 구현할 수 있음
    }

    public UserDTO getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User foundUser = user.get();
            return new UserDTO(foundUser.getUserId(), foundUser.getUsername(), foundUser.getEmail(), foundUser.getRole().getRoleName());
        } else {
            throw new RuntimeException("User not found.");
        }
    }

    public UserDTO createUser(UserDTO userDTO) {
        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());
        newUser.setEmail(userDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setRole(roleRepository.findByRoleName(userDTO.getRoleName()));
        userRepository.save(newUser);
        return new UserDTO(newUser.getUserId(), newUser.getUsername(), newUser.getEmail(), newUser.getRole().getRoleName(), newUser.getPassword());
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setUsername(userDTO.getUsername());
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setRole(roleRepository.findByRoleName(userDTO.getRoleName()));
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword())); // 비밀번호 암호화
            }
            userRepository.save(existingUser);

            return new UserDTO(existingUser.getUserId(), existingUser.getUsername(), existingUser.getEmail(), existingUser.getRole().getRoleName(), existingUser.getPassword());
        } else {
            throw new RuntimeException("User not found.");
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}