CREATE TABLE tb_disciplina (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    codigo VARCHAR(30) NOT NULL,
    semestralidade VARCHAR(6) NOT NULL,
    CONSTRAINT uk_disciplina_codigo_periodo UNIQUE (codigo, semestralidade)
);

CREATE INDEX idx_disciplina_semestralidade ON tb_disciplina (semestralidade);

CREATE TABLE tb_avaliacao_curricular (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    disciplina_id BIGINT NOT NULL,
    clareza_ementa INTEGER NOT NULL CHECK (clareza_ementa BETWEEN 1 AND 5),
    relevancia_conteudo INTEGER NOT NULL CHECK (relevancia_conteudo BETWEEN 1 AND 5),
    carga_horaria_adequada INTEGER NOT NULL CHECK (carga_horaria_adequada BETWEEN 1 AND 5),
    cumprimento_objetivos INTEGER NOT NULL CHECK (cumprimento_objetivos BETWEEN 1 AND 5),
    sugestoes_melhoria VARCHAR(2000),
    data_submissao TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_avaliacao_disciplina FOREIGN KEY (disciplina_id) REFERENCES tb_disciplina (id)
);

CREATE INDEX idx_avaliacao_disciplina ON tb_avaliacao_curricular (disciplina_id);
