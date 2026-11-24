package br.ucsal.monitorauc.web;

import br.ucsal.monitorauc.dominio.Reserva;
import br.ucsal.monitorauc.dominio.SessaoMonitoria;
import br.ucsal.monitorauc.dominio.StatusReserva;
import br.ucsal.monitorauc.servico.ReservaService;

import static br.ucsal.monitorauc.web.Html.esc;

public class PaginaReserva extends PaginaBase {

    private final ReservaService servico;

    public PaginaReserva(ReservaService servico) {
        super("/reserva");
        this.servico = servico;
    }

    @Override
    protected Resposta get(Requisicao req) {
        Reserva r = servico.consultar(req.param("codigo"));
        SessaoMonitoria s = servico.sessao(r.sessaoId());
        StringBuilder html = new StringBuilder();

        if ("1".equals(req.param("nova"))) {
            html.append("<div class=\"alerta sucesso\" role=\"status\"><strong>Reserva confirmada!</strong> ")
                    .append("Guarde o código abaixo para consultar ou cancelar.</div>");
        }
        if ("1".equals(req.param("cancelada"))) {
            html.append("<div class=\"alerta aviso\" role=\"status\"><strong>Reserva cancelada.</strong> ")
                    .append("A vaga foi liberada para outro estudante.</div>");
        }

        html.append("<h1>Sua reserva</h1>")
                .append("<p class=\"codigo\" aria-label=\"Código da reserva\">").append(esc(r.codigo())).append("</p>")
                .append("<dl class=\"detalhes\">")
                .append(item("Situação", r.status().rotulo()))
                .append(item("Disciplina", s.disciplina().nome()))
                .append(item("Quando", Html.dataHora(s.inicio()) + " – " + Html.hora(s.fim())))
                .append(item("Onde", s.local()))
                .append(item("Monitor(a)", s.monitor().nome()))
                .append(item("Nome", r.primeiroNome()))
                .append(item("Tópico", r.topico()))
                .append(r.descricao().isBlank() ? "" : item("Dúvida", r.descricao()))
                .append("</dl>");

        if (r.status() == StatusReserva.CONFIRMADA) {
            html.append("""
                    <h2>Não vai mais?</h2>
                    <p class="sub">Cancele para liberar a vaga para um colega.</p>
                    <form class="linha" method="post" action="/reserva">
                      <input type="hidden" name="codigo" value="%s">
                      <div style="flex:1;min-width:200px">
                        <label for="matricula">Confirme sua matrícula</label>
                        <input id="matricula" name="matricula" inputmode="numeric" required>
                      </div>
                      <button class="perigo" type="submit">Cancelar reserva</button>
                    </form>
                    """.formatted(esc(r.codigo())));
        }
        html.append("<p style=\"margin-top:24px\"><a class=\"botao secundario\" href=\"/\">Ver outros horários</a></p>");
        return Resposta.ok("Reserva " + r.codigo(), html.toString());
    }

    @Override
    protected Resposta post(Requisicao req) {
        Reserva r = servico.cancelar(req.param("codigo"), req.param("matricula"));
        return Resposta.redirecionar("/reserva?codigo=" + r.codigo() + "&cancelada=1");
    }

    private static String item(String rotulo, String valor) {
        return "<dt>" + esc(rotulo) + "</dt><dd>" + esc(valor) + "</dd>";
    }
}
