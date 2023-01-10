package com.example.session.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer ignoringCustomizer() {
        return (web) -> web
                .ignoring().requestMatchers(PathRequest.toH2Console());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .formLogin().permitAll()
                .and()
                .authorizeHttpRequests()
                .antMatchers("/").authenticated()
                .anyRequest().permitAll()
                .and()
                .cors()
                .and()
                .csrf().disable()
                .build();
    }

    @Autowired
    public void addUsers(UserDetailsManager userDetailsManager) {
        userDetailsManager.createUser(createUser("u1"));
        userDetailsManager.createUser(createUser("u2"));
    }

    private static UserDetails createUser(String name) {
        return User.withUsername(name)
                .password("{noop}" + name)
                .roles("user")
                .build();
    }
}
