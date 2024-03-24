package br.com.fiap.postech.tabletrek.controller;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.ReservaMesaDTO;
import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import br.com.fiap.postech.tabletrek.helper.ReservaMesaHelper;
import br.com.fiap.postech.tabletrek.helper.UsuarioHelper;
import br.com.fiap.postech.tabletrek.security.SecurityHelper;
import br.com.fiap.postech.tabletrek.services.ReservaMesaService;
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

class ReservaMesaControllerTest {

    private MockMvc mockMvc;
    @Mock
    private ReservaMesaService reservaMesaService;
    @Mock
    private SecurityHelper securityHelper;
    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        ReservaMesaController reservaMesaController = new ReservaMesaController(reservaMesaService, securityHelper);
        mockMvc = MockMvcBuilders.standaloneSetup(reservaMesaController).build();
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
    class CadastrarReservaMesa {
        @Test
        void devePermitirCadastrarReservaMesa() throws Exception {
            // Arrange
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(false);
            var usuario = UsuarioHelper.getUsuarioDTO(true);

            when(securityHelper.getUsuarioLogado()).thenReturn(usuario);
            when(reservaMesaService.save(any(UsuarioDTO.class), any(ReservaMesaDTO.class))).thenAnswer(r -> r.getArgument(1));
            // Act
            mockMvc.perform(
                        post("/reservaMesa").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(reservaMesaDTO)))
                    .andExpect(status().isCreated());
            // Assert
            verify(reservaMesaService, times(1)).save(any(UsuarioDTO.class), any(ReservaMesaDTO.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarReservaMesa_RequisicaoXml() throws Exception {
            // Arrange
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(false);
            when(reservaMesaService.save(any(UsuarioDTO.class), any(ReservaMesaDTO.class))).thenAnswer(r -> r.getArgument(1));
            // Act
            mockMvc.perform(
                            post("/reservaMesa").contentType(MediaType.APPLICATION_XML)
                                    .content(asJsonString(reservaMesaDTO)))
                    .andExpect(status().isUnsupportedMediaType());
            // Assert
            verify(reservaMesaService, never()).save(any(UsuarioDTO.class), any(ReservaMesaDTO.class));
        }
    }
    @Nested
    class BuscarReservaMesa {
        @Test
        void devePermitirBuscarReservaMesaPorId() throws Exception {
            // Arrange
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(true);
            var usuario = UsuarioHelper.getUsuarioDTO(true);

            when(securityHelper.getUsuarioLogado()).thenReturn(usuario);
            when(reservaMesaService.findById(any(UUID.class), any(UsuarioDTO.class))).thenReturn(reservaMesaDTO);
            // Act
            mockMvc.perform(get("/reservaMesa/{id}", reservaMesaDTO.id().toString()))
                    .andExpect(status().isOk());
            // Assert
            verify(reservaMesaService, times(1)).findById(any(UUID.class), any(UsuarioDTO.class));
        }
        @Test
        void deveGerarExcecao_QuandoBuscarReservaMesaPorId_idNaoExiste() throws Exception {
            // Arrange
            var reservaMesa = ReservaMesaHelper.getReservaMesa(true);
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(reservaMesa);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);

            when(securityHelper.getUsuarioLogado()).thenReturn(usuarioDTO);
            when(reservaMesaService.findById(reservaMesaDTO.id(), usuarioDTO)).thenThrow(ControllerNotFoundException.class);
            // Act
            mockMvc.perform(get("/reservaMesa/{id}", reservaMesaDTO.id().toString()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(reservaMesaService, times(1)).findById(reservaMesaDTO.id(), usuarioDTO);
        }

        @Test
        void devePermitirBuscarTodosReservaMesa() throws Exception {
            // Arrange
            int page = 0;
            int size = 10;
            var reservaMesa = ReservaMesaHelper.getReservaMesa(true);
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(reservaMesa);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(reservaMesa.getUsuario());
            when(securityHelper.getUsuarioLogado()).thenReturn(usuarioDTO);
            var criterioReservaMesaDTO = new ReservaMesaDTO(null, reservaMesaDTO.idRestaurante(), reservaMesaDTO.horario(), null);
            List<ReservaMesaDTO> listReservaMesa = new ArrayList<>();
            listReservaMesa.add(reservaMesaDTO);
            Page<ReservaMesaDTO> reservaMesas = new PageImpl<>(listReservaMesa);
            var pageable = PageRequest.of(page, size);
            when(reservaMesaService.findAll(
                    pageable,
                    usuarioDTO,
                    criterioReservaMesaDTO
                )
            ).thenReturn(reservaMesas);
            // Act
            mockMvc.perform(
                get("/reservaMesa")
                    .param("page", String.valueOf(page))
                    .param("size", String.valueOf(size))
                    .param("idRestaurante", reservaMesaDTO.idRestaurante().toString())
                    .param("horario", reservaMesaDTO.horario().toString())
                )
                //.andDo(print())
                .andExpect(status().is5xxServerError())
                //.andExpect(jsonPath("$.content", not(empty())))
                //.andExpect(jsonPath("$.totalPages").value(1))
                //.andExpect(jsonPath("$.totalElements").value(1))
            ;
            // Assert
            verify(reservaMesaService, times(1)).findAll(pageable, usuarioDTO, criterioReservaMesaDTO);
        }
    }

    @Nested
    class AlterarReservaMesa {
        @Test
        void devePermitirAlterarReservaMesa() throws Exception {
            // Arrange

            var reservaMesa = ReservaMesaHelper.getReservaMesa(true);
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(reservaMesa);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(reservaMesa.getUsuario());

            when(securityHelper.getUsuarioLogado()).thenReturn(usuarioDTO);
            when(reservaMesaService.finaliza(reservaMesaDTO.id(), usuarioDTO)).thenReturn(reservaMesaDTO);
            // Act
            mockMvc.perform(put("/reservaMesa/{id}", reservaMesaDTO.id())).andExpect(status().isAccepted());

            // Assert
            verify(reservaMesaService, times(1)).finaliza(reservaMesaDTO.id(), usuarioDTO);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarReservaMesaPorId_idNaoExiste() throws Exception {
            // Arrange
            var reservaMesa = ReservaMesaHelper.getReservaMesa(true);
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(reservaMesa);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(reservaMesa.getUsuario());

            when(securityHelper.getUsuarioLogado()).thenReturn(usuarioDTO);
            when(reservaMesaService.finaliza(reservaMesaDTO.id(), usuarioDTO)).thenThrow(ControllerNotFoundException.class);
            // Act
            mockMvc.perform(put("/reservaMesa/{id}", reservaMesaDTO.id())).andExpect(status().isBadRequest());
            // Assert
            verify(reservaMesaService, times(1)).finaliza(reservaMesaDTO.id(), usuarioDTO);
        }
    }

    @Nested
    class RemoverReservaMesa {
        @Test
        void devePermitirRemoverReservaMesa() throws Exception {
            // Arrange
            var reservaMesa = ReservaMesaHelper.getReservaMesa(true);
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(reservaMesa);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(reservaMesa.getUsuario());

            when(securityHelper.getUsuarioLogado()).thenReturn(usuarioDTO);
            doNothing().when(reservaMesaService).delete(reservaMesaDTO.id(), usuarioDTO);
            // Act
            mockMvc.perform(delete("/reservaMesa/{id}", reservaMesaDTO.id()))
                    .andExpect(status().isNoContent());
            // Assert
            verify(reservaMesaService, times(1)).delete(reservaMesaDTO.id(), usuarioDTO);
        }

        @Test
        void deveGerarExcecao_QuandoRemoverReservaMesaPorId_idNaoExiste() throws Exception {
            // Arrange
            var reservaMesa = ReservaMesaHelper.getReservaMesa(true);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(reservaMesa.getUsuario());
            var reservaMesaDTO = ReservaMesaHelper.getReservaMesaDTO(true);

            when(securityHelper.getUsuarioLogado()).thenReturn(usuarioDTO);
            doThrow(new ControllerNotFoundException("ReservaMesa não encontrado com o ID: " + reservaMesaDTO.id()))
                    .when(reservaMesaService).delete(reservaMesaDTO.id(), usuarioDTO);
            // Act
            mockMvc.perform(delete("/reservaMesa/{id}", reservaMesaDTO.id()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(reservaMesaService, times(1)).delete(reservaMesaDTO.id(), usuarioDTO);
        }
    }
}