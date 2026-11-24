package br.ucsal.monitorauc.web;

import br.ucsal.monitorauc.servico.ReservaRecusadaException;
import br.ucsal.monitorauc.validacao.ErrosDeValidacao;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class PaginaBase implements HttpHandler {

    private static final Logger LOG = Logger.getLogger(PaginaBase.class.getName());

    private final String caminho;

    protected PaginaBase(String caminho) {
        this.caminho = caminho;
    }

    public String caminho() {
        return caminho;
    }

    @Override
    public final void handle(HttpExchange troca) throws IOException {
        Resposta resposta;
        try {
            if (!caminho.equals(troca.getRequestURI().getPath())) {
                resposta = new Resposta.Pagina(404, "Página não encontrada",
                        "<h1>Página não encontrada</h1><p><a href=\"/\">Voltar aos horários</a></p>");
            } else {
                resposta = processar(Requisicao.de(troca));
            }
        } catch (ReservaRecusadaException e) {
            resposta = new Resposta.Pagina(409, "Não foi possível concluir", paginaErro(List.of(e.getMessage())));
        } catch (ErrosDeValidacao e) {
            resposta = new Resposta.Pagina(400, "Dados inválidos", paginaErro(e.mensagens()));
        } catch (IllegalArgumentException e) {
            resposta = new Resposta.Pagina(400, "Requisição inválida", paginaErro(List.of(e.getMessage())));
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Erro inesperado em " + caminho, e);
            resposta = new Resposta.Pagina(500, "Erro interno",
                    paginaErro(List.of("Ocorreu um erro inesperado. Tente novamente.")));
        }
        escrever(troca, resposta);
    }

    Resposta processar(Requisicao req) {
        return switch (req.metodo()) {
            case "GET" -> get(req);
            case "POST" -> post(req);
            default -> new Resposta.Pagina(405, "Método não permitido", "<h1>Método não permitido</h1>");
        };
    }

    protected Resposta get(Requisicao req) {
        return new Resposta.Pagina(405, "Método não permitido", "<h1>Método não permitido</h1>");
    }

    protected Resposta post(Requisicao req) {
        return new Resposta.Pagina(405, "Método não permitido", "<h1>Método não permitido</h1>");
    }

    private static String paginaErro(List<String> mensagens) {
        return "<h1>Não foi possível concluir</h1>" + Html.erros(mensagens)
                + "<p><a class=\"botao secundario\" href=\"javascript:history.back()\">Voltar</a> "
                + "<a class=\"botao\" href=\"/\">Ver horários</a></p>";
    }

    private static void escrever(HttpExchange troca, Resposta resposta) throws IOException {
        var cabecalhos = troca.getResponseHeaders();
        cabecalhos.set("X-Content-Type-Options", "nosniff");
        cabecalhos.set("Cache-Control", "no-store");
        switch (resposta) {
            case Resposta.Redirecionar r -> {
                cabecalhos.set("Location", r.destino());
                troca.sendResponseHeaders(303, -1);
            }
            case Resposta.Pagina p -> {
                byte[] bytes = Html.layout(p.titulo(), p.corpo()).getBytes(StandardCharsets.UTF_8);
                cabecalhos.set("Content-Type", "text/html; charset=utf-8");
                troca.sendResponseHeaders(p.status(), bytes.length);
                try (OutputStream out = troca.getResponseBody()) {
                    out.write(bytes);
                }
            }
        }
        troca.close();
    }
}
