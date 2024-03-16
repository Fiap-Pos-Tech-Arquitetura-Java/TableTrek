package br.com.fiap.postech.tabletrek.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenDTO(
        String accessToken,

        String erro
) {
}
