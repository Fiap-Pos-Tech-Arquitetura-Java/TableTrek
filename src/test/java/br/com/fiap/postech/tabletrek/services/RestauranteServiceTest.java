package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.entities.Restaurante;
import br.com.fiap.postech.tabletrek.repository.RestauranteRepository;
import br.com.fiap.postech.tabletrek.helper.RestauranteHelper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        var restauranteDTO = RestauranteHelper.getRestauranteDTO(false);
        when(restauranteRepository.save(any(Restaurante.class))).thenAnswer(r -> r.getArgument(0));
        // Act
        var restauranteSalvo = restauranteService.save(restauranteDTO);
        // Assert
        assertThat(restauranteSalvo)
                .isInstanceOf(RestauranteDTO.class)
                .isNotNull();
        assertThat(restauranteSalvo.nome()).isEqualTo(restauranteDTO.nome());
        assertThat(restauranteSalvo.id()).isNotNull();
        verify(restauranteRepository, times(1)).save(any(Restaurante.class));
    }

    @Test
    void devePermitirBuscarRestaurantePorId() {
        // Arrange
        var restaurante = RestauranteHelper.getRestaurante(true);
        var restauranteDTO = RestauranteHelper.getRestauranteDTO(restaurante);
        when(restauranteRepository.findById(restauranteDTO.id())).thenReturn(Optional.of(restaurante));
        // Act
        var restautranteObtido = restauranteService.findById(restauranteDTO.id());
        // Assert
        assertThat(restautranteObtido).isEqualTo(restauranteDTO);
        verify(restauranteRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void deveGerarExcecao_QuandoBuscarRestaurantePorId_idNaoExiste() {
        // Arrange
        var restauranteDTO = RestauranteHelper.getRestauranteDTO(true);
        when(restauranteRepository.findById(restauranteDTO.id())).thenReturn(Optional.empty());
        // Act
        assertThatThrownBy(() -> restauranteService.findById(restauranteDTO.id()))
                .isInstanceOf(ControllerNotFoundException.class)
                .hasMessage("Restaurante não encontrado com o ID: " + restauranteDTO.id());
        // Assert
        verify(restauranteRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void devePermitirBuscarTodosRestaurante() {
        // Arrange
        var restaurantes = Arrays.asList(
                RestauranteHelper.getRestaurante(true),
                RestauranteHelper.getRestaurante(true),
                RestauranteHelper.getRestaurante(true)
        );
        when(restauranteRepository.findAll()).thenReturn(restaurantes);
        // Act
        var restauranteObtidos = restauranteService.findAll();
        // Assert
        assertThat(restauranteObtidos).hasSize(3);
        verify(restauranteRepository, times(1)).findAll();
    }

    @Test
    void devePermitirAlterarRestaurante() {
        // Arrange
        var restaurante = RestauranteHelper.getRestaurante(true);
        var restauranteDTO = RestauranteHelper.getRestauranteDTO(restaurante);
        var novoRestauranteDTO = new RestauranteDTO(restauranteDTO.id(),
                RandomStringUtils.random(20, true, true));
        when(restauranteRepository.findById(restaurante.getId())).thenReturn(Optional.of(restaurante));
        when(restauranteRepository.save(any(Restaurante.class))).thenAnswer(r -> r.getArgument(0));
        // Act
        var restauranteSalvo = restauranteService.update(restauranteDTO.id(), novoRestauranteDTO);
        // Assert
        assertThat(restauranteSalvo)
                .isInstanceOf(RestauranteDTO.class)
                .isNotNull();
        assertThat(restauranteSalvo.nome()).isEqualTo(novoRestauranteDTO.nome());
        assertThat(restauranteSalvo.nome()).isNotEqualTo(restauranteDTO.nome());
        verify(restauranteRepository, times(1)).findById(any(UUID.class));
        verify(restauranteRepository, times(1)).save(any(Restaurante.class));
    }

    @Test
    void deveGerarExcecao_QuandoAlterarRestaurantePorId_idNaoExiste() {
        // Arrange
        var restaurante = RestauranteHelper.getRestaurante(true);
        var restauranteDTO = RestauranteHelper.getRestauranteDTO(restaurante);
        when(restauranteRepository.findById(restaurante.getId())).thenReturn(Optional.empty());
        // Act && Assert
        assertThatThrownBy(() -> restauranteService.update(restauranteDTO.id(), restauranteDTO))
                .isInstanceOf(ControllerNotFoundException.class)
                .hasMessage("Restaurante não encontrado com o ID: " + restauranteDTO.id());
        verify(restauranteRepository, times(1)).findById(any(UUID.class));
        verify(restauranteRepository, never()).save(any(Restaurante.class));
    }

    @Test
    void devePermitirRemoverRestaurante() {
        // Arrange
        var restaurante = RestauranteHelper.getRestaurante(true);
        when(restauranteRepository.findById(restaurante.getId())).thenReturn(Optional.of(restaurante));
        doNothing().when(restauranteRepository).deleteById(restaurante.getId());
        // Act
        restauranteService.delete(restaurante.getId());
        // Assert
        verify(restauranteRepository, times(1)).findById(restaurante.getId());
        verify(restauranteRepository, times(1)).deleteById(restaurante.getId());
    }

    @Test
    void deveGerarExcecao_QuandRemoverRestaurantePorId_idNaoExiste() {
        // Arrange
        var restaurante = RestauranteHelper.getRestaurante(true);
        doNothing().when(restauranteRepository).deleteById(restaurante.getId());
        // Act && Assert
        assertThatThrownBy(() -> restauranteService.delete(restaurante.getId()))
                .isInstanceOf(ControllerNotFoundException.class)
                .hasMessage("Restaurante não encontrado com o ID: " + restaurante.getId());
        verify(restauranteRepository, times(1)).findById(restaurante.getId());
        verify(restauranteRepository, never()).deleteById(restaurante.getId());
    }
}