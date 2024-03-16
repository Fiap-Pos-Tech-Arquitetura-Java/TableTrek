package br.com.fiap.postech.tabletrek.helper;

import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.entities.Restaurante;
import br.com.fiap.postech.tabletrek.entities.Usuario;

import java.util.UUID;

public class RestauranteHelper {
    public static Restaurante getRestaurante(Boolean geraId, String idUsuario) {
        var usuario = new Usuario();
        usuario.setId(UUID.fromString(idUsuario));
        var restaurante = new Restaurante(
                usuario,
                "JoJo Ramen",
                "rua asdfpasdfa, 34123 - Paraiso - São Paulo - SP",
                "Segunda a Sexta 11h as 23h",
                10,
                "Japonesa");
        if (!geraId) {
            restaurante.setId(null);
        }
        return restaurante;
    }
    public static Restaurante getRestaurante(Boolean geraId) {
        return getRestaurante(geraId, UsuarioHelper.getUsuario(true).getId().toString());
    }
    public static RestauranteDTO getRestauranteDTO(Boolean geraId, String idUsuario) {
        var restaurante = getRestaurante(geraId, idUsuario);
        return getRestauranteDTO(restaurante);
    }
    public static RestauranteDTO getRestauranteDTO(Boolean geraId) {
        var restaurante = getRestaurante(geraId);
        return getRestauranteDTO(restaurante);
    }
    public static RestauranteDTO getRestauranteDTO(Restaurante restaurante) {
        return new RestauranteDTO(
                restaurante.getId(),
                restaurante.getUsuario().getId(),
                restaurante.getNome(),
                restaurante.getLocalizacao(),
                restaurante.getHorarioFuncionamento(),
                restaurante.getCapacidade(),
                restaurante.getTipoCozinha()
        );
    }
}
