package br.sp.gov.fatec.ubs.backend.exceptions;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Classe utilitária para validação de CRM (Conselho Regional de Medicina)
 */
public class ValidaCRM {
    
    private static final Pattern CRM_PADRAO = Pattern.compile("^\\d{4,7}[A-Z]{2}$");
    
    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA",
            "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN",
            "RS", "RO", "RR", "SC", "SP", "SE", "TO"
    );
    
    private ValidaCRM() {}

    /**
     * Forma única de gravar o CRM: maiúsculo e sem separadores.
     * "123456-sp", "123456/SP" e " 123456 sp " viram todos "123456SP" — sem
     * isso o mesmo médico passava pelo índice único com grafias diferentes.
     */
    public static String normalizar(String crm) {
        if (crm == null) {
            return null;
        }
        return crm.replaceAll("[\\s./-]", "").toUpperCase();
    }
    
    /**
     * Método público e estático que valida o formato e o estado de um CRM.
     * @param crm A String do CRM a ser validada.
     * @return true se o CRM for válido, false caso contrário.
     */
    public static boolean isValid(String crm) {
        if (crm == null || crm.trim().isEmpty()) {
            return false;
        }
        
        // Remove espaços e separadores e converte para maiúsculo
        crm = normalizar(crm);
        
        // 1. Validação de formato
        if (!CRM_PADRAO.matcher(crm).matches()) {
            return false;
        }
        
        // 2. Validação de conteúdo (o estado)
        String estado = crm.substring(crm.length() - 2);
        return ESTADOS_VALIDOS.contains(estado);
    }
    
    /**
     * Formata o CRM removendo espaços e separadores e colocando em maiúsculo
     * @param crm CRM a ser formatado
     * @return CRM formatado ou null se inválido
     */
    public static String format(String crm) {
        if (!isValid(crm)) {
            return null;
        }
        return normalizar(crm);
    }
}