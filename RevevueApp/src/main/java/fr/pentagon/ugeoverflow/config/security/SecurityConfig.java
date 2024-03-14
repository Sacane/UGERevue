package fr.pentagon.ugeoverflow.config.security;

import fr.pentagon.ugeoverflow.config.authentication.CustomUserDetailsService;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.service.CustomPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new CustomPasswordEncoder();
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
    return config.build();  // TODO disable anonymous authentication
  }

  @Bean
  @Profile("dev")
  public SecurityFilterChain securityFilterChainDev(HttpSecurity http) throws Exception {
    return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(authorize ->
            authorize.requestMatchers("/**", "/api/login", "/h2-console/**")
                .permitAll()
                .anyRequest()
                .authenticated()
            )
            .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .formLogin(c -> c.loginPage("http://localhost:4200/login"))
            .httpBasic(AbstractHttpConfigurer::disable)
            .anonymous(AbstractHttpConfigurer::disable)
            .build();
  }
}
