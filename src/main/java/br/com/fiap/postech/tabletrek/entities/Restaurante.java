package br.com.fiap.postech.tabletrek.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tb_restaurante")
public class Restaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String nome;

    public Restaurante() {
        super();
    }

    public Restaurante(String nome) {
        this();
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Restaurante restaurante = (Restaurante) o;

        return id.equals(restaurante.id);
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
