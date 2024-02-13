package br.com.fiap.postech.tabletrek.services;


import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.helper.RestauranteHelper;
import br.com.fiap.postech.tabletrek.repository.RestauranteRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class RestauranteServiceIT {
    @Autowired
    private RestauranteRepository restauranteRepository;
    @Autowired
    private RestauranteService restauranteService;

    @Test
    void devePermitirCadastrarRestaurante() {
        // Arrange
        var restauranteDTO = RestauranteHelper.getRestauranteDTO(false);
        // Act
        var restauranteSalvo = restauranteService.save(restauranteDTO);
        // Assert
        assertThat(restauranteSalvo)
                .isInstanceOf(RestauranteDTO.class)
                .isNotNull();
        assertThat(restauranteSalvo.nome()).isEqualTo(restauranteDTO.nome());
        assertThat(restauranteSalvo.id()).isNotNull();
    }

    @Test
    void devePermitirBuscarRestaurantePorId() {
        // Arrange
        var id = UUID.fromString("b35d3a29-408a-4d1a-964c-2261cb0e252f");
        var nome = "Tojiro Sushi";
        // Act
        var restautranteObtido = restauranteService.findById(id);
        // Assert
        assertThat(restautranteObtido).isNotNull().isInstanceOf(RestauranteDTO.class);
        assertThat(restautranteObtido.nome()).isEqualTo(nome);
        assertThat(restautranteObtido.id()).isNotNull();
        assertThat(restautranteObtido.id()).isEqualTo(id);
    }

    @Test
    void deveGerarExcecao_QuandoBuscarRestaurantePorId_idNaoExiste() {
        fail("não fiz ainda");
    }

    @Test
    void devePermitirBuscarTodosRestaurante() {
        fail("não fiz ainda");
    }

    @Test
    void devePermitirAlterarRestaurante() {
        fail("não fiz ainda");
    }

    @Test
    void deveGerarExcecao_QuandoAlterarRestaurantePorId_idNaoExiste() {
        fail("não fiz ainda");
    }

    @Test
    void devePermitirRemoverRestaurante() {
        fail("não fiz ainda");
    }

    @Test
    void deveGerarExcecao_QuandRemoverRestaurantePorId_idNaoExiste() {
        fail("não fiz ainda");
    }
}