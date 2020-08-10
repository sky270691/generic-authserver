package com.genericauthserver.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    //    @Bean
//    public UserDetailsService userDetailsService(){
//        return new UserDataServiceImpl();
//    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().and().csrf().disable();
        http.authorizeRequests()
        .mvcMatchers("/api/v1/login").permitAll()
        .mvcMatchers("/api/v1/register").permitAll();
        http.requiresChannel().anyRequest().requiresSecure();

        http.authorizeRequests().mvcMatchers("/mycode").authenticated();

        http.cors(corsConfigurer ->{
            CorsConfigurationSource corsConfigurationSource = request -> {
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
                corsConfiguration.setAllowedMethods(Arrays.asList("POST","GET","PUT","DELETE"));
                corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization","Content-Type","Voucher","Content-Disposition","X-Code","email","app-id"));

                return corsConfiguration;
            };
            corsConfigurer.configurationSource(corsConfigurationSource);
        });
    }
}
