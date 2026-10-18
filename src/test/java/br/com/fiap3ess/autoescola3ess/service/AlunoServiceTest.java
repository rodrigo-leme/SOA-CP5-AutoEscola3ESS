package br.com.fiap3ess.autoescola3ess.service;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.mapper.AlunoMapper;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.aluno.DadosAtualizacaoAluno;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.aluno.DadosCadastroAluno;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.aluno.DadosDetalhamentoAluno;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Aluno;
import br.com.fiap3ess.autoescola3ess.application.port.out.AlunoRepository;
import br.com.fiap3ess.autoescola3ess.application.service.AlunoService;
import br.com.fiap3ess.autoescola3ess.exception.type.AlunoNotFoundException;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.Endereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.mapper.EnderecoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AlunoServiceTest {
    @Mock
    AlunoRepository repository;

    AlunoService service;

    @BeforeEach
    void setUp() {
        EnderecoMapper enderecoMapper = new EnderecoMapper();
        service = new AlunoService(repository, new AlunoMapper(enderecoMapper), enderecoMapper);
    }

    @Test
    @DisplayName("Expectativa: Cadastrar aluno como ativo.")
    void cadastrarAlunoCenario1() {
        when(repository.save(any(Aluno.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DadosDetalhamentoAluno resultado = service.cadastrarAluno(new DadosCadastroAluno(
                "Aluno Teste", "aluno@email.com.br", "(11) 98765-4321", "12345678901",
                new DadosEndereco("Rua Teste", "10", null, "Centro", "São Paulo", "SP", "01234-567")));

        assertThat(resultado.nome()).isEqualTo("Aluno Teste");
        assertThat(resultado.ativo()).isTrue();
    }

    @Test
    @DisplayName("Expectativa: Atualizar apenas os campos informados do aluno.")
    void atualizarAlunoCenario1() {
        Aluno aluno = alunoAtivo();
        when(repository.findById(1L)).thenReturn(Optional.of(aluno));
        when(repository.save(aluno)).thenReturn(aluno);

        DadosDetalhamentoAluno resultado = service.atualizarAluno(
                new DadosAtualizacaoAluno(1L, "Nome Novo", null, null, null));

        assertThat(resultado.nome()).isEqualTo("Nome Novo");
        assertThat(resultado.email()).isEqualTo("aluno@email.com.br");
    }

    @Test
    @DisplayName("Expectativa: Exclusão lógica deve inativar o aluno.")
    void excluirAlunoCenario1() {
        Aluno aluno = alunoAtivo();
        when(repository.findById(1L)).thenReturn(Optional.of(aluno));

        service.excluirAluno(1L);

        assertThat(aluno.isAtivo()).isFalse();
        verify(repository).save(aluno);
    }

    @Test
    @DisplayName("Expectativa: Lançar exceção ao detalhar aluno inexistente.")
    void detalharAlunoCenario1() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.detalharAluno(99L))
                .isInstanceOf(AlunoNotFoundException.class);
    }

    private Aluno alunoAtivo() {
        return new Aluno(1L, "Aluno Teste", "aluno@email.com.br", "(11) 98765-4321", "12345678901",
                new Endereco("Rua Teste", "10", null, "Centro", "São Paulo", "SP", "01234-567"), true);
    }
}
