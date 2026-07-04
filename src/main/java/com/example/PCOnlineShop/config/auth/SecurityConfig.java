package com.example.PCOnlineShop.config.auth;

import com.example.PCOnlineShop.model.account.Account;
import com.example.PCOnlineShop.repository.account.AccountRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final AccountRepository accountRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler csrfRequestHandler = new CsrfTokenRequestAttributeHandler();
        csrfRequestHandler.setCsrfRequestAttributeName("_csrf");

        http.csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(csrfRequestHandler)
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/payment/webhook", HttpMethod.POST.name()),
                                new AntPathRequestMatcher("/cart/add/**", HttpMethod.POST.name()),
                                new AntPathRequestMatcher("/api/build/**", HttpMethod.POST.name())
                        )
                )
                .addFilterAfter(csrfCookieFilter(), CsrfFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/products/**", "/product/detail/**", "/category/**").permitAll()
                        .requestMatchers("/home").not().hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/", "/about-us", "/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/payment/callback/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/payment/webhook").permitAll()
                        .requestMatchers("/build/**", "/api/build/**").not().hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/assets/**", "/css/**", "/js/**", "/image/**", "/images/**", "/static/**",
                                "/webfonts/**", "/uploads/**", "/error").permitAll()
                        .requestMatchers("/blog/**", "/chat/**").permitAll()
                        .requestMatchers("/dashboard/admin").hasRole("ADMIN")
                        .requestMatchers("/dashboard/staff").hasAnyRole("STAFF", "ADMIN")
                        .requestMatchers("/staff/list/**", "/staff/add/**", "/staff/edit/**", "/staff/view/**",
                                "/staff/delete/**").hasRole("ADMIN")
                        .requestMatchers("/admin/brand/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/staff/products/**", "/staff/warranty/**", "/staff/shipping/**")
                                .hasAnyRole("STAFF", "ADMIN")
                        .requestMatchers("/orders/checkout", "/account/orders/**", "/payment/continue/**").hasRole("CUSTOMER")
                        .requestMatchers("/admin/orders/**").hasRole("ADMIN")
                        .requestMatchers("/staff/orders/**").hasRole("STAFF")
                        .requestMatchers("/orders/list", "/orders/detail/**", "/payment/info/**")
                                .hasAnyRole("CUSTOMER", "STAFF", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/cart").permitAll()
                        .requestMatchers(HttpMethod.POST, "/cart/add/**").permitAll()
                        .requestMatchers("/cart/**", "/checkout/**").hasRole("CUSTOMER")
                        .requestMatchers("/profile/**").hasAnyRole("CUSTOMER", "STAFF")
                        .requestMatchers("/orders/update-all-status").hasAnyRole("STAFF", "ADMIN")
                        .requestMatchers("/customer/list/**", "/customer/add/**", "/customer/view/**",
                                "/customer/edit/**", "/customer/delete/**")
                                .hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/staff/feedback/**").hasRole("STAFF")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("phoneNumber")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/home", false)
                        .failureUrl("/auth/login?error=true")
                        .failureHandler((request, response, exception) -> {
                            if (exception instanceof DisabledException) {
                                String phoneNumber = request.getParameter("phoneNumber");
                                Account account = accountRepository.findByPhoneNumber(phoneNumber).orElse(null);
                                if (account == null) {
                                    response.sendRedirect("/auth/login?error=true");
                                } else {
                                    response.sendRedirect("/auth/verify?email=" + encodeQueryParam(account.getEmail()));
                                }
                            } else {
                                response.sendRedirect("/auth/login?error=true");
                            }
                        })
                        .successHandler((request, response, authentication) -> {
                            var successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
                            successHandler.setDefaultTargetUrl("/home");

                            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STAFF"))) {
                                response.sendRedirect("/dashboard/staff");
                            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                                response.sendRedirect("/dashboard/admin");
                            } else {
                                String redirectUrl = request.getParameter("redirect");
                                if (isSafeRedirectUrl(redirectUrl)) {
                                    response.sendRedirect(redirectUrl);
                                } else {
                                    successHandler.onAuthenticationSuccess(request, response, authentication);
                                }
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", HttpMethod.POST.name()))
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    private OncePerRequestFilter csrfCookieFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                if (csrfToken != null) {
                    csrfToken.getToken();
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    private boolean isSafeRedirectUrl(String redirectUrl) {
        return redirectUrl != null
                && !redirectUrl.isBlank()
                && redirectUrl.startsWith("/")
                && !redirectUrl.startsWith("//");
    }

    private String encodeQueryParam(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
