package br.com.avaliacaocurricular.service;

import br.com.avaliacaocurricular.dto.AvaliacaoForm;
import br.com.avaliacaocurricular.entity.AvaliacaoCurricular;
import br.com.avaliacaocurricular.entity.Disciplina;
import br.com.avaliacaocurricular.repository.AvaliacaoCurricularRepository;
import br.com.avaliacaocurricular.repository.DisciplinaRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvaliacaoService {

    private final DisciplinaRepository disciplinas;
    private final AvaliacaoCurricularRepository avaliacoes;

    public AvaliacaoService(DisciplinaRepository disciplinas, AvaliacaoCurricularRepository avaliacoes) {
        this.disciplinas = disciplinas;
        this.avaliacoes = avaliacoes;
    }

    @Transactional
    public AvaliacaoCurricular registrar(AvaliacaoForm form) {
        Disciplina disciplina = disciplinas.findById(form.getDisciplinaId())
                .orElseThrow(DisciplinaNaoEncontradaException::new);

        String sugestao = form.getSugestoesMelhoria();
        if (sugestao != null) {
            sugestao = sugestao.isBlank() ? null : sugestao.strip();
        }

        return avaliacoes.save(new AvaliacaoCurricular(disciplina, form.getClarezaEmenta(),
                form.getRelevanciaConteudo(), form.getCargaHorariaAdequada(), form.getCumprimentoObjetivos(),
                sugestao, Instant.now()));
    }
}
