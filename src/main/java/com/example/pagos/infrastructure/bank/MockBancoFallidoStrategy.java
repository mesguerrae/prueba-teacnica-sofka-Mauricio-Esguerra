package com.example.pagos.infrastructure.bank;

import com.example.pagos.domain.model.Transferencia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Estrategia de banco que siempre falla.
 * Útil para probar reintentos y el flujo de transacciones FALLIDAS.
 */
@Component("BANCO_FALLA")
public class MockBancoFallidoStrategy implements BancoDestinoStrategy {

    private static final Logger log = LoggerFactory.getLogger(MockBancoFallidoStrategy.class);

    private final RestClient restClient;

    public MockBancoFallidoStrategy(RestClient.Builder builder,
                                     @Value("${mock-banco.base-url:http://mock-banco-colombia:8080}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl + "/api").build();
    }
    @Override
    public void enviar(Transferencia transferencia) {
        log.warn("Simulando fallo al enviar transferencia {} al banco BANCO_FALLA", transferencia.getId());
        throw new IllegalStateException("Banco destino fuera de servicio (simulado)");
    }
}

