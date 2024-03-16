package br.com.fiap.postech.tabletrek.helper;

import br.com.fiap.postech.tabletrek.dto.AvaliacaoDTO;
import br.com.fiap.postech.tabletrek.entities.Avaliacao;
import br.com.fiap.postech.tabletrek.entities.ReservaMesa;
import br.com.fiap.postech.tabletrek.entities.Restaurante;
import br.com.fiap.postech.tabletrek.entities.Usuario;

import java.time.LocalDateTime;
import java.util.UUID;

public class AvaliacaoHelper {

    public static Avaliacao getAvaliacao(Boolean geraId, String idReservaMesa) {
        var reservaMesa = new ReservaMesa();
        reservaMesa.setId(UUID.fromString(idReservaMesa));
        var avaliacao = new Avaliacao(
                reservaMesa,
                5,
                "boa comida e otimo atendimento");
        if (!geraId) {
            avaliacao.setId(null);
        }
        return avaliacao;
    }

    public static Avaliacao getAvaliacao(Boolean geraId) {
        return getAvaliacao(geraId, UUID.randomUUID().toString());
    }

    public static AvaliacaoDTO getAvaliacaoDTO(Boolean geraId) {
        var avaliacao = getAvaliacao(geraId);
        return getAvaliacaoDTO(avaliacao);
    }

    public static AvaliacaoDTO getAvaliacaoDTO(Boolean geraId, String idReservaMesa) {
        var avaliacao = getAvaliacao(geraId, idReservaMesa);
        return getAvaliacaoDTO(avaliacao);
    }
    public static AvaliacaoDTO getAvaliacaoDTO(Avaliacao avaliacao) {
        return new AvaliacaoDTO(
                avaliacao.getId(),
                avaliacao.getReservaMesa().getId(),
                avaliacao.getNota(),
                avaliacao.getComentario()
        );
    }
}
