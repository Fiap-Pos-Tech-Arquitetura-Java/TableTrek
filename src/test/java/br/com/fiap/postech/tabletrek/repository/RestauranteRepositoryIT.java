package br.com.fiap.postech.tabletrek.repository;

import br.com.fiap.postech.tabletrek.entities.Restaurante;
import br.com.fiap.postech.tabletrek.helper.RestauranteHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class RestauranteRepositoryIT {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Test
    void devePermitirCriarEstrutura() {
        var totalRegistros = restauranteRepository.count();
        assertThat(totalRegistros).isEqualTo(3);
    }
    
    @Test
    void devePermitirCadastrarRestaurante() {
        // Arrange
        var restaurante = RestauranteHelper.getRestaurante(true);
        // Act
        var restauranteCadastrado = restauranteRepository.save(restaurante);
        // Assert
        assertThat(restauranteCadastrado).isInstanceOf(Restaurante.class).isNotNull();
        assertThat(restauranteCadastrado.getId()).isEqualTo(restaurante.getId());
        assertThat(restauranteCadastrado.getNome()).isEqualTo(restaurante.getNome());
    }
    @Test
    void devePermitirBuscarRestaurante() {
        // Arrange
        var id = UUID.fromString("b35d3a29-408a-4d1a-964c-2261cb0e252f");
        var nome = "Tojiro Sushi";
        // Act
        var restauranteOpcional = restauranteRepository.findById(id);
        // Assert
        assertThat(restauranteOpcional).isPresent();
        restauranteOpcional.ifPresent(
                restauranteRecebido -> {
                    assertThat(restauranteRecebido).isInstanceOf(Restaurante.class).isNotNull();
                    assertThat(restauranteRecebido.getId()).isEqualTo(id);
                    assertThat(restauranteRecebido.getNome()).isEqualTo(nome);
                }
        );
        //verify(restauranteRepository, times(1)).findById(restaurante.getId());
    }
    @Test
    void devePermitirRemoverRestaurante() {
        // Arrange
        var id = UUID.fromString("ada8399b-44f0-499c-82d9-5ca9ed1670da");
        // Act
        restauranteRepository.deleteById(id);
        // Assert
        var restauranteOpcional = restauranteRepository.findById(id);
        assertThat(restauranteOpcional).isEmpty();
    }
    @Test
    void devePermitirListarRestaurantes() {
        // Arrange
        // Act
        var restaurantesListados = restauranteRepository.findAll();
        // Assert
        assertThat(restaurantesListados).hasSize(3);
    }
}
