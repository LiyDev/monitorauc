package br.ucsal.monitorauc.web;

import br.ucsal.monitorauc.dominio.PedidoReserva;
import br.ucsal.monitorauc.dominio.Reserva;
import br.ucsal.monitorauc.dominio.SessaoMonitoria;
import br.ucsal.monitorauc.servico.ReservaRecusadaException;
import br.ucsal.monitorauc.servico.ReservaService;
import br.ucsal.monitorauc.validacao.ErrosDeValidacao;

import java.util.List;

import static br.ucsal.monitorauc.web.Html.esc;

public class PaginaSessao extends PaginaBase {

    private final ReservaService servico;

    public PaginaSessao(ReservaService servico) {
        super("/sessao");
        this.servico = servico;
    }

    @Override
    protected Resposta get(Requisicao req) {
        SessaoMonitoria sessao = servico.sessao(req.param("id"));
        return formulario(sessao, req, List.of(), 200);
    }

    @Override
    protected Resposta post(Requisicao req) {
        SessaoMonitoria sessao = servico.sessao(req.param("id"));
        PedidoReserva pedido = new PedidoReserva(sessao.id(), req.param("matricula"),
                req.param("primeiroNome"), req.param("topico"), req.param("descricao"));
        try {
            Reserva reserva = servico.reservar(pedido);
            return Resposta.redirecionar("/reserva?codigo=" + reserva.codigo() + "&nova=1");
        } catch (ErrosDeValidacao e) {
            return formulario(sessao, req, e.mensagens(), 400);
        } catch (ReservaRecusadaException e) {
            return formulario(sessao, req, List.of(e.getMessage()), 409);
        }
    }

    private Resposta formulario(SessaoMonitoria s, Requisicao req, List<String> erros, int status) {
        int vagas = servico.vagasRestantes(s);
        String topicoAtual = req.param("topico", "");

        StringBuilder opcoes = new StringBuilder("<option value=\"\">Selecione…</option>");
        for (String t : s.disciplina().topicos()) {
            opcoes.append("<option").append(t.equals(topicoAtual) ? " selected" : "")
                    .append(" value=\"").append(esc(t)).append("\">").append(esc(t)).append("</option>");
        }

        String corpo = """
                <p><a href="/">← Voltar aos horários</a></p>
                <h1>Reservar vaga</h1>
                <div class="card" style="margin-bottom:20px">
                  <span class="etiqueta">%s</span>
                  <h3>%s</h3>
                  <p class="meta">%s – %s · %s</p>
                  <p class="meta">%s</p>
                  <p class="vagas%s">%s</p>
                </div>
                %s
                <form class="campos" method="post" action="/sessao?id=%s" novalidate>
                  <div>
                    <label for="matricula">Matrícula <span class="dica">(somente números)</span></label>
                    <input id="matricula" name="matricula" inputmode="numeric" autocomplete="off" required value="%s">
                  </div>
                  <div>
                    <label for="primeiroNome">Primeiro nome <span class="dica">(como o monitor vai te chamar)</span></label>
                    <input id="primeiroNome" name="primeiroNome" autocomplete="given-name" required maxlength="30" value="%s">
                  </div>
                  <div>
                    <label for="topico">Tópico da dúvida</label>
                    <select id="topico" name="topico" required>%s</select>
                  </div>
                  <div>
                    <label for="descricao">Descreva a dúvida <span class="dica">(opcional, até 280 caracteres)</span></label>
                    <textarea id="descricao" name="descricao" maxlength="280">%s</textarea>
                  </div>
                  <div><button type="submit">Confirmar reserva</button></div>
                </form>
                """.formatted(
                esc(s.disciplina().codigo()), esc(s.disciplina().nome()),
                esc(Html.dataHora(s.inicio())), esc(Html.hora(s.fim())), esc(s.local()),
                esc(s.monitor().nome()),
                vagas > 0 ? "" : " lotada",
                vagas > 0 ? vagas + " de " + s.capacidade() + (vagas == 1 ? " vaga livre" : " vagas livres") : "Sessão lotada",
                Html.erros(erros),
                esc(s.id()),
                esc(req.param("matricula", "")), esc(req.param("primeiroNome", "")),
                opcoes, esc(req.param("descricao", "")));
        return new Resposta.Pagina(status, "Reservar · " + s.disciplina().codigo(), corpo);
    }
}
