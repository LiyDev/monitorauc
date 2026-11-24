# Roteiro de demonstração e contingência

## Antes da mostra

1. `./executar.sh` (ou `java -jar target/monitorauc.jar`) — **reinicie** antes de apresentar para ter dados limpos.
2. Abra duas abas: <http://localhost:8080/> e <http://localhost:8080/monitor/sessao?id=S1>.
3. Zoom do navegador em 125–150% para a banca enxergar.
4. Desative notificações do computador.

O app **não precisa de internet**: roda tudo local.

## Fluxo principal (≈ 1min40)

| # | Ação | O que a banca deve ver |
|---|---|---|
| 1 | Na aba de horários, clicar no filtro **Programação Orientada a Objetos Avançada** | Só sessões de POOA; "5 de 8 vagas livres" no Lab 3 |
| 2 | **Reservar vaga** (Lab 3) | Formulário com dados da sessão |
| 3 | Matrícula `20260042`, nome `Camila`, tópico **Padrões de projeto (GoF)**, dúvida "Quando usar Strategy ou Template Method?" → **Confirmar** | "Reserva confirmada!" e código `MON-XXXXX` |
| 4 | Aba do monitor → **↻ Atualizar** | Grupo **Padrões de projeto (GoF) — 3 estudantes**, Camila incluída; 4 aguardando |
| 5 | Clicar **Ordem de chegada** e depois **Agrupar por tópico** | Mesma fila, duas organizações (Strategy) |
| 6 | **Concluir grupo (3)** | "Atendimento registrado"; 1 aguardando, 3 atendidos |

## Testes manuais de erro (se perguntarem)

| Caso | Como | Esperado |
|---|---|---|
| Dados inválidos | Matrícula `12ab`, nome vazio | Lista de erros, dados preservados |
| Duplicidade | Reservar de novo com `20260042` na mesma sessão | "Esta matrícula já possui uma reserva nesta sessão." |
| Lotação | Sessão de Cálculo I (3 vagas, 2 ocupadas): reservar 2 vezes com matrículas diferentes | Segunda recusada: "Sessão lotada" |
| Cancelar | Na página da reserva, informar a matrícula e **Cancelar reserva** | Situação "Cancelada" e vaga liberada |
| Concorrência | `mvn test -Dtest=ReservaServiceTest` | 200 tentativas simultâneas, exatamente 5 aceitas |

## Contingência

- **Computador da sala falhou:** rodar em um notebook da equipe (testado previamente) — só precisa de JDK 21.
- **Porta 8080 ocupada:** `./executar.sh 9090`.
- **Tudo falhou:** usar o roteiro visual em `docs/img/` (capturas 1 a 5, na ordem do fluxo) ou os slides de demonstração em `docs/pitch.pdf`. O roteiro visual é apoio; o MVP executável é o entregável.
