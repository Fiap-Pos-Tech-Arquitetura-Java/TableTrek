package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;

import java.util.List;
import java.util.UUID;

public interface RestauranteService {
    RestauranteDTO save(RestauranteDTO restauranteDTO);

    List<RestauranteDTO> findAll();

    RestauranteDTO findById(UUID id);

    RestauranteDTO update(UUID id, RestauranteDTO restauranteDTO);

    void delete(UUID id);
}
