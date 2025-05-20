package com.api.GestionaFacilRestaurants.configurations;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.api.GestionaFacilRestaurants.security.JwtAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${api.base.path}")
    private String apiBasePath;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,AuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors // Habilitar CORS
                .configurationSource(request -> new org.springframework.web.cors.CorsConfiguration().applyPermitDefaultValues())
            )
            .authorizeHttpRequests(authRequest -> authRequest
                .requestMatchers(apiBasePath+"/auth/**").permitAll()
                .requestMatchers(apiBasePath + "/documents/**").permitAll()
                .requestMatchers(apiBasePath + "/comandas/**").permitAll()
                .requestMatchers(apiBasePath + "/reports/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public List<String> permittedUrls() {
        return List.of(apiBasePath+"/auth/login",apiBasePath + "/documents",apiBasePath + "/comandas");
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@SuppressWarnings("null") CorsRegistry registry) {
                // Cambiar "http://example.com" por dominio de producción
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173", "https://dominio.com")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("Authorization", "Cache-Control", "Content-Type");
            }
        };
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // Crear el bean de RestTemplate
    }
    
}
