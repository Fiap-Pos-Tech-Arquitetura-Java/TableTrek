package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import java.util.UUID;

import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *  RestauranteService
 */
public interface RestauranteService {
    RestauranteDTO save(UsuarioDTO usuarioDTO, RestauranteDTO restauranteDTO);

    Page<RestauranteDTO> findAll(Pageable pageable, RestauranteDTO restauranteDTO);

    RestauranteDTO findById(UUID id);

    RestauranteDTO update(UUID id, UsuarioDTO usuarioLogado, RestauranteDTO restauranteDTO);

    void delete(UUID id, UsuarioDTO usuarioLogado);
}
