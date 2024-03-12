package br.com.fiap.postech.tabletrek.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "tb_usuario")
public class Usuario {
    @Id
    @Column(name = "id", nullable = false)
    //@GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "nome", nullable = false)
    private String nome;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "senha", nullable = false)
    private String senha;
    @Column(name = "telefone", nullable = false)
    private Long telefone;

    public Usuario() {
        super();
    }

    public Usuario(String nome, String email, String senha, Long telefone) {
        this();
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Usuario usuario = (Usuario) o;

        return id.equals(usuario.id);
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Long getTelefone() {
        return telefone;
    }

    public void setTelefone(Long telefone) {
        this.telefone = telefone;
    }
}
