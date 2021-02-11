package spring.study.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 기본값이 on인 csrf 취약점 보안 해제
                .csrf().disable()
                .headers()
                // SockJs는 기본적으로 HTML iframe 요소를 통한 전송을 허용하지 않도록 설정되는데 해당 내용을 해제
                .frameOptions().sameOrigin()
                .and()
                // 권한없이 페이지 접근하면 로그인 페이지로 이동
                .formLogin()
                .and()
                .authorizeRequests()
                .antMatchers("/chat/**").hasRole("USER")
                .anyRequest().permitAll();
    }

    /**
     * 태스트를 위해 in-memory 계정을 임의로 생성
     * 서비스에 사용시에는 DB 데이터를 이용하도록 수정이 필요
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("황주환")
                .password("{noop}1234")
                .roles("USER")
                .and()
                .withUser("강태환")
                .password("{noop}1234")
                .roles("USER")
                .and()
                .withUser("황철원")
                .password("{noop}1234")
                .roles("GUEST");
    }
}
