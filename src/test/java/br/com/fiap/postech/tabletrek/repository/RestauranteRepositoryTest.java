package br.com.fiap.postech.tabletrek.repository;

import br.com.fiap.postech.tabletrek.entities.Restaurante;
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

class RestauranteRepositoryTest {

    /*private Pattern UUID_REGEX =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");*/

    @Mock
    private RestauranteRepository restauranteRepository;

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
    void devePermitirCadastrarRestaurante() {
        // Arrange
        var restaurante = RestauranteRepositoryHelper.getRestaurante(false);
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(restaurante);
        // Act
        var savedRestaurante = restauranteRepository.save(restaurante);
        // Assert
        assertThat(savedRestaurante).isNotNull().isEqualTo(restaurante);
        //assertTrue(UUID_REGEX.matcher(savedRestaurante.getId()).matches());
        verify(restauranteRepository, times(1)).save(any(Restaurante.class));
    }

    @Test
    void devePermitirBuscarRestaurante() {
        // Arrange
        //var id = UUID.randomUUID();
        var restaurante = RestauranteRepositoryHelper.getRestaurante(true);
        //restaurante.setId(id);
        when(restauranteRepository.findById(restaurante.getId())).thenReturn(Optional.of(restaurante));
        // Act
        var restauranteOpcional = restauranteRepository.findById(restaurante.getId());
        // Assert
        assertThat(restauranteOpcional).isNotNull().containsSame(restaurante);
        restauranteOpcional.ifPresent(
                restauranteRecebido -> {
                    assertThat(restauranteRecebido).isInstanceOf(Restaurante.class).isNotNull();
                    assertThat(restauranteRecebido.getId()).isEqualTo(restaurante.getId());
                    assertThat(restauranteRecebido.getNome()).isEqualTo(restaurante.getNome());
                }
        );
        verify(restauranteRepository, times(1)).findById(restaurante.getId());
    }
    @Test
    void devePermitirRemoverRestaurante() {
        //Arrange
        var id = UUID.randomUUID();
        doNothing().when(restauranteRepository).deleteById(id);
        //Act
        restauranteRepository.deleteById(id);
        //Assert
        verify(restauranteRepository, times(1)).deleteById(id);
    }
    @Test
    void devePermitirListarRestaurantes() {
        // Arrange
        var restaurante1 = RestauranteRepositoryHelper.getRestaurante(true);
        var restaurante2 = RestauranteRepositoryHelper.getRestaurante(true);
        var listaRestaurantes = Arrays.asList(
                restaurante1,
                restaurante2
        );
        when(restauranteRepository.findAll()).thenReturn(listaRestaurantes);
        // Act
        var restaurantesListados = restauranteRepository.findAll();
        assertThat(restaurantesListados)
                .hasSize(2)
                .containsExactlyInAnyOrder(restaurante1, restaurante2);
        verify(restauranteRepository, times(1)).findAll();
    }
}