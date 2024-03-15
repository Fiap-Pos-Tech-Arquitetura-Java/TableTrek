package br.com.fiap.postech.tabletrek.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReservaMesaDTO(
        @NotBlank(message = "id da reserva de mesa")
        @Schema(example = "1")
        UUID id,

        @NotBlank(message = "id do restaurante da reserva de mesa")
        @Schema(example = "11234")
        UUID idRestaurante,

        @NotBlank(message = "id do usuario da reserva de mesa")
        @Schema(example = "11234")
        UUID idUsuario,

        @NotBlank(message = "data hora da reserva de mesa")
        @Schema(example = "anderson.wagner@gmail.com")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime horario,

        @NotBlank(message = "status da reserva de mesa")
        @Schema(example = "123456")
        String status
) {
}
