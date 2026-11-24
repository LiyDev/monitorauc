# Anexo C – Definição do MVP

**Solução:** Monitoria UCSAL

**Usuário principal:** o estudante que precisa de monitoria e o monitor.

**Resultado principal:** o estudante garante uma vaga numa sessão de monitoria informando o tópico da dúvida, e o monitor vê a fila organizada por tópico e registra os atendimentos.

**Fluxo de ponta a ponta:** escolher a sessão → informar matrícula, primeiro nome e tópico (entrada) → validação e checagem de vagas e duplicidade sob concorrência (processamento) → código de reserva e vaga ocupada; a dúvida aparece agrupada no painel do monitor (resultado) → o monitor conclui o atendimento individual ou do grupo (resultado verificável nos contadores).

**Critérios de pronto:**

1. Reserva válida gera código MON-XXXXX e reduz as vagas exibidas.
2. Dados inválidos mostram todas as mensagens sem perder o que foi digitado.
3. Sessão lotada, encerrada ou matrícula repetida na mesma sessão são recusadas com mensagem clara.
4. Reservas simultâneas nunca ultrapassam a capacidade (teste automatizado com 200 Virtual Threads).
5. O painel agrupa por tópico (maior grupo primeiro) e permite alternar para ordem de chegada.
6. Concluir atendimento atualiza os contadores de aguardando e atendidos.
7. O estudante consegue consultar e cancelar a reserva com código e matrícula, e a vaga é liberada.
8. `mvn test` passa (24 testes) e o app roda com `./executar.sh` em máquina limpa com JDK 21.

**Fora do escopo:** login e autenticação; cadastro de sessões pelo monitor; banco de dados; notificações; integração com sistemas da UCSal; aplicativo móvel nativo.

**Maior risco:** os estudantes não adotarem a reserva e continuarem só aparecendo; os monitores não abrirem o painel antes da sessão.

**Teste principal:** pedir a 3 a 5 estudantes que reservem uma vaga sem ajuda (meta: menos de 60 s, sem erro) e mostrar o painel a pelo menos um monitor, perguntando se ele usaria para se preparar.

**Limitações conhecidas:** área do monitor sem login; dados em memória (somem ao reiniciar); sessões vêm dos dados de demonstração; sem proteção CSRF. Mais detalhes no README.
