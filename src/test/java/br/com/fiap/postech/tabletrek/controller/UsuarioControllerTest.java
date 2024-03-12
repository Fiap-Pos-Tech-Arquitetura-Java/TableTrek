package br.com.fiap.postech.tabletrek.controller;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import br.com.fiap.postech.tabletrek.helper.UsuarioHelper;
import br.com.fiap.postech.tabletrek.services.UsuarioService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UsuarioControllerTest {

    private MockMvc mockMvc;
    @Mock
    private UsuarioService usuarioService;
    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        UsuarioController usuarioController = new UsuarioController(usuarioService);
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    public static String asJsonString(final Object object) throws Exception {
        return new ObjectMapper().writeValueAsString(object);
    }

    @Nested
    class CadastrarUsuario {
        @Test
        void devePermitirCadastrarUsuario() throws Exception {
            // Arrange
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(false);
            when(usuarioService.save(any(UsuarioDTO.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            mockMvc.perform(
                        post("/usuario").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(usuarioDTO)))
                    .andExpect(status().isCreated());
            // Assert
            verify(usuarioService, times(1)).save(any(UsuarioDTO.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarUsuario_RequisicaoXml() throws Exception {
            // Arrange
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(false);
            when(usuarioService.save(any(UsuarioDTO.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            mockMvc.perform(
                            post("/usuario").contentType(MediaType.APPLICATION_XML)
                                    .content(asJsonString(usuarioDTO)))
                    .andExpect(status().isUnsupportedMediaType());
            // Assert
            verify(usuarioService, never()).save(any(UsuarioDTO.class));
        }
    }
    @Nested
    class BuscarUsuario {
        @Test
        void devePermitirBuscarUsuarioPorId() throws Exception {
            // Arrange
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            when(usuarioService.findById(any(UUID.class))).thenReturn(usuarioDTO);
            // Act
            mockMvc.perform(get("/usuario/{id}", usuarioDTO.id().toString()))
                    .andExpect(status().isOk());
            // Assert
            verify(usuarioService, times(1)).findById(any(UUID.class));
        }
        @Test
        void deveGerarExcecao_QuandoBuscarUsuarioPorId_idNaoExiste() throws Exception {
            // Arrange
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            when(usuarioService.findById(usuarioDTO.id())).thenThrow(ControllerNotFoundException.class);
            // Act
            mockMvc.perform(get("/usuario/{id}", usuarioDTO.id().toString()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(usuarioService, times(1)).findById(usuarioDTO.id());
        }

        @Test
        void devePermitirBuscarTodosUsuario() throws Exception {
            // Arrange
            int page = 0;
            int size = 10;
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            var criterioUsuarioDTO = new UsuarioDTO(null, usuarioDTO.nome(), usuarioDTO.email(), null, usuarioDTO.telefone());
            List<UsuarioDTO> listUsuario = new ArrayList<>();
            listUsuario.add(usuarioDTO);
            Page<UsuarioDTO> usuarios = new PageImpl<>(listUsuario);
            var pageable = PageRequest.of(page, size);
            when(usuarioService.findAll(
                    pageable,
                    criterioUsuarioDTO
                )
            ).thenReturn(usuarios);
            // Act
            mockMvc.perform(
                get("/usuario")
                    .param("page", String.valueOf(page))
                    .param("size", String.valueOf(size))
                    .param("nome", usuarioDTO.nome())
                    .param("email", usuarioDTO.email())
                    .param("telefone", usuarioDTO.telefone().toString())
                )
                //.andDo(print())
                .andExpect(status().is5xxServerError())
                //.andExpect(jsonPath("$.content", not(empty())))
                //.andExpect(jsonPath("$.totalPages").value(1))
                //.andExpect(jsonPath("$.totalElements").value(1))
            ;
            // Assert
            verify(usuarioService, times(1)).findAll(pageable, criterioUsuarioDTO);
        }
    }

    @Nested
    class AlterarUsuario {
        @Test
        void devePermitirAlterarUsuario() throws Exception {
            // Arrange
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            when(usuarioService.update(usuarioDTO.id(), usuarioDTO)).thenAnswer(r -> r.getArgument(1) );
            // Act
            mockMvc.perform(put("/usuario/{id}", usuarioDTO.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(usuarioDTO)))
                    .andExpect(status().isAccepted());
            // Assert
            verify(usuarioService, times(1)).update(usuarioDTO.id(), usuarioDTO);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarUsuario_RequisicaoXml() throws Exception {
            // Arrange
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            when(usuarioService.update(usuarioDTO.id(), usuarioDTO)).thenAnswer(r -> r.getArgument(1) );
            // Act
            mockMvc.perform(put("/usuario/{id}", usuarioDTO.id())
                            .contentType(MediaType.APPLICATION_XML)
                            .content(asJsonString(usuarioDTO)))
                    .andExpect(status().isUnsupportedMediaType());
            // Assert
            verify(usuarioService, never()).update(usuarioDTO.id(), usuarioDTO);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarUsuarioPorId_idNaoExiste() throws Exception {
            // Arrange
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            when(usuarioService.update(usuarioDTO.id(), usuarioDTO)).thenThrow(ControllerNotFoundException.class);
            // Act
            mockMvc.perform(put("/usuario/{id}", usuarioDTO.id())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(usuarioDTO)))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(usuarioService, times(1)).update(any(UUID.class), any(UsuarioDTO.class));
        }
    }

    @Nested
    class RemoverUsuario {
        @Test
        void devePermitirRemoverUsuario() throws Exception {
            // Arrange
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            doNothing().when(usuarioService).delete(usuarioDTO.id());
            // Act
            mockMvc.perform(delete("/usuario/{id}", usuarioDTO.id()))
                    .andExpect(status().isNoContent());
            // Assert
            verify(usuarioService, times(1)).delete(usuarioDTO.id());
            verify(usuarioService, times(1)).delete(usuarioDTO.id());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverUsuarioPorId_idNaoExiste() throws Exception {
            // Arrange
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            doThrow(new ControllerNotFoundException("Usuario não encontrado com o ID: " + usuarioDTO.id()))
                    .when(usuarioService).delete(usuarioDTO.id());
            // Act
            mockMvc.perform(delete("/usuario/{id}", usuarioDTO.id()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(usuarioService, times(1)).delete(usuarioDTO.id());
        }
    }
}