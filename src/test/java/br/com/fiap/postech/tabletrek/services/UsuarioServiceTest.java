package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import br.com.fiap.postech.tabletrek.entities.Usuario;
import br.com.fiap.postech.tabletrek.helper.UsuarioHelper;
import br.com.fiap.postech.tabletrek.repository.UsuarioRepository;
import br.com.fiap.postech.tabletrek.security.JwtService;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jstService;

    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        usuarioService = new UsuarioServiceImpl(usuarioRepository, jstService);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class CadastrarUsuario {
        @Test
        void devePermitirCadastrarUsuario() {
            // Arrange
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(false);
            when(usuarioRepository.save(any(Usuario.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var usuarioSalvo = usuarioService.save(usuarioDTO);
            // Assert
            assertThat(usuarioSalvo)
                    .isInstanceOf(UsuarioDTO.class)
                    .isNotNull();
            assertThat(usuarioSalvo.nome()).isEqualTo(usuarioDTO.nome());
            assertThat(usuarioSalvo.id()).isNotNull();
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
        }
    }

    @Nested
    class BuscarUsuario {
        @Test
        void devePermitirBuscarUsuarioPorId() {
            // Arrange
            var usuario = UsuarioHelper.getUsuario(true);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            when(usuarioRepository.findById(usuarioDTO.id())).thenReturn(Optional.of(usuario));
            // Act
            var restautranteObtido = usuarioService.findById(usuarioDTO.id());
            // Assert
            assertThat(restautranteObtido).isEqualTo(usuarioDTO);
            verify(usuarioRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarUsuarioPorId_idNaoExiste() {
            // Arrange
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(true);
            when(usuarioRepository.findById(usuarioDTO.id())).thenReturn(Optional.empty());
            UUID uuid = usuarioDTO.id();
            // Act
            assertThatThrownBy(() -> usuarioService.findById(uuid))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Usuario não encontrado com o ID: " + usuarioDTO.id());
            // Assert
            verify(usuarioRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void devePermitirBuscarTodosUsuario() {
            // Arrange
            UsuarioDTO criteriosDeBusca = UsuarioHelper.getUsuarioDTO(false);
            Page<Usuario> usuarios = new PageImpl<>(Arrays.asList(
                    UsuarioHelper.getUsuario(true),
                    UsuarioHelper.getUsuario(true),
                    UsuarioHelper.getUsuario(true)
            ));
            when(usuarioRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(usuarios);
            // Act
            var usuarioObtidos = usuarioService.findAll(Pageable.unpaged(), criteriosDeBusca);
            // Assert
            assertThat(usuarioObtidos).hasSize(3);
            assertThat(usuarioObtidos.getContent()).asList().allSatisfy(
                    usuario -> {
                        assertThat(usuario)
                                .isNotNull()
                                .isInstanceOf(UsuarioDTO.class);
                    }
            );
            verify(usuarioRepository, times(1)).findAll(any(Example.class), any(Pageable.class));
        }
    }

    @Nested
    class AlterarUsuario {
        @Test
        void devePermitirAlterarUsuario() {
            // Arrange
            var usuario = UsuarioHelper.getUsuario(true);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            var novoUsuarioDTO = new UsuarioDTO(usuarioDTO.id(),
                    RandomStringUtils.random(20, true, true),
                    usuarioDTO.email(),
                    RandomStringUtils.random(20, true, true),
                    10 + (long) (Math.random() * 100)
            );
            when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
            when(usuarioRepository.save(any(Usuario.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var usuarioSalvo = usuarioService.update(usuarioDTO.id(), novoUsuarioDTO);
            // Assert
            assertThat(usuarioSalvo)
                    .isInstanceOf(UsuarioDTO.class)
                    .isNotNull();
            assertThat(usuarioSalvo.nome()).isEqualTo(novoUsuarioDTO.nome());
            assertThat(usuarioSalvo.nome()).isNotEqualTo(usuarioDTO.nome());

            //assertThat(usuarioSalvo.email()).isEqualTo(novoUsuarioDTO.email());
            //assertThat(usuarioSalvo.email()).isNotEqualTo(usuarioDTO.email());

            /*BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            Boolean senhaCerta = encoder.matches(usuarioDTO.senha(), novoUsuarioDTO.senha());
            assertThat(senhaCerta).isTrue();
            assertThat(usuarioSalvo.senha()).isEqualTo(novoUsuarioDTO.senha());
            assertThat(usuarioSalvo.senha()).isNotEqualTo(usuarioDTO.senha());*/

            assertThat(usuarioSalvo.telefone()).isEqualTo(novoUsuarioDTO.telefone());
            assertThat(usuarioSalvo.telefone()).isNotEqualTo(usuarioDTO.telefone());

            verify(usuarioRepository, times(1)).findById(any(UUID.class));
            verify(usuarioRepository, times(1)).save(any(Usuario.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarUsuarioPorId_idNaoExiste() {
            // Arrange
            var usuario = UsuarioHelper.getUsuario(true);
            var usuarioDTO = UsuarioHelper.getUsuarioDTO(usuario);
            when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());
            UUID uuid = usuarioDTO.id();
            // Act && Assert
            assertThatThrownBy(() -> usuarioService.update(uuid, usuarioDTO))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Usuario não encontrado com o ID: " + usuarioDTO.id());
            verify(usuarioRepository, times(1)).findById(any(UUID.class));
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }
    }

    @Nested
    class RemoverUsuario {
        @Test
        void devePermitirRemoverUsuario() {
            // Arrange
            var usuario = UsuarioHelper.getUsuario(true);
            when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
            doNothing().when(usuarioRepository).deleteById(usuario.getId());
            // Act
            usuarioService.delete(usuario.getId());
            // Assert
            verify(usuarioRepository, times(1)).findById(any(UUID.class));
            verify(usuarioRepository, times(1)).deleteById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandRemoverUsuarioPorId_idNaoExiste() {
            // Arrange
            var usuario = UsuarioHelper.getUsuario(true);
            doNothing().when(usuarioRepository).deleteById(usuario.getId());
            UUID uuid = usuario.getId();
            // Act && Assert
            assertThatThrownBy(() -> usuarioService.delete(uuid))
                    .isInstanceOf(ControllerNotFoundException.class)
                    .hasMessage("Usuario não encontrado com o ID: " + usuario.getId());
            verify(usuarioRepository, times(1)).findById(any(UUID.class));
            verify(usuarioRepository, never()).deleteById(any(UUID.class));
        }
    }
}