package br.com.fiap.postech.tabletrek.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestauranteDTO(
        @NotBlank(message = "id do restaurante")
        @Schema(example = "1")
        UUID id,

        @NotBlank(message = "nome do restaurante")
        @Schema(example = "JoJo Ramen")
        String nome,

        @NotBlank(message = "localização do restaurante")
        @Schema(example = "R. Jacofer, 615 - Limao, São Paulo - SP, 02712-070")
        String localizacao,

        @NotBlank(message = "horario de funcionamento do restaurante")
        @Schema(example = "11:00–22:00")
        String horarioFuncionamento,

        @NotBlank(message = "capacidade de mesas do restaurante")
        @Schema(example = "60")
        Integer capacidade,

        @NotBlank(message = "tipo da culinaria do restaurante")
        @Schema(example = "Nordestina")
        String tipoCozinha
) {
}
