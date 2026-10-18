package br.com.fiap3ess.autoescola3ess.service;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrucao.DadosAgendamento;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.instrucao.DadosDetalhamentoAgendamento;
import br.com.fiap3ess.autoescola3ess.adapter.out.repository.mapper.InstrutorEntityMapper;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Aluno;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrucao;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrutor;
import br.com.fiap3ess.autoescola3ess.application.core.specification.contract.ValidadorAgendamento;
import br.com.fiap3ess.autoescola3ess.application.port.out.AlunoRepository;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrucaoRepository;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrutorRepository;
import br.com.fiap3ess.autoescola3ess.application.service.AgendaDeInstrucoes;
import br.com.fiap3ess.autoescola3ess.exception.type.AlunoNotFoundException;
import br.com.fiap3ess.autoescola3ess.exception.type.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.Endereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Especialidade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AgendaDeInstrucoesTest {
    @Mock
    AlunoRepository alunoRepository;

    @Mock
    InstrutorRepository instrutorRepository;

    @Mock
    InstrucaoRepository instrucaoRepository;

    @Mock
    ValidadorAgendamento validador;

    AgendaDeInstrucoes agenda;

    final LocalDateTime proximaSegundaAs10 = LocalDateTime
            .now()
            .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
            .withHour(10).withMinute(0).withSecond(0).withNano(0);

    @BeforeEach
    void setUp() {
        agenda = new AgendaDeInstrucoes(
                alunoRepository,
                instrutorRepository,
                instrucaoRepository,
                List.of(validador),
                new InstrutorEntityMapper()
        );
    }

    @Test
    @DisplayName("Expectativa: Lançar exceção quando o aluno não existir.")
    void agendarCenario1() {
        when(alunoRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> agenda.agendar(new DadosAgendamento(1L, null, Especialidade.MOTOS, proximaSegundaAs10)))
                .isInstanceOf(AlunoNotFoundException.class);
        verify(instrucaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Expectativa: Agendar instrução quando o instrutor for informado e as validações passarem.")
    void agendarCenario2() {
        DadosAgendamento dados = new DadosAgendamento(1L, 2L, null, proximaSegundaAs10);
        when(alunoRepository.existsById(1L)).thenReturn(true);
        when(instrutorRepository.existsById(2L)).thenReturn(true);
        when(alunoRepository.getReferenceById(1L)).thenReturn(aluno());
        when(instrutorRepository.getReferenceById(2L)).thenReturn(instrutor());
        when(instrucaoRepository.save(any(Instrucao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DadosDetalhamentoAgendamento resultado = agenda.agendar(dados);

        verify(validador).validar(dados);
        assertThat(resultado.nomeAluno()).isEqualTo("Aluno Teste");
        assertThat(resultado.nomeInstrutor()).isEqualTo("Instrutor Teste");
        assertThat(resultado.dataHora()).isEqualTo(proximaSegundaAs10);
    }

    @Test
    @DisplayName("Expectativa: Exigir especialidade quando o instrutor não for informado.")
    void agendarCenario3() {
        when(alunoRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> agenda.agendar(new DadosAgendamento(1L, null, null, proximaSegundaAs10)))
                .isInstanceOf(ValidacaoException.class);
    }

    @Test
    @DisplayName("Expectativa: Lançar exceção quando não houver instrutor disponível.")
    void agendarCenario4() {
        when(alunoRepository.existsById(1L)).thenReturn(true);
        when(instrutorRepository.escolherInstrutorAleatorioDisponivel(Especialidade.VANS, proximaSegundaAs10))
                .thenReturn(null);

        assertThatThrownBy(() -> agenda.agendar(new DadosAgendamento(1L, null, Especialidade.VANS, proximaSegundaAs10)))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Nenhum instrutor");
    }

    private Endereco endereco() {
        return new Endereco("Rua Teste", "1", null, "Centro", "São Paulo", "SP", "01234-567");
    }

    private Aluno aluno() {
        return new Aluno(1L, "Aluno Teste", "aluno@email.com.br", "(11) 98765-4321", "12345678901", endereco(), true);
    }

    private Instrutor instrutor() {
        return new Instrutor(2L, "Instrutor Teste", "instrutor@email.com.br", "(11) 91234-5678",
                "01234567890", Especialidade.CARROS, endereco(), true);
    }
}
