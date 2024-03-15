package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.ReservaMesaDTO;
import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.entities.ReservaMesa;
import br.com.fiap.postech.tabletrek.entities.Restaurante;
import br.com.fiap.postech.tabletrek.entities.Usuario;
import br.com.fiap.postech.tabletrek.repository.ReservaMesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
@Service
public class ReservaMesaServiceImpl implements ReservaMesaService {
    private final ReservaMesaRepository reservaMesaRepository;
    private final RestauranteService restauranteService;

    @Autowired
    public ReservaMesaServiceImpl(ReservaMesaRepository reservaMesaRepository, RestauranteService restauranteService) {
        this.reservaMesaRepository = reservaMesaRepository;
        this.restauranteService = restauranteService;
    }

    private ReservaMesaDTO toDTO(ReservaMesa reservaMesa) {
        return toDTO(Boolean.TRUE, reservaMesa);
    }

    private ReservaMesaDTO toDTO(Boolean includeId, ReservaMesa reservaMesa) {
        if (reservaMesa == null) {
            return new ReservaMesaDTO(null, null, null, null, null);
        }
        UUID id = getId(includeId, reservaMesa);
        return new ReservaMesaDTO(
                id,
                reservaMesa.getRestaurante().getId(),
                reservaMesa.getUsuario().getId(),
                reservaMesa.getHorario(),
                reservaMesa.getStatus()
        );
    }
    private UUID getId(boolean includeId, ReservaMesa reservaMesa) {
        if (includeId) {
            return reservaMesa.getId();
        }
        return null;
    }

    private ReservaMesa toEntity(ReservaMesaDTO reservaMesaDTO) {
        if (reservaMesaDTO != null) {
            Restaurante restaurante = new Restaurante();
            restaurante.setId(reservaMesaDTO.idRestaurante());
            Usuario usuario = new Usuario();
            usuario.setId(reservaMesaDTO.idUsuario());
            return new ReservaMesa(
                    restaurante,
                    usuario,
                    reservaMesaDTO.horario(),
                    reservaMesaDTO.status()
            );
        } else {
            return new ReservaMesa();
        }
    }

    @Override
    public ReservaMesaDTO save(ReservaMesaDTO reservaMesaDTO) {
        RestauranteDTO restauranteDTO = restauranteService.findById(reservaMesaDTO.idRestaurante());
        Restaurante restaurante = new Restaurante();
        restaurante.setId(restauranteDTO.id());
        Long quantidadeReservaPorRestaurantePorHorario = reservaMesaRepository.countByRestauranteAndHorario(restaurante, reservaMesaDTO.horario());
        if (quantidadeReservaPorRestaurantePorHorario + 1 > restauranteDTO.capacidade()) {
            throw new ControllerNotFoundException("Não foi possivel reservar uma mesa nesse restaurante por falta de disponibilidade para o horário informado");
        }
        ReservaMesa reservaMesa = toEntity(reservaMesaDTO);
        reservaMesa = reservaMesaRepository.save(reservaMesa);
        return toDTO(reservaMesa);
    }

    @Override
    public Page<ReservaMesaDTO> findAll(Pageable pageable, ReservaMesaDTO reservaMesaDTO) {
        ReservaMesa reservaMesa = toEntity(reservaMesaDTO);
        reservaMesa.setId(null);
        Example<ReservaMesa> reservaMesaExample = Example.of(reservaMesa);
        Page<ReservaMesa> reservaMesas = reservaMesaRepository.findAll(reservaMesaExample, pageable);
        return new PageImpl<>(reservaMesas.stream().map(this::toDTO).toList());
    }
    @Override
    public ReservaMesaDTO findById(UUID id) {
        return toDTO(get(id));
    }

    public ReservaMesa get(UUID id) {
        return reservaMesaRepository.findById(id)
                .orElseThrow(() -> new ControllerNotFoundException("ReservaMesa não encontrado com o ID: " + id));
    }

    @Override
    public ReservaMesaDTO finaliza(UUID id) {
        ReservaMesa reservaMesa = get(id);
        reservaMesa.setStatus("FINALIZADA");
        reservaMesa = reservaMesaRepository.save(reservaMesa);
        return toDTO(Boolean.FALSE, reservaMesa);
    }
    @Override
    public void delete(UUID id) {
        findById(id);
        reservaMesaRepository.deleteById(id);
    }
}
