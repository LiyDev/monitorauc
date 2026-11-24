package br.ucsal.monitorauc.web;

import br.ucsal.monitorauc.dominio.SessaoMonitoria;
import br.ucsal.monitorauc.repositorio.SessaoRepositorio;
import br.ucsal.monitorauc.servico.ReservaService;

import static br.ucsal.monitorauc.web.Html.esc;

public class PaginaMonitor extends PaginaBase {

    private final SessaoRepositorio sessoes;
    private final ReservaService servico;

    public PaginaMonitor(SessaoRepositorio sessoes, ReservaService servico) {
        super("/monitor");
        this.sessoes = sessoes;
        this.servico = servico;
    }

    @Override
    protected Resposta get(Requisicao req) {
        StringBuilder html = new StringBuilder("""
                <h1>Área do monitor</h1>
                <div class="alerta aviso">Demonstração: no MVP esta área não exige login (ver limitações no README).</div>
                <div class="grade">
                """);
        for (SessaoMonitoria s : sessoes.listarTodas()) {
            int ocupadas = s.capacidade() - servico.vagasRestantes(s);
            html.append("<article class=\"card\">")
                    .append("<span class=\"etiqueta\">").append(esc(s.disciplina().codigo())).append("</span>")
                    .append("<h3>").append(esc(s.monitor().nome())).append("</h3>")
                    .append("<p class=\"meta\">").append(esc(Html.dataHora(s.inicio()))).append("</p>")
                    .append("<p class=\"meta\">").append(esc(s.local())).append("</p>")
                    .append("<p class=\"vagas\">").append(ocupadas).append(" de ").append(s.capacidade())
                    .append(" vagas reservadas</p>")
                    .append("<a class=\"botao\" href=\"/monitor/sessao?id=").append(esc(s.id()))
                    .append("\">Abrir painel</a></article>");
        }
        html.append("</div>");
        return Resposta.ok("Área do monitor", html.toString());
    }
}
