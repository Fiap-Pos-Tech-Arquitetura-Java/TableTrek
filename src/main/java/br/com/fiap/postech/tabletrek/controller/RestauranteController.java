package br.com.fiap.postech.tabletrek.controller;

import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.services.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurante")
public class RestauranteController {

    private final RestauranteService restauranteService;
    @Autowired
    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    @Operation(summary = "registra um restaurante")
    @PostMapping
    public ResponseEntity<RestauranteDTO> save(@Valid @RequestBody RestauranteDTO restauranteDTO) {
        RestauranteDTO savedRestauranteDTO = restauranteService.save(restauranteDTO);
        return new ResponseEntity<>(savedRestauranteDTO, HttpStatus.CREATED);
    }

}
