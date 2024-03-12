package br.com.fiap.postech.tabletrek.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UsuarioDTO(
        @NotBlank(message = "id do usuario")
        @Schema(example = "1")
        UUID id,

        @NotBlank(message = "nome do usuario")
        @Schema(example = "Anderson Wagner")
        String nome,

        @NotBlank(message = "email do usuario")
        @Schema(example = "anderson.wagner@gmail.com")
        String email,

        @NotBlank(message = "senha do usuario")
        @Schema(example = "123456")
        String senha,

        @NotBlank(message = "telefone do usuario")
        @Schema(example = "60")
        Long telefone
) {
}
