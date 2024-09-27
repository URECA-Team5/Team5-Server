package ureca.team5.handicine.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ureca.team5.handicine.dto.UserDTO;
import ureca.team5.handicine.dto.LoginRequestDTO;
import ureca.team5.handicine.security.JwtTokenProvider;
import ureca.team5.handicine.service.UserService;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;

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

    // Kakao 소셜 로그인 성공 후 리다이렉트
    @GetMapping("/oauth2/callback/kakao")
    public void kakaoLoginSuccess(HttpServletResponse response, OAuth2AuthenticationToken authToken) throws IOException {
        // 첫 번째 권한을 가져와서 roleName으로 사용
        String roleName = authToken.getAuthorities().stream()
                .findFirst()  // 첫 번째 권한을 가져옴
                .map(GrantedAuthority::getAuthority)
                .orElse("MEMBER");

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(authToken.getName(), roleName, null);  // userId는 null로 설정

        // 클라이언트로 리다이렉션, 토큰을 쿼리 파라미터로 전달
        response.sendRedirect("http://localhost:3000/oauth2/callback?token=" + token);
    }
}