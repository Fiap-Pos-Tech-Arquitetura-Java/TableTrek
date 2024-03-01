package br.com.fiap.postech.tabletrek.controller;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.services.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @Operation(summary = "lista todos os restaurantes")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<RestauranteDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String localizacao,
            @RequestParam(required = false) String tipoCozinha
    ) {
        RestauranteDTO restauranteDTO = new RestauranteDTO(null, nome, localizacao, null, null, tipoCozinha);
        var pageable = PageRequest.of(page, size);
        var restaurantes = restauranteService.findAll(pageable, restauranteDTO);
        return new ResponseEntity<>(restaurantes, HttpStatus.OK);
    }

    @Operation(summary = "lista um restaurante por seu id")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        try {
            RestauranteDTO restaurante = restauranteService.findById(id);
            return ResponseEntity.ok(restaurante);
        } catch (ControllerNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "altera um restaurante por seu id")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody RestauranteDTO restauranteDTO) {
        try {
            RestauranteDTO updatedRestaurante = restauranteService.update(id, restauranteDTO);
            return new ResponseEntity<>(updatedRestaurante, HttpStatus.ACCEPTED);
        } catch (ControllerNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "remove um restaurante por seu id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            restauranteService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ControllerNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
