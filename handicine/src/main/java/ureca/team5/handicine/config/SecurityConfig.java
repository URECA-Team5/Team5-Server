package ureca.team5.handicine.config;

import ureca.team5.handicine.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() // REST API이므로 기본설정 사용안함. 기본설정은 비인증시 로그인폼 화면으로 리다이렉트됨
                .csrf().disable() // REST API이므로 csrf 보안이 필요없으므로 disable처리.
                .formLogin().disable() // REST API이므로 form login이 필요없으므로 disable처리.
                .sessionManagement().disable() // JWT 토큰으로 인증하므로 세션은 필요없으므로 disable처리.
                .authorizeRequests() // 다음 리퀘스트에 대한 사용권한 체크
                .antMatchers("/api/auth/**").permitAll() // 가입 및 인증 주소는 누구나 접근가능
                .antMatchers("/api/qna/**").permitAll() // 질문게시판 주소는 누구나 접근가능
                .antMatchers("/api/board/**").permitAll() // 자유게시판 주소는 누구나 접근가능
                .antMatchers("/api/roles/**").permitAll() // role 주소는 누구나 접근가능
                .anyRequest().hasRole("USER") // 그 외 나머지 요청은 모두 인증된 회원만 접근 가능
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
