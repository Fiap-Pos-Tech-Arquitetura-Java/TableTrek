package br.com.fiap.postech.tabletrek.repository;

import br.com.fiap.postech.tabletrek.entities.ReservaMesa;
import br.com.fiap.postech.tabletrek.helper.ReservaMesaHelper;
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
class ReservaMesaRepositoryIT {
    @Autowired
    private ReservaMesaRepository reservaMesaRepository;

    @Test
    void devePermitirCriarEstrutura() {
        var totalRegistros = reservaMesaRepository.count();
        assertThat(totalRegistros).isEqualTo(3);
    }
    
    @Test
    void devePermitirCadastrarReservaMesa() {
        // Arrange
        var reservaMesa = ReservaMesaHelper.getReservaMesa(true, "52a85f11-9f0f-4dc6-b92f-abc3881328a8", "d32c6406-a4a2-4503-ac12-d14b8a3b788f");
        // Act
        var reservaMesaCadastrado = reservaMesaRepository.save(reservaMesa);
        // Assert
        assertThat(reservaMesaCadastrado).isInstanceOf(ReservaMesa.class).isNotNull();
        assertThat(reservaMesaCadastrado.getId()).isEqualTo(reservaMesa.getId());
        assertThat(reservaMesaCadastrado.getRestaurante()).isEqualTo(reservaMesa.getRestaurante());
        assertThat(reservaMesaCadastrado.getUsuario()).isEqualTo(reservaMesa.getUsuario());
        assertThat(reservaMesaCadastrado.getHorario()).isEqualTo(reservaMesa.getHorario());
        assertThat(reservaMesaCadastrado.getStatus()).isEqualTo(reservaMesa.getStatus());
    }
    @Test
    void devePermitirBuscarReservaMesa() {
        // Arrange
        var id = UUID.fromString("15dc1918-9e48-4beb-9b63-4aad3914c8a7");
        var status = "FINALIZADA";
        // Act
        var reservaMesaOpcional = reservaMesaRepository.findById(id);
        // Assert
        assertThat(reservaMesaOpcional).isPresent();
        reservaMesaOpcional.ifPresent(
                reservaMesaRecebido -> {
                    assertThat(reservaMesaRecebido).isInstanceOf(ReservaMesa.class).isNotNull();
                    assertThat(reservaMesaRecebido.getId()).isEqualTo(id);
                    assertThat(reservaMesaRecebido.getStatus()).isEqualTo(status);
                }
        );
        //verify(reservaMesaRepository, times(1)).findById(reservaMesa.getId());
    }
    @Test
    void devePermitirRemoverReservaMesa() {
        // Arrange
        var id = UUID.fromString("86d6f0bb-3dd8-48f3-9078-4fb8c8e2c7c1");
        // Act
        reservaMesaRepository.deleteById(id);
        // Assert
        var reservaMesaOpcional = reservaMesaRepository.findById(id);
        assertThat(reservaMesaOpcional).isEmpty();
    }
    @Test
    void devePermitirListarReservaMesas() {
        // Arrange
        // Act
        var reservaMesasListados = reservaMesaRepository.findAll();
        // Assert
        assertThat(reservaMesasListados).hasSize(3);
    }
}
