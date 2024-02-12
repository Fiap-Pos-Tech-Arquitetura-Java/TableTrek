package br.com.fiap.postech.tabletrek.helper;

import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.entities.Restaurante;

import java.util.UUID;

public class RestauranteHelper {
    public static Restaurante getRestaurante(Boolean geraId) {
        var restaurante = new Restaurante("JoJo Ramen");
        if (geraId) {
            restaurante.setId(UUID.randomUUID());
        }
        return restaurante;
    }
    public static RestauranteDTO getRestauranteDTO(Boolean geraId) {
        var restaurante = getRestaurante(geraId);
        return getRestauranteDTO(restaurante);
    }
    public static RestauranteDTO getRestauranteDTO(Restaurante restaurante) {
        return new RestauranteDTO(restaurante.getId(), restaurante.getNome());
    }
}
