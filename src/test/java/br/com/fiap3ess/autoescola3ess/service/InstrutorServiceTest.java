package br.com.fiap3ess.autoescola3ess.service;

import br.com.fiap3ess.autoescola3ess.adapter.in.controller.mapper.InstrutorMapper;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.request.instrutor.DadosCadastroInstrutor;
import br.com.fiap3ess.autoescola3ess.adapter.in.controller.response.instrutor.DadosDetalhamentoInstrutor;
import br.com.fiap3ess.autoescola3ess.application.core.domain.Instrutor;
import br.com.fiap3ess.autoescola3ess.application.port.out.InstrutorRepository;
import br.com.fiap3ess.autoescola3ess.application.service.InstrutorService;
import br.com.fiap3ess.autoescola3ess.exception.type.InstrutorNotFoundException;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.Endereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.dto.DadosEndereco;
import br.com.fiap3ess.autoescola3ess.shared.vo.endereco.mapper.EnderecoMapper;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Especialidade;
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

// Teste unitário: o repositório é simulado (mock), então não precisa de banco de dados
@ExtendWith(MockitoExtension.class)
public class InstrutorServiceTest {
    @Mock
    InstrutorRepository repository;

    InstrutorService service;

    @BeforeEach
    void setUp() {
        EnderecoMapper enderecoMapper = new EnderecoMapper();
        service = new InstrutorService(repository, new InstrutorMapper(enderecoMapper), enderecoMapper);
    }

    @Test
    @DisplayName("Expectativa: Cadastrar instrutor ativo e devolver o ID gerado.")
    void cadastrarInstrutorCenario1() {
        when(repository.save(any(Instrutor.class))).thenAnswer(invocation -> {
            Instrutor i = invocation.getArgument(0);
            return new Instrutor(1L, i.getNome(), i.getEmail(), i.getTelefone(), i.getCnh(),
                    i.getEspecialidade(), i.getEndereco(), i.isAtivo());
        });

        DadosDetalhamentoInstrutor resultado = service.cadastrarInstrutor(new DadosCadastroInstrutor(
                "Instrutor Teste", "instrutor@email.com.br", "(11) 91234-5678", "01234567890",
                Especialidade.CARROS,
                new DadosEndereco("Rua Teste", "1", null, "Centro", "São Paulo", "SP", "01234-567")));

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.ativo()).isTrue();
        assertThat(resultado.especialidade()).isEqualTo(Especialidade.CARROS);
    }

    @Test
    @DisplayName("Expectativa: Lançar exceção ao detalhar instrutor inexistente.")
    void detalharInstrutorCenario1() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.detalharInstrutor(99L))
                .isInstanceOf(InstrutorNotFoundException.class);
    }

    @Test
    @DisplayName("Expectativa: Exclusão lógica deve inativar o instrutor.")
    void excluirInstrutorCenario1() {
        Instrutor instrutor = new Instrutor(1L, "Instrutor Teste", "instrutor@email.com.br",
                "(11) 91234-5678", "01234567890", Especialidade.MOTOS,
                new Endereco("Rua Teste", "1", null, "Centro", "São Paulo", "SP", "01234-567"), true);
        when(repository.findById(1L)).thenReturn(Optional.of(instrutor));

        service.excluirInstrutor(1L);

        assertThat(instrutor.isAtivo()).isFalse();
        verify(repository).save(instrutor);
    }
}
