package br.com.fiap.postech.tabletrek.controller;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.AvaliacaoDTO;
import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import br.com.fiap.postech.tabletrek.helper.AvaliacaoHelper;
import br.com.fiap.postech.tabletrek.helper.UsuarioHelper;
import br.com.fiap.postech.tabletrek.security.SecurityHelper;
import br.com.fiap.postech.tabletrek.services.AvaliacaoService;
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

class AvaliacaoControllerTest {

    private MockMvc mockMvc;
    @Mock
    private AvaliacaoService avaliacaoService;
    @Mock
    private SecurityHelper securityHelper;
    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        AvaliacaoController avaliacaoController = new AvaliacaoController(avaliacaoService, securityHelper);
        mockMvc = MockMvcBuilders.standaloneSetup(avaliacaoController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    public static String asJsonString(final Object object) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper.writeValueAsString(object);
    }

    @Nested
    class CadastrarAvaliacao {
        @Test
        void devePermitirCadastrarAvaliacao() throws Exception {
            // Arrange
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(false);
            var usuario = UsuarioHelper.getUsuarioDTO(true);

            when(securityHelper.getUsuarioLogado()).thenReturn(usuario);
            when(avaliacaoService.save(any(AvaliacaoDTO.class), any(UsuarioDTO.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            mockMvc.perform(
                        post("/avaliacao").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(avaliacaoDTO)))
                    .andExpect(status().isCreated());
            // Assert
            verify(avaliacaoService, times(1)).save(any(AvaliacaoDTO.class), any(UsuarioDTO.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarAvaliacao_RequisicaoXml() throws Exception {
            // Arrange
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(false);
            when(avaliacaoService.save(any(AvaliacaoDTO.class), any(UsuarioDTO.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            mockMvc.perform(
                            post("/avaliacao").contentType(MediaType.APPLICATION_XML)
                                    .content(asJsonString(avaliacaoDTO)))
                    .andExpect(status().isUnsupportedMediaType());
            // Assert
            verify(avaliacaoService, never()).save(any(AvaliacaoDTO.class), any(UsuarioDTO.class));
        }
    }
    @Nested
    class BuscarAvaliacao {
        @Test
        void devePermitirBuscarAvaliacaoPorId() throws Exception {
            // Arrange
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(true);
            when(avaliacaoService.findById(any(UUID.class))).thenReturn(avaliacaoDTO);
            // Act
            mockMvc.perform(get("/avaliacao/{id}", avaliacaoDTO.id().toString()))
                    .andExpect(status().isOk());
            // Assert
            verify(avaliacaoService, times(1)).findById(any(UUID.class));
        }
        @Test
        void deveGerarExcecao_QuandoBuscarAvaliacaoPorId_idNaoExiste() throws Exception {
            // Arrange
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(true);
            when(avaliacaoService.findById(avaliacaoDTO.id())).thenThrow(ControllerNotFoundException.class);
            // Act
            mockMvc.perform(get("/avaliacao/{id}", avaliacaoDTO.id().toString()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(avaliacaoService, times(1)).findById(avaliacaoDTO.id());
        }

        @Test
        void devePermitirBuscarTodosAvaliacao() throws Exception {
            // Arrange
            int page = 0;
            int size = 10;
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(true);
            var criterioAvaliacaoDTO = new AvaliacaoDTO(null, avaliacaoDTO.idReservaMesa(), null, null);
            List<AvaliacaoDTO> listAvaliacao = new ArrayList<>();
            listAvaliacao.add(avaliacaoDTO);
            Page<AvaliacaoDTO> avaliacaos = new PageImpl<>(listAvaliacao);
            var pageable = PageRequest.of(page, size);
            when(avaliacaoService.findAll(
                    pageable,
                    criterioAvaliacaoDTO
                )
            ).thenReturn(avaliacaos);
            // Act
            mockMvc.perform(
                get("/avaliacao")
                    .param("page", String.valueOf(page))
                    .param("size", String.valueOf(size))
                    .param("idReservaMesa", avaliacaoDTO.idReservaMesa().toString())
                )
                //.andDo(print())
                .andExpect(status().is5xxServerError())
                //.andExpect(jsonPath("$.content", not(empty())))
                //.andExpect(jsonPath("$.totalPages").value(1))
                //.andExpect(jsonPath("$.totalElements").value(1))
            ;
            // Assert
            verify(avaliacaoService, times(1)).findAll(pageable, criterioAvaliacaoDTO);
        }
    }

    @Nested
    class AlterarAvaliacao {
        @Test
        void devePermitirAlterarAvaliacao() throws Exception {
            // Arrange
            var avaliacao = AvaliacaoHelper.getAvaliacao(true);
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(avaliacao);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(avaliacao.getReservaMesa().getUsuario());
            var novaAvaliacaoDTO = new AvaliacaoDTO(
                    avaliacaoDTO.id(),
                    avaliacaoDTO.idReservaMesa(),
                    avaliacaoDTO.nota() - 1,
                    avaliacaoDTO.comentario() + "1234"
            );
            when(securityHelper.getUsuarioLogado()).thenReturn(usuarioDTO);
            when(avaliacaoService.update(avaliacaoDTO.id(), novaAvaliacaoDTO, usuarioDTO)).thenReturn(novaAvaliacaoDTO);
            // Act
            mockMvc.perform(put("/avaliacao/{id}", avaliacaoDTO.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(novaAvaliacaoDTO)))
                    .andExpect(status().isAccepted());

            // Assert
            verify(avaliacaoService, times(1)).update(avaliacaoDTO.id(), novaAvaliacaoDTO, usuarioDTO);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarAvaliacaoPorId_idNaoExiste() throws Exception {
            // Arrange
            var avaliacao = AvaliacaoHelper.getAvaliacao(true);
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(avaliacao);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(avaliacao.getReservaMesa().getUsuario());
            var novaAvaliacaoDTO = new AvaliacaoDTO(
                    avaliacaoDTO.id(),
                    avaliacaoDTO.idReservaMesa(),
                    avaliacaoDTO.nota(),
                    avaliacaoDTO.comentario()
            );
            when(securityHelper.getUsuarioLogado()).thenReturn(usuarioDTO);
            when(avaliacaoService.update(avaliacaoDTO.id(), novaAvaliacaoDTO, usuarioDTO)).thenThrow(ControllerNotFoundException.class);
            // Act
            mockMvc.perform(put("/avaliacao/{id}", avaliacaoDTO.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(novaAvaliacaoDTO)))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(avaliacaoService, times(1)).update(avaliacaoDTO.id(), novaAvaliacaoDTO, usuarioDTO);
        }
    }

    @Nested
    class RemoverAvaliacao {
        @Test
        void devePermitirRemoverAvaliacao() throws Exception {
            // Arrange
            var avaliacao = AvaliacaoHelper.getAvaliacao(true);
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(avaliacao);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(avaliacao.getReservaMesa().getUsuario());
            when(securityHelper.getUsuarioLogado()).thenReturn(usuarioDTO);
            doNothing().when(avaliacaoService).delete(avaliacaoDTO.id(), usuarioDTO);
            // Act
            mockMvc.perform(delete("/avaliacao/{id}", avaliacaoDTO.id()))
                    .andExpect(status().isNoContent());
            // Assert
            verify(avaliacaoService, times(1)).delete(avaliacaoDTO.id(), usuarioDTO);
        }

        @Test
        void deveGerarExcecao_QuandoRemoverAvaliacaoPorId_idNaoExiste() throws Exception {
            // Arrange
            var avaliacao = AvaliacaoHelper.getAvaliacao(true);
            var avaliacaoDTO = AvaliacaoHelper.getAvaliacaoDTO(avaliacao);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(avaliacao.getReservaMesa().getUsuario());
            doThrow(new ControllerNotFoundException("Avaliacao não encontrado com o ID: " + avaliacaoDTO.id()))
                    .when(avaliacaoService).delete(avaliacaoDTO.id(), usuarioDTO);
            when(securityHelper.getUsuarioLogado()).thenReturn(usuarioDTO);
            // Act
            mockMvc.perform(delete("/avaliacao/{id}", avaliacaoDTO.id()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(avaliacaoService, times(1)).delete(avaliacaoDTO.id(), usuarioDTO);
        }
    }
}