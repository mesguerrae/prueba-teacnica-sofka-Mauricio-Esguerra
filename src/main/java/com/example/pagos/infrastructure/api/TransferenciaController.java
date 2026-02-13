package com.example.pagos.infrastructure.api;

import com.example.pagos.application.ProcesarTransferenciaCommand;
import com.example.pagos.application.ProcesarTransferenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/transferencias")
public class TransferenciaController {

    private final ProcesarTransferenciaService procesarTransferenciaService;

    public TransferenciaController(ProcesarTransferenciaService procesarTransferenciaService) {
        this.procesarTransferenciaService = procesarTransferenciaService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@Valid @RequestBody TransferenciaRequest request) {
        UUID id = procesarTransferenciaService.procesar(
                new ProcesarTransferenciaCommand(
                        request.bancoOrigen(),
                        request.bancoDestino(),
                        request.cuentaOrigen(),
                        request.cuentaDestino(),
                        request.monto(),
                        request.moneda()
                )
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of("id", id.toString(), "estado", "PENDIENTE"));
    }
}

