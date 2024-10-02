package tn.zeros.smg.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tn.zeros.smg.utils.RSAKeyProperties;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    @Value("${frontend.origin}")
    private String frontendOrigin;
    private final RSAKeyProperties keys;
    private final UserDetailsService userDetailsService;

    /**
     * Provides a password encoder implementation using BCrypt.
     * This bean is used to encode and decode passwords for user authentication.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the authentication manager for the application.
     * This method sets up the user details service and password encoder to be used
     * for authentication.
     * The authentication manager is then returned as a Spring bean.
     *
     * @param http the HttpSecurity object used to configure the security filter
     *             chain
     * @return the configured AuthenticationManager
     * @throws Exception if there is an error configuring the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    /**
     * Configures the security filter chain for the application.
     * This method sets up the authorization rules, JWT authentication, and session
     * management for the application.
     * It allows public access to certain endpoints (e.g. API documentation, login,
     * registration) and requires authentication for all other requests.
     * The JWT authentication is configured to use the `jwtAuthenticationConverter`
     * bean to extract authorities from the JWT token.
     * The session management is configured to use a stateless session policy, which
     * means no session is created or used.
     *
     * @param http the HttpSecurity object used to configure the security filter
     *             chain
     * @return the configured SecurityFilterChain
     * @throws Exception if there is an error configuring the security filter chain
     */
    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**",
                                "/auth/login", "/auth/logout", "/auth/register", "/auth/login-token",
                                "/auth/refresh-token",
                                "/user/verify", "/user/upload/**", "/actuator/**")
                        .permitAll()
                        .requestMatchers("/article/delete/**", "/article/add", "/article/update/**")
                        .hasAuthority("ADMIN")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    /**
     * Creates a JWT decoder that uses the public key to verify JWT tokens.
     * This bean is used to decode and validate JWT tokens received in requests.
     *
     * @return a JWT decoder instance that uses the configured public key
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(keys.getPublicKey()).build();
    }

    /**
     * Creates a JWT encoder that uses the configured public and private keys to
     * sign JWT tokens.
     * This bean is used to generate and sign JWT tokens that can be used for
     * authentication and authorization.
     *
     * @return a JWT encoder instance that uses the configured public and private
     *         keys
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(keys.getPublicKey()).privateKey(keys.getPrivateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    /**
     * Creates a JWT authentication converter that extracts authorities from the JWT
     * token.
     * The converter is configured to use the "roles" claim in the JWT token as the
     * authorities,
     * and it prefixes the authorities with "ROLE_".
     *
     * @return a JWT authentication converter instance
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtConverter;
    }

    /**
     * Configures the CORS settings for the application.
     * This method sets up the CORS settings for the application.
     * It allows requests from the specified origins, with the specified methods and
     * headers.
     * The CORS settings are configured to use the "roles" claim in the JWT token as
     * the
     * authorities,
     * and it prefixes the authorities with "ROLE_".
     *
     * @return a CorsConfigurationSource instance that configures the CORS settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(frontendOrigin)); // Adjust as needed
        log.info("Allowed Origins : {}", configuration.getAllowedOrigins());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
