package br.com.fiap.postech.tabletrek.repository;

import br.com.fiap.postech.tabletrek.entities.Restaurante;

import java.util.UUID;

class RestauranteRepositoryHelper {
    static Restaurante getRestaurante(Boolean geraId) {
        var restaurante = new Restaurante("JoJo Ramen");
        if (geraId) {
            restaurante.setId(UUID.randomUUID());
        }
        return restaurante;
    }
}
