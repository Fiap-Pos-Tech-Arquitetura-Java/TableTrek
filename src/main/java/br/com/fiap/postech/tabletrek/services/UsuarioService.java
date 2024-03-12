package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 *  UsuarioService
 */
public interface UsuarioService {

    UsuarioDTO save(UsuarioDTO usuarioDTO);

    Page<UsuarioDTO> findAll(Pageable pageable, UsuarioDTO usuarioDTO);

    UsuarioDTO findById(UUID id);

    UsuarioDTO update(UUID id, UsuarioDTO usuarioDTO);

    void delete(UUID id);

    String login(UsuarioDTO usuarioDTO) throws Exception;
}
