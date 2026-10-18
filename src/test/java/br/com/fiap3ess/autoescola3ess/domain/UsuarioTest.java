package br.com.fiap3ess.autoescola3ess.domain;

import br.com.fiap3ess.autoescola3ess.application.core.domain.Usuario;
import br.com.fiap3ess.autoescola3ess.config.security.service.TokenService;
import br.com.fiap3ess.autoescola3ess.shared.vo.enumeration.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UsuarioTest {
    @Test
    @DisplayName("Expectativa: Perfil ADMIN deve gerar a autoridade ROLE_ADMIN.")
    void authoritiesCenario1() {
        Usuario usuario = new Usuario(1L, "admin", "senhaHash", Role.ADMIN);

        assertThat(usuario.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
        assertThat(usuario.getUsername()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Expectativa: Token JWT gerado deve devolver o login do usuário como subject.")
    void tokenCenario1() {
        TokenService tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "segredo-de-teste");
        Usuario usuario = new Usuario(1L, "usuario.teste", "senhaHash", Role.USER);

        String token = tokenService.generateToken(usuario);

        assertThat(tokenService.getSubject(token)).isEqualTo("usuario.teste");
    }

    @Test
    @DisplayName("Expectativa: Token JWT inválido deve ser recusado.")
    void tokenCenario2() {
        TokenService tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "segredo-de-teste");

        assertThatThrownBy(() -> tokenService.getSubject("token.invalido.qualquer"))
                .isInstanceOf(RuntimeException.class);
    }
}
