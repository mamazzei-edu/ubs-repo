package br.sp.gov.fatec.ubs.backend.controllers;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Verificação de saúde, pública (liberada em SecurityConfiguration).
 *
 * O app mobile chama na abertura, antes do login, para saber se alcança a API
 * e se a API alcança o banco — e mostrar a mensagem certa para cada caso.
 *
 * Responde só "ok"/"indisponivel": nada de versão, host ou mensagem de erro do
 * banco, que ajudariam quem estivesse sondando o servidor.
 */
@RestController
@RequestMapping("/status")
public class StatusController {

    private static final Logger log = LoggerFactory.getLogger(StatusController.class);
    private static final int TIMEOUT_BANCO_SEGUNDOS = 3;

    private final DataSource dataSource;

    public StatusController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> status() {
        boolean bancoOk = bancoDisponivel();

        Map<String, String> corpo = new LinkedHashMap<>();
        corpo.put("api", "ok");
        corpo.put("bancoDeDados", bancoOk ? "ok" : "indisponivel");

        return ResponseEntity
                .status(bancoOk ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE)
                .body(corpo);
    }

    private boolean bancoDisponivel() {
        try (Connection conexao = dataSource.getConnection()) {
            return conexao.isValid(TIMEOUT_BANCO_SEGUNDOS);
        } catch (Exception e) {
            log.warn("Banco de dados indisponível: {}", e.getMessage());
            return false;
        }
    }
}
