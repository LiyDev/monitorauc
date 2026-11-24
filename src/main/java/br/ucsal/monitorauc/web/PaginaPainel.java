package br.ucsal.monitorauc.web;

import br.ucsal.monitorauc.dominio.Reserva;
import br.ucsal.monitorauc.dominio.SessaoMonitoria;
import br.ucsal.monitorauc.fila.CatalogoPoliticas;
import br.ucsal.monitorauc.fila.GrupoAtendimento;
import br.ucsal.monitorauc.fila.PoliticaFila;
import br.ucsal.monitorauc.servico.EventoReserva;
import br.ucsal.monitorauc.servico.RegistroAtividades;
import br.ucsal.monitorauc.servico.ReservaService;
import br.ucsal.monitorauc.servico.ReservaService.PainelSessao;

import java.util.List;

import static br.ucsal.monitorauc.web.Html.esc;

public class PaginaPainel extends PaginaBase {

    private final ReservaService servico;
    private final CatalogoPoliticas politicas;
    private final RegistroAtividades atividades;

    public PaginaPainel(ReservaService servico, CatalogoPoliticas politicas, RegistroAtividades atividades) {
        super("/monitor/sessao");
        this.servico = servico;
        this.politicas = politicas;
        this.atividades = atividades;
    }

    @Override
    protected Resposta get(Requisicao req) {
        PoliticaFila politica = politicas.porId(req.param("ordem", ""));
        PainelSessao painel = servico.painel(req.param("id"), politica);
        SessaoMonitoria s = painel.sessao();
        String base = "/monitor/sessao?id=" + esc(s.id());

        StringBuilder html = new StringBuilder();
        if ("1".equals(req.param("ok"))) {
            html.append("<div class=\"alerta sucesso\" role=\"status\">Atendimento registrado.</div>");
        }
        html.append("<p><a href=\"/monitor\">← Todas as sessões</a></p>")
                .append("<h1>").append(esc(s.disciplina().nome())).append("</h1>")
                .append("<p class=\"sub\">").append(esc(Html.dataHora(s.inicio()))).append(" – ")
                .append(esc(Html.hora(s.fim()))).append(" · ").append(esc(s.local())).append(" · ")
                .append(esc(s.monitor().nome())).append("</p>")
                .append("<div class=\"numeros\">")
                .append(numero(painel.pendentes(), "aguardando"))
                .append(numero(painel.atendidas(), "atendidos"))
                .append(numero(painel.vagasRestantes(), "vagas livres"))
                .append(numero(painel.grupos().size(), painel.politica().id().equals("topico") ? "grupos de tópico" : "na fila"))
                .append("</div>");

        html.append("<h2>Fila de atendimento</h2><nav class=\"filtros\" aria-label=\"Organização da fila\">");
        for (PoliticaFila p : politicas.todas()) {
            html.append("<a href=\"").append(base).append("&amp;ordem=").append(esc(p.id()))
                    .append("\" aria-current=\"").append(p.id().equals(politica.id())).append("\">")
                    .append(esc(p.descricao())).append("</a>");
        }
        html.append("<a href=\"").append(base).append("&amp;ordem=").append(esc(politica.id()))
                .append("\">↻ Atualizar</a></nav>");

        if (painel.grupos().isEmpty()) {
            html.append("<div class=\"alerta aviso\">Ninguém aguardando atendimento.</div>");
        }
        for (GrupoAtendimento g : painel.grupos()) {
            html.append(grupo(s.id(), politica.id(), g));
        }

        List<EventoReserva> recentes = atividades.recentesDaSessao(s.id(), 8);
        if (!recentes.isEmpty()) {
            html.append("<h2>Atividade recente</h2><ul class=\"atividades\">");
            for (EventoReserva e : recentes) {
                html.append("<li>").append(esc(Html.hora(e.quando()))).append(" · ")
                        .append(esc(e.tipo().rotulo())).append(" — ")
                        .append(esc(e.reserva().primeiroNome())).append(" (")
                        .append(esc(e.reserva().topico())).append(")</li>");
            }
            html.append("</ul>");
        }
        return Resposta.ok("Painel · " + s.disciplina().codigo(), html.toString());
    }

    @Override
    protected Resposta post(Requisicao req) {
        String sessaoId = servico.sessao(req.param("id")).id();
        String codigos = req.param("codigos", "");
        for (String codigo : codigos.split(",")) {
            if (!codigo.isBlank()) {
                servico.marcarAtendida(sessaoId, codigo.strip());
            }
        }
        return Resposta.redirecionar("/monitor/sessao?id=" + sessaoId + "&ordem="
                + politicas.porId(req.param("ordem", "")).id() + "&ok=1");
    }

    private static String grupo(String sessaoId, String ordem, GrupoAtendimento g) {
        StringBuilder html = new StringBuilder("<section class=\"grupo\"><div class=\"grupo-topo\">")
                .append("<h3>").append(esc(g.titulo()));
        int n = g.reservas().size();
        if (n > 1) {
            html.append(" <span class=\"etiqueta\">").append(n).append(" estudantes</span>");
        }
        html.append("</h3>");
        if (n > 1) {
            String todos = String.join(",", g.reservas().stream().map(Reserva::codigo).toList());
            html.append(formAtender(sessaoId, ordem, todos, "Concluir grupo (" + n + ")", ""));
        }
        html.append("</div><ul class=\"pessoas\">");
        for (Reserva r : g.reservas()) {
            html.append("<li><span><strong>").append(esc(r.primeiroNome())).append("</strong>");
            if (!r.descricao().isBlank()) {
                html.append(" — ").append(esc(r.descricao()));
            }
            html.append("<br><small>").append(esc(r.codigo())).append(" · reservou às ")
                    .append(esc(Html.hora(r.criadaEm()))).append("</small></span>")
                    .append(formAtender(sessaoId, ordem, r.codigo(), "Concluir", "secundario"))
                    .append("</li>");
        }
        return html.append("</ul></section>").toString();
    }

    private static String formAtender(String sessaoId, String ordem, String codigos, String rotulo, String classe) {
        return "<form method=\"post\" action=\"/monitor/sessao\">"
                + "<input type=\"hidden\" name=\"id\" value=\"" + esc(sessaoId) + "\">"
                + "<input type=\"hidden\" name=\"ordem\" value=\"" + esc(ordem) + "\">"
                + "<input type=\"hidden\" name=\"codigos\" value=\"" + esc(codigos) + "\">"
                + "<button type=\"submit\" class=\"" + classe + "\">" + esc(rotulo) + "</button></form>";
    }

    private static String numero(int valor, String rotulo) {
        return "<div class=\"numero\"><b>" + valor + "</b>" + esc(rotulo) + "</div>";
    }
}
