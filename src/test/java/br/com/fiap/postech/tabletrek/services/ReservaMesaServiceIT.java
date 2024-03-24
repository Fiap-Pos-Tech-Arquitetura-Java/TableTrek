package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.ReservaMesaDTO;
import br.com.fiap.postech.tabletrek.entities.Usuario;
import br.com.fiap.postech.tabletrek.helper.ReservaMesaHelper;
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
class ReservaMesaServiceIT {
    @Autowired
    private ReservaMesaService reservaMesaService;

    @Nested
    class CadastrarReservaMesa {
        @Test
        void devePermitirCadastrarReservaMesa() {
            // Arrange
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(false, "52a85f11-9f0f-4dc6-b92f-abc3881328a8");
            var usuario = new Usuario();
            usuario.setId(UUID.fromString("d32c6406-a4a2-4503-ac12-d14b8a3b788f"));
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            // Act
            var reservaMesaSalvo = reservaMesaService.save(usuarioDTO, reservaMesaDTO);
            // Assert
            assertThat(reservaMesaSalvo)
                    .isInstanceOf(ReservaMesaDTO.class)
                    .isNotNull();
            assertThat(reservaMesaSalvo.idRestaurante()).isEqualTo(reservaMesaDTO.idRestaurante());
            assertThat(reservaMesaSalvo.id()).isNotNull();
        }
    }

    @Nested
    class BuscarReservaMesa {
        @Test
        void devePermitirBuscarReservaMesaPorId() {
            // Arrange
            var id = UUID.fromString("c055e2a7-4871-4408-aeb5-cbf2e3d31eaa");
            var idRestaurante = UUID.fromString("52a85f11-9f0f-4dc6-b92f-abc3881328a8");
            var usuario = new Usuario();
            usuario.setId(UUID.fromString("d32c6406-a4a2-4503-ac12-d14b8a3b788f"));
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            // Act
            var reservaMesaObtido = reservaMesaService.findById(id, usuarioDTO);
            // Assert
            assertThat(reservaMesaObtido).isNotNull().isInstanceOf(ReservaMesaDTO.class);
            assertThat(reservaMesaObtido.idRestaurante()).isEqualTo(idRestaurante);
            assertThat(reservaMesaObtido.id()).isNotNull();
            assertThat(reservaMesaObtido.id()).isEqualTo(id);
        }

        @Test
        void deveGerarExcecao_QuandoBuscarReservaMesaPorId_idNaoExiste() {
            // Arrange
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(true);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            UUID uuid = reservaMesaDTO.id();
            // Act &&  Assert
            assertThatThrownBy(() -> reservaMesaService.findById(uuid, usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("ReservaMesa não encontrado com o ID: " + reservaMesaDTO.id());
        }

        @Test
        void devePermitirBuscarTodosReservaMesa() {
            // Arrange
            ReservaMesaDTO criteriosDeBusca = new ReservaMesaDTO(null,null,null,null);
            var usuario = new Usuario();
            usuario.setId(UUID.fromString("d32c6406-a4a2-4503-ac12-d14b8a3b788f"));
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            // Act
            var listaReservaMesasObtidos = reservaMesaService.findAll(Pageable.unpaged(), usuarioDTO, criteriosDeBusca);
            // Assert
            assertThat(listaReservaMesasObtidos).isNotNull().isInstanceOf(Page.class);
            assertThat(listaReservaMesasObtidos.getContent()).asList().hasSize(1);
            assertThat(listaReservaMesasObtidos.getContent()).asList().allSatisfy(
                reservaMesaObtido -> {
                    assertThat(reservaMesaObtido).isNotNull();
                }
            );
        }
    }


    @Nested
    class AlterarReservaMesa {

        @Test
        void devePermitirAlterarReservaMesa() {
            // Arrange
            var id = UUID.fromString("c055e2a7-4871-4408-aeb5-cbf2e3d31eaa");
            var usuario = new Usuario();
            usuario.setId(UUID.fromString("d32c6406-a4a2-4503-ac12-d14b8a3b788f"));
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            // Act
            var reservaMesaAtualizada = reservaMesaService.finaliza(id, usuarioDTO);
            // Assert
            assertThat(reservaMesaAtualizada).isNotNull().isInstanceOf(ReservaMesaDTO.class);
            assertThat(reservaMesaAtualizada.id()).isNull();
            assertThat(reservaMesaAtualizada.status()).isEqualTo("FINALIZADA");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarReservaMesaPorId_idNaoExiste() {
            // Arrange
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(true);
            var usuario = new Usuario();
            usuario.setId(UUID.fromString("d32c6406-a4a2-4503-ac12-d14b8a3b788f"));
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            var uuid = reservaMesaDTO.id();
            // Act &&  Assert
            assertThatThrownBy(() -> reservaMesaService.finaliza(uuid, usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("ReservaMesa não encontrado com o ID: " + reservaMesaDTO.id());
        }
    }

    @Nested
    class RemoverReservaMesa {
        @Test
        void devePermitirRemoverReservaMesa() {
            // Arrange
            var id = UUID.fromString("86d6f0bb-3dd8-48f3-9078-4fb8c8e2c7c1");
            var usuario = new Usuario();
            usuario.setId(UUID.fromString("ffd28058-4c16-41ce-9f03-80dfbc177aaf"));
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            // Act
            reservaMesaService.delete(id, usuarioDTO);
            // Assert
            assertThatThrownBy(() -> reservaMesaService.findById(id, usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("ReservaMesa não encontrado com o ID: " + id);
        }

        @Test
        void deveGerarExcecao_QuandoRemoverReservaMesaPorId_OutroUsuarioSemSerOQueCadastrou() {
            // Arrange
            var id = UUID.fromString("86d6f0bb-3dd8-48f3-9078-4fb8c8e2c7c1");
            var usuario = new Usuario();
            usuario.setId(UUID.fromString("d32c6406-a4a2-4503-ac12-d14b8a3b788f"));
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            // Act &&  Assert
            assertThatThrownBy(() -> reservaMesaService.delete(id, usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Somente o usuario que fez a reserva ou o dono do restaurante podem deletar uma reserva de uma mesa. ID: " + id);
        }

        @Test
        void deveGerarExcecao_QuandoRemoverReservaMesaPorId_idNaoExiste() {
            // Arrange
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(true);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            var uuid = reservaMesaDTO.id();
            // Act &&  Assert
            assertThatThrownBy(() -> reservaMesaService.delete(uuid, usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("ReservaMesa não encontrado com o ID: " + reservaMesaDTO.id());
            ;
        }
    }
}