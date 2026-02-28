package com.example.hello.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfigurationSource;
import com.example.hello.security.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private CorsConfigurationSource corsConfigurationSource;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/admin/dashboard");
        handler.setAlwaysUseDefaultTargetUrl(true);
        return handler;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // 静态资源和登录页面公开访问
                .requestMatchers("/", "/login", "/admin/login", "/css/**", "/static/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                
                // 微信小程序API接口 - 全部公开访问
                .requestMatchers("/api/projects/list", "/api/projects/detail", "/api/projects/categories", 
                               "/api/projects/hot", "/api/projects/recommend", "/api/projects/by-status",
                               "/api/projects/by-price-range", "/api/projects/sales-ranking", 
                               "/api/projects/rating-ranking").permitAll()
                
                // 用户相关API接口 - 公开访问
                .requestMatchers("/api/users/**").permitAll()
                
                // 订单相关API接口 - 公开访问
                .requestMatchers("/api/orders/**").permitAll()
                
                // 评论相关API接口 - 公开访问
                .requestMatchers("/api/reviews/**").permitAll()
                
                // 分类相关API接口 - 公开访问
                .requestMatchers("/api/category/**").permitAll()
                
                // 余额相关API接口 - 公开访问
                .requestMatchers("/api/balance/**").permitAll()
                
                // 微信小程序API接口 - 公开访问
                .requestMatchers("/api/wechat/**").permitAll()
                
                // 需要管理员权限的接口 - 项目管理
                .requestMatchers("/api/projects/create", "/api/projects/update/**", "/api/projects/delete/**",
                               "/api/projects/batch-delete", "/api/projects/set-hot/**", "/api/projects/set-recommend/**",
                               "/api/projects/update-status/**", "/api/projects/statistics", "/api/projects/admin/**").hasRole("ADMIN")
                
                // 需要管理员权限的接口 - 订单管理
                .requestMatchers("/api/order/admin/**").hasRole("ADMIN")
                
                // 用户优惠券接口 - 公开访问
                .requestMatchers("/api/coupons/**").permitAll()
                
                // 其他管理员API需要管理员权限
                .requestMatchers("/admin/api/**").hasRole("ADMIN")
                
                // 管理员页面需要认证
                .requestMatchers("/admin/**").authenticated()
                
                // 其他所有请求公开访问（微信小程序）
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(successHandler())
                .failureUrl("/admin/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable());
        
        return http.build();
    }
}


