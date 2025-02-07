package br.sp.gov.fatec.ubs.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration  // Indica que esta classe é uma classe de configuração do Spring
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Configuração global de CORS
        registry.addMapping("/api/**")  // Define que a configuração será para todos os endpoints que começam com "/api/"
                .allowedOrigins("http://localhost:4200")  // Permite requisições de http://localhost:4200 (frontend do Angular)
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Permite métodos GET, POST, PUT e DELETE
                .allowedHeaders("*");  // Permite todos os cabeçalhos (pode restringir conforme necessário)
    }
}

