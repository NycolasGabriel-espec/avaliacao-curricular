package br.com.avaliacaocurricular.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AvaliacaoForm {

    @NotNull(message = "Selecione uma disciplina.")
    private Long disciplinaId;

    @NotNull(message = "Selecione uma nota para a clareza da ementa.")
    @Min(value = 1, message = "A nota deve estar entre 1 e 5.")
    @Max(value = 5, message = "A nota deve estar entre 1 e 5.")
    private Integer clarezaEmenta;

    @NotNull(message = "Selecione uma nota para a relevância do conteúdo.")
    @Min(value = 1, message = "A nota deve estar entre 1 e 5.")
    @Max(value = 5, message = "A nota deve estar entre 1 e 5.")
    private Integer relevanciaConteudo;

    @NotNull(message = "Selecione uma nota para a adequação da carga horária.")
    @Min(value = 1, message = "A nota deve estar entre 1 e 5.")
    @Max(value = 5, message = "A nota deve estar entre 1 e 5.")
    private Integer cargaHorariaAdequada;

    @NotNull(message = "Selecione uma nota para o cumprimento dos objetivos.")
    @Min(value = 1, message = "A nota deve estar entre 1 e 5.")
    @Max(value = 5, message = "A nota deve estar entre 1 e 5.")
    private Integer cumprimentoObjetivos;

    @Size(max = 2000, message = "Escreva uma sugestão com até 2.000 caracteres.")
    private String sugestoesMelhoria;

    public Long getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(Long disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public Integer getClarezaEmenta() {
        return clarezaEmenta;
    }

    public void setClarezaEmenta(Integer clarezaEmenta) {
        this.clarezaEmenta = clarezaEmenta;
    }

    public Integer getRelevanciaConteudo() {
        return relevanciaConteudo;
    }

    public void setRelevanciaConteudo(Integer relevanciaConteudo) {
        this.relevanciaConteudo = relevanciaConteudo;
    }

    public Integer getCargaHorariaAdequada() {
        return cargaHorariaAdequada;
    }

    public void setCargaHorariaAdequada(Integer cargaHorariaAdequada) {
        this.cargaHorariaAdequada = cargaHorariaAdequada;
    }

    public Integer getCumprimentoObjetivos() {
        return cumprimentoObjetivos;
    }

    public void setCumprimentoObjetivos(Integer cumprimentoObjetivos) {
        this.cumprimentoObjetivos = cumprimentoObjetivos;
    }

    public String getSugestoesMelhoria() {
        return sugestoesMelhoria;
    }

    public void setSugestoesMelhoria(String sugestoesMelhoria) {
        this.sugestoesMelhoria = sugestoesMelhoria;
    }
}
