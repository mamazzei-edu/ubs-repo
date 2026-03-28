package br.sp.gov.fatec.ubs.backend.services;

import br.sp.gov.fatec.ubs.backend.model.Medico;
import br.sp.gov.fatec.ubs.backend.repositories.MedicoRepository;
import br.sp.gov.fatec.ubs.backend.exceptions.ValidaCRM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    // Alterado para List<String> especialidades
    public Medico criarMedico(String nomeCompleto, List<String> especialidades, String crm, String email, String telefone) {
        if (!ValidaCRM.isValid(crm)) {
            throw new IllegalArgumentException("CRM inválido: " + crm);
        }

        String crmFormatado = ValidaCRM.format(crm);

        if (medicoRepository.findByCrm(crmFormatado).isPresent()) {
            throw new IllegalArgumentException("CRM já cadastrado: " + crmFormatado);
        }

        if (medicoRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado: " + email);
        }

        Medico medico = new Medico();
        medico.setFullName(nomeCompleto);
        medico.setEspecialidades(especialidades);
        medico.setCrm(crmFormatado);
        medico.setEmail(email);
        medico.setTelefone(telefone);

        return medicoRepository.save(medico);
    }

    public List<Medico> listarTodos() {
        return medicoRepository.findAll();
    }

    public List<Medico> listarAtivos() {
        return medicoRepository.findByAtivoTrue();
    }

    public Optional<Medico> buscarPorId(Integer id) {
        return medicoRepository.findById(id);
    }

    public Optional<Medico> buscarPorCrm(String crm) {
        String crmFormatado = ValidaCRM.format(crm);
        if (crmFormatado == null) {
            return Optional.empty();
        }
        return medicoRepository.findByCrm(crmFormatado);
    }

    public List<Medico> buscarPorEspecialidade(String especialidade) {
        return medicoRepository.findByEspecialidadeAndAtivoTrue(especialidade);
    }

    public List<Medico> buscarPorNome(String nome) {
        return medicoRepository.findByFullNameContainingIgnoreCaseAndAtivoTrue(nome);
    }

    public Medico atualizarMedico(Integer id, String nomeCompleto, List<String> especialidades, String crm, String email, String telefone) {
        Optional<Medico> medicoOptional = medicoRepository.findById(id);
        if (medicoOptional.isEmpty()) {
            throw new IllegalArgumentException("Médico não encontrado");
        }

        Medico medico = medicoOptional.get();

        if (medico.getCrm() != null && !medico.getCrm().equals(crm)) {
            if (!ValidaCRM.isValid(crm)) {
                throw new IllegalArgumentException("CRM inválido: " + crm);
            }
            String crmFormatado = ValidaCRM.format(crm);
            if (medicoRepository.existsByCrmAndIdNot(crmFormatado, id)) {
                throw new IllegalArgumentException("CRM já cadastrado: " + crmFormatado);
            }
            medico.setCrm(crmFormatado);
        } else if (medico.getCrm() == null && crm != null) {
            if (!ValidaCRM.isValid(crm)) {
                throw new IllegalArgumentException("CRM inválido: " + crm);
            }
            String crmFormatado = ValidaCRM.format(crm);
            if (medicoRepository.existsByCrmAndIdNot(crmFormatado, id)) {
                throw new IllegalArgumentException("CRM já cadastrado: " + crmFormatado);
            }
            medico.setCrm(crmFormatado);
        }

        if (!medico.getEmail().equals(email)) {
            if (medicoRepository.existsByEmailAndIdNot(email, id)) {
                throw new IllegalArgumentException("Email já cadastrado: " + email);
            }
            medico.setEmail(email);
        }

        medico.setFullName(nomeCompleto);
        medico.setEspecialidades(especialidades);
        medico.setTelefone(telefone);

        return medicoRepository.save(medico);
    }

    public Medico alterarStatus(Integer id, boolean ativo) {
        Optional<Medico> medicoOptional = medicoRepository.findById(id);
        if (medicoOptional.isEmpty()) {
            throw new IllegalArgumentException("Médico não encontrado");
        }

        Medico medico = medicoOptional.get();
        medico.setAtivo(ativo);

        return medicoRepository.save(medico);
    }

    public void desativarMedico(Integer id) {
        alterarStatus(id, false);
    }

    public void deletarMedico(Integer id) {
        if (!medicoRepository.existsById(id)) {
            throw new IllegalArgumentException("Médico não encontrado");
        }
        medicoRepository.deleteById(id);
    }
}