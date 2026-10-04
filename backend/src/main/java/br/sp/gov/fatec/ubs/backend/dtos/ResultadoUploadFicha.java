package br.sp.gov.fatec.ubs.backend.dtos;

import java.util.List;

import br.sp.gov.fatec.ubs.backend.model.Paciente;

/**
 * Resposta do upload de uma ficha em PDF.
 *
 * @param paciente          dados para exibir no formulário. Quando o CPF já existe na
 *                          base, vem o registro gravado com os campos preenchidos pela
 *                          ficha sobrepostos — e com o id, para a gravação atualizar em
 *                          vez de inserir.
 * @param pacienteExistente true quando o CPF já constava na base
 * @param alteracoes        o que muda em relação ao que está gravado; vazio para paciente novo
 */
public record ResultadoUploadFicha(Paciente paciente,
                                   boolean pacienteExistente,
                                   List<AlteracaoCampo> alteracoes) {
}
