package br.ucsal.monitorauc.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Html {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA_HORA =
            DateTimeFormatter.ofPattern("EEE, dd/MM 'às' HH:mm", PT_BR);
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm", PT_BR);

    private Html() {
    }

    public static String esc(Object valor) {
        if (valor == null) {
            return "";
        }
        String s = valor.toString();
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (char c : s.toCharArray()) {
            switch (c) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&#39;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String dataHora(LocalDateTime dt) {
        return DATA_HORA.format(dt);
    }

    public static String hora(LocalDateTime dt) {
        return HORA.format(dt);
    }

    public static String erros(java.util.List<String> mensagens) {
        if (mensagens == null || mensagens.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("<div class=\"alerta erro\" role=\"alert\"><strong>Não foi possível concluir:</strong><ul>");
        mensagens.forEach(m -> sb.append("<li>").append(esc(m)).append("</li>"));
        return sb.append("</ul></div>").toString();
    }

    public static String layout(String titulo, String corpo) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                <meta charset="utf-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>%s · MonitoraUC</title>
                <style>%s</style>
                </head>
                <body>
                <a class="pular" href="#conteudo">Pular para o conteúdo</a>
                <header class="topo">
                  <a class="marca" href="/">Monitora<span>UC</span></a>
                  <nav aria-label="Principal">
                    <a href="/">Horários</a>
                    <a href="/consultar">Minha reserva</a>
                    <a href="/monitor">Área do monitor</a>
                  </nav>
                </header>
                <main id="conteudo">
                %s
                </main>
                <footer class="rodape">MVP acadêmico · POOALAB 2026.2 · dados fictícios de demonstração</footer>
                </body>
                </html>
                """.formatted(esc(titulo), CSS, corpo);
    }

    private static final String CSS = """
            :root{--tinta:#1c1a33;--suave:#5f5c78;--fundo:#f4f3fa;--card:#fff;--borda:#e2e0ef;
              --navy:#0f0d2e;--primaria:#5b3fd0;--primaria-escura:#4430a8;--lilas:#d2a0ee;--lilas-fundo:#efe7fb;
              --ok:#1d6b3a;--ok-fundo:#e6f4ea;--erro:#b3261e;--erro-fundo:#fdecea;--aviso-fundo:#fff6e0;
              --sombra:0 1px 2px rgba(15,13,46,.06),0 8px 24px rgba(15,13,46,.06)}
            *{box-sizing:border-box}
            a{color:var(--primaria)}
            body{margin:0;font:16px/1.55 system-ui,-apple-system,"Segoe UI",Roboto,sans-serif;color:var(--tinta);background:var(--fundo)}
            .pular{position:absolute;left:-999px}.pular:focus{left:8px;top:8px;background:#fff;padding:8px;z-index:9}
            .topo{display:flex;flex-wrap:wrap;gap:12px;align-items:center;justify-content:space-between;
              padding:16px max(24px,calc((100% - 1000px)/2));background:linear-gradient(120deg,var(--navy) 0%,#2a1f63 100%);color:#fff}
            .topo a{color:#fff;text-decoration:none}
            .marca{font-weight:800;font-size:1.35rem;letter-spacing:.5px}.marca span{color:var(--lilas)}
            .topo nav{display:flex;flex-wrap:wrap;gap:6px}
            .topo nav a{padding:6px 14px;border-radius:99px;font-weight:500;color:#e6e2f7}
            .topo nav a:hover,.topo nav a:focus{background:rgba(255,255,255,.12);color:#fff}
            main{max-width:1000px;margin:0 auto;padding:32px 24px 16px}
            h1{font-size:1.85rem;line-height:1.2;margin:0 0 8px;color:var(--navy)}
            h2{font-size:1.2rem;margin:28px 0 10px;color:var(--navy)}
            .sub{color:var(--suave);margin:0 0 24px;font-size:1.05rem}
            .grade{display:grid;gap:18px;grid-template-columns:repeat(auto-fill,minmax(280px,1fr))}
            .card{background:var(--card);border:1px solid var(--borda);border-radius:16px;padding:20px;box-shadow:var(--sombra);
              border-top:4px solid var(--primaria);transition:transform .15s ease,box-shadow .15s ease}
            .card:hover{transform:translateY(-2px);box-shadow:0 2px 4px rgba(15,13,46,.08),0 14px 32px rgba(15,13,46,.10)}
            .card h3{margin:8px 0 6px;font-size:1.1rem;color:var(--navy)}
            .meta{color:var(--suave);font-size:.93rem;margin:2px 0}
            .etiqueta{display:inline-block;font-size:.75rem;font-weight:700;letter-spacing:.5px;padding:3px 10px;border-radius:99px;
              background:var(--lilas-fundo);color:var(--primaria-escura)}
            .vagas{font-weight:700;margin:12px 0;color:var(--ok)}.vagas.lotada{color:var(--erro)}
            .botao,button{display:inline-block;font:inherit;font-weight:600;padding:10px 18px;border-radius:10px;
              border:2px solid var(--primaria);background:var(--primaria);color:#fff;text-decoration:none;cursor:pointer;
              transition:background .15s ease}
            .botao:hover,button:hover{background:var(--primaria-escura);border-color:var(--primaria-escura)}
            .botao.secundario,button.secundario{background:#fff;color:var(--primaria)}
            .botao.secundario:hover,button.secundario:hover{background:var(--lilas-fundo)}
            button.perigo{background:#fff;color:var(--erro);border-color:var(--erro)}
            button.perigo:hover{background:var(--erro-fundo)}
            .botao:focus-visible,button:focus-visible,input:focus-visible,select:focus-visible,textarea:focus-visible,a:focus-visible{
              outline:3px solid #f0b400;outline-offset:2px}
            form.campos{display:grid;gap:16px;max-width:560px;background:#fff;border:1px solid var(--borda);border-radius:16px;
              padding:24px;box-shadow:var(--sombra)}
            label{font-weight:600;display:block;margin-bottom:6px}
            .dica{font-weight:400;color:var(--suave);font-size:.9rem}
            input,select,textarea{width:100%;font:inherit;padding:10px 12px;border:1px solid #b9b5cf;border-radius:10px;background:#fff}
            input:focus,select:focus,textarea:focus{border-color:var(--primaria)}
            textarea{min-height:96px}
            .alerta{border-radius:12px;padding:14px 18px;margin:0 0 20px;border-left:6px solid}
            .alerta ul{margin:6px 0 0;padding-left:20px}
            .erro{background:var(--erro-fundo);border-color:var(--erro)}
            .sucesso{background:var(--ok-fundo);border-color:var(--ok)}
            .aviso{background:var(--aviso-fundo);border-color:#c98a00}
            .codigo{display:inline-block;font:700 1.9rem/1.2 ui-monospace,Consolas,monospace;letter-spacing:3px;
              color:var(--primaria-escura);background:var(--lilas-fundo);padding:10px 18px;border-radius:12px;margin:4px 0}
            .filtros{display:flex;flex-wrap:wrap;gap:8px;margin:0 0 22px}
            .filtros a{padding:7px 14px;border-radius:99px;border:1px solid var(--borda);background:#fff;
              color:var(--tinta);text-decoration:none;font-size:.92rem;font-weight:500}
            .filtros a:hover{border-color:var(--primaria);color:var(--primaria)}
            .filtros a[aria-current="true"]{background:var(--primaria);color:#fff;border-color:var(--primaria)}
            .numeros{display:flex;flex-wrap:wrap;gap:14px;margin:14px 0 6px}
            .numero{background:#fff;border:1px solid var(--borda);border-radius:14px;padding:12px 18px;min-width:140px;
              box-shadow:var(--sombra);color:var(--suave);font-size:.92rem}
            .numero b{display:block;font-size:1.7rem;color:var(--navy);line-height:1.2}
            .grupo{background:#fff;border:1px solid var(--borda);border-radius:16px;padding:16px 20px;margin:0 0 14px;
              box-shadow:var(--sombra);border-left:5px solid var(--lilas)}
            .grupo-topo{display:flex;flex-wrap:wrap;gap:10px;justify-content:space-between;align-items:center}
            .grupo h3{margin:0;font-size:1.08rem;color:var(--navy)}
            .pessoas{list-style:none;margin:12px 0 0;padding:0}
            .pessoas li{display:flex;flex-wrap:wrap;gap:8px;justify-content:space-between;align-items:center;
              padding:10px 0;border-top:1px solid #efedf6}
            .pessoas small{color:var(--suave)}
            .pessoas button{padding:5px 14px;font-size:.9rem}
            dl.detalhes{display:grid;grid-template-columns:max-content 1fr;gap:8px 20px;margin:18px 0;background:#fff;
              border:1px solid var(--borda);border-radius:16px;padding:18px 22px;box-shadow:var(--sombra)}
            dl.detalhes dt{font-weight:600;color:var(--suave)}dl.detalhes dd{margin:0}
            .linha{display:flex;flex-wrap:wrap;gap:10px;align-items:end}
            .atividades{list-style:none;padding:0;margin:0;color:var(--suave);font-size:.92rem}
            .atividades li{padding:4px 0}
            .rodape{text-align:center;color:var(--suave);font-size:.85rem;padding:28px 24px;margin-top:24px;border-top:1px solid var(--borda)}
            @media (max-width:600px){h1{font-size:1.5rem}main{padding:24px 16px}.topo{padding:14px 16px}}
            """;
}
