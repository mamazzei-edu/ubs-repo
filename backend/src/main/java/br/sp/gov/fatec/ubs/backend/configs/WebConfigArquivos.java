package br.sp.gov.fatec.ubs.backend.configs;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.core.env.Environment; // Necessario para usar variaveis de ambiente

@Configuration
public class WebConfigArquivos implements WebMvcConfigurer {

    @Autowired
    private Environment environment;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> allowedOrigins = Arrays.asList(environment.getProperty("LISTA_HOSTS").split(","));
        System.out.println("Allowed origins: " + environment.getProperty("LISTA_HOSTS").split(","));

        registry.addMapping("/arquivos/**") // Adiciona o caminho de API que você quer liberar
                .allowedOrigins(allowedOrigins.toArray(new String[0])) // Permite que o frontend no localhost:4200 faça
                                                                       // requisições
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos permitidos
                .allowedHeaders("*") // Permite todos os cabeçalhos
                .allowCredentials(true); // Permite o envio de cookies, se necessário
    }
}
