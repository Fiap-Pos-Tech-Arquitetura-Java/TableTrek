package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.dto.AvaliacaoDTO;
import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 *  AvaliacaoService
 */
public interface AvaliacaoService {

    AvaliacaoDTO save(AvaliacaoDTO avaliacaoDTO, UsuarioDTO usuarioDTO);

    Page<AvaliacaoDTO> findAll(Pageable pageable, AvaliacaoDTO avaliacaoDTODTO);

    AvaliacaoDTO findById(UUID id);

    AvaliacaoDTO update(UUID id, AvaliacaoDTO avaliacaoDTO);

    void delete(UUID id);
}
