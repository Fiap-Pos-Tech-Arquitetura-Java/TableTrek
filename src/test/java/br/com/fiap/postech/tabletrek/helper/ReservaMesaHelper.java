package br.com.fiap.postech.tabletrek.helper;

import br.com.fiap.postech.tabletrek.dto.ReservaMesaDTO;
import br.com.fiap.postech.tabletrek.entities.ReservaMesa;
import br.com.fiap.postech.tabletrek.entities.Restaurante;
import br.com.fiap.postech.tabletrek.entities.Usuario;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReservaMesaHelper {

    public static ReservaMesa getReservaMesa(Boolean geraId, String idRestaurante, String idUsuario) {
        var restaurante = new Restaurante();
        restaurante.setId(UUID.fromString(idRestaurante));
        var usuario = new Usuario();
        usuario.setId(UUID.fromString(idUsuario));
        var reservaMesa = new ReservaMesa(
                restaurante,
                usuario,
                LocalDateTime.now().withMinute(0).withSecond(0),
                "PENDENTE");
        if (!geraId) {
            reservaMesa.setId(null);
        }
        return reservaMesa;
    }

    public static ReservaMesa getReservaMesa(Boolean geraId) {
        return getReservaMesa(geraId, UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

    public static ReservaMesaDTO getReservaMesaDTO(Boolean geraId) {
        var reservaMesa = getReservaMesa(geraId);
        return getReservaMesaDTO(reservaMesa);
    }

    public static ReservaMesaDTO getReservaMesaDTO(Boolean geraId, String idRestaurante, String idUsuario) {
        var reservaMesa = getReservaMesa(geraId, idRestaurante, idUsuario);
        return getReservaMesaDTO(reservaMesa);
    }
    public static ReservaMesaDTO getReservaMesaDTO(ReservaMesa reservaMesa) {
        return new ReservaMesaDTO(
                reservaMesa.getId(),
                reservaMesa.getRestaurante().getId(),
                reservaMesa.getUsuario().getId(),
                reservaMesa.getHorario(),
                reservaMesa.getStatus()
        );
    }
}
