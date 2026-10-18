package br.com.fiap3ess.autoescola3ess.controller;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.aluno.DadosDetalhamentoAluno;
import br.com.fiap3ess.autoescola3ess.application.service.AlunoService;
import br.com.fiap3ess.autoescola3ess.exception.type.AlunoNotFoundException;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto.DadosEndereco;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AlunoControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AlunoService service;

    @Test
    @DisplayName("Expectativa: Retornar código 201 ao cadastrar aluno com dados válidos.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarCenario1() throws Exception {
        when(service.cadastrarAluno(any())).thenReturn(detalhamento(1L));

        MockHttpServletResponse response = mockMvc
                .perform(post("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAluno("12345678901")))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).endsWith("/alunos/1");
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 ao cadastrar aluno com CPF inválido.")
    @WithMockUser(roles = "ADMIN")
    void cadastrarCenario2() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(post("/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAluno("123")))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 404 ao detalhar aluno inexistente.")
    @WithMockUser(roles = "ADMIN")
    void detalharCenario1() throws Exception {
        when(service.detalharAluno(99L)).thenThrow(new AlunoNotFoundException("ID do aluno informado não existe!"));

        MockHttpServletResponse response = mockMvc
                .perform(get("/alunos/99"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 204 ao excluir aluno.")
    @WithMockUser(roles = "ADMIN")
    void excluirCenario1() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(delete("/alunos/1"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        verify(service).excluirAluno(1L);
    }

    private String jsonAluno(String cpf) {
        return """
                {
                  "nome": "Aluno Teste",
                  "email": "aluno@email.com.br",
                  "telefone": "(11) 98765-4321",
                  "cpf": "%s",
                  "endereco": {
                    "logradouro": "Rua Teste",
                    "numero": "10",
                    "bairro": "Centro",
                    "cidade": "São Paulo",
                    "uf": "SP",
                    "cep": "01234-567"
                  }
                }
                """.formatted(cpf);
    }

    private DadosDetalhamentoAluno detalhamento(Long id) {
        return new DadosDetalhamentoAluno(
                id,
                "Aluno Teste",
                "aluno@email.com.br",
                "(11) 98765-4321",
                "12345678901",
                new DadosEndereco("Rua Teste", "10", null, "Centro", "São Paulo", "SP", "01234-567"),
                true
        );
    }
}
