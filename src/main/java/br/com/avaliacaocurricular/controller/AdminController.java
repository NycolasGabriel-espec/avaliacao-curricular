package br.com.avaliacaocurricular.controller;

import br.com.avaliacaocurricular.dto.DisciplinaForm;
import br.com.avaliacaocurricular.dto.IndicadorDisciplina;
import br.com.avaliacaocurricular.service.DashboardService;
import br.com.avaliacaocurricular.service.DisciplinaDuplicadaException;
import br.com.avaliacaocurricular.service.DisciplinaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DashboardService dashboardService;
    private final DisciplinaService disciplinaService;

    public AdminController(DashboardService dashboardService, DisciplinaService disciplinaService) {
        this.dashboardService = dashboardService;
        this.disciplinaService = disciplinaService;
    }

    @GetMapping
    public String inicio() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/entrar")
    public String entrar() {
        return "admin/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "") String periodo, Model model) {
        String periodoSelecionado = periodo.strip();
        List<IndicadorDisciplina> indicadores = dashboardService.indicadores(periodoSelecionado);
        model.addAttribute("indicadores", indicadores);
        model.addAttribute("periodos", dashboardService.periodos());
        model.addAttribute("periodo", periodoSelecionado);
        model.addAttribute("totalAvaliacoes", indicadores.stream().mapToLong(IndicadorDisciplina::total).sum());
        model.addAttribute("disciplinasAvaliadas", indicadores.stream().filter(indicador -> indicador.total() > 0).count());
        model.addAttribute("totalDisciplinas", indicadores.size());
        return "admin/dashboard";
    }

    @GetMapping("/disciplinas")
    public String disciplinas(Model model) {
        model.addAttribute("disciplinaForm", new DisciplinaForm());
        return mostrarDisciplinas(model);
    }

    @PostMapping("/disciplinas")
    public String criarDisciplina(@Valid @ModelAttribute DisciplinaForm disciplinaForm,
                                  BindingResult bindingResult, Model model,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return mostrarDisciplinas(model);
        }

        try {
            disciplinaService.criar(disciplinaForm);
        } catch (DisciplinaDuplicadaException exception) {
            bindingResult.rejectValue("codigo", "disciplina.duplicada",
                    "Já existe uma disciplina com esse código no período informado.");
            return mostrarDisciplinas(model);
        }

        redirectAttributes.addFlashAttribute("disciplinaCriada", true);
        return "redirect:/admin/disciplinas";
    }

    private String mostrarDisciplinas(Model model) {
        model.addAttribute("disciplinas", disciplinaService.listar());
        return "admin/disciplinas";
    }
}
