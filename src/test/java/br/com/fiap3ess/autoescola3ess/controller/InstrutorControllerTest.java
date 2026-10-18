package br.com.fiap3ess.autoescola3ess.controller;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.instrutor.DadosDetalhamentoInstrutor;
import br.com.fiap3ess.autoescola3ess.application.service.InstrutorService;
import br.com.fiap3ess.autoescola3ess.exception.type.InstrutorNotFoundException;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Especialidade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class InstrutorControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    InstrutorService service;

    private static final String JSON_INSTRUTOR_VALIDO = """
            {
              "nome": "Instrutor Teste",
              "email": "instrutor@email.com.br",
              "telefone": "(11) 91234-5678",
              "cnh": "01234567890",
              "especialidade": "MOTOS",
              "endereco": {
                "logradouro": "Rua Teste",
                "numero": "100",
                "bairro": "Centro",
                "cidade": "São Paulo",
                "uf": "SP",
                "cep": "01234-567"
              }
            }
            """;

    @Test
    @DisplayName("Expectativa: Retornar código 201 ao cadastrar instrutor com dados válidos (ADMIN).")
    @WithMockUser(roles = "ADMIN")
    void cadastrarCenario1() throws Exception {
        when(service.cadastrarInstrutor(any())).thenReturn(detalhamento(1L));

        MockHttpServletResponse response = mockMvc
                .perform(post("/instrutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_INSTRUTOR_VALIDO))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).endsWith("/instrutores/1");
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 ao cadastrar instrutor com dados inválidos.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarCenario2() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(post("/instrutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 403 quando um USER tenta cadastrar instrutor.")
    @WithMockUser(roles = "USER")
    void cadastrarCenario3() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(post("/instrutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_INSTRUTOR_VALIDO))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 200 ao detalhar instrutor existente.")
    @WithMockUser(roles = "ADMIN")
    void detalharCenario1() throws Exception {
        when(service.detalharInstrutor(1L)).thenReturn(detalhamento(1L));

        MockHttpServletResponse response = mockMvc
                .perform(get("/instrutores/1"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("instrutor@email.com.br");
    }

    @Test
    @DisplayName("Expectativa: Retornar código 404 ao detalhar instrutor inexistente.")
    @WithMockUser(roles = "ADMIN")
    void detalharCenario2() throws Exception {
        when(service.detalharInstrutor(99L)).thenThrow(new InstrutorNotFoundException("ID do instrutor informado não existe!"));

        MockHttpServletResponse response = mockMvc
                .perform(get("/instrutores/99"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 401/403 quando não há usuário autenticado.")
    void detalharCenario3() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/instrutores/1"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isIn(HttpStatus.UNAUTHORIZED.value(), HttpStatus.FORBIDDEN.value());
    }

    private DadosDetalhamentoInstrutor detalhamento(Long id) {
        return new DadosDetalhamentoInstrutor(
                id,
                "Instrutor Teste",
                "instrutor@email.com.br",
                "(11) 91234-5678",
                "01234567890",
                Especialidade.MOTOS,
                new DadosEndereco("Rua Teste", "100", null, "Centro", "São Paulo", "SP", "01234-567"),
                true
        );
    }
}
