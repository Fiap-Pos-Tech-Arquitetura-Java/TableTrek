package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.ReservaMesaDTO;
import br.com.fiap.postech.tabletrek.helper.ReservaMesaHelper;
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
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(false, "52a85f11-9f0f-4dc6-b92f-abc3881328a8", "d32c6406-a4a2-4503-ac12-d14b8a3b788f");
            // Act
            var reservaMesaSalvo = reservaMesaService.save(reservaMesaDTO);
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
            // Act
            var reservaMesaObtido = reservaMesaService.findById(id);
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
            UUID uuid = reservaMesaDTO.id();
            // Act &&  Assert
            assertThatThrownBy(() -> reservaMesaService.findById(uuid))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("ReservaMesa não encontrado com o ID: " + reservaMesaDTO.id());
        }

        @Test
        void devePermitirBuscarTodosReservaMesa() {
            // Arrange
            ReservaMesaDTO criteriosDeBusca = new ReservaMesaDTO(null,null,null,null,null);
            // Act
            var listaReservaMesasObtidos = reservaMesaService.findAll(Pageable.unpaged(), criteriosDeBusca);
            // Assert
            assertThat(listaReservaMesasObtidos).isNotNull().isInstanceOf(Page.class);
            assertThat(listaReservaMesasObtidos.getContent()).asList().hasSize(3);
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
            // Act
            var reservaMesaAtualizada = reservaMesaService.finaliza(id);
            // Assert
            assertThat(reservaMesaAtualizada).isNotNull().isInstanceOf(ReservaMesaDTO.class);
            assertThat(reservaMesaAtualizada.id()).isNull();
            assertThat(reservaMesaAtualizada.status()).isEqualTo("FINALIZADA");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarReservaMesaPorId_idNaoExiste() {
            // Arrange
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(true);
            var uuid = reservaMesaDTO.id();
            // Act &&  Assert
            assertThatThrownBy(() -> reservaMesaService.finaliza(uuid))
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
            // Act
            reservaMesaService.delete(id);
            // Assert
            assertThatThrownBy(() -> reservaMesaService.findById(id))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("ReservaMesa não encontrado com o ID: " + id);
            ;
        }

        @Test
        void deveGerarExcecao_QuandRemoverReservaMesaPorId_idNaoExiste() {
            // Arrange
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(true);
            var uuid = reservaMesaDTO.id();
            // Act &&  Assert
            assertThatThrownBy(() -> reservaMesaService.delete(uuid))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("ReservaMesa não encontrado com o ID: " + reservaMesaDTO.id());
            ;
        }
    }
}