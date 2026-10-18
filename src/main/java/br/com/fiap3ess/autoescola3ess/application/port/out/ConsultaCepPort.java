package br.com.fiap3ess.autoescola3ess.application.port.out;

import br.com.fiap3ess.autoescola3ess.adapter.out.client.dto.DadosViaCep;

// Porta de saída para consulta de CEP em um serviço externo
public interface ConsultaCepPort {
    DadosViaCep consultarCep(String cep);
}
