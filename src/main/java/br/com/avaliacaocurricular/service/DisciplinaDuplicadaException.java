package br.com.avaliacaocurricular.service;

public class DisciplinaDuplicadaException extends RuntimeException {

    public DisciplinaDuplicadaException(Throwable cause) {
        super("Já existe uma disciplina com este código no período informado.", cause);
    }
}
