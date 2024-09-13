package ureca.team5.handicine.config;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import ureca.team5.handicine.security.JwtAuthenticationFilter;
import ureca.team5.handicine.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 최신 SecurityFilterChain 방식
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // Form 로그인 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 비활성화
                .sessionManagement(AbstractHttpConfigurer::disable) // 세션 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        // 메인 페이지와 로그인하지 않은 사용자도 접근 가능한 페이지
                        .requestMatchers("/", "/qna", "/qna/{question_id}", "/board", "/board/{post_id}").permitAll()

                        // Q&A 게시판에 답변 작성은 전문가만 가능
                        .requestMatchers("/qna/{question_id}/answers").hasRole("EXPERT")

                        // Role API 등 관리자가 필요한 요청
                        .requestMatchers("/api/roles/**").hasRole("ADMIN")

                        // 나머지 요청들은 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

        return http.build();
    }

    // 비밀번호 암호화를 위한 PasswordEncoder 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager 설정
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}