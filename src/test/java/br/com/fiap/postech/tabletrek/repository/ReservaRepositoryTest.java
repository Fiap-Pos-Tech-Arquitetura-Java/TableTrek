package br.com.fiap.postech.tabletrek.repository;

import br.com.fiap.postech.tabletrek.entities.ReservaMesa;
import br.com.fiap.postech.tabletrek.helper.ReservaMesaHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReservaRepositoryTest {

    @Mock
    private ReservaMesaRepository reservaMesaRepository;

    AutoCloseable openMocks;
    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void devePermitirCadastrarReservaMesa() {
        // Arrange
        var reservaMesa = ReservaMesaHelper.getReservaMesa(false, "52a85f11-9f0f-4dc6-b92f-abc3881328a8", "d32c6406-a4a2-4503-ac12-d14b8a3b788f");
        when(reservaMesaRepository.save(any(ReservaMesa.class))).thenReturn(reservaMesa);
        // Act
        var savedReservaMesa = reservaMesaRepository.save(reservaMesa);
        // Assert
        assertThat(savedReservaMesa).isNotNull().isEqualTo(reservaMesa);
        verify(reservaMesaRepository, times(1)).save(any(ReservaMesa.class));
    }

    @Test
    void devePermitirBuscarReservaMesa() {
        // Arrange
        var reservaMesa = ReservaMesaHelper.getReservaMesa(true, "52a85f11-9f0f-4dc6-b92f-abc3881328a8", "d32c6406-a4a2-4503-ac12-d14b8a3b788f");
        when(reservaMesaRepository.findById(reservaMesa.getId())).thenReturn(Optional.of(reservaMesa));
        // Act
        var reservaMesaOpcional = reservaMesaRepository.findById(reservaMesa.getId());
        // Assert
        assertThat(reservaMesaOpcional).isNotNull().containsSame(reservaMesa);
        reservaMesaOpcional.ifPresent(
                reservaMesaRecebido -> {
                    assertThat(reservaMesaRecebido).isInstanceOf(ReservaMesa.class).isNotNull();
                    assertThat(reservaMesaRecebido.getId()).isEqualTo(reservaMesa.getId());
                    assertThat(reservaMesaRecebido.getStatus()).isEqualTo(reservaMesa.getStatus());
                }
        );
        verify(reservaMesaRepository, times(1)).findById(reservaMesa.getId());
    }
    @Test
    void devePermitirRemoverReservaMesa() {
        //Arrange
        var id = UUID.randomUUID();
        doNothing().when(reservaMesaRepository).deleteById(id);
        //Act
        reservaMesaRepository.deleteById(id);
        //Assert
        verify(reservaMesaRepository, times(1)).deleteById(id);
    }
    @Test
    void devePermitirListarReservaMesas() {
        // Arrange
        var reservaMesa1 = ReservaMesaHelper.getReservaMesa(true, "52a85f11-9f0f-4dc6-b92f-abc3881328a8", "d32c6406-a4a2-4503-ac12-d14b8a3b788f");
        var reservaMesa2 = ReservaMesaHelper.getReservaMesa(true, "b35d3a29-408a-4d1a-964c-2261cb0e252f", "a6df9ca4-09d7-41a1-bb5b-c8cb800f7452");
        var listaReservaMesas = Arrays.asList(
                reservaMesa1,
                reservaMesa2
        );
        when(reservaMesaRepository.findAll()).thenReturn(listaReservaMesas);
        // Act
        var reservaMesasListados = reservaMesaRepository.findAll();
        assertThat(reservaMesasListados)
                .hasSize(2)
                .containsExactlyInAnyOrder(reservaMesa1, reservaMesa2);
        verify(reservaMesaRepository, times(1)).findAll();
    }
}