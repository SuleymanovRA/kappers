package ru.kappers.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = {"ru.kappers.security"})
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private DataSource dataSource;

    private AuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    @Qualifier("dataSource")
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired
    public void setRestAuthenticationEntryPoint(AuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // TODO enable https
                // .requiresChannel().anyRequest().requiresSecure().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/rest/admin/**").hasRole("ROLE_ADMIN")
                .antMatchers("/rest/**").permitAll()
                .antMatchers("/", "/index").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/index")
                .and()
                .logout().permitAll()
                .and()
                .httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint);
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
        return new BCryptPasswordEncoder(6);
    }
}