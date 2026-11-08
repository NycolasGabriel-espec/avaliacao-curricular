package br.com.avaliacaocurricular;

import br.com.avaliacaocurricular.dto.IndicadorDisciplina;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:avaliacao_integracao;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.flyway.locations=classpath:db/migration",
    "app.admin.username=coordenacao-teste",
    "app.admin.password=senha-exclusiva-dos-testes"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AvaliacaoCurricularIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JdbcTemplate jdbc;

    private long disciplinaAnterior;
    private long disciplinaAtual;
    private long disciplinaSemAvaliacoes;

    @BeforeEach
    void preparaDisciplinas() {
        jdbc.update("delete from tb_avaliacao_curricular");
        jdbc.update("delete from tb_disciplina");
        disciplinaAnterior = insereDisciplina("ADS101", "Algoritmos", "2026.1");
        disciplinaAtual = insereDisciplina("ADS101", "Algoritmos", "2026.2");
        disciplinaSemAvaliacoes = insereDisciplina("ADS203", "Banco de Dados", "2026.2");
    }

    @Test
    void abreFormularioPublicoComDisciplinasDisponiveis() throws Exception {
        mvc.perform(get("/"))
            .andExpect(redirectedUrl("/avaliacoes/nova"));

        mvc.perform(get("/avaliacoes/nova"))
            .andExpect(status().isOk())
            .andExpect(unauthenticated())
            .andExpect(view().name("avaliacoes/formulario"))
            .andExpect(model().attributeExists("avaliacao"))
            .andExpect(model().attribute("disciplinas", hasSize(3)))
            .andExpect(content().string(containsString("action=\"/avaliacoes/salvar\"")))
            .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    void salvaAvaliacaoAnonimaEExibeConfirmacao() throws Exception {
        mvc.perform(avaliacao(disciplinaAtual, 1, 5, 3, 4)
                .with(csrf())
                .param("sugestoesMelhoria", "Incluir mais exercícios práticos."))
            .andExpect(unauthenticated())
            .andExpect(redirectedUrl("/avaliacoes/sucesso"));

        var registro = jdbc.queryForMap("""
            select disciplina_id, clareza_ementa, relevancia_conteudo, carga_horaria_adequada,
                   cumprimento_objetivos, sugestoes_melhoria, data_submissao
            from tb_avaliacao_curricular
            """);
        assertThat(registro)
            .containsEntry("disciplina_id", disciplinaAtual)
            .containsEntry("clareza_ementa", 1)
            .containsEntry("relevancia_conteudo", 5)
            .containsEntry("carga_horaria_adequada", 3)
            .containsEntry("cumprimento_objetivos", 4)
            .containsEntry("sugestoes_melhoria", "Incluir mais exercícios práticos.");
        assertThat(registro.get("data_submissao")).isNotNull();

        mvc.perform(get("/avaliacoes/sucesso"))
            .andExpect(status().isOk())
            .andExpect(view().name("avaliacoes/sucesso"));
    }

    @Test
    void rejeitaNotasAusentesForaDaEscalaOuNaoInteiras() throws Exception {
        mvc.perform(post("/avaliacoes/salvar")
                .with(csrf())
                .param("disciplinaId", Long.toString(disciplinaAtual))
                .param("relevanciaConteudo", "0")
                .param("cargaHorariaAdequada", "6")
                .param("cumprimentoObjetivos", "2.5"))
            .andExpect(status().isOk())
            .andExpect(view().name("avaliacoes/formulario"))
            .andExpect(model().attribute("disciplinas", hasSize(3)))
            .andExpect(model().attributeHasFieldErrors("avaliacao", "clarezaEmenta", "relevanciaConteudo",
                "cargaHorariaAdequada", "cumprimentoObjetivos"));

        assertThat(totalAvaliacoes()).isZero();
    }

    @Test
    void rejeitaDisciplinaInexistente() throws Exception {
        mvc.perform(avaliacao(Long.MAX_VALUE, 3, 3, 3, 3).with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("avaliacoes/formulario"))
            .andExpect(model().attributeHasFieldErrors("avaliacao", "disciplinaId"));

        assertThat(totalAvaliacoes()).isZero();
    }

    @Test
    void rejeitaSugestaoMaiorQueOLimite() throws Exception {
        mvc.perform(avaliacao(disciplinaAtual, 3, 3, 3, 3)
                .with(csrf())
                .param("sugestoesMelhoria", "a".repeat(2001)))
            .andExpect(status().isOk())
            .andExpect(view().name("avaliacoes/formulario"))
            .andExpect(model().attributeHasFieldErrors("avaliacao", "sugestoesMelhoria"));

        assertThat(totalAvaliacoes()).isZero();
    }

    @Test
    void protegeSubmissaoPublicaContraCsrf() throws Exception {
        mvc.perform(avaliacao(disciplinaAtual, 3, 3, 3, 3))
            .andExpect(status().isForbidden());

        assertThat(totalAvaliacoes()).isZero();
    }

    @Test
    void protegeAreaAdministrativaEPermiteLoginELogoutDaCoordenacao() throws Exception {
        mvc.perform(get("/admin/dashboard"))
            .andExpect(status().is3xxRedirection())
            .andExpect(result -> assertThat(URI.create(result.getResponse().getRedirectedUrl()).getPath())
                .isEqualTo("/admin/entrar"));
        mvc.perform(get("/admin/disciplinas").with(user("aluno").roles("ALUNO")))
            .andExpect(status().isForbidden());

        mvc.perform(get("/admin/entrar"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/login"))
            .andExpect(content().string(containsString("action=\"/admin/entrar\"")))
            .andExpect(content().string(containsString("name=\"_csrf\"")));

        mvc.perform(post("/admin/entrar")
                .with(csrf())
                .param("username", "coordenacao-teste")
                .param("password", "senha-incorreta"))
            .andExpect(unauthenticated())
            .andExpect(redirectedUrl("/admin/entrar?error"));

        var login = mvc.perform(post("/admin/entrar")
                .with(csrf())
                .param("username", "coordenacao-teste")
                .param("password", "senha-exclusiva-dos-testes"))
            .andExpect(authenticated().withRoles("COORDENACAO"))
            .andExpect(redirectedUrl("/admin/dashboard"))
            .andReturn();
        var sessao = (MockHttpSession) login.getRequest().getSession(false);
        assertThat(sessao).isNotNull();

        mvc.perform(get("/admin/dashboard").session(sessao))
            .andExpect(status().isOk());
        mvc.perform(post("/admin/sair").session(sessao).with(csrf()))
            .andExpect(unauthenticated())
            .andExpect(redirectedUrl("/admin/entrar?logout"));
        assertThat(sessao.isInvalid()).isTrue();
    }

    @Test
    void calculaMediasEIncluiDisciplinasSemRespostasNoPeriodoSelecionado() throws Exception {
        mvc.perform(avaliacao(disciplinaAtual, 1, 2, 3, 4).with(csrf()))
            .andExpect(redirectedUrl("/avaliacoes/sucesso"));
        mvc.perform(avaliacao(disciplinaAtual, 4, 3, 4, 5).with(csrf()))
            .andExpect(redirectedUrl("/avaliacoes/sucesso"));
        mvc.perform(avaliacao(disciplinaAnterior, 5, 5, 5, 5).with(csrf()))
            .andExpect(redirectedUrl("/avaliacoes/sucesso"));

        var dashboard = mvc.perform(get("/admin/dashboard")
                .with(user("coordenacao").roles("COORDENACAO"))
                .param("periodo", "2026.2"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/dashboard"))
            .andExpect(model().attribute("periodo", "2026.2"))
            .andExpect(model().attribute("totalAvaliacoes", 2L))
            .andExpect(model().attribute("disciplinasAvaliadas", 1L))
            .andExpect(model().attribute("totalDisciplinas", 2))
            .andReturn();

        var atributos = dashboard.getModelAndView().getModel();
        assertThat((List<?>) atributos.get("periodos"))
            .extracting(Object::toString)
            .containsExactlyInAnyOrder("2026.1", "2026.2");
        assertThat(atributos.get("indicadores")).isEqualTo(List.of(
            new IndicadorDisciplina(disciplinaAtual, "ADS101", "Algoritmos", "2026.2", 2L, 2.5, 2.5, 3.5, 4.5),
            new IndicadorDisciplina(disciplinaSemAvaliacoes, "ADS203", "Banco de Dados", "2026.2", 0L,
                null, null, null, null)
        ));
    }

    @Test
    void cadastraDisciplinaERejeitaCodigoDuplicadoNoMesmoPeriodo() throws Exception {
        var cadastro = mvc.perform(post("/admin/disciplinas")
                .with(user("coordenacao").roles("COORDENACAO"))
                .with(csrf())
                .param("nome", "Redes de Computadores")
                .param("codigo", "ADS301")
                .param("semestralidade", "2026.2"))
            .andExpect(redirectedUrl("/admin/disciplinas"))
            .andExpect(flash().attribute("disciplinaCriada", true))
            .andReturn();

        mvc.perform(get("/admin/disciplinas")
                .with(user("coordenacao").roles("COORDENACAO"))
                .flashAttrs(cadastro.getFlashMap()))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/disciplinas"))
            .andExpect(model().attribute("disciplinas", hasSize(4)))
            .andExpect(content().string(containsString("Redes de Computadores")));

        mvc.perform(get("/avaliacoes/nova"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("disciplinas", hasSize(4)));

        mvc.perform(post("/admin/disciplinas")
                .with(user("coordenacao").roles("COORDENACAO"))
                .with(csrf())
                .param("nome", "Outra disciplina")
                .param("codigo", "ADS301")
                .param("semestralidade", "2026.2"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/disciplinas"))
            .andExpect(model().attributeHasFieldErrors("disciplinaForm", "codigo"));

        var cadastrada = jdbc.queryForObject("select nome from tb_disciplina where codigo = ? and semestralidade = ?",
            String.class, "ADS301", "2026.2");
        assertThat(cadastrada).isEqualTo("Redes de Computadores");
    }

    @Test
    void bancoAplicaEscalaDeNotasEIntegridadeReferencial() {
        assertThatThrownBy(() -> insereAvaliacaoDireta(disciplinaAtual, 0))
            .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> insereAvaliacaoDireta(Long.MAX_VALUE, 3))
            .isInstanceOf(DataIntegrityViolationException.class);
        assertThat(totalAvaliacoes()).isZero();
    }

    private long insereDisciplina(String codigo, String nome, String periodo) {
        jdbc.update("insert into tb_disciplina (codigo, nome, semestralidade) values (?, ?, ?)", codigo, nome, periodo);
        return jdbc.queryForObject("select id from tb_disciplina where codigo = ? and semestralidade = ?",
            Long.class, codigo, periodo);
    }

    private void insereAvaliacaoDireta(long disciplinaId, int clareza) {
        jdbc.update("""
            insert into tb_avaliacao_curricular (disciplina_id, clareza_ementa, relevancia_conteudo,
                carga_horaria_adequada, cumprimento_objetivos, data_submissao)
            values (?, ?, 3, 3, 3, current_timestamp)
            """, disciplinaId, clareza);
    }

    private long totalAvaliacoes() {
        return jdbc.queryForObject("select count(*) from tb_avaliacao_curricular", Long.class);
    }

    private MockHttpServletRequestBuilder avaliacao(long disciplinaId, int clareza, int relevancia, int carga,
                                                    int objetivos) {
        return post("/avaliacoes/salvar")
            .param("disciplinaId", Long.toString(disciplinaId))
            .param("clarezaEmenta", Integer.toString(clareza))
            .param("relevanciaConteudo", Integer.toString(relevancia))
            .param("cargaHorariaAdequada", Integer.toString(carga))
            .param("cumprimentoObjetivos", Integer.toString(objetivos));
    }
}
