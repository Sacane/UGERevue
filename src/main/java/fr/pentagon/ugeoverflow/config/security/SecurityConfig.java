package fr.pentagon.ugeoverflow.config.security;

import fr.pentagon.ugeoverflow.config.auth.CustomUserDetailsService;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService(UserRepository userRepository) {
    return new CustomUserDetailsService(userRepository);
  }

  @Bean
  public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    var authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder);

    return new ProviderManager(authenticationProvider);
  }

  @Bean
  @Profile("prod")
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // Source CSRF: https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript-spa
    var config = http
        .authorizeHttpRequests(authorize -> {
          authorize.requestMatchers("/**", "/api/login", "/h2-console/**").permitAll(); //TODO turn "/**" matching to every front root
          authorize.anyRequest().authenticated();
        });
    config.csrf((csrf) ->
            csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
        .addFilterBefore(new CsrfCookieFilter(), BasicAuthenticationFilter.class);
    return config.build();
  }

  @Bean
  @Profile("dev")
  public SecurityFilterChain securityFilterChainDev(HttpSecurity http, RequestMappingHandlerMapping requireScanner) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorize ->
            authorize.requestMatchers("/**", "/api/login", "/h2-console/**")
                .permitAll()
                .anyRequest()
                .authenticated())
        .httpBasic(AbstractHttpConfigurer::disable)
        .headers(configurer -> configurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
        .addFilterBefore(new AuthFilter(requireScanner), BasicAuthenticationFilter.class)
        .build();
  }
}
