package br.com.fiap3ess.autoescola3ess.service;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.application.core.specification.ValidadorHorarioFuncionamento;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Especialidade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Regra de negócio: funcionamento de segunda a sábado, das 06:00 às 21:00
public class ValidadorHorarioFuncionamentoTest {
    private final ValidadorHorarioFuncionamento validador = new ValidadorHorarioFuncionamento();

    @Test
    @DisplayName("Expectativa: Recusar agendamento no domingo.")
    void validarCenario1() {
        LocalDateTime domingo = LocalDateTime.now()
                .with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
                .withHour(10).withMinute(0);

        assertThatThrownBy(() -> validador.validar(new DadosAgendamento(1L, null, Especialidade.MOTOS, domingo)))
                .isInstanceOf(ValidacaoException.class);
    }

    @Test
    @DisplayName("Expectativa: Aceitar agendamento em dia útil dentro do horário.")
    void validarCenario2() {
        LocalDateTime segunda = LocalDateTime.now()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                .withHour(10).withMinute(0);

        assertThatCode(() -> validador.validar(new DadosAgendamento(1L, null, Especialidade.MOTOS, segunda)))
                .doesNotThrowAnyException();
    }
}
