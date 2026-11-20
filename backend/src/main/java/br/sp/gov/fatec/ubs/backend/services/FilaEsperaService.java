
package br.sp.gov.fatec.ubs.backend.services;

import br.sp.gov.fatec.ubs.backend.model.Paciente;
import br.sp.gov.fatec.ubs.backend.repositories.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;

@Service
public class FilaEsperaService {

    private final ArrayDeque<Paciente> fila = new ArrayDeque<>();
    private final PacienteRepository pacienteRepository;

    public FilaEsperaService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    // Adiciona um paciente ao final da fila.
    public synchronized Optional<Paciente> adicionarPaciente(Long id) {

        Optional<Paciente> pacienteEncontrado =
                pacienteRepository.findById(id);

        if (pacienteEncontrado.isEmpty()) {
            return Optional.empty();
        }

        Paciente paciente = pacienteEncontrado.get();

        // Impede que o mesmo paciente entre duas vezes na fila.
        boolean jaEstaNaFila = fila.stream()
                .anyMatch(p -> p.getId().equals(id));

        if (jaEstaNaFila) {
            throw new IllegalStateException(
                    "Este paciente já está na fila de espera."
            );
        }

        fila.addLast(paciente);

        return Optional.of(paciente);
    }

    // Retorna todos os pacientes na ordem de chegada.
    public synchronized List<Paciente> listarFila() {
        return List.copyOf(fila);
    }

    // Chama o primeiro paciente e o remove da fila.
    public synchronized Optional<Paciente> chamarProximo() {
        return Optional.ofNullable(fila.pollFirst());
    }

    // Remove um paciente específico da fila.
    public synchronized boolean removerPaciente(Long id) {
        return fila.removeIf(p -> p.getId().equals(id));
    }
}
