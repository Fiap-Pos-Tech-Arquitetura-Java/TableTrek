package br.com.fiap.postech.tabletrek.controller;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.TokenDTO;
import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import br.com.fiap.postech.tabletrek.services.UsuarioService;
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
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "registra um usuario")
    @PostMapping
    public ResponseEntity<UsuarioDTO> save(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO savedUsuarioDTO = usuarioService.save(usuarioDTO);
        return new ResponseEntity<>(savedUsuarioDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "lista todos os usuarios")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<UsuarioDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long telefone
    ) {
        UsuarioDTO usuarioDTO = new UsuarioDTO(null, nome, email, null, telefone);
        var pageable = PageRequest.of(page, size);
        var usuarios = usuarioService.findAll(pageable, usuarioDTO);
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @Operation(summary = "lista um usuario por seu id")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        try {
            UsuarioDTO usuario = usuarioService.findById(id);
            return ResponseEntity.ok(usuario);
        } catch (ControllerNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "altera um usuario por seu id")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            UsuarioDTO updatedUsuario = usuarioService.update(id, usuarioDTO);
            return new ResponseEntity<>(updatedUsuario, HttpStatus.ACCEPTED);
        } catch (ControllerNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "remove um usuario por seu id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            usuarioService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ControllerNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "realiza o login do usuario")
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            TokenDTO token = usuarioService.login(usuarioDTO);
            return new ResponseEntity<>(token, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new TokenDTO(null, e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }
}
