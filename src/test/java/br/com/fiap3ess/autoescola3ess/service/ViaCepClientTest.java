package br.com.fiap3ess.autoescola3ess.service;

import br.com.fiap3ess.autoescola3ess.adapter.out.client.ViaCepClient;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ViaCepClientTest {
    @Test
    @DisplayName("Expectativa: Recusar CEP com formato inválido antes de chamar a API externa.")
    void consultarCepCenario1() {
        ViaCepClient client = new ViaCepClient("https://viacep.com.br/ws");

        assertThatThrownBy(() -> client.consultarCep("123"))
                .isInstanceOf(ValidacaoException.class);
    }
}
