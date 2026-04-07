package com.project.evaluation.config;

import com.project.evaluation.entity.Result;
import com.project.evaluation.filter.JwtAuthenticationFilter;
import com.project.evaluation.filter.SensitiveWordFilter;
import com.project.evaluation.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;

    @Bean
    public AuthenticationManager authenticationManager() {
        // 匹配合适的 AuthenticationProvider
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // 配置基于数据库认证的 userDetailsService 对象
        provider.setUserDetailsService(userDetailsService);
        // 确保使用与数据库密码格式一致的 PasswordEncoder
        provider.setPasswordEncoder(passwordEncoder);
        // 创建并返回认证管理器对象(实现类 ProviderManager)
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/login", "/ws/**", "/error").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint((request, response, authException) -> {
                        ResponseUtil.write(response, Result.error(authException.getMessage()));
                    });
                    exception.accessDeniedHandler((request, response, accessDeniedException) -> {
                        ResponseUtil.write(response, Result.error(accessDeniedException.getMessage()));
                    });
                })
                .addFilterBefore(sensitiveWordFilter, AuthorizationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, AuthorizationFilter.class);
        return http.build();
    }
}