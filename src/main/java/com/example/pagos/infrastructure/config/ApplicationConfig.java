package com.example.pagos.infrastructure.config;

import com.example.pagos.application.ProcesarTransferenciaService;
import com.example.pagos.domain.ports.BancoDestinoClientPort;
import com.example.pagos.domain.ports.TransferenciaRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ProcesarTransferenciaService procesarTransferenciaService(TransferenciaRepositoryPort transferenciaRepositoryPort,
                                                                     BancoDestinoClientPort bancoDestinoClientPort) {
        return new ProcesarTransferenciaService(transferenciaRepositoryPort, bancoDestinoClientPort);
    }
}

