package br.sp.gov.fatec.ubs.backend.armazenamento;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigArquivos implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/arquivos/**") // Adiciona o caminho de API que você quer liberar
                .allowedOrigins("http://localhost:4200") // Permite que o frontend no localhost:4200 faça requisições
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos permitidos
                .allowedHeaders("*") // Permite todos os cabeçalhos
                .allowCredentials(true); // Permite o envio de cookies, se necessário
    }
}
