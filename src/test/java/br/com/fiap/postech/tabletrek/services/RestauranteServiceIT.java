package br.com.fiap.postech.tabletrek.services;


import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.entities.Usuario;
import br.com.fiap.postech.tabletrek.helper.RestauranteHelper;
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
class RestauranteServiceIT {
    @Autowired
    private RestauranteService restauranteService;

    @Nested
    class CadastrarRestaurante {
        @Test
        void devePermitirCadastrarRestaurante() {
            // Arrange
            var restaurante = RestauranteHelper.getRestaurante(false, "d32c6406-a4a2-4503-ac12-d14b8a3b788f");
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(restaurante);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(restaurante.getUsuario());
            // Act
            var restauranteSalvo = restauranteService.save(usuarioDTO, restauranteDTO);
            // Assert
            assertThat(restauranteSalvo)
                    .isInstanceOf(RestauranteDTO.class)
                    .isNotNull();
            assertThat(restauranteSalvo.nome()).isEqualTo(restauranteDTO.nome());
            assertThat(restauranteSalvo.id()).isNotNull();
        }
    }

    @Nested
    class BuscarRestaurante {
        @Test
        void devePermitirBuscarRestaurantePorId() {
            // Arrange
            var id = UUID.fromString("b35d3a29-408a-4d1a-964c-2261cb0e252f");
            var nome = "Tojiro Sushi";
            // Act
            var restauranteObtido = restauranteService.findById(id);
            // Assert
            assertThat(restauranteObtido).isNotNull().isInstanceOf(RestauranteDTO.class);
            assertThat(restauranteObtido.nome()).isEqualTo(nome);
            assertThat(restauranteObtido.id()).isNotNull();
            assertThat(restauranteObtido.id()).isEqualTo(id);
        }

        @Test
        void deveGerarExcecao_QuandoBuscarRestaurantePorId_idNaoExiste() {
            // Arrange
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(true);
            UUID uuid = restauranteDTO.id();
            // Act &&  Assert
            assertThatThrownBy(() -> restauranteService.findById(uuid))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Restaurante não encontrado com o ID: " + restauranteDTO.id());
        }

        @Test
        void devePermitirBuscarTodosRestaurante() {
            // Arrange
            RestauranteDTO criteriosDeBusca = new RestauranteDTO(null,null,null,null,null,null);
            // Act
            var listaRestaurantesObtidos = restauranteService.findAll(Pageable.unpaged(), criteriosDeBusca);
            // Assert
            assertThat(listaRestaurantesObtidos).isNotNull().isInstanceOf(Page.class);
            assertThat(listaRestaurantesObtidos.getContent()).asList().hasSize(4);
            assertThat(listaRestaurantesObtidos.getContent()).asList().allSatisfy(
                restauranteObtido -> {
                    assertThat(restauranteObtido).isNotNull();
                }
            );
        }
    }


    @Nested
    class AlterarRestaurante {

        @Test
        void devePermitirAlterarRestaurante() {
            // Arrange
            var id = UUID.fromString("b35d3a29-408a-4d1a-964c-2261cb0e252f");
            var idUsuario = UUID.fromString("a6df9ca4-09d7-41a1-bb5b-c8cb800f7452");
            var usuario = new Usuario();
            usuario.setId(idUsuario);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            var nome = "Tojiro Sushi novo";
            var localizacao = "rua aaaabbbbcccc 12341 - Campinas - SP";
            var horarioFuncionamento = "13h as 23h30m";
            var capacidade = 123;
            var tipoCozinha = "Japonesa";
            // Act
            var restauranteAtualizada = restauranteService.update(id, usuarioDTO, new RestauranteDTO(id, nome, localizacao, horarioFuncionamento, capacidade, tipoCozinha));
            // Assert
            assertThat(restauranteAtualizada).isNotNull().isInstanceOf(RestauranteDTO.class);
            assertThat(restauranteAtualizada.id()).isNull();
            assertThat(restauranteAtualizada.nome()).isEqualTo(nome);
            assertThat(restauranteAtualizada.localizacao()).isEqualTo(localizacao);
            assertThat(restauranteAtualizada.horarioFuncionamento()).isEqualTo(horarioFuncionamento);
            assertThat(restauranteAtualizada.capacidade()).isEqualTo(capacidade);
            assertThat(restauranteAtualizada.tipoCozinha()).isEqualTo(tipoCozinha);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarRestaurante_OutroUsuarioSemSerOQueCadastrou() {
            var id = UUID.fromString("b35d3a29-408a-4d1a-964c-2261cb0e252f");
            var idUsuario = UUID.fromString("d32c6406-a4a2-4503-ac12-d14b8a3b788f");
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(false);
            var usuario = new Usuario();
            usuario.setId(idUsuario);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            // Act &&  Assert
            assertThatThrownBy(() -> restauranteService.update(id, usuarioDTO, restauranteDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Restaurante somente pode ser alterado pelo seu dono. ID: " + id);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarRestaurantePorId_idNaoExiste() {
            // Arrange
            var restaurante = RestauranteHelper.getRestaurante(true);
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(restaurante);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(restaurante.getUsuario());
            var uuid = restauranteDTO.id();
            // Act &&  Assert
            assertThatThrownBy(() -> restauranteService.update(uuid, usuarioDTO, restauranteDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Restaurante não encontrado com o ID: " + restauranteDTO.id());
        }
    }

    @Nested
    class RemoverRestaurante {
        @Test
        void devePermitirRemoverRestaurante() {
            // Arrange
            var id = UUID.fromString("b35d3a29-408a-4d1a-964c-2261cb0e252f");
            var idUsuario = UUID.fromString("a6df9ca4-09d7-41a1-bb5b-c8cb800f7452");
            var usuario = new Usuario();
            usuario.setId(idUsuario);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            // Act
            restauranteService.delete(id, usuarioDTO);
            // Assert
            assertThatThrownBy(() -> restauranteService.findById(id))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Restaurante não encontrado com o ID: " + id);
        }

        @Test
        void deveGerarExcecao_QuandoRemoverRestaurante_OutroUsuarioSemSerOQueCadastrou() {
            // Arrange
            var id = UUID.fromString("ada8399b-44f0-499c-82d9-5ca9ed1670da");
            var idUsuario = UUID.fromString("a6df9ca4-09d7-41a1-bb5b-c8cb800f7452");
            var usuario = new Usuario();
            usuario.setId(idUsuario);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            // Act &&  Assert
            assertThatThrownBy(() -> restauranteService.delete(id, usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Restaurante somente pode ser alterado pelo seu dono. ID: " + id);
        }

        @Test
        void deveGerarExcecao_QuandRemoverRestaurantePorId_idNaoExiste() {
            // Arrange
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(true);
            var uuid = restauranteDTO.id();
            // Act &&  Assert
            assertThatThrownBy(() -> restauranteService.delete(uuid, null))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Restaurante não encontrado com o ID: " + restauranteDTO.id());
            ;
        }
    }
}