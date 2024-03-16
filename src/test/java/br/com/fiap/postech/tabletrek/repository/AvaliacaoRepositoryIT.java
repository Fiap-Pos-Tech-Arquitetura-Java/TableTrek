package br.com.fiap.postech.tabletrek.repository;

import br.com.fiap.postech.tabletrek.entities.Avaliacao;
import br.com.fiap.postech.tabletrek.helper.AvaliacaoHelper;
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
class AvaliacaoRepositoryIT {
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Test
    void devePermitirCriarEstrutura() {
        var totalRegistros = avaliacaoRepository.count();
        assertThat(totalRegistros).isEqualTo(3);
    }
    
    @Test
    void devePermitirCadastrarAvaliacao() {
        // Arrange
        var avaliacao = AvaliacaoHelper.getAvaliacao(true, "15dc1918-9e48-4beb-9b63-4aad3914c8a7");
        // Act
        var avaliacaoCadastrado = avaliacaoRepository.save(avaliacao);
        // Assert
        assertThat(avaliacaoCadastrado).isInstanceOf(Avaliacao.class).isNotNull();
        assertThat(avaliacaoCadastrado.getId()).isEqualTo(avaliacao.getId());
        assertThat(avaliacaoCadastrado.getReservaMesa()).isEqualTo(avaliacao.getReservaMesa());
        assertThat(avaliacaoCadastrado.getNota()).isEqualTo(avaliacao.getNota());
        assertThat(avaliacaoCadastrado.getComentario()).isEqualTo(avaliacao.getComentario());
    }
    @Test
    void devePermitirBuscarAvaliacao() {
        // Arrange
        var id = UUID.fromString("d9cb2906-b308-4a54-a0f3-c0b89e67c7c0");
        var idReservaMesa = UUID.fromString("15dc1918-9e48-4beb-9b63-4aad3914c8a7");
        var nota = 5;
        var comentario = "boa comida e otimo atendimento";
        // Act
        var avaliacaoOpcional = avaliacaoRepository.findById(id);
        // Assert
        assertThat(avaliacaoOpcional).isPresent();
        avaliacaoOpcional.ifPresent(
                avaliacaoRecebido -> {
                    assertThat(avaliacaoRecebido).isInstanceOf(Avaliacao.class).isNotNull();
                    assertThat(avaliacaoRecebido.getReservaMesa().getId()).isEqualTo(idReservaMesa);
                    assertThat(avaliacaoRecebido.getNota()).isEqualTo(nota);
                    assertThat(avaliacaoRecebido.getComentario()).isEqualTo(comentario);
                }
        );
        //verify(avaliacaoRepository, times(1)).findById(avaliacao.getId());
    }
    @Test
    void devePermitirRemoverAvaliacao() {
        // Arrange
        var id = UUID.fromString("86d6f0bb-3dd8-48f3-9078-4fb8c8e2c7c1");
        // Act
        avaliacaoRepository.deleteById(id);
        // Assert
        var avaliacaoOpcional = avaliacaoRepository.findById(id);
        assertThat(avaliacaoOpcional).isEmpty();
    }
    @Test
    void devePermitirListarAvaliacaos() {
        // Arrange
        // Act
        var avaliacaosListados = avaliacaoRepository.findAll();
        // Assert
        assertThat(avaliacaosListados).hasSize(3);
    }
}
