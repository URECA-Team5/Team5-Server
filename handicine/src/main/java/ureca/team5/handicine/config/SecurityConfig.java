package ureca.team5.handicine.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
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
import ureca.team5.handicine.security.OAuth2AuthenticationSuccessHandler;
import ureca.team5.handicine.service.CustomOAuth2UserService;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider,
                          OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
                          CustomOAuth2UserService customOAuth2UserService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    // 최신 SecurityFilterChain 방식
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .formLogin(form -> form
                        .loginPage("/login")  // Define custom login page for local login
                        .permitAll()           // Allow all users to access the login page
                )
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 비활성화
                .sessionManagement(AbstractHttpConfigurer::disable) // 세션 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        // 메인 페이지와 로그인하지 않은 사용자도 접근 가능한 페이지
                        .requestMatchers("/", "/api/qna", "/api/qna/{question_id}", "/api/medicines/search", "/api/board", "/api/board/{post_id}", "/api/users/signup", "/api/users/login", "/login", "/oauth2/**").permitAll()

                        // Q&A 게시판에 답변 작성은 전문가만 가능
                        .requestMatchers(HttpMethod.POST, "/api/qna/{question_id}/answers").hasRole("EXPERT")

                        // Q&A 게시판에 답변 조회는 누구나 가능
                        .requestMatchers(HttpMethod.GET, "/api/qna/{question_id}/answers").permitAll()

                        // Role API 등 관리자가 필요한 요청
                        .requestMatchers("/api/roles/**").hasRole("ADMIN")

                        // 나머지 요청들은 인증 필요
                        .anyRequest().authenticated()
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)) // 사용자 정보를 처리하는 서비스 등록
                        .successHandler(oAuth2AuthenticationSuccessHandler) // OAuth2 로그인 성공 시 처리
                        .failureHandler((request, response, exception) -> {
                            System.out.println("OAuth2 login failed: " + exception.getMessage());
                            exception.printStackTrace();  // 예외 전체를 출력해서 원인 확인
                            response.sendRedirect("/login?error");  // 로그인 실패 시 리디렉션 처리
                        })
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true); // 인증된 요청 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}