package br.com.avaliacaocurricular.service;

import br.com.avaliacaocurricular.dto.IndicadorDisciplina;
import br.com.avaliacaocurricular.repository.DisciplinaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final DisciplinaRepository disciplinas;

    public DashboardService(DisciplinaRepository disciplinas) {
        this.disciplinas = disciplinas;
    }

    public List<String> periodos() {
        return disciplinas.listarPeriodos();
    }

    public List<IndicadorDisciplina> indicadores(String periodo) {
        return disciplinas.calcularIndicadores(periodo == null ? "" : periodo.strip());
    }
}
