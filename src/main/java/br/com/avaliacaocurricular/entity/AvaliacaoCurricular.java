package br.com.avaliacaocurricular.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "tb_avaliacao_curricular")
public class AvaliacaoCurricular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disciplina_id", nullable = false, updatable = false)
    private Disciplina disciplina;

    @Column(name = "clareza_ementa", nullable = false, updatable = false)
    private int clarezaEmenta;

    @Column(name = "relevancia_conteudo", nullable = false, updatable = false)
    private int relevanciaConteudo;

    @Column(name = "carga_horaria_adequada", nullable = false, updatable = false)
    private int cargaHorariaAdequada;

    @Column(name = "cumprimento_objetivos", nullable = false, updatable = false)
    private int cumprimentoObjetivos;

    @Column(name = "sugestoes_melhoria", length = 2000, updatable = false)
    private String sugestoesMelhoria;

    @Column(name = "data_submissao", nullable = false, updatable = false)
    private Instant dataSubmissao;

    protected AvaliacaoCurricular() {
    }

    public AvaliacaoCurricular(Disciplina disciplina, int clarezaEmenta, int relevanciaConteudo,
                               int cargaHorariaAdequada, int cumprimentoObjetivos,
                               String sugestoesMelhoria, Instant dataSubmissao) {
        this.disciplina = disciplina;
        this.clarezaEmenta = clarezaEmenta;
        this.relevanciaConteudo = relevanciaConteudo;
        this.cargaHorariaAdequada = cargaHorariaAdequada;
        this.cumprimentoObjetivos = cumprimentoObjetivos;
        this.sugestoesMelhoria = sugestoesMelhoria;
        this.dataSubmissao = dataSubmissao;
    }

    public Long getId() {
        return id;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public int getClarezaEmenta() {
        return clarezaEmenta;
    }

    public int getRelevanciaConteudo() {
        return relevanciaConteudo;
    }

    public int getCargaHorariaAdequada() {
        return cargaHorariaAdequada;
    }

    public int getCumprimentoObjetivos() {
        return cumprimentoObjetivos;
    }

    public String getSugestoesMelhoria() {
        return sugestoesMelhoria;
    }

    public Instant getDataSubmissao() {
        return dataSubmissao;
    }
}
