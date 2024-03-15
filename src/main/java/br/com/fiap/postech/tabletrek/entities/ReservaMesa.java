package br.com.fiap.postech.tabletrek.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_reserva_mesa")
public class ReservaMesa {
    @Id
    @Column(name = "id", nullable = false)
    //@GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne()
    @JoinColumn(name="id_restaurante", nullable=false)
    private Restaurante restaurante;
    @ManyToOne()
    @JoinColumn(name="id_usuario", nullable=false)
    private Usuario usuario;
    @Column(name = "horario", nullable = false)
    private LocalDateTime horario;
    @Column(name = "status", nullable = false)
    private String status;

    public ReservaMesa() {
        super();
    }

    public ReservaMesa(Restaurante restaurante, Usuario usuario, LocalDateTime horario, String status) {
        this();
        this.id = UUID.randomUUID();
        this.restaurante = restaurante;
        this.usuario = usuario;
        this.horario = horario;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReservaMesa reservaMesa = (ReservaMesa) o;

        return id.equals(reservaMesa.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
