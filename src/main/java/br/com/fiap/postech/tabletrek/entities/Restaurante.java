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

    @ManyToOne()
    @JoinColumn(name="id_usuario", nullable=false)
    private Usuario usuario;

    @Column(name = "nome", nullable = false)
    private String nome;
    @Column(name = "localizacao", nullable = false)
    private String localizacao;
    @Column(name = "horariofuncionamento", nullable = false)
    private String horarioFuncionamento;
    @Column(name = "capacidade", nullable = false)
    private Integer capacidade;
    @Column(name = "tipocozinha", nullable = false)
    private String tipoCozinha;

    public Restaurante() {
        super();
    }

    public Restaurante(Usuario usuario, String nome, String localizacao, String horarioFuncionamento, Integer capacidade, String tipoCozinha) {
        this();
        this.id = UUID.randomUUID();
        this.usuario = usuario;
        this.nome = nome;
        this.localizacao = localizacao;
        this.horarioFuncionamento = horarioFuncionamento;
        this.capacidade = capacidade;
        this.tipoCozinha = tipoCozinha;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public String getTipoCozinha() {
        return tipoCozinha;
    }

    public void setTipoCozinha(String tipoCozinha) {
        this.tipoCozinha = tipoCozinha;
    }
}
