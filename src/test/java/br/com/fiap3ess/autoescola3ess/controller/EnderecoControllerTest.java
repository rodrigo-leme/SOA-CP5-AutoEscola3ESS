package br.com.fiap3ess.autoescola3ess.controller;

import br.com.fiap3ess.autoescola3ess.adapter.out.client.dto.DadosViaCep;
import br.com.fiap3ess.autoescola3ess.application.port.out.ConsultaCepPort;
import br.com.fiap3ess.autoescola3ess.exception.type.CepNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

// Testa o endpoint que consome a API externa ViaCEP (o cliente HTTP é simulado com Mockito)
@SpringBootTest
@AutoConfigureMockMvc
public class EnderecoControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ConsultaCepPort consultaCep;

    @Test
    @DisplayName("Expectativa: Retornar código 200 e o endereço quando o CEP existir.")
    @WithMockUser
    void consultarCepCenario1() throws Exception {
        when(consultaCep.consultarCep("01001000")).thenReturn(new DadosViaCep(
                "01001-000", "Praça da Sé", "lado ímpar", "Sé", "São Paulo", "SP", null));

        MockHttpServletResponse response = mockMvc
                .perform(get("/enderecos/cep/01001000"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("01001-000");
    }

    @Test
    @DisplayName("Expectativa: Retornar código 404 quando o CEP não existir.")
    @WithMockUser
    void consultarCepCenario2() throws Exception {
        when(consultaCep.consultarCep("99999999")).thenThrow(new CepNotFoundException("CEP não encontrado!"));

        MockHttpServletResponse response = mockMvc
                .perform(get("/enderecos/cep/99999999"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
