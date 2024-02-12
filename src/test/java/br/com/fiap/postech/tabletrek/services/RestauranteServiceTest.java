package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.entities.Restaurante;
import br.com.fiap.postech.tabletrek.repository.RestauranteRepository;
import br.com.fiap.postech.tabletrek.helper.RestauranteHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RestauranteServiceTest {

    private RestauranteService restauranteService;

    @Mock
    private RestauranteRepository restauranteRepository;

    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        restauranteService = new RestauranteServiceImpl(restauranteRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Test
    void devePermitirCadastrarRestaurante() {
        // Arrange
        //var restaurante = RestauranteRepositoryHelper.getRestaurante(false);
        var restauranteDTO = RestauranteHelper.getRestauranteDTO(false);
        when(restauranteRepository.save(any(Restaurante.class))).thenAnswer(r -> r.getArgument(0));
        // Act
        var restauranteSalvo = restauranteService.save(restauranteDTO);
        // Assert
        assertThat(restauranteSalvo)
                .isInstanceOf(RestauranteDTO.class)
                .isNotNull();
        assertThat(restauranteSalvo.nome()).isEqualTo(restauranteDTO.nome());
        //
        assertThat(restauranteSalvo.id()).isNotNull();
        verify(restauranteRepository, times(1)).save(any(Restaurante.class));
    }

    @Test
    void devePermitirBuscarRestaurante() {
        // Arrange
        // Act
        // Assert
        fail("não fiz ainda");
    }

    @Test
    void devePermitirRemoverRestaurante() {
        // Arrange
        // Act
        // Assert
        fail("não fiz ainda");
    }

    @Test
    void devePermitirListarRestaurantes() {
        // Arrange
        // Act
        // Assert
        fail("não fiz ainda");
    }
}