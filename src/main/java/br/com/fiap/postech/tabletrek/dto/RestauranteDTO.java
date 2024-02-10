package br.com.fiap.postech.tabletrek.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestauranteDTO(
        @NotBlank(message = "id do restaurante")
        @Schema(example = "1", required = true)
        String id,

        @NotBlank(message = "nome do restaurante")
        @Schema(example = "JoJo Ramen", required = true)
        String nome
) {
}
