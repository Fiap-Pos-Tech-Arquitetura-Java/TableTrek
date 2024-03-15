package br.com.fiap.postech.tabletrek.repository;

import br.com.fiap.postech.tabletrek.entities.ReservaMesa;
import br.com.fiap.postech.tabletrek.entities.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ReservaMesaRepository extends JpaRepository<ReservaMesa, UUID> {
    long countByRestauranteAndHorario(Restaurante restaurante, LocalDateTime horario);
}
