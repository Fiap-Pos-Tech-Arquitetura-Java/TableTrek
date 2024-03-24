package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.AvaliacaoDTO;
import br.com.fiap.postech.tabletrek.dto.ReservaMesaDTO;
import br.com.fiap.postech.tabletrek.dto.UsuarioDTO;
import br.com.fiap.postech.tabletrek.entities.Avaliacao;
import br.com.fiap.postech.tabletrek.entities.ReservaMesa;
import br.com.fiap.postech.tabletrek.repository.AvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AvaliacaoServiceImpl implements AvaliacaoService {
    private final AvaliacaoRepository avaliacaoRepository;
    private final ReservaMesaService reservaMesaService;

    @Autowired
    public AvaliacaoServiceImpl(AvaliacaoRepository avaliacaoRepository, ReservaMesaService reservaMesaService) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.reservaMesaService = reservaMesaService;
    }

    private AvaliacaoDTO toDTO(Avaliacao avaliacao) {
        return toDTO(Boolean.TRUE, avaliacao);
    }

    private AvaliacaoDTO toDTO(Boolean includeId, Avaliacao avaliacao) {
        if (avaliacao == null) {
            return new AvaliacaoDTO(null, null, null, null);
        }
        UUID id = getId(includeId, avaliacao);
        return new AvaliacaoDTO(
                id,
                avaliacao.getReservaMesa().getId(),
                avaliacao.getNota(),
                avaliacao.getComentario()
        );
    }
    private UUID getId(boolean includeId, Avaliacao avaliacao) {
        if (includeId) {
            return avaliacao.getId();
        }
        return null;
    }

    private Avaliacao toEntity(AvaliacaoDTO avaliacaoDTO) {
        if (avaliacaoDTO != null) {
            var reservaMesa = new ReservaMesa();
            reservaMesa.setId(avaliacaoDTO.idReservaMesa());
            return new Avaliacao(
                    reservaMesa,
                    avaliacaoDTO.nota(),
                    avaliacaoDTO.comentario()
            );
        } else {
            return new Avaliacao();
        }
    }

    @Override
    public AvaliacaoDTO save(AvaliacaoDTO avaliacaoDTO, UsuarioDTO usuarioDTO) {
        reservaMesaService.findById(avaliacaoDTO.idReservaMesa(), usuarioDTO);
        Avaliacao avaliacao = toEntity(avaliacaoDTO);
        avaliacao = avaliacaoRepository.save(avaliacao);
        return toDTO(avaliacao);
    }

    @Override
    public Page<AvaliacaoDTO> findAll(Pageable pageable, AvaliacaoDTO avaliacaoDTO) {
        Avaliacao avaliacao = toEntity(avaliacaoDTO);
        avaliacao.setId(null);
        Example<Avaliacao> avaliacaoExample = Example.of(avaliacao);
        Page<Avaliacao> avaliacaos = avaliacaoRepository.findAll(avaliacaoExample, pageable);
        return new PageImpl<>(avaliacaos.stream().map(this::toDTO).toList());
    }
    @Override
    public AvaliacaoDTO findById(UUID id) {
        return toDTO(get(id));
    }

    public Avaliacao get(UUID id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ControllerNotFoundException("Avaliacao não encontrado com o ID: " + id));
    }

    @Override
    public AvaliacaoDTO update(UUID id, AvaliacaoDTO avaliacaoDTO) {
        Avaliacao avaliacao = get(id);
        avaliacao.setNota(avaliacaoDTO.nota());
        avaliacao.setComentario(avaliacaoDTO.comentario());
        avaliacao = avaliacaoRepository.save(avaliacao);
        return toDTO(Boolean.FALSE, avaliacao);
    }
    @Override
    public void delete(UUID id) {
        findById(id);
        avaliacaoRepository.deleteById(id);
    }
}
