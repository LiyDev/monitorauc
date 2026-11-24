# MonitoraUC: Problema e Validação

**Solução:** Monitoria UCSAL  
**Curso e turma:** BES, T1  
**Equipe:** Emilly Gonçalves, Graziele Souza, Juracy Neto, Sathya Diaz e Victoria Melo

---

## Anexo A – Ficha do problema

| Campo | Preenchimento |
|---|---|
| **Público** | Estudantes de graduação da UCSal que procuram monitoria (com foco inicial nos cursos de Informática) e os monitores dessas disciplinas. |
| **Situação** | Na semana anterior a avaliações e entregas, quando a procura por monitoria aumenta. Os horários são divulgados em grupos de mensagem ou avisos em sala. |
| **Problema** | O estudante quer tirar uma dúvida específica, mas não sabe com certeza quando e onde é a monitoria, não consegue garantir vaga e não tem como avisar o assunto antes. O monitor não sabe quantos virão nem o que preparar. |
| **Impacto** | Espera longa ou desistência; sessões lotadas ou vazias; o monitor repete a mesma explicação várias vezes; dúvidas não resolvidas antes da avaliação. |
| **Alternativa atual** | Perguntar no grupo da turma, mandar mensagem direta ao monitor ou simplesmente comparecer e aguardar. |
| **Hipótese crítica** | H1: estudantes já deixaram de ir ou desistiram da monitoria por falta de informação ou por causa da espera.<br>H2: monitores recebem dúvidas repetidas na mesma sessão.<br>H3: estudantes aceitariam reservar a vaga e informar o tópico antes. |
| **Evidência** | Entrevistas curtas com estudantes e monitores; observação de uma sessão de monitoria; teste do MVP com 3 a 5 pessoas (tempo para reservar e entendimento do fluxo). |
| **Recorte** | Reserva de vaga com tópico e painel do monitor com fila agrupada por tópico. Ficam de fora: login, cadastro de sessões e notificações. |

---

## Anexo B – Registro de validação

Registro sem dados pessoais. Uma linha para cada teste ou fonte de evidência.

| Data e perfil | Hipótese | Evidência observada | Decisão |
|---|---|---|---|
| Nov/2026, equipe (testes automatizados, `mvn test`) | Critérios de pronto do MVP (Anexo C) | 24 testes passando: reserva válida, validação de formulário, duplicidade, lotação, sessão encerrada, cancelamento, conclusão de atendimento e fluxo web de ponta a ponta. | Critérios 1 a 8 do Anexo C atendidos no código. |
| Nov/2026, equipe (teste de concorrência) | Reservas simultâneas não podem ultrapassar a capacidade | 200 reservas simultâneas em Virtual Threads numa sessão de 5 vagas: exatamente 5 aceitas e 195 recusadas. | Mantida a trava por sessão no `ReservaService`. |
| Nov/2026, equipe (teste manual do fluxo, `demo/roteiro-demonstracao.md`) | O fluxo principal funciona do início ao fim | Reserva, código, painel agrupado, alternância de ordem e conclusão do grupo funcionando; capturas em `docs/img/`. | Fluxo pronto para a demonstração. |
| Pendente: estudantes e monitores | H1, H2 e H3 | Entrevistas e teste com usuários ainda não realizados. | Aplicar na mostra: pedir a 3 a 5 estudantes que reservem sem ajuda (meta: menos de 60 s) e mostrar o painel a pelo menos um monitor. |

### Síntese dos aprendizados

As evidências que temos hoje são técnicas: o MVP cumpre os critérios de pronto e trata a concorrência corretamente. As hipóteses sobre o comportamento de estudantes e monitores (H1, H2 e H3) vêm da vivência da equipe e ainda precisam ser testadas com usuários.

### Mudanças realizadas no problema ou na solução

O recorte foi reduzido a um fluxo completo (reserva, confirmação e atendimento) em vez de várias telas parciais. A regra de vagas ficou no serviço, com trava por sessão, para não depender da interface. Próximo passo: validar H1 a H3 durante a mostra e registrar os resultados aqui.

---

## Como validar (roteiro)

### Entrevista com estudante (3 a 5 min)

1. Você já usou monitoria aqui? Em qual disciplina?
2. Como ficou sabendo do horário da última vez?
3. Já desistiu de ir ou de esperar? Por quê?
4. Se pudesse reservar a vaga e dizer o assunto antes, usaria?

### Entrevista com monitor (5 min)

1. Como você divulga os horários hoje?
2. Acontece de várias pessoas terem a mesma dúvida? Com que frequência?
3. Saber antes os assuntos mudaria sua preparação?

### Teste do MVP (5 min por pessoa)

Pedir: "Reserve uma vaga na monitoria de POOA para tirar dúvida sobre Streams." Não ajudar, cronometrar e anotar onde a pessoa hesitou.
