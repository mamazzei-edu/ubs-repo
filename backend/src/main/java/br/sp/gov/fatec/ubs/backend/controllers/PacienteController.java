package br.sp.gov.fatec.ubs.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import br.sp.gov.fatec.ubs.backend.model.Paciente;
import br.sp.gov.fatec.ubs.backend.services.PacienteService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pacientes")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MEDICO')")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    /**
     * Grava um paciente. O CPF é obrigatório, validado e único na base.
     *
     * Sem id: cadastro novo — 201, ou 409 se o CPF já pertence a alguém (o
     * corpo traz pacienteExistenteId). Com id: confirmação da conferência da
     * ficha, atualiza aquele registro — 200.
     */
    @PostMapping
    public ResponseEntity<Paciente> salvarPaciente(@RequestBody Paciente paciente) {
        boolean novo = paciente.getId() == null;
        Paciente pacienteSalvo = pacienteService.cadastrar(paciente);
        return new ResponseEntity<>(pacienteSalvo, novo ? HttpStatus.CREATED : HttpStatus.OK);
    }

    // Endpoint para listar todos os pacientes
    @GetMapping
    public ResponseEntity<List<Paciente>> listarPacientes() {
        List<Paciente> pacientes = pacienteService.listarPacientes();
        return new ResponseEntity<>(pacientes, HttpStatus.OK);
    }

    /**
     * Busca pelo CPF, com ou sem máscara. Usado pela tela de cadastro para
     * avisar da duplicata ANTES de o usuário preencher o resto da ficha.
     */
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Paciente> buscarPorCpf(@PathVariable String cpf) {
        return pacienteService.buscarPorCpf(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Autocompletar do agendamento (web e app): parte do nome ou começo do
     * CPF. Devolve no máximo {@code limite} pacientes, para o app não precisar
     * baixar a base inteira.
     */
    @GetMapping("/busca")
    public ResponseEntity<List<Paciente>> buscar(@RequestParam String termo,
            @RequestParam(defaultValue = "20") int limite) {
        int limiteSeguro = Math.max(1, Math.min(limite, 50));
        return ResponseEntity.ok(pacienteService.buscarPorNomeOuCpf(termo, limiteSeguro));
    }

    // Endpoint para buscar um paciente pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPaciente(@PathVariable Long id) {
        Optional<Paciente> paciente = pacienteService.buscarPacientePorId(id);
        if (paciente.isPresent()) {
            return new ResponseEntity<>(paciente.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint para excluir paciente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirPaciente(@PathVariable Long id) {
        Optional<Paciente> paciente = pacienteService.buscarPacientePorId(id);
        if (paciente.isPresent()) {
            pacienteService.excluirPaciente(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Atualiza o paciente de id informado. A cópia dos campos é feita no
     * serviço, por reflexão: a lista escrita à mão que existia aqui cobria 20
     * dos 48 campos, e os demais eram descartados em silêncio.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> atualizarPaciente(
            @PathVariable Long id, @RequestBody Paciente pacienteAtualizado) {
        Paciente salvo = pacienteService.editarPaciente(id, pacienteAtualizado);
        if (salvo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(salvo, HttpStatus.OK);
    }
}
