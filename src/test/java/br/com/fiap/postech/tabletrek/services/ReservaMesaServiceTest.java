package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.ReservaMesaDTO;
import br.com.fiap.postech.tabletrek.entities.ReservaMesa;
import br.com.fiap.postech.tabletrek.entities.Restaurante;
import br.com.fiap.postech.tabletrek.helper.ReservaMesaHelper;
import br.com.fiap.postech.tabletrek.helper.RestauranteHelper;
import br.com.fiap.postech.tabletrek.helper.UsuarioHelper;
import br.com.fiap.postech.tabletrek.repository.ReservaMesaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservaMesaServiceTest {

    private ReservaMesaService reservaMesaService;

    @Mock
    private ReservaMesaRepository reservaMesaRepository;

    @Mock
    private RestauranteService restauranteService;

    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        reservaMesaService = new ReservaMesaServiceImpl(reservaMesaRepository, restauranteService);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class CadastrarReservaMesa {
        @Test
        void devePermitirCadastrarReservaMesa() {
            // Arrange
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(true);
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(false, restauranteDTO.id().toString());
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            when(restauranteService.findById(restauranteDTO.id())).thenReturn(restauranteDTO);
            when(reservaMesaRepository.save(any(ReservaMesa.class))).thenAnswer(r -> r.getArgument(0));
            when(reservaMesaRepository.countByRestauranteAndHorario(any(Restaurante.class), any(LocalDateTime.class))).thenReturn(0L);
            // Act
            var reservaMesaSalvo = reservaMesaService.save(usuarioDTO, reservaMesaDTO);
            // Assert
            assertThat(reservaMesaSalvo)
                    .isInstanceOf(ReservaMesaDTO.class)
                    .isNotNull();
            assertThat(reservaMesaSalvo.idRestaurante()).isEqualTo(reservaMesaDTO.idRestaurante());
            assertThat(reservaMesaSalvo.id()).isNotNull();
            verify(restauranteService, times(1)).findById(any(UUID.class));
            verify(reservaMesaRepository, times(1)).save(any(ReservaMesa.class));
            verify(reservaMesaRepository, times(1)).countByRestauranteAndHorario(any(Restaurante.class), any(LocalDateTime.class));
        }
        @Test
        void deveGerarExcecao_QuandoCadastrarReservaMesa_mesaIndisponivel() {
            // Arrange
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(true);
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(false, restauranteDTO.id().toString());
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            when(restauranteService.findById(restauranteDTO.id())).thenReturn(restauranteDTO);
            when(reservaMesaRepository.save(any(ReservaMesa.class))).thenAnswer(r -> r.getArgument(0));
            when(reservaMesaRepository.countByRestauranteAndHorario(any(Restaurante.class), any(LocalDateTime.class))).thenReturn((long) (restauranteDTO.capacidade() + 10));
            // Act
            assertThatThrownBy(() -> reservaMesaService.save(usuarioDTO, reservaMesaDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Não foi possivel reservar uma mesa nesse restaurante por falta de disponibilidade para o horário informado");
            // Assert
            verify(restauranteService, times(1)).findById(any(UUID.class));
            verify(reservaMesaRepository, times(0)).save(any(ReservaMesa.class));
            verify(reservaMesaRepository, times(1)).countByRestauranteAndHorario(any(Restaurante.class), any(LocalDateTime.class));
        }
    }

    @Nested
    class BuscarReservaMesa {
        @Test
        void devePermitirBuscarReservaMesaPorId() {
            // Arrange
            var reservaMesa = ReservaMesaHelper.getReservaMesa(true);
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(reservaMesa);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(reservaMesa.getUsuario());
            when(reservaMesaRepository.findById(reservaMesaDTO.id())).thenReturn(Optional.of(reservaMesa));
            // Act
            var restautranteObtido = reservaMesaService.findById(reservaMesaDTO.id(), usuarioDTO);
            // Assert
            assertThat(restautranteObtido).isEqualTo(reservaMesaDTO);
            verify(reservaMesaRepository, times(1)).findById(any(UUID.class));
        }
        @Test
        void deveGerarExcecao_QuandoBuscarReservaMesaPorId_OutroUsuarioSemSerOQueCadastrou() {
            // Arrange
            var reservaMesa = ReservaMesaHelper.getReservaMesa(true);
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(reservaMesa);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            when(reservaMesaRepository.findById(reservaMesaDTO.id())).thenReturn(Optional.of(reservaMesa));
            // Act
            assertThatThrownBy(() -> reservaMesaService.findById(reservaMesaDTO.id(), usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Somente o usuario que fez a reserva ou " +
                            "o dono do restaurante podem consultar uma reserva de uma mesa. ID: " + reservaMesaDTO.id());
            // Assert
            verify(reservaMesaRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarReservaMesaPorId_idNaoExiste() {
            // Arrange
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(true);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            when(reservaMesaRepository.findById(reservaMesaDTO.id())).thenReturn(Optional.empty());
            UUID uuid = reservaMesaDTO.id();
            // Act
            assertThatThrownBy(() -> reservaMesaService.findById(uuid, usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("ReservaMesa não encontrado com o ID: " + reservaMesaDTO.id());
            // Assert
            verify(reservaMesaRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void devePermitirBuscarTodosReservaMesa() {
            // Arrange
            ReservaMesaDTO criteriosDeBusca = ReservaMesaHelper.getReservaMesaDTO(false);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            Page<ReservaMesa> reservaMesas = new PageImpl<>(Arrays.asList(
                    ReservaMesaHelper.getReservaMesa(true),
                    ReservaMesaHelper.getReservaMesa(true),
                    ReservaMesaHelper.getReservaMesa(true)
            ));
            when(reservaMesaRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(reservaMesas);
            // Act
            var reservaMesaObtidos = reservaMesaService.findAll(Pageable.unpaged(), usuarioDTO, criteriosDeBusca);
            // Assert
            assertThat(reservaMesaObtidos).hasSize(3);
            assertThat(reservaMesaObtidos.getContent()).asList().allSatisfy(
                    reservaMesa -> {
                        assertThat(reservaMesa)
                                .isNotNull()
                                .isInstanceOf(ReservaMesaDTO.class);
                    }
            );
            verify(reservaMesaRepository, times(1)).findAll(any(Example.class), any(Pageable.class));
        }
    }

    @Nested
    class AlterarReservaMesa {
        @Test
        void devePermitirAlterarReservaMesa() {
            // Arrange
            var reservaMesa = ReservaMesaHelper.getReservaMesa(true);
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(reservaMesa);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(reservaMesa.getUsuario());
            var novoStatusReservaMesa = "FINALIZADA";
            when(reservaMesaRepository.findById(reservaMesa.getId())).thenReturn(Optional.of(reservaMesa));
            when(reservaMesaRepository.save(any(ReservaMesa.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var reservaMesaSalvo = reservaMesaService.finaliza(reservaMesaDTO.id(), usuarioDTO);
            // Assert
            assertThat(reservaMesaSalvo)
                    .isInstanceOf(ReservaMesaDTO.class)
                    .isNotNull();
            assertThat(reservaMesaSalvo.status()).isEqualTo(novoStatusReservaMesa);
            assertThat(reservaMesaSalvo.status()).isNotEqualTo(reservaMesaDTO.status());

            verify(reservaMesaRepository, times(1)).findById(any(UUID.class));
            verify(reservaMesaRepository, times(1)).save(any(ReservaMesa.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarReservaMesaPorId_OutroUsuarioSemSerOQueCadastrou() {
            // Arrange
            var reservaMesa = ReservaMesaHelper.getReservaMesa(true);
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(reservaMesa);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            var novoStatusReservaMesa = "FINALIZADA";
            when(reservaMesaRepository.findById(reservaMesa.getId())).thenReturn(Optional.of(reservaMesa));
            when(reservaMesaRepository.save(any(ReservaMesa.class))).thenAnswer(r -> r.getArgument(0));
            // Act && Assert
            assertThatThrownBy(() -> reservaMesaService.finaliza(reservaMesa.getId(), usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Somente o usuario que fez a reserva ou " +
                            "o dono do restaurante podem finalizar uma reserva de uma mesa. ID: " + reservaMesaDTO.id());
            verify(reservaMesaRepository, times(1)).findById(any(UUID.class));
            verify(reservaMesaRepository, never()).save(any(ReservaMesa.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarReservaMesaPorId_idNaoExiste() {
            // Arrange
            var reservaMesa = ReservaMesaHelper.getReservaMesa(true);
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(reservaMesa);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            when(reservaMesaRepository.findById(reservaMesa.getId())).thenReturn(Optional.empty());
            UUID uuid = reservaMesaDTO.id();
            // Act && Assert
            assertThatThrownBy(() -> reservaMesaService.finaliza(uuid, usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("ReservaMesa não encontrado com o ID: " + reservaMesaDTO.id());
            verify(reservaMesaRepository, times(1)).findById(any(UUID.class));
            verify(reservaMesaRepository, never()).save(any(ReservaMesa.class));
        }
    }

    @Nested
    class RemoverReservaMesa {
        @Test
        void devePermitirRemoverReservaMesa() {
            // Arrange
            var reservaMesa = ReservaMesaHelper.getReservaMesa(true);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(reservaMesa.getUsuario());
            when(reservaMesaRepository.findById(reservaMesa.getId())).thenReturn(Optional.of(reservaMesa));
            doNothing().when(reservaMesaRepository).deleteById(reservaMesa.getId());
            // Act
            reservaMesaService.delete(reservaMesa.getId(), usuarioDTO);
            // Assert
            verify(reservaMesaRepository, times(2)).findById(any(UUID.class));
            verify(reservaMesaRepository, times(1)).deleteById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoRemoverReservaMesaPorId_OutroUsuarioSemSerOQueCadastrou() {
            // Arrange
            var reservaMesa = ReservaMesaHelper.getReservaMesa(true);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            when(reservaMesaRepository.findById(reservaMesa.getId())).thenReturn(Optional.of(reservaMesa));
            doNothing().when(reservaMesaRepository).deleteById(reservaMesa.getId());
            // Act
            assertThatThrownBy(() -> reservaMesaService.delete(reservaMesa.getId(), usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Somente o usuario que fez a reserva ou " +
                            "o dono do restaurante podem deletar uma reserva de uma mesa. ID: " + reservaMesa.getId());
            verify(reservaMesaRepository, times(2)).findById(any(UUID.class));
            verify(reservaMesaRepository, never()).deleteById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandRemoverReservaMesaPorId_idNaoExiste() {
            // Arrange
            var reservaMesa = ReservaMesaHelper.getReservaMesa(true);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            doNothing().when(reservaMesaRepository).deleteById(reservaMesa.getId());
            UUID uuid = reservaMesa.getId();
            // Act && Assert
            assertThatThrownBy(() -> reservaMesaService.delete(uuid, usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("ReservaMesa não encontrado com o ID: " + reservaMesa.getId());
            verify(reservaMesaRepository, times(1)).findById(any(UUID.class));
            verify(reservaMesaRepository, never()).deleteById(any(UUID.class));
        }
    }
}