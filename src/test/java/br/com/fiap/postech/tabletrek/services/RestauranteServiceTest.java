package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.entities.Restaurante;
import br.com.fiap.postech.tabletrek.repository.RestauranteRepository;
import br.com.fiap.postech.tabletrek.helper.RestauranteHelper;
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

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Nested
    class CadastrarRestaurante {
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
    }

    @Nested
    class BuscarRestaurante {
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
            UUID uuid = restauranteDTO.id();
            // Act
            assertThatThrownBy(() -> restauranteService.findById(uuid))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Restaurante não encontrado com o ID: " + restauranteDTO.id());
            // Assert
            verify(restauranteRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void devePermitirBuscarTodosRestaurante() {
            // Arrange
            RestauranteDTO criteriosDeBusca = RestauranteHelper.getRestauranteDTO(false);
            Page<Restaurante> restaurantes = new PageImpl<>(Arrays.asList(
                    RestauranteHelper.getRestaurante(true),
                    RestauranteHelper.getRestaurante(true),
                    RestauranteHelper.getRestaurante(true)
            ));
            when(restauranteRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(restaurantes);
            // Act
            var restauranteObtidos = restauranteService.findAll(Pageable.unpaged(), criteriosDeBusca);
            // Assert
            assertThat(restauranteObtidos).hasSize(3);
            assertThat(restauranteObtidos.getContent()).asList().allSatisfy(
                    restaurante -> {
                        assertThat(restaurante)
                                .isNotNull()
                                .isInstanceOf(RestauranteDTO.class);
                    }
            );
            verify(restauranteRepository, times(1)).findAll(any(Example.class), any(Pageable.class));
        }
    }

    @Nested
    class AlterarRestaurante {
        @Test
        void devePermitirAlterarRestaurante() {
            // Arrange
            var restaurante = RestauranteHelper.getRestaurante(true);
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(restaurante);
            var novoRestauranteDTO = new RestauranteDTO(restauranteDTO.id(),
                    restauranteDTO.idUsuario(),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    RandomStringUtils.random(20, true, true),
                    10 + (int) (Math.random() * 100),
                    RandomStringUtils.random(20, true, true)
            );
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

            assertThat(restauranteSalvo.localizacao()).isEqualTo(novoRestauranteDTO.localizacao());
            assertThat(restauranteSalvo.localizacao()).isNotEqualTo(restauranteDTO.localizacao());

            assertThat(restauranteSalvo.horarioFuncionamento()).isEqualTo(novoRestauranteDTO.horarioFuncionamento());
            assertThat(restauranteSalvo.horarioFuncionamento()).isNotEqualTo(restauranteDTO.horarioFuncionamento());

            assertThat(restauranteSalvo.capacidade()).isEqualTo(novoRestauranteDTO.capacidade());
            assertThat(restauranteSalvo.capacidade()).isNotEqualTo(restauranteDTO.capacidade());

            assertThat(restauranteSalvo.tipoCozinha()).isEqualTo(novoRestauranteDTO.tipoCozinha());
            assertThat(restauranteSalvo.tipoCozinha()).isNotEqualTo(restauranteDTO.tipoCozinha());

            verify(restauranteRepository, times(1)).findById(any(UUID.class));
            verify(restauranteRepository, times(1)).save(any(Restaurante.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarRestaurantePorId_idNaoExiste() {
            // Arrange
            var restaurante = RestauranteHelper.getRestaurante(true);
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(restaurante);
            when(restauranteRepository.findById(restaurante.getId())).thenReturn(Optional.empty());
            UUID uuid = restauranteDTO.id();
            // Act && Assert
            assertThatThrownBy(() -> restauranteService.update(uuid, restauranteDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Restaurante não encontrado com o ID: " + restauranteDTO.id());
            verify(restauranteRepository, times(1)).findById(any(UUID.class));
            verify(restauranteRepository, never()).save(any(Restaurante.class));
        }
    }

    @Nested
    class RemoverRestaurante {
        @Test
        void devePermitirRemoverRestaurante() {
            // Arrange
            var restaurante = RestauranteHelper.getRestaurante(true);
            when(restauranteRepository.findById(restaurante.getId())).thenReturn(Optional.of(restaurante));
            doNothing().when(restauranteRepository).deleteById(restaurante.getId());
            // Act
            restauranteService.delete(restaurante.getId());
            // Assert
            verify(restauranteRepository, times(1)).findById(any(UUID.class));
            verify(restauranteRepository, times(1)).deleteById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandRemoverRestaurantePorId_idNaoExiste() {
            // Arrange
            var restaurante = RestauranteHelper.getRestaurante(true);
            doNothing().when(restauranteRepository).deleteById(restaurante.getId());
            UUID uuid = restaurante.getId();
            // Act && Assert
            assertThatThrownBy(() -> restauranteService.delete(uuid))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Restaurante não encontrado com o ID: " + restaurante.getId());
            verify(restauranteRepository, times(1)).findById(any(UUID.class));
            verify(restauranteRepository, never()).deleteById(any(UUID.class));
        }
    }
}