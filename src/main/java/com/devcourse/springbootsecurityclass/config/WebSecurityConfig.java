package com.devcourse.springbootsecurityclass.config;

import com.devcourse.springbootsecurityclass.filter.JwtAuthenticationFilter;
import com.devcourse.springbootsecurityclass.oauth2.OAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@Import(SecurityServiceBeanConfig.class)
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    public WebSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/assets/**"), new AntPathRequestMatcher("/h2-console/**"));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry ->
                        registry
                                /*.requestMatchers(new AntPathRequestMatcher("/me")).hasAnyRole("USER", "ADMIN")
                                .requestMatchers(new AntPathRequestMatcher("/admin")).access(new WebExpressionAuthorizationManager("hasRole('ADMIN') && isFullyAuthenticated()"))*/
                                .requestMatchers(new AntPathRequestMatcher("/api/user/me")).hasAnyRole("USER", "ADMIN")
                                .anyRequest().permitAll())
                .oauth2Login(configurer ->
                        configurer.successHandler(oAuth2AuthenticationSuccessHandler))
                /*.sessionManagement(configurer ->
                        configurer.sessionCreationPolicy(STATELESS))*/
                /*.formLogin(configurer ->
                        configurer
                                .defaultSuccessUrl("/")
                                .permitAll())*/
                /*.rememberMe(
                        configurer ->
                                configurer.rememberMeCookieName("remember-me")
                                        .tokenValiditySeconds(60 * 5))*/
                /*.logout(
                        configurer ->
                                configurer.logoutUrl("/logout")
                                        .logoutSuccessUrl("/")
                )*/;

        http.addFilterBefore(jwtAuthenticationFilter, LogoutFilter.class);

        return http.build();
    }

}
