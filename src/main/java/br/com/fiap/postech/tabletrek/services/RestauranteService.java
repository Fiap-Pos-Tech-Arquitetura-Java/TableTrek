package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RestauranteService {
    RestauranteDTO save(RestauranteDTO restauranteDTO);

    Page<RestauranteDTO> findAll(Pageable pageable);

    RestauranteDTO findById(UUID id);

    RestauranteDTO update(UUID id, RestauranteDTO restauranteDTO);

    void delete(UUID id);
}
