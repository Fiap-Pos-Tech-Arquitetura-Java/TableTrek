package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.AvaliacaoDTO;
import br.com.fiap.postech.tabletrek.entities.Usuario;
import br.com.fiap.postech.tabletrek.helper.AvaliacaoHelper;
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
class AvaliacaoServiceIT {
    @Autowired
    private AvaliacaoService avaliacaoService;

    @Nested
    class CadastrarAvaliacao {
        @Test
        void devePermitirCadastrarAvaliacao() {
            // Arrange
            var avaliacao = AvaliacaoHelper.getAvaliacao(
                    false,
                    "15dc1918-9e48-4beb-9b63-4aad3914c8a7",
                    "d32c6406-a4a2-4503-ac12-d14b8a3b788f"
            );
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(avaliacao);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(avaliacao.getReservaMesa().getUsuario());
            // Act
            var avaliacaoSalvo = avaliacaoService.save(avaliacaoDTO, usuarioDTO);
            // Assert
            assertThat(avaliacaoSalvo)
                    .isInstanceOf(AvaliacaoDTO.class)
                    .isNotNull();
            assertThat(avaliacaoSalvo.idReservaMesa()).isEqualTo(avaliacaoDTO.idReservaMesa());
            assertThat(avaliacaoSalvo.nota()).isEqualTo(avaliacaoDTO.nota());
            assertThat(avaliacaoSalvo.comentario()).isEqualTo(avaliacaoDTO.comentario());
            assertThat(avaliacaoSalvo.id()).isNotNull();
        }
    }

    @Nested
    class BuscarAvaliacao {
        @Test
        void devePermitirBuscarAvaliacaoPorId() {
            // Arrange
            var id = UUID.fromString("d9cb2906-b308-4a54-a0f3-c0b89e67c7c0");
            var idReservaMesa = UUID.fromString("15dc1918-9e48-4beb-9b63-4aad3914c8a7");
            var nota = 5;
            var comentario = "boa comida e otimo atendimento";
            // Act
            var avaliacaoObtido = avaliacaoService.findById(id);
            // Assert
            assertThat(avaliacaoObtido).isNotNull().isInstanceOf(AvaliacaoDTO.class);
            assertThat(avaliacaoObtido.idReservaMesa()).isEqualTo(idReservaMesa);
            assertThat(avaliacaoObtido.nota()).isEqualTo(nota);
            assertThat(avaliacaoObtido.comentario()).isEqualTo(comentario);
            assertThat(avaliacaoObtido.id()).isNotNull();
            assertThat(avaliacaoObtido.id()).isEqualTo(id);
        }

        @Test
        void deveGerarExcecao_QuandoBuscarAvaliacaoPorId_idNaoExiste() {
            // Arrange
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(true);
            UUID uuid = avaliacaoDTO.id();
            // Act &&  Assert
            assertThatThrownBy(() -> avaliacaoService.findById(uuid))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Avaliacao não encontrado com o ID: " + avaliacaoDTO.id());
        }

        @Test
        void devePermitirBuscarTodosAvaliacao() {
            // Arrange
            AvaliacaoDTO criteriosDeBusca = new AvaliacaoDTO(null,null,null,null);
            // Act
            var listaAvaliacaosObtidos = avaliacaoService.findAll(Pageable.unpaged(), criteriosDeBusca);
            // Assert
            assertThat(listaAvaliacaosObtidos).isNotNull().isInstanceOf(Page.class);
            assertThat(listaAvaliacaosObtidos.getContent()).asList().hasSize(3);
            assertThat(listaAvaliacaosObtidos.getContent()).asList().allSatisfy(
                avaliacaoObtido -> {
                    assertThat(avaliacaoObtido).isNotNull();
                }
            );
        }
    }


    @Nested
    class AlterarAvaliacao {

        @Test
        void devePermitirAlterarAvaliacao() {
            // Arrange
            var id = UUID.fromString("384efca3-1b34-4022-b74b-9da5cbe0157d");
            var idUsuario = "739eef35-88bc-4de7-9f22-e19d8c5f22da";
            var novaAvaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(false, "15dc1918-9e48-4beb-9b63-4aad3914c8a7", idUsuario);
            var usuario = new Usuario();
            usuario.setId(UUID.fromString(idUsuario));
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            // Act
            var avaliacaoAtualizada = avaliacaoService.update(id, novaAvaliacaoDTO, usuarioDTO);
            // Assert
            assertThat(avaliacaoAtualizada).isNotNull().isInstanceOf(AvaliacaoDTO.class);
            assertThat(avaliacaoAtualizada.id()).isNull();
            assertThat(avaliacaoAtualizada.nota()).isEqualTo(novaAvaliacaoDTO.nota());
            assertThat(avaliacaoAtualizada.comentario()).isEqualTo(novaAvaliacaoDTO.comentario());
        }

        @Test
        void deveGerarExcecao_QuandoAlterarAvaliacaoPorId_idNaoExiste() {
            // Arrange
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(true);
            var novaAvaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(false, "15dc1918-9e48-4beb-9b63-4aad3914c8a7", "b35d3a29-408a-4d1a-964c-2261cb0e252f");
            var usuario = new Usuario();
            usuario.setId(UUID.fromString("a6df9ca4-09d7-41a1-bb5b-c8cb800f7452"));
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            var uuid = avaliacaoDTO.id();
            // Act &&  Assert
            assertThatThrownBy(() -> avaliacaoService.update(uuid, novaAvaliacaoDTO, usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Avaliacao não encontrado com o ID: " + avaliacaoDTO.id());
        }
    }

    @Nested
    class RemoverAvaliacao {
        @Test
        void devePermitirRemoverAvaliacao() {
            // Arrange
            var id = UUID.fromString("31971864-2c43-44cd-9cf8-943992981f54");
            var usuario = new Usuario();
            usuario.setId(UUID.fromString("739eef35-88bc-4de7-9f22-e19d8c5f22da"));
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            // Act
            avaliacaoService.delete(id, usuarioDTO);
            // Assert
            assertThatThrownBy(() -> avaliacaoService.findById(id))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Avaliacao não encontrado com o ID: " + id);
            ;
        }

        @Test
        void deveGerarExcecao_QuandRemoverAvaliacaoPorId_idNaoExiste() {
            // Arrange
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(true);
            var usuario = new Usuario();
            usuario.setId(UUID.fromString("a6df9ca4-09d7-41a1-bb5b-c8cb800f7452"));
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            var uuid = avaliacaoDTO.id();
            // Act &&  Assert
            assertThatThrownBy(() -> avaliacaoService.delete(uuid, usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Avaliacao não encontrado com o ID: " + avaliacaoDTO.id());
        }
    }
}