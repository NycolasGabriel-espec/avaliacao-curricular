package br.com.avaliacaocurricular.service;

import br.com.avaliacaocurricular.dto.DisciplinaForm;
import br.com.avaliacaocurricular.entity.Disciplina;
import br.com.avaliacaocurricular.repository.DisciplinaRepository;
import java.sql.SQLException;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinas;

    public DisciplinaService(DisciplinaRepository disciplinas) {
        this.disciplinas = disciplinas;
    }

    @Transactional(readOnly = true)
    public List<Disciplina> listar() {
        return disciplinas.findAllByOrderBySemestralidadeDescNomeAscCodigoAsc();
    }

    @Transactional
    public Disciplina criar(DisciplinaForm form) {
        Disciplina disciplina = new Disciplina(form.getNome(), form.getCodigo(), form.getSemestralidade());

        try {
            return disciplinas.saveAndFlush(disciplina);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getMostSpecificCause() instanceof SQLException sql && "23505".equals(sql.getSQLState())) {
                throw new DisciplinaDuplicadaException(ex);
            }
            throw ex;
        }
    }
}
