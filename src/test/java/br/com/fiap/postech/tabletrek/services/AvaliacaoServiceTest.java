package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.AvaliacaoDTO;
import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import br.com.fiap.postech.tabletrek.entities.Avaliacao;
import br.com.fiap.postech.tabletrek.helper.AvaliacaoHelper;
import br.com.fiap.postech.tabletrek.helper.ReservaMesaHelper;
import br.com.fiap.postech.tabletrek.helper.UsuarioHelper;
import br.com.fiap.postech.tabletrek.repository.AvaliacaoRepository;
import org.apache.commons.lang3.RandomStringUtils;
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

class AvaliacaoServiceTest {

    private AvaliacaoService avaliacaoService;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private ReservaMesaService reservaMesaService;

    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        avaliacaoService = new AvaliacaoServiceImpl(avaliacaoRepository, reservaMesaService);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class CadastrarAvaliacao {
        @Test
        void devePermitirCadastrarAvaliacao() {
            // Arrange
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(true);
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(false, reservaMesaDTO.id().toString());
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            when(reservaMesaService.findById(reservaMesaDTO.id(), usuarioDTO)).thenReturn(reservaMesaDTO);
            when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var avaliacaoSalvo = avaliacaoService.save(avaliacaoDTO, usuarioDTO);
            // Assert
            assertThat(avaliacaoSalvo)
                    .isInstanceOf(AvaliacaoDTO.class)
                    .isNotNull();
            assertThat(avaliacaoSalvo.idReservaMesa()).isEqualTo(avaliacaoDTO.idReservaMesa());
            assertThat(avaliacaoSalvo.id()).isNotNull();
            verify(reservaMesaService, times(1)).findById(any(UUID.class), any(UsuarioDTO.class));
            verify(avaliacaoRepository, times(1)).save(any(Avaliacao.class));
        }
    }

    @Nested
    class BuscarAvaliacao {
        @Test
        void devePermitirBuscarAvaliacaoPorId() {
            // Arrange
            var avaliacao = AvaliacaoHelper.getAvaliacao(true);
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(avaliacao);
            when(avaliacaoRepository.findById(avaliacaoDTO.id())).thenReturn(Optional.of(avaliacao));
            // Act
            var restautranteObtido = avaliacaoService.findById(avaliacaoDTO.id());
            // Assert
            assertThat(restautranteObtido).isEqualTo(avaliacaoDTO);
            verify(avaliacaoRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarAvaliacaoPorId_idNaoExiste() {
            // Arrange
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(true);
            when(avaliacaoRepository.findById(avaliacaoDTO.id())).thenReturn(Optional.empty());
            UUID uuid = avaliacaoDTO.id();
            // Act
            assertThatThrownBy(() -> avaliacaoService.findById(uuid))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Avaliacao não encontrado com o ID: " + avaliacaoDTO.id());
            // Assert
            verify(avaliacaoRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void devePermitirBuscarTodosAvaliacao() {
            // Arrange
            AvaliacaoDTO criteriosDeBusca = AvaliacaoHelper.getAvaliacaoDTO(false);
            Page<Avaliacao> avaliacaos = new PageImpl<>(Arrays.asList(
                    AvaliacaoHelper.getAvaliacao(true),
                    AvaliacaoHelper.getAvaliacao(true),
                    AvaliacaoHelper.getAvaliacao(true)
            ));
            when(avaliacaoRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(avaliacaos);
            // Act
            var avaliacaoObtidos = avaliacaoService.findAll(Pageable.unpaged(), criteriosDeBusca);
            // Assert
            assertThat(avaliacaoObtidos).hasSize(3);
            assertThat(avaliacaoObtidos.getContent()).asList().allSatisfy(
                    avaliacao -> {
                        assertThat(avaliacao)
                                .isNotNull()
                                .isInstanceOf(AvaliacaoDTO.class);
                    }
            );
            verify(avaliacaoRepository, times(1)).findAll(any(Example.class), any(Pageable.class));
        }
    }

    @Nested
    class AlterarAvaliacao {
        @Test
        void devePermitirAlterarAvaliacao() {
            // Arrange
            var avaliacao = AvaliacaoHelper.getAvaliacao(true);
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(avaliacao);
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(true);
            var novaAvaliacaoDTO = new AvaliacaoDTO(
                    avaliacaoDTO.id(),
                    reservaMesaDTO.id(),
                    10 + (int) (Math.random() * 100),
                    RandomStringUtils.random(20, true, true)
            );

            when(avaliacaoRepository.findById(avaliacao.getId())).thenReturn(Optional.of(avaliacao));
            when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var avaliacaoSalvo = avaliacaoService.update(avaliacaoDTO.id(), novaAvaliacaoDTO);
            // Assert
            assertThat(avaliacaoSalvo)
                    .isInstanceOf(AvaliacaoDTO.class)
                    .isNotNull();
            assertThat(avaliacaoSalvo.nota()).isEqualTo(novaAvaliacaoDTO.nota());
            assertThat(avaliacaoSalvo.nota()).isNotEqualTo(avaliacaoDTO.nota());
            assertThat(avaliacaoSalvo.comentario()).isEqualTo(novaAvaliacaoDTO.comentario());
            assertThat(avaliacaoSalvo.comentario()).isNotEqualTo(avaliacaoDTO.comentario());

            verify(avaliacaoRepository, times(1)).findById(any(UUID.class));
            verify(avaliacaoRepository, times(1)).save(any(Avaliacao.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarAvaliacaoPorId_idNaoExiste() {
            // Arrange
            var avaliacao = AvaliacaoHelper.getAvaliacao(true);
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(avaliacao);
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(true);
            var novaAvaliacaoDTO = new AvaliacaoDTO(avaliacaoDTO.id(),
                    reservaMesaDTO.id(),
                    10 + (int) (Math.random() * 100),
                    RandomStringUtils.random(20, true, true)
            );
            when(avaliacaoRepository.findById(avaliacao.getId())).thenReturn(Optional.empty());
            UUID uuid = avaliacaoDTO.id();
            // Act && Assert
            assertThatThrownBy(() -> avaliacaoService.update(uuid, novaAvaliacaoDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Avaliacao não encontrado com o ID: " + avaliacaoDTO.id());
            verify(avaliacaoRepository, times(1)).findById(any(UUID.class));
            verify(avaliacaoRepository, never()).save(any(Avaliacao.class));
        }
    }

    @Nested
    class RemoverAvaliacao {
        @Test
        void devePermitirRemoverAvaliacao() {
            // Arrange
            var avaliacao = AvaliacaoHelper.getAvaliacao(true);
            when(avaliacaoRepository.findById(avaliacao.getId())).thenReturn(Optional.of(avaliacao));
            doNothing().when(avaliacaoRepository).deleteById(avaliacao.getId());
            // Act
            avaliacaoService.delete(avaliacao.getId());
            // Assert
            verify(avaliacaoRepository, times(1)).findById(any(UUID.class));
            verify(avaliacaoRepository, times(1)).deleteById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandRemoverAvaliacaoPorId_idNaoExiste() {
            // Arrange
            var avaliacao = AvaliacaoHelper.getAvaliacao(true);
            doNothing().when(avaliacaoRepository).deleteById(avaliacao.getId());
            UUID uuid = avaliacao.getId();
            // Act && Assert
            assertThatThrownBy(() -> avaliacaoService.delete(uuid))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Avaliacao não encontrado com o ID: " + avaliacao.getId());
            verify(avaliacaoRepository, times(1)).findById(any(UUID.class));
            verify(avaliacaoRepository, never()).deleteById(any(UUID.class));
        }
    }
}