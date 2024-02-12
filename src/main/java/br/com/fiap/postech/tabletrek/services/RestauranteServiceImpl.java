package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.controller.exception.ControllerNotFoundException;
import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.entities.Restaurante;
import br.com.fiap.postech.tabletrek.repository.RestauranteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RestauranteServiceImpl implements RestauranteService {
    private final RestauranteRepository restauranteRepository;

    @Autowired
    public RestauranteServiceImpl(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    private RestauranteDTO toDTO(Restaurante restaurante) {
        return toDTO(Boolean.TRUE, restaurante);
    }

    private RestauranteDTO toDTO(Boolean includeId, Restaurante restaurante) {
        if (restaurante == null) {
            return new RestauranteDTO(null, null);
        }
        UUID id = getId(includeId, restaurante);
        return new RestauranteDTO(
                id,
                restaurante.getNome()
        );
    }
    private UUID getId(boolean includeId, Restaurante restaurante) {
        if (includeId) {
            return restaurante.getId();
        }
        return null;
    }

    private Restaurante toEntity(RestauranteDTO restauranteDTO) {
        if (restauranteDTO != null) {
            return new Restaurante(restauranteDTO.nome());
        } else {
            return new Restaurante();
        }
    }

    @Override
    public RestauranteDTO save(RestauranteDTO restauranteDTO) {
        Restaurante restaurante = toEntity(restauranteDTO);
        restaurante = restauranteRepository.save(restaurante);
        return toDTO(restaurante);
    }

    public void save(Restaurante compraTempo) {
        restauranteRepository.save(compraTempo);
    }
    @Override
    public List<RestauranteDTO> findAll() {
        List<Restaurante> restaurantes = restauranteRepository.findAll();
        return restaurantes.stream().map(this::toDTO).toList();
    }
    @Override
    public RestauranteDTO findById(UUID id) {
        return toDTO(get(id));
    }

    public Restaurante get(UUID id) {
        return restauranteRepository.findById(id)
                .orElseThrow(() -> new ControllerNotFoundException("Restaurante não encontrado com o ID: " + id));
    }

    @Override
    public RestauranteDTO update(UUID id, RestauranteDTO restauranteDTO) {
        Restaurante restaurante = get(id);
        restaurante.setNome(restauranteDTO.nome());
        restaurante = restauranteRepository.save(restaurante);
        return toDTO(Boolean.FALSE, restaurante);
    }
    @Override
    public void delete(UUID id) {
        findById(id);
        restauranteRepository.deleteById(id);
    }
}
