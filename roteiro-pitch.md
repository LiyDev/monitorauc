# Anexo D – Roteiro do pitch (5 min)

**Solução:** Monitoria UCSAL · Slides: [`pitch.pdf`](pitch.pdf)

| Parte | Conteúdo | Anotações da equipe |
|---|---|---|
| **Abertura** | Véspera de prova: você vai à monitoria com uma dúvida de Strategy e encontra a sala lotada, ou vazia porque ninguém sabia o horário. O MonitoraUC organiza isso. | Graziele. Começar pela situação do dia a dia, sem citar números que ainda não medimos. |
| **Público e contexto** | Estudantes que procuram monitoria e monitores. | Graziele. Explicar quem enfrenta o problema e em que momento ele aparece. |
| **Evidências** | Ainda não fizemos as entrevistas com usuários. O que já temos é evidência técnica: 24 testes automatizados, entre eles um de concorrência com 200 tentativas de reserva para 5 vagas, em que exatamente 5 são aceitas. | Emilly. Deixar claro que os testes mostram que o sistema funciona, mas não provam que os estudantes vão usar. A validação com usuários acontece na mostra. |
| **Proposta** | O estudante garante a vaga e informa o tópico; o monitor chega sabendo quantos virão e agrupa as dúvidas iguais. Recorte: reserva e painel. | Juracy. Login, cadastro de sessões, banco de dados e notificações ficaram de fora. |
| **Demonstração** | 1) Horários, filtrar POOA. 2) Reservar como "Camila", tópico Padrões de projeto. 3) Mostrar o código. 4) Painel do monitor com o grupo de 3 estudantes. 5) "Concluir grupo" e mostrar os contadores mudando. | Sathya opera e Juracy narra. Nomes e matrículas são fictícios. Reiniciar o servidor antes. |
| **Decisões e limites** | Java 21 sem dependências de execução. Strategy na fila, Observer nos eventos, Template Method nas páginas, Virtual Threads e trava por sessão. Limitações: sem login e dados em memória. | Victoria. Explicar por que cada conceito foi usado. O JUnit 5 só aparece no escopo de testes. |
| **Aprendizado** | Recortar a ideia em um fluxo completo e tratar a concorrência na regra de negócio, com teste. Próximo passo: testar com estudantes e monitores reais, depois login institucional, cadastro de sessões e persistência. | Victoria. Encerrar com o link do repositório e abrir para perguntas. |

## Perguntas prováveis

- **Por que não usaram banco de dados?** Recorte do MVP; os repositórios são interfaces, e trocar por banco não muda as regras.
- **E se dois alunos clicarem ao mesmo tempo na última vaga?** Trava por sessão no `ReservaService`; teste `reservasSimultaneasNuncaUltrapassamACapacidade`.
- **Qualquer um pode abrir o painel do monitor?** Sim, é uma limitação declarada; o próximo passo é login.
- **Onde está o Strategy?** `fila/PoliticaFila.java` e as duas implementações.
- **O que a IA fez?** Ver o Anexo E; todos devem conseguir explicar qualquer classe.
