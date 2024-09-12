package ureca.team5.handicine.service;

import ureca.team5.handicine.dto.UserDTO;
import ureca.team5.handicine.entity.User;
import ureca.team5.handicine.repository.UserRepository;
import ureca.team5.handicine.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

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
        // newUser.setPassword(userDTO.getPassword()); // 비밀번호 암호화는 별도의 로직으로 처리
        newUser.setRole(roleRepository.findByRoleName(userDTO.getRoleName()));
        userRepository.save(newUser);
        return new UserDTO(newUser.getUserId(), newUser.getUsername(), newUser.getEmail(), newUser.getRole().getRoleName());
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setUsername(userDTO.getUsername());
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setRole(roleRepository.findByRoleName(userDTO.getRoleName()));
            userRepository.save(existingUser);

            return new UserDTO(existingUser.getUserId(), existingUser.getUsername(), existingUser.getEmail(), existingUser.getRole().getRoleName());
        } else {
            throw new RuntimeException("User not found.");
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}