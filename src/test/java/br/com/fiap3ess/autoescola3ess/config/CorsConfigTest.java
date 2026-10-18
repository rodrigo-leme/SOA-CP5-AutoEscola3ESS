package br.com.fiap3ess.autoescola3ess.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;

// Simula a requisição "preflight" (OPTIONS) que o navegador envia antes de uma chamada cross-origin
@SpringBootTest
@AutoConfigureMockMvc
public class CorsConfigTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Expectativa: Permitir origem cadastrada no CORS.")
    void corsCenario1() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(options("/instrutores")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getHeader("Access-Control-Allow-Origin")).isEqualTo("http://localhost:3000");
    }

    @Test
    @DisplayName("Expectativa: Bloquear origem não cadastrada no CORS.")
    void corsCenario2() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(options("/instrutores")
                        .header("Origin", "http://site-nao-autorizado.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getHeader("Access-Control-Allow-Origin")).isNull();
    }
}
