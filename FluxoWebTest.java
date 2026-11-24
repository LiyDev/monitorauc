package br.ucsal.monitorauc;

import br.ucsal.monitorauc.web.Servidor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FluxoWebTest {

    private Servidor servidor;
    private HttpClient http;
    private String base;

    @BeforeEach
    void subir() throws Exception {
        servidor = App.criar(0, Clock.systemDefaultZone(), false).iniciar();
        base = "http://localhost:" + servidor.porta();
        http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    }

    @AfterEach
    void descer() {
        servidor.close();
    }

    private HttpResponse<String> get(String caminho) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create(base + caminho)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String caminho, String... pares) throws Exception {
        StringBuilder corpo = new StringBuilder();
        for (int i = 0; i < pares.length; i += 2) {
            if (!corpo.isEmpty()) {
                corpo.append('&');
            }
            corpo.append(pares[i]).append('=').append(URLEncoder.encode(pares[i + 1], StandardCharsets.UTF_8));
        }
        return http.send(HttpRequest.newBuilder(URI.create(base + caminho))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(corpo.toString())).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void estudanteReservaEMonitorConcluiAtendimento() throws Exception {
        HttpResponse<String> inicio = get("/");
        assertEquals(200, inicio.statusCode());
        assertTrue(inicio.body().contains("Programação Orientada a Objetos Avançada"));

        HttpResponse<String> reserva = post("/sessao?id=S1",
                "matricula", "20260001", "primeiroNome", "João",
                "topico", "SOLID", "descricao", "Exemplo de <script>alert(1)</script> LSP");
        assertEquals(200, reserva.statusCode());
        assertTrue(reserva.body().contains("Reserva confirmada"));
        assertTrue(reserva.uri().getPath().equals("/reserva"), "Post/Redirect/Get");
        assertFalse(reserva.body().contains("<script>alert(1)"), "entrada do usuário é escapada");

        Matcher m = Pattern.compile("MON-[A-Z2-9]{5}").matcher(reserva.body());
        assertTrue(m.find());
        String codigo = m.group();

        HttpResponse<String> painel = get("/monitor/sessao?id=S1&ordem=topico");
        assertTrue(painel.body().contains("João"));
        assertTrue(painel.body().contains(codigo));
        assertTrue(painel.body().contains("<b>1</b>aguardando"));

        HttpResponse<String> concluido = post("/monitor/sessao", "id", "S1", "ordem", "topico", "codigos", codigo);
        assertTrue(concluido.body().contains("Atendimento registrado"));
        assertTrue(concluido.body().contains("<b>0</b>aguardando"));
        assertTrue(concluido.body().contains("<b>1</b>atendidos"));
    }

    @Test
    void formularioInvalidoMostraErrosEPreservaDados() throws Exception {
        HttpResponse<String> r = post("/sessao?id=S1",
                "matricula", "abc", "primeiroNome", "Ana", "topico", "", "descricao", "minha dúvida");
        assertEquals(400, r.statusCode());
        assertTrue(r.body().contains("apenas números"));
        assertTrue(r.body().contains("Informe o tópico da dúvida."));
        assertTrue(r.body().contains("minha dúvida"), "o texto digitado não se perde");
    }

    @Test
    void estudanteCancelaPelaPaginaDaReserva() throws Exception {
        HttpResponse<String> reserva = post("/sessao?id=S5",
                "matricula", "20260002", "primeiroNome", "Bia", "topico", "Limites", "descricao", "");
        Matcher m = Pattern.compile("MON-[A-Z2-9]{5}").matcher(reserva.body());
        assertTrue(m.find());

        HttpResponse<String> cancelada = post("/reserva", "codigo", m.group(), "matricula", "20260002");
        assertTrue(cancelada.body().contains("Reserva cancelada"));
        assertTrue(cancelada.body().contains("<dd>Cancelada</dd>"));
    }

    @Test
    void caminhoOuSessaoInexistenteNaoQuebra() throws Exception {
        assertEquals(404, get("/nao-existe").statusCode());
        assertEquals(409, get("/sessao?id=XYZ").statusCode());
        assertEquals(409, get("/reserva?codigo=MON-00000").statusCode());
    }
}
