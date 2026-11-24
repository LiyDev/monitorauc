# MonitoraUC

Agendamento de monitorias com fila organizada por tópico de dúvida.
O estudante vê os horários de monitoria, reserva uma vaga e informa o tópico da dúvida.
O monitor recebe a fila antes da sessão, agrupa dúvidas iguais para explicar de uma vez
e registra os atendimentos concluídos. MVP desenvolvido para o POOALAB 2026.2 (UCSal).

**Repositório:** <https://github.com/LiyDev/monitorauc>

---

## Problema, público e proposta de valor

**Público:** estudantes de graduação que procuram monitoria e os monitores dessas disciplinas.

**Problema (hipótese a validar):** para estudantes que precisam de monitoria, os horários
costumam ser divulgados de forma dispersa (grupos de mensagem, avisos em sala) e não há
como garantir vaga nem avisar antes qual é a dúvida. A consequência é sessão lotada
ou vazia, espera longa e monitor explicando o mesmo assunto várias vezes, sem preparação.
Hoje esse público tenta resolver por mensagens no grupo da turma ou simplesmente
comparecendo e aguardando.

**Proposta de valor:** o estudante garante a vaga em menos de um minuto e chega
sabendo que será atendido; o monitor chega sabendo quantas pessoas virão e quais
assuntos preparar, e atende dúvidas iguais em grupo.

Detalhes e evidências: [`docs/problema-validacao.md`](docs/problema-validacao.md).

## Integrantes e turma

Curso: Bacharelado em Engenharia de Software (BES) · Turma T1 · Programação Orientada a Objetos Avançada

| Nome completo | Papel principal |
|---|---|
| Lilian Emilly Cardoso Gonçalves | Evidências e validação |
| Graziele Cupertino de Souza | Problema e público |
| Juracy Barreto Torres Neto | Proposta e recorte do MVP |
| Sathya Ines Diaz Sarnelli | Demonstração e submissão |
| Victoria Ribeiro Carvalho Melo | Decisões técnicas e limites |

## Tecnologias e justificativas

| Tecnologia | Por quê |
|---|---|
| **Java 21** | Linguagem da disciplina; Records, `sealed`, pattern matching em `switch` e **Virtual Threads**. |
| **Servidor HTTP embutido do JDK** (`com.sun.net.httpserver`) | Zero dependências de execução: roda em qualquer máquina com JDK 21, sem banco nem servidor externo. Facilita a reprodutibilidade e a contingência na mostra. |
| **HTML renderizado no servidor + CSS** | Funciona sem JavaScript, em qualquer navegador e no celular; formulários acessíveis. |
| **JUnit 5** (apenas testes) | Testes automatizados de regras, concorrência e fluxo web de ponta a ponta. |
| **Maven** (opcional) | Build e testes padronizados. Há também scripts que usam só o `javac`. |

Persistência em memória (o MVP reinicia limpo a cada execução, com dados de demonstração).

## Pré-requisitos

- **JDK 21** ou superior (`java -version` deve mostrar 21+). Ex.: Eclipse Temurin 21.
- **Maven 3.9+** — apenas para rodar os testes ou gerar o `.jar`. Não é necessário para só executar.
- Navegador atualizado.

## Instalação e execução

### Opção A — só com o JDK (mais simples)

```bash
# Linux / macOS
./executar.sh            # porta 8080
./executar.sh 9090       # outra porta

# Windows
executar.bat
```

### Opção B — com Maven

```bash
mvn clean package        # compila, roda os testes e gera target/monitorauc.jar
java -jar target/monitorauc.jar           # porta 8080, com reservas de exemplo
java -jar target/monitorauc.jar 9090 --vazio   # outra porta, sem reservas de exemplo
```

Depois abra:
- Estudante: <http://localhost:8080/>
- Monitor: <http://localhost:8080/monitor>

Para encerrar: `Ctrl+C`.

## Dados de demonstração

Gerados automaticamente ao iniciar (`src/main/java/.../demo/DadosDemonstracao.java`).
**Tudo é fictício** — nenhum dado real de estudante ou monitor.

- 4 disciplinas (POOA, Estrutura de Dados, Banco de Dados, Cálculo I) e 5 sessões nos
  próximos dias úteis (as datas são calculadas a partir de hoje, então a demo funciona em qualquer dia).
- 5 reservas de exemplo (sessões S1 e S5) para o painel do monitor não começar vazio.
- Matrículas de teste: qualquer número de 6 a 12 dígitos (ex.: `20260042`).
- Não há login nem senha no MVP.

## Testes

```bash
mvn test
```

24 testes em `src/test/java`:

| Classe | O que verifica |
|---|---|
| `ReservaServiceTest` | Reserva válida, validação, tópico inválido, duplicidade, lotação, sessão encerrada, cancelamento, conclusão de atendimento, eventos (Observer) e **200 reservas simultâneas em Virtual Threads numa sessão de 5 vagas → exatamente 5 aceitas**. |
| `PoliticaFilaTest` | Estratégias de fila: agrupamento por tópico (maior grupo primeiro, desempate por chegada) e ordem de chegada. |
| `ValidadorTest` | Validação por anotações + reflexão, acumulando todas as mensagens. |
| `FluxoWebTest` | Sobe o servidor em porta livre e executa o fluxo principal via HTTP: reservar → ver código → painel do monitor → concluir; erros de formulário; cancelamento; escape de HTML (XSS); 404. |

