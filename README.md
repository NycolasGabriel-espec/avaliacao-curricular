# Web App de Avaliação Curricular

Aplicação para coletar a percepção dos alunos sobre disciplinas e acompanhar os resultados por período letivo. O formulário público recebe quatro notas de 1 a 5 e uma sugestão opcional. A área da coordenação permite cadastrar disciplinas e comparar os indicadores.

## Executar localmente

Com um JDK 25 ou superior instalado, execute o comando abaixo na raiz do projeto.

```bash
./mvnw spring-boot:run
```

No Windows, use `mvnw.cmd`. O Maven Wrapper baixa o Maven e as dependências na primeira execução.

- [Formulário de avaliação](http://localhost:8080/avaliacoes/nova)
- [Área da coordenação](http://localhost:8080/admin/entrar)

O perfil padrão `dev` escuta na interface local, carrega disciplinas demonstrativas e guarda os dados entre reinicializações em `data/avaliacao-curricular.mv.db`. Use **coordenacao** e **demo-local** para acessar a área administrativa local. É possível substituir esses valores com `APP_ADMIN_USERNAME` e `APP_ADMIN_PASSWORD`.

## Fluxo de avaliação

1. Selecione uma disciplina e confira seu código e período.
2. Dê uma nota inteira de 1 a 5 para clareza da ementa, relevância do conteúdo, adequação da carga horária e cumprimento dos objetivos.
3. Se desejar, escreva uma sugestão com até 2.000 caracteres. Ao enviar, a aplicação valida as respostas e registra o horário da submissão.
4. Na coordenação, filtre os resultados por período. O painel mostra o volume de respostas, as médias por critério e as disciplinas que ainda aguardam avaliações.

O cadastro da coordenação identifica cada oferta pelo código e pelo período letivo no formato `AAAA.1` ou `AAAA.2`. É possível oferecer a mesma disciplina em períodos diferentes. As médias consideram as respostas da oferta selecionada. Quando uma oferta está sem respostas, as médias ficam em branco.

O formulário dispensa identificação do aluno e o banco armazena apenas a disciplina, as notas, a sugestão e o horário. Como o envio é público, uma pessoa pode avaliar mais de uma vez. A sugestão é armazenada para uso institucional e o painel exibe indicadores agregados. A avaliação de docentes e a consolidação por curso dependem da definição desses fluxos e dados.

## Testar e empacotar

```bash
./mvnw test
./mvnw clean verify
```

Os testes de integração usam H2 em memória e exercitam a submissão, a persistência, as validações, os indicadores e o acesso da coordenação. O JAR executável é gerado em `target/avaliacao-curricular-0.1.0-SNAPSHOT.jar`.

## Executar com PostgreSQL

Crie um banco PostgreSQL e disponibilize um usuário com permissões para criar e alterar as tabelas e operar os dados no esquema usado pelo aplicativo. Preencha as variáveis a partir de `.env.example` no ambiente do processo. Em um shell POSIX, os comandos abaixo carregam as variáveis de uma cópia privada chamada `.env`.

```bash
set -a
. ./.env
set +a
java -jar target/avaliacao-curricular-0.1.0-SNAPSHOT.jar
```

O perfil `prod` exige a URL JDBC, as credenciais do banco e as credenciais da coordenação. O Flyway aplica as migrações no início e o Hibernate valida o esquema. O banco de produção começa sem disciplinas. Após entrar, cadastre as ofertas que devem aparecer no formulário.

Sirva a aplicação por HTTPS. Atrás de um proxy reverso, encaminhe o protocolo original e restrinja o acesso direto à porta da aplicação. O perfil de produção usa cookie de sessão seguro.

## Arquitetura

Monólito em Java e Spring Boot 3. Os controladores Spring MVC recebem os formulários Thymeleaf e delegam a validação de negócio aos serviços. A persistência usa Spring Data JPA, com H2 em desenvolvimento e PostgreSQL em produção. As tabelas `tb_disciplina` e `tb_avaliacao_curricular` representam o relacionamento de uma oferta de disciplina para muitas avaliações.

A interface usa Bootstrap 5 e Chart.js, ambos servidos pelo aplicativo. O Spring Security exige a conta da coordenação para acessar o painel e o cadastro, e aplica proteção CSRF aos formulários.

| Método | Rota | Acesso |
| --- | --- | --- |
| GET | `/avaliacoes/nova` | Formulário público |
| POST | `/avaliacoes/salvar` | Submissão pública |
| GET | `/avaliacoes/sucesso` | Confirmação do envio |
| GET, POST | `/admin/entrar` | Autenticação |
| GET | `/admin/dashboard` | Indicadores da coordenação |
| GET, POST | `/admin/disciplinas` | Cadastro da coordenação |
| POST | `/admin/sair` | Encerramento da sessão |

## Licença

A licença de uso e distribuição do projeto ainda será definida.
