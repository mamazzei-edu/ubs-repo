package br.sp.gov.fatec.ubs.backend.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.sp.gov.fatec.ubs.backend.model.Paciente;
import br.sp.gov.fatec.ubs.backend.services.PacienteService;

@RestController
@RequestMapping("/api/pacientes")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER', 'MEDICO')")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    // Endpoint para salvar um novo paciente
    @PostMapping
    public ResponseEntity<Paciente> salvarPaciente(@RequestBody Paciente paciente) {
        Paciente pacienteSalvo = pacienteService.salvarPaciente(paciente);
        return new ResponseEntity<>(pacienteSalvo, HttpStatus.CREATED);
    }

    // Endpoint para listar todos os pacientes com paginação
    @GetMapping
    public ResponseEntity<?> listarPacientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(pacienteService.listarPacientesPaginados(page, size), HttpStatus.OK);
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

    // Endpoint para buscar paciente por prontuário
    @GetMapping("/prontuario/{prontuario}")
    public ResponseEntity<Paciente> buscarPacientePorProntuario(@PathVariable String prontuario) {
        try {
            Optional<Paciente> paciente = pacienteService.buscarPacientePorProntuario(prontuario);
            if (paciente.isPresent()) {
                return new ResponseEntity<>(paciente.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar paciente por prontuário: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint para buscar paciente por nome completo
    @GetMapping("/nome/{nomeCompleto}")
    public ResponseEntity<Paciente> buscarPacientePorNome(@PathVariable String nomeCompleto) {
        try {
            Optional<Paciente> paciente = pacienteService.buscarPacientePorNome(nomeCompleto);
            if (paciente.isPresent()) {
                return new ResponseEntity<>(paciente.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar paciente por nome: " + e.getMessage());
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

    // Endpoint para atualizar um paciente
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> atualizarPaciente(
            @PathVariable Long id, @RequestBody Paciente pacienteAtualizado) {
        Optional<Paciente> pacienteOpt = pacienteService.buscarPacientePorId(id);

        if (pacienteOpt.isPresent()) {
            Paciente paciente = pacienteOpt.get();

            // Atualizando os campos
            paciente.setNomeCompleto(pacienteAtualizado.getNomeCompleto());
            paciente.setNomeSocial(pacienteAtualizado.getNomeSocial());
            paciente.setNomeMae(pacienteAtualizado.getNomeMae());
            paciente.setNomePai(pacienteAtualizado.getNomePai());
            paciente.setDataNascimento(pacienteAtualizado.getDataNascimento());
            paciente.setSexo(pacienteAtualizado.getSexo());
            paciente.setNacionalidade(pacienteAtualizado.getNacionalidade());
            paciente.setMunicipioNascimento(pacienteAtualizado.getMunicipioNascimento());
            paciente.setRacaCor(pacienteAtualizado.getRacaCor());
            paciente.setFrequentaEscola(pacienteAtualizado.getFrequentaEscola());
            paciente.setEscolaridade(pacienteAtualizado.getEscolaridade());
            paciente.setSituacaoFamiliar(pacienteAtualizado.getSituacaoFamiliar());
            paciente.setEstabelecimentoVinculo(pacienteAtualizado.getEstabelecimentoVinculo());
            paciente.setDeficiente(pacienteAtualizado.getDeficiente());
            paciente.setTelefoneCelular(pacienteAtualizado.getTelefoneCelular());
            paciente.setTelefoneResidencial(pacienteAtualizado.getTelefoneResidencial());
            paciente.setTelefoneComercial(pacienteAtualizado.getTelefoneComercial());
            paciente.setEmail(pacienteAtualizado.getEmail());
            paciente.setCpf(pacienteAtualizado.getCpf());
            paciente.setCns(pacienteAtualizado.getCns());
            paciente.setProntuario(pacienteAtualizado.getProntuario());
            paciente.setEtnia(pacienteAtualizado.getEtnia());
            paciente.setOcupacao(pacienteAtualizado.getOcupacao());
            paciente.setVisual(pacienteAtualizado.getVisual());
            paciente.setAuditiva(pacienteAtualizado.getAuditiva());
            paciente.setMotora(pacienteAtualizado.getMotora());
            paciente.setIntelectual(pacienteAtualizado.getIntelectual());
            paciente.setOpm(pacienteAtualizado.getOpm());
            paciente.setCep(pacienteAtualizado.getCep());
            paciente.setLogradouro(pacienteAtualizado.getLogradouro());
            paciente.setNumero(pacienteAtualizado.getNumero());
            paciente.setBairro(pacienteAtualizado.getBairro());
            paciente.setComplemento(pacienteAtualizado.getComplemento());
            paciente.setUf(pacienteAtualizado.getUf());
            paciente.setMunicipioResidencia(pacienteAtualizado.getMunicipioResidencia());
            paciente.setDistritoAdministrativo(pacienteAtualizado.getDistritoAdministrativo());
            paciente.setTipoLogradouro(pacienteAtualizado.getTipoLogradouro());
            paciente.setOrigemEndereco(pacienteAtualizado.getOrigemEndereco());
            paciente.setReferencia(pacienteAtualizado.getReferencia());
            paciente.setRg(pacienteAtualizado.getRg());
            paciente.setOrgaoEmissor(pacienteAtualizado.getOrgaoEmissor());
            paciente.setPisPasepNis(pacienteAtualizado.getPisPasepNis());
            paciente.setCnh(pacienteAtualizado.getCnh());
            paciente.setCtps(pacienteAtualizado.getCtps());
            paciente.setTituloEleitor(pacienteAtualizado.getTituloEleitor());
            paciente.setPassaporte(pacienteAtualizado.getPassaporte());
            paciente.setEstabelecimentoCadastro(pacienteAtualizado.getEstabelecimentoCadastro());
            // Atualize outros campos conforme necessário...

            Paciente pacienteAtualizadoSalvo = pacienteService.salvarPaciente(paciente);
            return new ResponseEntity<>(pacienteAtualizadoSalvo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint para buscar pacientes por nome parcial
    @GetMapping("/buscar/nome-parcial/{prefixo}")
    public ResponseEntity<?> buscarPorNomeParcial(@PathVariable String prefixo) {
        return new ResponseEntity<>(pacienteService.buscarPorNomeParcial(prefixo), HttpStatus.OK);
    }

    // Endpoint para buscar pacientes por CPF parcial
    @GetMapping("/buscar/cpf-parcial/{prefixo}")
    public ResponseEntity<?> buscarPorCpfParcial(@PathVariable String prefixo) {
        return new ResponseEntity<>(pacienteService.buscarPorCpfParcial(prefixo), HttpStatus.OK);
    }
}
