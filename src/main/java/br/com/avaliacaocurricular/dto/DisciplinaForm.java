package br.com.avaliacaocurricular.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class DisciplinaForm {

    @NotBlank(message = "Informe o nome da disciplina.")
    @Size(max = 150, message = "O nome deve ter até 150 caracteres.")
    private String nome;

    @NotBlank(message = "Informe o código acadêmico.")
    @Size(max = 30, message = "O código deve ter até 30 caracteres.")
    private String codigo;

    @NotBlank(message = "Informe o período letivo.")
    @Pattern(regexp = "[0-9]{4}\\.[12]", message = "Use o formato AAAA.1 ou AAAA.2.")
    private String semestralidade;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = limpar(nome);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = limpar(codigo);
    }

    public String getSemestralidade() {
        return semestralidade;
    }

    public void setSemestralidade(String semestralidade) {
        this.semestralidade = limpar(semestralidade);
    }

    private static String limpar(String texto) {
        return texto == null ? null : texto.strip();
    }
}
