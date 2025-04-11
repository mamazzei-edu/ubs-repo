package br.sp.gov.fatec.ubs.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import br.sp.gov.fatec.ubs.backend.armazenamento.ArmazenamentoPropriedades;
import br.sp.gov.fatec.ubs.backend.armazenamento.ArmazenamentoService;


@ComponentScan("br.sp.gov.fatec.ubs.backend.armazenamento")
@ComponentScan("br.sp.gov.fatec.ubs.backend")
@EnableConfigurationProperties(ArmazenamentoPropriedades.class)	
@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	CommandLineRunner init(ArmazenamentoService armazenamentoService) {
		return (args) -> {
			armazenamentoService.deletarTodos();
			armazenamentoService.init();
			System.out.println("Iniciando a aplicação...");
		};
	}


}