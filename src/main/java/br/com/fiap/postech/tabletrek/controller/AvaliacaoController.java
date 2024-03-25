package br.com.fiap.postech.tabletrek.controller;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.AvaliacaoDTO;
import br.com.fiap.postech.tabletrek.security.SecurityHelper;
import br.com.fiap.postech.tabletrek.services.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/avaliacao")
public class AvaliacaoController {
    private final AvaliacaoService avaliacaoService;
    private final SecurityHelper securityHelper;
    @Autowired
    public AvaliacaoController(AvaliacaoService avaliacaoService, SecurityHelper securityHelper) {
        this.avaliacaoService = avaliacaoService;
        this.securityHelper = securityHelper;
    }

    @Operation(summary = "registra um avaliacao")
    @PostMapping
    public ResponseEntity<AvaliacaoDTO> save(@Valid @RequestBody AvaliacaoDTO avaliacaoDTO) {
        AvaliacaoDTO savedAvaliacaoDTO = avaliacaoService.save(avaliacaoDTO, securityHelper.getUsuarioLogado());
        return new ResponseEntity<>(savedAvaliacaoDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "lista todos as avaliações")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<AvaliacaoDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID idReservaMesa) {
        AvaliacaoDTO avaliacaoDTO = new AvaliacaoDTO(null, idReservaMesa, null, null);
        var pageable = PageRequest.of(page, size);
        var avaliacoes = avaliacaoService.findAll(pageable, avaliacaoDTO);
        return new ResponseEntity<>(avaliacoes, HttpStatus.OK);
    }

    @Operation(summary = "lista uma avaliacao por seu id")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        try {
            AvaliacaoDTO avaliacao = avaliacaoService.findById(id);
            return ResponseEntity.ok(avaliacao);
        } catch (ControllerNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "atualiza uma avaliacao por seu id")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody AvaliacaoDTO avaliacaoDTO) {
        try {
            AvaliacaoDTO updatedAvaliacao = avaliacaoService.update(id, avaliacaoDTO, securityHelper.getUsuarioLogado());
            return new ResponseEntity<>(updatedAvaliacao, HttpStatus.ACCEPTED);
        } catch (ControllerNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "remove uma avaliacao por seu id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            avaliacaoService.delete(id, securityHelper.getUsuarioLogado());
            return ResponseEntity.noContent().build();
        } catch (ControllerNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
