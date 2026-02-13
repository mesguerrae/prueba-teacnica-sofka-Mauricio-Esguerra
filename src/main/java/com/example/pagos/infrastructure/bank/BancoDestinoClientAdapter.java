package com.example.pagos.infrastructure.bank;

import com.example.pagos.domain.model.Transferencia;
import com.example.pagos.domain.ports.BancoDestinoClientPort;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BancoDestinoClientAdapter implements BancoDestinoClientPort {

    private final Map<String, BancoDestinoStrategy> strategies;

    public BancoDestinoClientAdapter(Map<String, BancoDestinoStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    @Retry(name = "banco-destino")
    public void procesarTransferencia(Transferencia transferencia) {
        BancoDestinoStrategy strategy = strategies.get(transferencia.getBancoDestino());
        if (strategy == null) {
            throw new IllegalArgumentException("No existe estrategia configurada para banco destino: " + transferencia.getBancoDestino());
        }
        strategy.enviar(transferencia);
    }
}

