package br.com.avaliacaocurricular.dto;

public record IndicadorDisciplina(Long id, String codigo, String nome, String semestralidade, long total,
                                  Double clarezaEmenta, Double relevanciaConteudo,
                                  Double cargaHorariaAdequada, Double cumprimentoObjetivos) {
}
