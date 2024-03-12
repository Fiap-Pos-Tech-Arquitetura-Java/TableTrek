package br.com.fiap.postech.tabletrek.repository;

import br.com.fiap.postech.tabletrek.entities.Usuario;
import br.com.fiap.postech.tabletrek.helper.UsuarioHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
class UsuarioRepositoryIT {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void devePermitirCriarEstrutura() {
        var totalRegistros = usuarioRepository.count();
        assertThat(totalRegistros).isEqualTo(3);
    }
    
    @Test
    void devePermitirCadastrarUsuario() {
        // Arrange
        var usuario = UsuarioHelper.getUsuario(true);
        // Act
        var usuarioCadastrado = usuarioRepository.save(usuario);
        // Assert
        assertThat(usuarioCadastrado).isInstanceOf(Usuario.class).isNotNull();
        assertThat(usuarioCadastrado.getId()).isEqualTo(usuario.getId());
        assertThat(usuarioCadastrado.getNome()).isEqualTo(usuario.getNome());
        assertThat(usuarioCadastrado.getEmail()).isEqualTo(usuario.getEmail());
        assertThat(usuarioCadastrado.getSenha()).isEqualTo(usuario.getSenha());
        assertThat(usuarioCadastrado.getTelefone()).isEqualTo(usuario.getTelefone());
    }
    @Test
    void devePermitirBuscarUsuario() {
        // Arrange
        var id = UUID.fromString("d32c6406-a4a2-4503-ac12-d14b8a3b788f");
        var nome = "Anderson Wagner";
        // Act
        var usuarioOpcional = usuarioRepository.findById(id);
        // Assert
        assertThat(usuarioOpcional).isPresent();
        usuarioOpcional.ifPresent(
                usuarioRecebido -> {
                    assertThat(usuarioRecebido).isInstanceOf(Usuario.class).isNotNull();
                    assertThat(usuarioRecebido.getId()).isEqualTo(id);
                    assertThat(usuarioRecebido.getNome()).isEqualTo(nome);
                }
        );
        //verify(usuarioRepository, times(1)).findById(usuario.getId());
    }
    @Test
    void devePermitirRemoverUsuario() {
        // Arrange
        var id = UUID.fromString("ffd28058-4c16-41ce-9f03-80dfbc177aaf");
        // Act
        usuarioRepository.deleteById(id);
        // Assert
        var usuarioOpcional = usuarioRepository.findById(id);
        assertThat(usuarioOpcional).isEmpty();
    }
    @Test
    void devePermitirListarUsuarios() {
        // Arrange
        // Act
        var usuariosListados = usuarioRepository.findAll();
        // Assert
        assertThat(usuariosListados).hasSize(3);
    }
}
