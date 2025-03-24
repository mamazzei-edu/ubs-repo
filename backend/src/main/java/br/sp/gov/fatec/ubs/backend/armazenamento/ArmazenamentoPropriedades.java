package br.sp.gov.fatec.ubs.backend.armazenamento;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class ArmazenamentoPropriedades {

    private String localArmazenamento = "upload-dir";

    public String getLocalArmazenamento() {
        return localArmazenamento;
    }

    public void setLocalArmazenamento(String localArmazenamento) {
        this.localArmazenamento = localArmazenamento;
    }
    
}
