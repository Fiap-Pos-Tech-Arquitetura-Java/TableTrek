package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.dto.ReservaMesaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 *  ReservaMesaService
 */
public interface ReservaMesaService {

    ReservaMesaDTO save(ReservaMesaDTO reservaMesaDTO);

    Page<ReservaMesaDTO> findAll(Pageable pageable, ReservaMesaDTO reservaMesaDTODTO);

    ReservaMesaDTO findById(UUID id);

    ReservaMesaDTO finaliza(UUID id);

    void delete(UUID id);
}
