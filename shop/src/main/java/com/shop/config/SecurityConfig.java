package com.shop.config;

import com.shop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

//websecurity configurerAdapter 를 상속받는 클래스에 @EnableWebSecurity 어노테이션을 선언하면
//SpringSecurityFilterChain이 자동 포함됨 // WebSecurityConfigurerAdapter 상속을 통한
// 메소드 오버라이딩을 통해 보안 설정을 커스터마이징 할 수 있음
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    MemberService memberService;

    //http 요청에 대한 보안 설정 / 페이지 권한 설정 ,로그인 페이지 설정, 로그아웃 메소드 등에 대한 설정 작성
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.formLogin()
                .loginPage("/members/login") //1.로그인 페이지 url 설정
                .defaultSuccessUrl("/") //2.로그인 성공시 이동할 url 설정
                .usernameParameter("email") //3.로그인시 사용할 파라미터 이름으로 email지정
                .failureUrl("/members/login/error") //4.로그인 실패시 이동할 url 설정
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) //5.로그아웃url
                .logoutSuccessUrl("/") //6.로그아웃 성공시 이동할 url
        ;

        http.authorizeRequests() //1.시큐리티 처리에 HttpServletRequest를 이용
                .mvcMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()
                //2. permitAll()을 통해 모든 사용자가 인증없이 해당 경로에 접글할 수 있도록 설정.
                //허용 사이트 : 메인페이지, 회원관련 URL , 뒤에서 만들 상품 상세 페이지, 상품 이미지
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                //3. /admin 으로 시작하는 경로는 해당 계정이 ADMIN Role 일 경우만 접근 가능하도록 설정
                .anyRequest().authenticated()
                // 4. 2~3에서 설정해준 경로를 제외한 나머지 경로들은 모드 인증을 요구하도록 설정
        ;

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        ; //5. 인증되지 않은 사용자가 리소스에 접근하였을 때 수행되는 핸들러 등록
    }
    //비밀번호를 db에 그대로 저장할경우 db가 해킹당하면 회원정보가 노출되고 , 이를 해결하려 BCryptPasswordEncoder의
    //해시 함수 이용하여 비밀번호를 암호화 하여 저장
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //스프링 시큐리티에서 인증은 AuthenticationManager을 통해 이루어지며, AuthenticationManagerBuilder가
        //AuthenticationManager를 생성합니다. userDetailService를 구현하고 있는 객체로
        //memberService를 지정해주며, 비밀번호 암호화를 위해 passwordEncoder를 지정해줍니다.
        auth.userDetailsService(memberService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    } //6. static 디렉터리의 하위 파일은 인증을 무시하도록 설정
}

