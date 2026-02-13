package com.example.pagos.infrastructure.bank;

import com.example.pagos.domain.model.Transferencia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component("BANCO_COLOMBIA")
public class MockBancoColombiaStrategy implements BancoDestinoStrategy {

    private static final Logger log = LoggerFactory.getLogger(MockBancoColombiaStrategy.class);

    private final RestClient restClient;

    public MockBancoColombiaStrategy(RestClient.Builder builder,
                                     @Value("${mock-banco.base-url:http://mock-banco-colombia:8080}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl + "/api").build();
    }

    @Override
    public void enviar(Transferencia transferencia) {
        log.info("Enviando transferencia al banco destino MOCK: {}", transferencia.getId());
        // Llamado simple al mock. En un escenario real incluiríamos payload detallado y manejo de respuesta.
        restClient.post()
                .uri("/transferencias")
                .body(transferencia)
                .retrieve()
                .toBodilessEntity();
    }
}

