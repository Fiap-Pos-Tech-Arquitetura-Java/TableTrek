package br.com.fiap.postech.tabletrek.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AvaliacaoDTO(
        @NotBlank(message = "id da reserva de mesa")
        @Schema(example = "1")
        UUID id,

        @NotBlank(message = "id da reserva de uma mesa em um restaurante")
        @Schema(example = "11234")
        UUID idReservaMesa,

        @NotBlank(message = "nota de 1 a 5, sendo 1 como ruim e 5 como otimo")
        @Schema(example = "5")
        Integer nota,

        @Schema(example = "boa comida e otimo atendimento")
        String comentario
) {
}
