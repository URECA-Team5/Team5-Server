package ureca.team5.handicine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ureca.team5.handicine.dto.UserDTO;
import ureca.team5.handicine.dto.LoginRequestDTO;
import ureca.team5.handicine.security.JwtTokenProvider;
import ureca.team5.handicine.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.ok(createdUser);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest) {
        String token = userService.login(loginRequest);
        return ResponseEntity.ok(token);
    }

    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        userService.logout();
        return ResponseEntity.ok("Logged out successfully");
    }

    // 마이페이지 조회
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsername(token);
        UserDTO userProfile = userService.getUserByUsername(username);
        return ResponseEntity.ok(userProfile);
    }

    // 마이페이지 수정
    @PatchMapping("/profile")
    public ResponseEntity<UserDTO> updateUserProfile(@RequestHeader("Authorization") String token, @RequestBody UserDTO userDTO) {
        String username = jwtTokenProvider.getUsername(token);
        UserDTO updatedUser = userService.updateUserByUsername(username, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // 계정 삭제
    @DeleteMapping("/{user_id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("user_id") Long user_id) {
        userService.deleteUser(user_id);
        return ResponseEntity.noContent().build();
    }
}