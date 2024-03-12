package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import br.com.fiap.postech.tabletrek.helper.UsuarioHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
class UsuarioServiceIT {
    @Autowired
    private UsuarioService usuarioService;

    @Nested
    class CadastrarUsuario {
        @Test
        void devePermitirCadastrarUsuario() {
            // Arrange
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(false);
            // Act
            var usuarioSalvo = usuarioService.save(usuarioDTO);
            // Assert
            assertThat(usuarioSalvo)
                    .isInstanceOf(UsuarioDTO.class)
                    .isNotNull();
            assertThat(usuarioSalvo.nome()).isEqualTo(usuarioDTO.nome());
            assertThat(usuarioSalvo.id()).isNotNull();
        }
    }

    @Nested
    class BuscarUsuario {
        @Test
        void devePermitirBuscarUsuarioPorId() {
            // Arrange
            var id = UUID.fromString("d32c6406-a4a2-4503-ac12-d14b8a3b788f");
            var nome = "Anderson Wagner";
            // Act
            var usuarioObtido = usuarioService.findById(id);
            // Assert
            assertThat(usuarioObtido).isNotNull().isInstanceOf(UsuarioDTO.class);
            assertThat(usuarioObtido.nome()).isEqualTo(nome);
            assertThat(usuarioObtido.id()).isNotNull();
            assertThat(usuarioObtido.id()).isEqualTo(id);
        }

        @Test
        void deveGerarExcecao_QuandoBuscarUsuarioPorId_idNaoExiste() {
            // Arrange
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            UUID uuid = usuarioDTO.id();
            // Act &&  Assert
            assertThatThrownBy(() -> usuarioService.findById(uuid))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Usuario não encontrado com o ID: " + usuarioDTO.id());
        }

        @Test
        void devePermitirBuscarTodosUsuario() {
            // Arrange
            UsuarioDTO criteriosDeBusca = new UsuarioDTO(null,null,null,null,null);
            // Act
            var listaUsuariosObtidos = usuarioService.findAll(Pageable.unpaged(), criteriosDeBusca);
            // Assert
            assertThat(listaUsuariosObtidos).isNotNull().isInstanceOf(Page.class);
            assertThat(listaUsuariosObtidos.getContent()).asList().hasSize(3);
            assertThat(listaUsuariosObtidos.getContent()).asList().allSatisfy(
                usuarioObtido -> {
                    assertThat(usuarioObtido).isNotNull();
                }
            );
        }
    }


    @Nested
    class AlterarUsuario {

        @Test
        void devePermitirAlterarUsuario() {
            // Arrange
            var id = UUID.fromString("a6df9ca4-09d7-41a1-bb5b-c8cb800f7452");
            var nome = "Kaiby novo";
            var email = "aaaa@bbb.com";
            var senha = "165243";
            var telefone = 11999999999L;
            // Act
            var usuarioAtualizada = usuarioService.update(id, new UsuarioDTO(id, nome, email, senha, telefone));
            // Assert
            assertThat(usuarioAtualizada).isNotNull().isInstanceOf(UsuarioDTO.class);
            assertThat(usuarioAtualizada.id()).isNull();
            assertThat(usuarioAtualizada.nome()).isEqualTo(nome);
            assertThat(usuarioAtualizada.email()).isEqualTo(email);
            //assertThat(usuarioAtualizada.senha()).isEqualTo(senha);
            assertThat(usuarioAtualizada.telefone()).isEqualTo(telefone);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarUsuarioPorId_idNaoExiste() {
            // Arrange
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            var uuid = usuarioDTO.id();
            // Act &&  Assert
            assertThatThrownBy(() -> usuarioService.update(uuid, usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Usuario não encontrado com o ID: " + usuarioDTO.id());
        }
    }

    @Nested
    class RemoverUsuario {
        @Test
        void devePermitirRemoverUsuario() {
            // Arrange
            var id = UUID.fromString("ffd28058-4c16-41ce-9f03-80dfbc177aaf");
            // Act
            usuarioService.delete(id);
            // Assert
            assertThatThrownBy(() -> usuarioService.findById(id))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Usuario não encontrado com o ID: " + id);
            ;
        }

        @Test
        void deveGerarExcecao_QuandRemoverUsuarioPorId_idNaoExiste() {
            // Arrange
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            var uuid = usuarioDTO.id();
            // Act &&  Assert
            assertThatThrownBy(() -> usuarioService.delete(uuid))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Usuario não encontrado com o ID: " + usuarioDTO.id());
            ;
        }
    }
}