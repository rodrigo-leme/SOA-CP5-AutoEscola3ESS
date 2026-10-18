package br.com.fiap3ess.autoescola3ess.adapter.out.client;

import br.com.fiap3ess.autoescola3ess.adapter.out.client.dto.DadosViaCep;
import br.com.fiap3ess.autoescola3ess.application.port.out.ConsultaCepPort;
import br.com.fiap3ess.autoescola3ess.exception.type.CepNotFoundException;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

// Cliente HTTP que consome o WebService externo ViaCEP usando o RestClient do Spring
@Component
public class ViaCepClient implements ConsultaCepPort {
    private final RestClient restClient;

    public ViaCepClient(@Value("${api.viacep.url}") String baseUrl) {
        this.restClient = RestClient.create(baseUrl);
    }

    @Override
    public DadosViaCep consultarCep(String cep) {
        String cepNumerico = cep.replaceAll("\\D", "");
        if (cepNumerico.length() != 8) {
            throw new ValidacaoException("CEP deve conter 8 dígitos!");
        }

        DadosViaCep resposta = restClient
                .get()
                .uri("/{cep}/json/", cepNumerico)
                .retrieve()
                .body(DadosViaCep.class);

        if (resposta == null || Boolean.TRUE.equals(resposta.erro())) {
            throw new CepNotFoundException("CEP não encontrado!");
        }
        return resposta;
    }
}
