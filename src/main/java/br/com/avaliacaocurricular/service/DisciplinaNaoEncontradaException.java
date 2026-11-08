package br.com.avaliacaocurricular.service;

public class DisciplinaNaoEncontradaException extends RuntimeException {

    public DisciplinaNaoEncontradaException() {
        super("Selecione uma disciplina disponível.");
    }
}
