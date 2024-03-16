package br.com.fiap.postech.tabletrek.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_avaliacao")
public class Avaliacao {
    @Id
    @Column(name = "id", nullable = false)
    //@GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne()
    @JoinColumn(name="id_reserva_mesa", nullable=false)
    private ReservaMesa reservaMesa;

    @Column(name = "nota", nullable = false)
    private Integer nota;
    @Column(name = "comentario", nullable = false)
    private String comentario;

    public Avaliacao() {
        super();
    }

    public Avaliacao(ReservaMesa reservaMesa, Integer nota, String comentario) {
        this();
        this.id = UUID.randomUUID();
        this.reservaMesa = reservaMesa;
        this.nota = nota;
        this.comentario = comentario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Avaliacao reservaMesa = (Avaliacao) o;

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

    public ReservaMesa getReservaMesa() {
        return reservaMesa;
    }

    public void setReservaMesa(ReservaMesa reservaMesa) {
        this.reservaMesa = reservaMesa;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