Roteiro de teste manual: [`demo/roteiro-demonstracao.md`](demo/roteiro-demonstracao.md).

## Roteiro curto do fluxo principal

1. Abra <http://localhost:8080/> → filtre **Programação Orientada a Objetos Avançada**.
2. Clique **Reservar vaga** na sessão do Laboratório 3.
3. Preencha matrícula `20260042`, nome `Camila`, tópico **Padrões de projeto (GoF)**, uma dúvida → **Confirmar reserva**.
4. Veja o **código** da reserva (ex.: `MON-AB12C`) e a vaga a menos.
5. Abra **Área do monitor** → **Abrir painel** da mesma sessão.
6. Veja Camila no grupo **Padrões de projeto (GoF) — 3 estudantes**. Clique **Concluir grupo (3)**.
7. Os contadores mudam: *aguardando* diminui, *atendidos* aumenta.

Capturas de tela: `docs/img/`.

## Arquitetura e relação com POOA (bônus)

```
web/        Páginas HTTP (Template Method), servidor com Virtual Threads
servico/    ReservaService (regras + concorrência), eventos (Observer)
fila/       Políticas de organização da fila (Strategy)
dominio/    Disciplina, SessaoMonitoria, Reserva, PedidoReserva (records)
validacao/  Anotações @Obrigatorio/@Tamanho/@Padrao + Validador por reflexão
repositorio/ Interfaces + implementações em memória (DIP)
demo/       Fábrica de dados de demonstração
```

| Conteúdo de POOA | Onde | Por que foi usado (não é enfeite) |
|---|---|---|
| **Strategy (GoF)** | `fila/PoliticaFila`, `AgrupadaPorTopico`, `OrdemDeChegada` | O monitor troca a organização da fila sem mudar o painel nem o serviço. |
| **Observer (GoF)** | `servico/ObservadorReserva`, `RegistroAtividades` | O serviço publica eventos sem conhecer quem consome (painel de atividades; futura notificação). |
| **Template Method (GoF)** | `web/PaginaBase` | Fluxo comum de requisição/erros fica em um só lugar; páginas implementam só `get`/`post`. |
| **SOLID — DIP / OCP / SRP** | `repositorio/*`, `ReservaService` recebe `Clock` e repositórios por construtor | Permite testar com relógio fixo e trocar a memória por banco sem alterar regras. |
| **Collections e Streams** | `AgrupadaPorTopico` (`groupingBy` + `Comparator`), contagens no serviço | Agrupar e ordenar dúvidas é o núcleo da proposta de valor. |
| **Concorrência e Virtual Threads** | `web/Servidor` (`newVirtualThreadPerTaskExecutor`), bloqueio por sessão em `ReservaService` | Evita *overbooking* quando muitos reservam ao mesmo tempo; comprovado por teste. |
| **Reflexão e anotações** | `validacao/*`, `dominio/PedidoReserva` | Regras de formulário declaradas no próprio record e verificadas genericamente. |

## Limitações conhecidas

- **Sem autenticação:** qualquer pessoa com o link acessa a área do monitor. O cancelamento exige código + matrícula, mas não há login.
- **Dados em memória:** tudo é perdido ao reiniciar. Não há banco de dados.
- **Sessões fixas:** horários vêm dos dados de demonstração; não há tela para o monitor cadastrar sessões.
- **Sem notificações** (e-mail/WhatsApp); o Observer está pronto para isso, mas não implementado.
- **Sem proteção CSRF** nos formulários (aceitável apenas em demonstração local).
- **Fuso horário:** usa o relógio da máquina onde o servidor roda.
- A validação do problema com o público é limitada — ver `docs/problema-validacao.md`.

## Próximos passos

1. Login institucional para monitores (e só para eles acessarem o painel).
2. Cadastro de sessões pelo próprio monitor.
3. Persistência em banco (ex.: PostgreSQL) implementando os mesmos repositórios.
4. Lembrete automático ao estudante antes da sessão (novo `ObservadorReserva`).

## Fontes, licenças, terceiros e uso de IA

- JUnit 5 (EPL 2.0) — somente testes. Nenhuma outra biblioteca.
- Declaração completa (Anexo E): [`docs/declaracao-ia-terceiros.md`](docs/declaracao-ia-terceiros.md).

## Documentos

- [`docs/problema-validacao.md`](docs/problema-validacao.md) — Anexos A e B
- [`docs/definicao-mvp.md`](docs/definicao-mvp.md) — Anexo C
- [`docs/roteiro-pitch.md`](docs/roteiro-pitch.md) — Anexo D
- [`docs/pitch.pdf`](docs/pitch.pdf) — slides do pitch
- [`docs/declaracao-ia-terceiros.md`](docs/declaracao-ia-terceiros.md) — Anexo E
- [`demo/roteiro-demonstracao.md`](demo/roteiro-demonstracao.md) — roteiro e contingência
