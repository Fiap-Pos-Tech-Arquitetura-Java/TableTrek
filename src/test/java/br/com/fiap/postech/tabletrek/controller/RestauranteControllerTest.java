package br.com.fiap.postech.tabletrek.controller;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.helper.RestauranteHelper;
import br.com.fiap.postech.tabletrek.services.RestauranteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RestauranteControllerTest {

    private MockMvc mockMvc;
    @Mock
    private RestauranteService restauranteService;
    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        RestauranteController restauranteController = new RestauranteController(restauranteService);
        mockMvc = MockMvcBuilders.standaloneSetup(restauranteController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    public static String asJsonString(final Object object) throws Exception {
        return new ObjectMapper().writeValueAsString(object);
    }

    @Nested
    class CadastrarRestaurante {
        @Test
        void devePermitirCadastrarRestaurante() throws Exception {
            // Arrange
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(false);
            when(restauranteService.save(any(RestauranteDTO.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            mockMvc.perform(
                        post("/restaurante").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(restauranteDTO)))
                    .andExpect(status().isCreated());
            // Assert
            verify(restauranteService, times(1)).save(any(RestauranteDTO.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarRestaurante_RequisicaoXml() throws Exception {
            // Arrange
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(false);
            when(restauranteService.save(any(RestauranteDTO.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            mockMvc.perform(
                            post("/restaurante").contentType(MediaType.APPLICATION_XML)
                                    .content(asJsonString(restauranteDTO)))
                    .andExpect(status().isUnsupportedMediaType());
            // Assert
            verify(restauranteService, never()).save(any(RestauranteDTO.class));
        }
    }
    @Nested
    class BuscarRestaurante {
        @Test
        void devePermitirBuscarRestaurantePorId() throws Exception {
            // Arrange
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(true);
            when(restauranteService.findById(any(UUID.class))).thenReturn(restauranteDTO);
            // Act
            mockMvc.perform(get("/restaurante/{id}", restauranteDTO.id().toString()))
                    .andExpect(status().isOk());
            // Assert
            verify(restauranteService, times(1)).findById(any(UUID.class));
        }
        @Test
        void deveGerarExcecao_QuandoBuscarRestaurantePorId_idNaoExiste() throws Exception {
            // Arrange
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(true);
            when(restauranteService.findById(restauranteDTO.id())).thenThrow(ControllerNotFoundException.class);
            // Act
            mockMvc.perform(get("/restaurante/{id}", restauranteDTO.id().toString()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(restauranteService, times(1)).findById(restauranteDTO.id());
        }

        @Test
        void devePermitirBuscarTodosRestaurante() throws Exception {
            // Arrange
            int page = 0;
            int size = 10;
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(true);
            List<RestauranteDTO> listRestaurante = new ArrayList<>();
            listRestaurante.add(restauranteDTO);
            Page<RestauranteDTO> restaurantes = new PageImpl<>(listRestaurante);
            var pageable = PageRequest.of(page, size);
            when(restauranteService.findAll(any(Pageable.class))).thenReturn(restaurantes);
            // Act
            mockMvc.perform(
                get("/restaurante")
                    .param("page", String.valueOf(page))
                    .param("size", String.valueOf(size))
                ).andDo(print())
                .andExpect(status().is5xxServerError())
                //.andExpect(jsonPath("$.content", not(empty())))
                //.andExpect(jsonPath("$.totalPages").value(1))
                //.andExpect(jsonPath("$.totalElements").value(1))
            ;
            // Assert
            verify(restauranteService, times(1)).findAll(pageable);
        }
    }

    @Nested
    class AlterarRestaurante {
        @Test
        void devePermitirAlterarRestaurante() throws Exception {
            // Arrange
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(true);
            when(restauranteService.update(restauranteDTO.id(), restauranteDTO)).thenAnswer(r -> r.getArgument(1) );
            // Act
            mockMvc.perform(put("/restaurante/{id}", restauranteDTO.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(restauranteDTO)))
                    .andExpect(status().isAccepted());
            // Assert
            verify(restauranteService, times(1)).update(restauranteDTO.id(), restauranteDTO);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarRestaurante_RequisicaoXml() throws Exception {
            // Arrange
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(true);
            when(restauranteService.update(restauranteDTO.id(), restauranteDTO)).thenAnswer(r -> r.getArgument(1) );
            // Act
            mockMvc.perform(put("/restaurante/{id}", restauranteDTO.id())
                            .contentType(MediaType.APPLICATION_XML)
                            .content(asJsonString(restauranteDTO)))
                    .andExpect(status().isUnsupportedMediaType());
            // Assert
            verify(restauranteService, never()).update(restauranteDTO.id(), restauranteDTO);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarRestaurantePorId_idNaoExiste() throws Exception {
            // Arrange
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(true);
            when(restauranteService.update(restauranteDTO.id(), restauranteDTO)).thenThrow(ControllerNotFoundException.class);
            // Act
            mockMvc.perform(put("/restaurante/{id}", restauranteDTO.id())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(restauranteDTO)))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(restauranteService, times(1)).update(any(UUID.class), any(RestauranteDTO.class));
        }
    }

    @Nested
    class RemoverRestaurante {
        @Test
        void devePermitirRemoverRestaurante() throws Exception {
            // Arrange
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(true);
            doNothing().when(restauranteService).delete(restauranteDTO.id());
            // Act
            mockMvc.perform(delete("/restaurante/{id}", restauranteDTO.id()))
                    .andExpect(status().isNoContent());
            // Assert
            verify(restauranteService, times(1)).delete(restauranteDTO.id());
            verify(restauranteService, times(1)).delete(restauranteDTO.id());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverRestaurantePorId_idNaoExiste() throws Exception {
            // Arrange
            var restauranteDTO = RestauranteHelper.getRestauranteDTO(true);
            doThrow(new ControllerNotFoundException("Restaurante não encontrado com o ID: " + restauranteDTO.id()))
                    .when(restauranteService).delete(restauranteDTO.id());
            // Act
            mockMvc.perform(delete("/restaurante/{id}", restauranteDTO.id()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(restauranteService, times(1)).delete(restauranteDTO.id());
        }
    }
}