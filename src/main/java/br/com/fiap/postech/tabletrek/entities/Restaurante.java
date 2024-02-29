package br.com.fiap.postech.tabletrek.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tb_restaurante")
public class Restaurante {
    @Id
    @Column(name = "id", nullable = false)
    //@GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "nome", nullable = false)
    private String nome;
    @Column(name = "localizacao", nullable = false)
    private String localizacao;
    @Column(name = "horariofuncionamento", nullable = false)
    private String horarioFuncionamento;
    @Column(name = "capacidade", nullable = false)
    private Integer capacidade;

    public Restaurante() {
        super();
    }

    public Restaurante(String nome, String localizacao, String horarioFuncionamento, Integer capacidade) {
        this();
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.localizacao = localizacao;
        this.horarioFuncionamento = horarioFuncionamento;
        this.capacidade = capacidade;
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

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(String horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }
}
