package ureca.team5.handicine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ureca.team5.handicine.dto.UserDTO;
import ureca.team5.handicine.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.ok(createdUser);
    }

    /* 로그인
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
    */

    // 마이페이지 조회
    @GetMapping("/profile/{user_id}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long user_id) {
        UserDTO userProfile = userService.getUserById(user_id);
        return ResponseEntity.ok(userProfile);
    }

    // 마이페이지 수정
    @PatchMapping("/profile/{user_id}")
    public ResponseEntity<UserDTO> updateUserProfile(@PathVariable Long user_id, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(user_id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // 계정 삭제
    @DeleteMapping("/{user_id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long user_id) {
        userService.deleteUser(user_id);
        return ResponseEntity.noContent().build();
    }
}