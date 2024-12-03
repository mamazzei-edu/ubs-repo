package br.sp.gov.fatec.ubs.backend.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    // Método para listar todos os pacientes
    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll();  // Busca todos os pacientes no banco
    }

    // Método para buscar um paciente pelo ID
    public Optional<Paciente> buscarPacientePorId(Long id) {
        return pacienteRepository.findById(id);  // Busca o paciente pelo ID
    }

    // Método para salvar um novo paciente
    public Paciente salvarPaciente(Paciente paciente) {
        return pacienteRepository.save(paciente);  // Salva o paciente no banco
    }
}
