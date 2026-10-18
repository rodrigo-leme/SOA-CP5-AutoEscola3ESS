package br.com.fiap3ess.autoescola3ess.controller;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.instrucao.DadosDetalhamentoAgendamento;
import br.com.fiap3ess.autoescola3ess.application.service.AgendaDeInstrucoes;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
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

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
public class InstrucaoControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AgendaDeInstrucoes agenda;

    private final LocalDateTime proximaSegundaAs10 = LocalDateTime
            .now()
            .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
            .withHour(10)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);

    private final String jsonAgendamento = """
            {
              "id_aluno": 1,
              "especialidade": "MOTOS",
              "data_hora": "%s"
            }
            """.formatted(proximaSegundaAs10.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")));

    @Test
    @DisplayName("Expectativa: Retornar código 400 para dados inválidos.")
    @WithMockUser
    void agendarInstrucaoCenario1() throws Exception {
        //act
        MockHttpServletResponse response = mockMvc
                .perform(post("/instrucoes"))
                .andReturn()
                .getResponse();

        //assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Expectativa: Retornar código 200 para agendamento com dados válidos.")
    @WithMockUser
    void agendarInstrucaoCenario2() throws Exception {
        //arrange
        when(agenda.agendar(any())).thenReturn(new DadosDetalhamentoAgendamento(
                1L, "Aluno Teste", "Instrutor Teste", Especialidade.MOTOS, proximaSegundaAs10));

        //act
        MockHttpServletResponse response = mockMvc
                .perform(post("/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAgendamento))
                .andReturn()
                .getResponse();

        //assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).contains("nome_aluno", "nome_instrutor");
    }

    @Test
    @DisplayName("Expectativa: Retornar código 400 quando uma regra de negócio for violada.")
    @WithMockUser
    void agendarInstrucaoCenario3() throws Exception {
        //arrange
        when(agenda.agendar(any())).thenThrow(new ValidacaoException("Nenhum instrutor disponível para a data/hora informada!"));

        //act
        MockHttpServletResponse response = mockMvc
                .perform(post("/instrucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAgendamento))
                .andReturn()
                .getResponse();

        //assert
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
