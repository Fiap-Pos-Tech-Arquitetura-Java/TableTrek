package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import br.com.fiap.postech.tabletrek.entities.Usuario;
import br.com.fiap.postech.tabletrek.repository.UsuarioRepository;
import br.com.fiap.postech.tabletrek.security.JwtService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;

    private final JwtService jstService;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, JwtService jstService) {
        this.usuarioRepository = usuarioRepository;
        this.jstService = jstService;
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        return toDTO(Boolean.TRUE, usuario);
    }

    private UsuarioDTO toDTO(Boolean includeId, Usuario usuario) {
        if (usuario == null) {
            return new UsuarioDTO(null, null, null, null, null);
        }
        UUID id = getId(includeId, usuario);
        return new UsuarioDTO(
                id,
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getTelefone()
        );
    }
    private UUID getId(boolean includeId, Usuario usuario) {
        if (includeId) {
            return usuario.getId();
        }
        return null;
    }

    private Usuario toEntity(UsuarioDTO usuarioDTO) {
        if (usuarioDTO != null) {
            return new Usuario(
                    usuarioDTO.nome(),
                    usuarioDTO.email(),
                    getEncryptedPassword(usuarioDTO.senha()),
                    usuarioDTO.telefone()
            );
        } else {
            return new Usuario();
        }
    }

    private String getEncryptedPassword(String senha) {
        if (senha != null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder.encode(senha);
        }
        return null;
    }

    @Override
    public UsuarioDTO save(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.findByEmail(usuarioDTO.email()).isPresent()) {
            throw new ControllerNotFoundException("Já existe um motorista cadastrado com esse email.");
        }
        Usuario usuario = toEntity(usuarioDTO);
        usuario = usuarioRepository.save(usuario);
        return toDTO(usuario);
    }

    @Override
    public Page<UsuarioDTO> findAll(Pageable pageable, UsuarioDTO usuarioDTO) {
        Usuario usuario = toEntity(usuarioDTO);
        usuario.setId(null);
        Example<Usuario> usuarioExample = Example.of(usuario);
        Page<Usuario> usuarios = usuarioRepository.findAll(usuarioExample, pageable);
        return new PageImpl<>(usuarios.stream().map(this::toDTO).toList());
    }
    @Override
    public UsuarioDTO findById(UUID id) {
        return toDTO(get(id));
    }

    public Usuario get(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ControllerNotFoundException("Usuario não encontrado com o ID: " + id));
    }

    @Override
    public UsuarioDTO update(UUID id, UsuarioDTO usuarioDTO) {
        Usuario usuario = get(id);
        if (StringUtils.isNotEmpty(usuarioDTO.nome())) {
            usuario.setNome(usuarioDTO.nome());
        }
        if (usuarioDTO.id() != null && !usuario.getId().equals(usuarioDTO.id())) {
            throw new ControllerNotFoundException("Não é possível alterar o id de um usuario.");
        }
        if (usuarioDTO.email() != null && !usuario.getEmail().equals(usuarioDTO.email())) {
            throw new ControllerNotFoundException("Não é possível alterar o email de um usuario.");
        }
        if (StringUtils.isNotEmpty(usuarioDTO.senha())) {
            usuario.setSenha(getEncryptedPassword(usuarioDTO.senha()));
        }
        if (usuarioDTO.telefone() != null) {
            usuario.setTelefone(usuarioDTO.telefone());
        }
        usuario = usuarioRepository.save(usuario);
        return toDTO(Boolean.FALSE, usuario);
    }
    @Override
    public void delete(UUID id) {
        findById(id);
        usuarioRepository.deleteById(id);
    }

    @Override
    public String login(UsuarioDTO usuarioDTO) throws Exception {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(usuarioDTO.email());
        if (optionalUsuario.isEmpty()) {
            throw new Exception("Usuario informado não encontrado.");
        }
        Usuario u = optionalUsuario.get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(usuarioDTO.senha(), u.getSenha())) {
            throw new Exception("Senha do Usuario informado não confere.");
        }
        return jstService.generateToken(usuarioDTO.email());
    }
}
