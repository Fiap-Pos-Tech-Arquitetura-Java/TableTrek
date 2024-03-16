package br.com.fiap.postech.tabletrek.repository;

import br.com.fiap.postech.tabletrek.entities.Avaliacao;
import br.com.fiap.postech.tabletrek.helper.AvaliacaoHelper;
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

class AvaliacaoRepositoryTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

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
    void devePermitirCadastrarAvaliacao() {
        // Arrange
        var avaliacao = AvaliacaoHelper.getAvaliacao(false, "52a85f11-9f0f-4dc6-b92f-abc3881328a8");
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);
        // Act
        var savedAvaliacao = avaliacaoRepository.save(avaliacao);
        // Assert
        assertThat(savedAvaliacao).isNotNull().isEqualTo(avaliacao);
        verify(avaliacaoRepository, times(1)).save(any(Avaliacao.class));
    }

    @Test
    void devePermitirBuscarAvaliacao() {
        // Arrange
        var avaliacao = AvaliacaoHelper.getAvaliacao(true, "52a85f11-9f0f-4dc6-b92f-abc3881328a8");
        when(avaliacaoRepository.findById(avaliacao.getId())).thenReturn(Optional.of(avaliacao));
        // Act
        var avaliacaoOpcional = avaliacaoRepository.findById(avaliacao.getId());
        // Assert
        assertThat(avaliacaoOpcional).isNotNull().containsSame(avaliacao);
        avaliacaoOpcional.ifPresent(
                avaliacaoRecebido -> {
                    assertThat(avaliacaoRecebido).isInstanceOf(Avaliacao.class).isNotNull();
                    assertThat(avaliacaoRecebido.getId()).isEqualTo(avaliacao.getId());
                    assertThat(avaliacaoRecebido.getReservaMesa()).isEqualTo(avaliacao.getReservaMesa());
                    assertThat(avaliacaoRecebido.getNota()).isEqualTo(avaliacao.getNota());
                    assertThat(avaliacaoRecebido.getComentario()).isEqualTo(avaliacao.getComentario());
                }
        );
        verify(avaliacaoRepository, times(1)).findById(avaliacao.getId());
    }
    @Test
    void devePermitirRemoverAvaliacao() {
        //Arrange
        var id = UUID.randomUUID();
        doNothing().when(avaliacaoRepository).deleteById(id);
        //Act
        avaliacaoRepository.deleteById(id);
        //Assert
        verify(avaliacaoRepository, times(1)).deleteById(id);
    }
    @Test
    void devePermitirListarAvaliacaos() {
        // Arrange
        var avaliacao1 = AvaliacaoHelper.getAvaliacao(true, "52a85f11-9f0f-4dc6-b92f-abc3881328a8");
        var avaliacao2 = AvaliacaoHelper.getAvaliacao(true, "b35d3a29-408a-4d1a-964c-2261cb0e252f");
        var listaAvaliacaos = Arrays.asList(
                avaliacao1,
                avaliacao2
        );
        when(avaliacaoRepository.findAll()).thenReturn(listaAvaliacaos);
        // Act
        var avaliacaosListados = avaliacaoRepository.findAll();
        assertThat(avaliacaosListados)
                .hasSize(2)
                .containsExactlyInAnyOrder(avaliacao1, avaliacao2);
        verify(avaliacaoRepository, times(1)).findAll();
    }
}