package br.com.avaliacaocurricular.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_disciplina")
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 30)
    private String codigo;

    @Column(nullable = false, length = 6)
    private String semestralidade;

    @OneToMany(mappedBy = "disciplina")
    private List<AvaliacaoCurricular> avaliacoes = new ArrayList<>();

    protected Disciplina() {
    }

    public Disciplina(String nome, String codigo, String semestralidade) {
        this.nome = nome;
        this.codigo = codigo;
        this.semestralidade = semestralidade;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getSemestralidade() {
        return semestralidade;
    }
}
