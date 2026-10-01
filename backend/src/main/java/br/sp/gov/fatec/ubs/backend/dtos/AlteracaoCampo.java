package br.sp.gov.fatec.ubs.backend.dtos;

/**
 * Uma diferença entre o registro já gravado e o que foi lido da ficha em PDF.
 *
 * @param campo      nome da propriedade (igual em Paciente, no modelo Angular e nos formulários)
 * @param rotulo     nome legível, para exibição
 * @param valorAtual o que está hoje no banco (pode ser null ou vazio)
 * @param valorNovo  o que a ficha traz
 */
public record AlteracaoCampo(String campo, String rotulo, String valorAtual, String valorNovo) {
}
