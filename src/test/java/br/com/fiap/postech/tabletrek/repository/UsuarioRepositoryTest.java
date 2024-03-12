package br.com.fiap.postech.tabletrek.repository;

import br.com.fiap.postech.tabletrek.entities.Usuario;
import br.com.fiap.postech.tabletrek.helper.UsuarioHelper;
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

class UsuarioRepositoryTest {

    @Mock
    private UsuarioRepository usuarioRepository;

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
    void devePermitirCadastrarUsuario() {
        // Arrange
        var usuario = UsuarioHelper.getUsuario(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        // Act
        var savedUsuario = usuarioRepository.save(usuario);
        // Assert
        assertThat(savedUsuario).isNotNull().isEqualTo(usuario);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void devePermitirBuscarUsuario() {
        // Arrange
        var usuario = UsuarioHelper.getUsuario(true);
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        // Act
        var usuarioOpcional = usuarioRepository.findById(usuario.getId());
        // Assert
        assertThat(usuarioOpcional).isNotNull().containsSame(usuario);
        usuarioOpcional.ifPresent(
                usuarioRecebido -> {
                    assertThat(usuarioRecebido).isInstanceOf(Usuario.class).isNotNull();
                    assertThat(usuarioRecebido.getId()).isEqualTo(usuario.getId());
                    assertThat(usuarioRecebido.getNome()).isEqualTo(usuario.getNome());
                }
        );
        verify(usuarioRepository, times(1)).findById(usuario.getId());
    }
    @Test
    void devePermitirRemoverUsuario() {
        //Arrange
        var id = UUID.randomUUID();
        doNothing().when(usuarioRepository).deleteById(id);
        //Act
        usuarioRepository.deleteById(id);
        //Assert
        verify(usuarioRepository, times(1)).deleteById(id);
    }
    @Test
    void devePermitirListarUsuarios() {
        // Arrange
        var usuario1 = UsuarioHelper.getUsuario(true);
        var usuario2 = UsuarioHelper.getUsuario(true);
        var listaUsuarios = Arrays.asList(
                usuario1,
                usuario2
        );
        when(usuarioRepository.findAll()).thenReturn(listaUsuarios);
        // Act
        var usuariosListados = usuarioRepository.findAll();
        assertThat(usuariosListados)
                .hasSize(2)
                .containsExactlyInAnyOrder(usuario1, usuario2);
        verify(usuarioRepository, times(1)).findAll();
    }
}