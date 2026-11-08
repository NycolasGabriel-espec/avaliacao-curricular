package br.com.avaliacaocurricular.repository;

import br.com.avaliacaocurricular.dto.IndicadorDisciplina;
import br.com.avaliacaocurricular.entity.Disciplina;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    List<Disciplina> findAllByOrderBySemestralidadeDescNomeAscCodigoAsc();

    @Query("select distinct d.semestralidade from Disciplina d order by d.semestralidade desc")
    List<String> listarPeriodos();

    @Query("""
            select new br.com.avaliacaocurricular.dto.IndicadorDisciplina(
                d.id, d.codigo, d.nome, d.semestralidade, count(a.id),
                avg(a.clarezaEmenta), avg(a.relevanciaConteudo),
                avg(a.cargaHorariaAdequada), avg(a.cumprimentoObjetivos))
            from Disciplina d
            left join d.avaliacoes a
            where (:periodo = '' or d.semestralidade = :periodo)
            group by d.id, d.codigo, d.nome, d.semestralidade
            order by d.semestralidade desc, d.nome asc, d.codigo asc
            """)
    List<IndicadorDisciplina> calcularIndicadores(@Param("periodo") String periodo);
}
