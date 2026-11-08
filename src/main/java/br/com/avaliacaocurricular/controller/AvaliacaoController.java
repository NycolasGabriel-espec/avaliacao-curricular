package br.com.avaliacaocurricular.controller;

import br.com.avaliacaocurricular.dto.AvaliacaoForm;
import br.com.avaliacaocurricular.service.AvaliacaoService;
import br.com.avaliacaocurricular.service.DisciplinaNaoEncontradaException;
import br.com.avaliacaocurricular.service.DisciplinaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AvaliacaoController {

    private final AvaliacaoService avaliacoes;
    private final DisciplinaService disciplinas;

    public AvaliacaoController(AvaliacaoService avaliacoes, DisciplinaService disciplinas) {
        this.avaliacoes = avaliacoes;
        this.disciplinas = disciplinas;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/avaliacoes/nova";
    }

    @GetMapping("/avaliacoes/nova")
    public String nova(Model model) {
        model.addAttribute("avaliacao", new AvaliacaoForm());
        return formulario(model);
    }

    @PostMapping("/avaliacoes/salvar")
    public String salvar(@Valid @ModelAttribute("avaliacao") AvaliacaoForm form,
                         BindingResult erros, Model model) {
        if (erros.hasErrors()) {
            return formulario(model);
        }

        try {
            avaliacoes.registrar(form);
            return "redirect:/avaliacoes/sucesso";
        } catch (DisciplinaNaoEncontradaException ex) {
            erros.rejectValue("disciplinaId", "disciplina.indisponivel", ex.getMessage());
            return formulario(model);
        }
    }

    @GetMapping("/avaliacoes/sucesso")
    public String sucesso() {
        return "avaliacoes/sucesso";
    }

    private String formulario(Model model) {
        model.addAttribute("disciplinas", disciplinas.listar());
        return "avaliacoes/formulario";
    }
}
