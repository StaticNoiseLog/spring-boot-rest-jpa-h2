package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain
            (HttpSecurity http) throws Exception {
        http.sessionManagement()
                .invalidSessionUrl("/")
                .and()
                // TODO: THis is recommended, more modern? .authorizeHttpRequests()
                .authorizeRequests()
                // -------- Allowing access to specific endpoints without authentication
                .antMatchers(
                        "/actuator/**", // /actuator and everything below
                        "/cats", // REST API endpoint
                        "/favicon.png", // the favicon referenced in index.html must be readable
                        "/about.html", // http://localhost:8080/about.html
                        "/ind*", // /index.html, /indie_cats.htm and any other top-level files starting with "ind"
                        "/") // allow access to index.html through http://localhost:8080 and http://localhost:8080/
                .permitAll() // no authentication, no authorization
                // -------- URL patterns with role-based authorization
                .antMatchers("/privileged/**")
                .hasAnyRole("DOG", "ADMIN")

                // -------- Authenticate all other requests
                .anyRequest().authenticated(); // authentication required for any URL that has not matched before
        http.formLogin(); // present a Spring Form TODO
//        HttpBasicConfigurer configurer = http.httpBasic(); // use basic auth
//        configurer.realmName("You are Entering the Cat Realm");

//        configurer.and()
//                .cookie()
//                .sameSite("None");


        return http.build();
    }


//    @Bean
//    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry = http
//                .csrf().disable()
//                .authorizeRequests();

//        // Allowing access to specific endpoints without authentication
//        http.authorizeRequests()
//                .antMatchers("/actuator/**", "/actuator/beans").permitAll();
//
//        // URL patterns with role-based authorization
//        expressionInterceptUrlRegistry
//                .antMatchers("/api/v1/**").hasAnyAuthority("USER");
//
//        // Authenticate all other requests
//        expressionInterceptUrlRegistry
//                .anyRequest().authenticated();

    // Configure HTTP Basic Authentication and set custom entry point
//        http.httpBasic();

//        return http.build();
//    }
}

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers("/cats").permitAll() // REST API completely open
////                .antMatchers("/static/**").permitAll() // REST API completely open
////                .antMatchers("/static/about.html").permitAll() // REST API completely open
////                .antMatchers("/about.html").permitAll() // WORKS!!! REST API completely open
//                .antMatchers("about.html").permitAll() // REST API completely open
////                .antMatchers("/index.html").permitAll() // entry point to the UI completely open
//                .antMatchers("/secured").hasRole("USER")
//                .anyRequest().authenticated()
//                .and()
//                .httpBasic();
//    }
