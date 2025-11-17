package br.sp.gov.fatec.ubs.backend.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.sp.gov.fatec.ubs.backend.model.Paciente;
import br.sp.gov.fatec.ubs.backend.repositories.PacienteRepository;

@Component
public class PacienteDataInitializer implements CommandLineRunner {

    private final PacienteRepository pacienteRepository;

    public PacienteDataInitializer(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verificar se já existem pacientes no banco
        long count = pacienteRepository.count();
        
        if (count >= 110) {
            System.out.println("ℹ️ Banco já possui " + count + " pacientes. Pulando inserção de dados fictícios.");
            return;
        }
        
        System.out.println("📋 Inserindo 110 pacientes fictícios para teste...");
        
        // Criar e salvar 110 pacientes fictícios
        String[] primeiroNomes = {
            "Ana", "Bruno", "Carlos", "Daniela", "Eduardo", "Fernanda", "Gabriel", "Helena",
            "Igor", "Juliana", "Kleber", "Larissa", "Marcos", "Natália", "Otávio", "Paula",
            "Quirino", "Rafaela", "Samuel", "Tatiana", "Ulisses", "Vanessa"
        };
        
        String[] sobrenomes = {
            "Silva", "Santos", "Oliveira", "Souza", "Costa", "Ferreira", "Rodrigues", "Almeida",
            "Nascimento", "Lima", "Araújo", "Carvalho", "Ribeiro", "Martins", "Pereira"
        };
        
        for (int i = 0; i < 110; i++) {
            Paciente paciente = new Paciente();
            
            // Gerar nome combinando primeiro nome e sobrenomes
            String nome = primeiroNomes[i % primeiroNomes.length] + " " + 
                         sobrenomes[(i / 10) % sobrenomes.length] + " " +
                         sobrenomes[(i / 5) % sobrenomes.length];
            
            // Gerar CPF fictício único
            String cpf = String.format("%03d.%03d.%03d-%02d", 
                (i + 1) / 100, 
                (i + 1) % 100, 
                (i + 1) * 3 % 1000, 
                (i + 1) % 100);
            
            paciente.setNomeCompleto(nome);
            paciente.setCpf(cpf);
            pacienteRepository.save(paciente);
        }
        
        System.out.println("✅ 110 pacientes fictícios inseridos com sucesso!");
    }
}
