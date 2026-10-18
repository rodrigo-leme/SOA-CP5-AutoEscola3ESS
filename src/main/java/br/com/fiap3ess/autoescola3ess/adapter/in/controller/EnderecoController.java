package br.com.fiap3ess.autoescola3ess.adapter.in.controller;

import br.com.fiap3ess.autoescola3ess.adapter.out.client.dto.DadosViaCep;
import br.com.fiap3ess.autoescola3ess.application.port.out.ConsultaCepPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enderecos")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Endereços", description = "Consulta de endereço por CEP (API externa ViaCEP)")
@RequiredArgsConstructor
public class EnderecoController {
    private final ConsultaCepPort consultaCep;

    @GetMapping("/cep/{cep}")
    @Operation(summary = "Consultar endereço pelo CEP no ViaCEP")
    public ResponseEntity<DadosViaCep> consultarCep(@PathVariable String cep) {
        return ResponseEntity.ok(consultaCep.consultarCep(cep));
    }
}
