package br.com.fiap.postech.tabletrek.controller;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.ReservaMesaDTO;
import br.com.fiap.postech.tabletrek.services.ReservaMesaService;
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
@RequestMapping("/reservaMesa")
public class ReservaMesaController {

    private final ReservaMesaService reservaMesaService;
    @Autowired
    public ReservaMesaController(ReservaMesaService reservaMesaService) {
        this.reservaMesaService = reservaMesaService;
    }

    @Operation(summary = "registra um reservaMesa")
    @PostMapping
    public ResponseEntity<ReservaMesaDTO> save(@Valid @RequestBody ReservaMesaDTO reservaMesaDTO) {
        ReservaMesaDTO savedReservaMesaDTO = reservaMesaService.save(reservaMesaDTO);
        return new ResponseEntity<>(savedReservaMesaDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "lista todos os reservaMesas")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<ReservaMesaDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID idRestaurante,
            @RequestParam(required = false) UUID idUsuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  LocalDateTime horario
    ) {
        ReservaMesaDTO reservaMesaDTO = new ReservaMesaDTO(null, idRestaurante, idUsuario, horario, null);
        var pageable = PageRequest.of(page, size);
        var reservaMesas = reservaMesaService.findAll(pageable, reservaMesaDTO);
        return new ResponseEntity<>(reservaMesas, HttpStatus.OK);
    }

    @Operation(summary = "lista um reservaMesa por seu id")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        try {
            ReservaMesaDTO reservaMesa = reservaMesaService.findById(id);
            return ResponseEntity.ok(reservaMesa);
        } catch (ControllerNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "finaliza um reservaMesa por seu id")
    @PutMapping("/{id}")
    public ResponseEntity<?> finaliza(@PathVariable UUID id) {
        try {
            ReservaMesaDTO updatedReservaMesa = reservaMesaService.finaliza(id);
            return new ResponseEntity<>(updatedReservaMesa, HttpStatus.ACCEPTED);
        } catch (ControllerNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "remove um reservaMesa por seu id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            reservaMesaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ControllerNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
