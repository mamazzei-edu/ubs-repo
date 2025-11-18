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
        
        System.out.println("📊 Total de pacientes no banco: " + count);
        
        if (count >= 110) {
            System.out.println("ℹ️ Banco já possui " + count + " pacientes. Pulando inserção de dados fictícios.");
            return;
        }
        
        int quantidadeInserir = (int)(110 - count);
        System.out.println("📋 Inserindo " + quantidadeInserir + " pacientes fictícios para completar 110...");
        
        // Criar e salvar pacientes fictícios
        String[] primeiroNomes = {
            "Ana", "Bruno", "Carlos", "Daniela", "Eduardo", "Fernanda", "Gabriel", "Helena",
            "Igor", "Juliana", "Kleber", "Larissa", "Marcos", "Natália", "Otávio", "Paula",
            "Quirino", "Rafaela", "Samuel", "Tatiana", "Ulisses", "Vanessa", "Xavier", "Yara", "Zeca"
        };
        
        String[] sobrenomes = {
            "Silva", "Santos", "Oliveira", "Souza", "Costa", "Ferreira", "Rodrigues", "Almeida",
            "Nascimento", "Lima", "Araújo", "Carvalho", "Ribeiro", "Martins", "Pereira", "Gomes",
            "Barbosa", "Rocha", "Dias", "Monteiro"
        };
        
        int inseridos = 0;
        for (int i = 0; i < 110; i++) {
            // Gerar nome único combinando primeiro nome e sobrenomes
            String nome = primeiroNomes[i % primeiroNomes.length] + " " + 
                         sobrenomes[(i / 10) % sobrenomes.length] + " " +
                         sobrenomes[(i / 5) % sobrenomes.length] + " " + i;
            
            // Gerar CPF fictício único
            String cpf = String.format("%03d.%03d.%03d-%02d", 
                (i + 100) / 100, 
                (i + 100) % 100, 
                (i + 100) * 3 % 1000, 
                (i + 100) % 100);
            
            // Gerar prontuário único
            String prontuario = "PRONT-" + String.format("%05d", i + 1000);
            
            // Verificar se já existe paciente com este nome ou prontuário
            if (pacienteRepository.findByNomeCompleto(nome).isEmpty() && 
                pacienteRepository.findByProntuario(prontuario).isEmpty()) {
                try {
                    Paciente paciente = new Paciente();
                    paciente.setNomeCompleto(nome);
                    paciente.setCpf(cpf);
                    paciente.setProntuario(prontuario);
                    pacienteRepository.save(paciente);
                    inseridos++;
                } catch (Exception e) {
                    System.out.println("⚠️ Erro ao inserir paciente " + i + ": " + e.getMessage());
                }
            } else {
                System.out.println("⏭️ Paciente " + i + " já existe, pulando...");
            }
        }
        
        System.out.println("✅ " + inseridos + " pacientes fictícios inseridos com sucesso!");
        System.out.println("📊 Total de pacientes no banco agora: " + pacienteRepository.count());
    }
}
