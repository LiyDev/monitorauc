package br.ucsal.monitorauc.web;

import br.ucsal.monitorauc.dominio.Disciplina;
import br.ucsal.monitorauc.dominio.SessaoMonitoria;
import br.ucsal.monitorauc.servico.ReservaService;

import java.util.Comparator;
import java.util.List;

import static br.ucsal.monitorauc.web.Html.esc;

public class PaginaInicio extends PaginaBase {

    private final ReservaService servico;

    public PaginaInicio(ReservaService servico) {
        super("/");
        this.servico = servico;
    }

    @Override
    protected Resposta get(Requisicao req) {
        String filtro = req.param("disciplina", "");
        List<SessaoMonitoria> todas = servico.sessoesDisponiveis();
        List<Disciplina> disciplinas = todas.stream()
                .map(SessaoMonitoria::disciplina)
                .distinct()
                .sorted(Comparator.comparing(Disciplina::nome))
                .toList();
        List<SessaoMonitoria> visiveis = todas.stream()
                .filter(s -> filtro.isEmpty() || s.disciplina().codigo().equals(filtro))
                .toList();

        StringBuilder html = new StringBuilder("""
                <h1>Horários de monitoria</h1>
                <p class="sub">Escolha um horário, reserve sua vaga e diga qual é a dúvida.
                O monitor recebe a lista antes e organiza o atendimento.</p>
                """);

        html.append("<nav class=\"filtros\" aria-label=\"Filtrar por disciplina\">")
                .append(filtroLink("", "Todas", filtro));
        disciplinas.forEach(d -> html.append(filtroLink(d.codigo(), d.nome(), filtro)));
        html.append("</nav>");

        if (visiveis.isEmpty()) {
            html.append("<div class=\"alerta aviso\">Nenhum horário disponível no momento.</div>");
        }
        html.append("<div class=\"grade\">");
        for (SessaoMonitoria s : visiveis) {
            int vagas = servico.vagasRestantes(s);
            html.append("<article class=\"card\">")
                    .append("<span class=\"etiqueta\">").append(esc(s.disciplina().codigo())).append("</span>")
                    .append("<h3>").append(esc(s.disciplina().nome())).append("</h3>")
                    .append("<p class=\"meta\">").append(esc(Html.dataHora(s.inicio())))
                    .append(" – ").append(esc(Html.hora(s.fim()))).append("</p>")
                    .append("<p class=\"meta\">").append(esc(s.local())).append("</p>")
                    .append("<p class=\"meta\">").append(esc(s.monitor().nome())).append("</p>");
            if (vagas > 0) {
                html.append("<p class=\"vagas\">").append(vagas).append(" de ").append(s.capacidade())
                        .append(vagas == 1 ? " vaga livre" : " vagas livres").append("</p>")
                        .append("<a class=\"botao\" href=\"/sessao?id=").append(esc(s.id()))
                        .append("\">Reservar vaga</a>");
            } else {
                html.append("<p class=\"vagas lotada\">Lotada (").append(s.capacidade())
                        .append(" de ").append(s.capacidade()).append(" vagas ocupadas)</p>");
            }
            html.append("</article>");
        }
        html.append("</div>");
        return Resposta.ok("Horários", html.toString());
    }

    private static String filtroLink(String codigo, String rotulo, String atual) {
        String href = codigo.isEmpty() ? "/" : "/?disciplina=" + esc(codigo);
        return "<a href=\"" + href + "\" aria-current=\"" + codigo.equals(atual) + "\">" + esc(rotulo) + "</a>";
    }
}
