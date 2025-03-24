package br.sp.gov.fatec.ubs.backend.armazenamento;

public class ArmazenamentoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ArmazenamentoException(String mensagem) {
        super(mensagem);
    }

    public ArmazenamentoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
    
}

