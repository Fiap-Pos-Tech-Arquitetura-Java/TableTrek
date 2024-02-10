package br.com.fiap.postech.tabletrek.services;

import br.com.fiap.postech.tabletrek.dto.RestauranteDTO;
import br.com.fiap.postech.tabletrek.entities.Restaurante;
import br.com.fiap.postech.tabletrek.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RestauranteService {
    @Autowired
    private RestauranteRepository restauranteRepository;

    private RestauranteDTO toDTO(Restaurante restaurante) {
        return toDTO(Boolean.TRUE, restaurante);
    }

    private RestauranteDTO toDTO(Boolean includeId, Restaurante restaurante) {
        return new RestauranteDTO(
                getId(includeId, restaurante),
                restaurante.getNome()
        );
    }
    private String getId(boolean includeId, Restaurante restaurante) {
        if (includeId) {
            return restaurante.getId();
        }
        return null;
    }

    private Restaurante toEntity(RestauranteDTO restauranteDTO) {
        return new Restaurante(restauranteDTO.nome());
    }

    public RestauranteDTO save(RestauranteDTO restauranteDTO) {
        Restaurante restaurante = toEntity(restauranteDTO);
        restaurante = restauranteRepository.save(restaurante);
        return toDTO(restaurante);
    }

    public void save(Restaurante compraTempo) {
        restauranteRepository.save(compraTempo);
    }
}
