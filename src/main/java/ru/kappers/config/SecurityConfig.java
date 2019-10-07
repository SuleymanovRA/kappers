package ru.kappers.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import ru.kappers.model.Role;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private DataSource dataSource;

//    private AuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    @Qualifier("dataSource")
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

//    @Autowired
//    public void setRestAuthenticationEntryPoint(AuthenticationEntryPoint restAuthenticationEntryPoint) {
//        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(
                        "/",
                        "/ui/**",
                        "/login",
                        "/favicon.ico",
                        "/sign-in/**",
                        "/sign-up/**",
                        "/sign-in/logout-success",
                        "/sign-in/get-authority",
                        "/rest/dict/**"
                ).permitAll()
                .antMatchers("/rest/api/fixtures/twoweeks").hasAnyAuthority(Role.Names.ADMIN)
                .antMatchers(
                        HttpMethod.GET,
                        "/v2/api-docs",
                        "/swagger-resources/**",
                        "/swagger-ui.html**",
                        "/webjars/**",
                        "favicon.ico"
                        ).permitAll()
                .anyRequest().authenticated()
//                .and()
//                .sessionManagement().maximumSessions(10)
//                .expiredUrl("/login")
//                .and()
//                .invalidSessionUrl("/login")
//                .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .logout()
                .permitAll()
                .and()
                .httpBasic()
                .and().logout()
                .logoutUrl("/sign-in/perform-logout")
                .logoutSuccessUrl("/sign-in/logout-success")
                .permitAll();
        ;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(new StringBuilder()
                        .append("select u.user_name as username, u.password, (not u.isblocked) as enabled")
                        .append(" from users u")
                        .append(" where u.user_name = ?")
                        .toString())
                .authoritiesByUsernameQuery(new StringBuilder()
                        .append("select u.user_name as username, r.name as role")
                        .append(" from users u inner join roles r on r.id = u.role_id")
                        .append(" where u.user_name = ?")
                        .toString());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }
//
//    @Bean
//    public HttpSessionEventPublisher httpSessionEventPublisher() {
//        return new HttpSessionEventPublisher();
//    }
}
