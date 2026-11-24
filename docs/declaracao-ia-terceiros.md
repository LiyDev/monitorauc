# Anexo E – Declaração de uso de inteligência artificial e de componentes de terceiros

**Solução:** Monitoria UCSAL  
**Curso e turma:** BES, T1 · **Equipe:** Monitoria UCSAL  
**Integrantes:** Emilly Gonçalves, Graziele Souza, Juracy Neto, Sathya Diaz e Victoria Melo

## 1. Uso de inteligência artificial

**Houve uso de IA?** ( X ) Sim (   ) Não

| Ferramenta ou modelo | Finalidade ou tarefa | Parte afetada | Revisão e validação realizadas |
|---|---|---|---|
| Claude (Anthropic), modelo Claude Opus 5.5 | Ferramenta de apoio no desenvolvimento do código-fonte | `src/main/java/` | Código revisado pela equipe e verificado com `mvn test` e com o teste manual do fluxo principal. |
| Claude (Anthropic), modelo Claude Opus 5.5 | Apoio na escrita dos testes automatizados | `src/test/java/` | Testes executados e conferidos com o comportamento esperado do fluxo. |
| Claude (Anthropic), modelo Claude Opus 5.5 | Correção gramatical dos documentos entregáveis, do README, do roteiro de entrevistas e dos slides do pitch | `README.md`, `docs/`, `demo/` | Conferimos que os textos não afirmam validação com usuários que não aconteceu. |

**Observações sobre o uso de IA:** a IA não foi usada como evidência de validação. O Anexo B registra apenas testes feitos pela equipe; a validação com usuários ainda não foi realizada. Nenhum dado pessoal, credencial ou informação institucional foi enviado à ferramenta. O problema e as hipóteses do Anexo A foram definidos pela equipe antes do uso da IA.

## 2. Componentes e recursos de terceiros

**Houve uso de componentes de terceiros?** ( X ) Sim (   ) Não

| Componente ou recurso | Origem ou link | Versão e licença | Função e alterações realizadas |
|---|---|---|---|
| JUnit Jupiter | https://junit.org/junit5/ | 5.11.4 · EPL-2.0 | Testes automatizados (somente escopo de testes). Sem alterações. |
| Apache Maven e plugins (surefire, jar) | https://maven.apache.org | 3.9+ · Apache 2.0 | Build, execução dos testes e geração do `.jar`. Sem alterações. |
| JDK 21 (`com.sun.net.httpserver`) | https://openjdk.org | 21 · GPLv2 com Classpath Exception | Servidor HTTP embutido usado pela aplicação. Sem alterações. |
| Manual PooaLAB 2026.2 (UCSal) | Material da disciplina | Não se aplica | Estrutura dos anexos, critérios e roteiro do pitch. |

A aplicação não tem nenhuma dependência de execução além do JDK.

## 3. Materiais ou código preexistentes

**Houve material produzido antes do PooaLAB?** (   ) Sim ( X ) Não

**Identificação e origem:** não se aplica.

**Contribuição nova realizada pela equipe:** todo o projeto foi criado durante o PooaLAB 2026.2.

## 4. Declaração da equipe

Declaramos que as informações acima são completas e verdadeiras; que revisamos e testamos as contribuições apoiadas por IA; que identificamos as fontes, licenças e partes preexistentes relevantes; que não apresentamos como autoria própria material de terceiros; e que não compartilhamos dados pessoais, credenciais ou informações restritas de forma indevida.

**Responsável pela submissão:** Sathya Diaz

### Ciência dos integrantes

| Nome completo | Assinatura ou registro de ciência |
|---|---|
| Lilian Emilly Cardoso Gonçalves | Emilly Gonçalves |
| Graziele Cupertino de Souza | Graziele Souza |
| Juracy Barreto Torres Neto | Juracy Neto |
| Sathya Ines Diaz Sarnelli | Sathya Diaz |
| Victoria Ribeiro Carvalho Melo | Victoria Melo |

**Data:** 20/11/2026
