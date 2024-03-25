package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.dto.ReservaMesaDTO;
import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import br.com.fiap.postech.tabletrek.entities.ReservaMesa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 *  ReservaMesaService
 */
public interface ReservaMesaService {

    ReservaMesaDTO save(UsuarioDTO usuarioDTO, ReservaMesaDTO reservaMesaDTO);

    Page<ReservaMesaDTO> findAll(Pageable pageable, UsuarioDTO usuarioDTO, ReservaMesaDTO reservaMesaDTO);

    ReservaMesaDTO findById(UUID id, UsuarioDTO usuarioDTO);

    ReservaMesa get(UUID id);

    ReservaMesaDTO finaliza(UUID id, UsuarioDTO usuarioDTO);

    void delete(UUID id, UsuarioDTO usuarioDTO);
}
