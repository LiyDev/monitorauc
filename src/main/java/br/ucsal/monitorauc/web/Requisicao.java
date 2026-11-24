package br.ucsal.monitorauc.web;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class Requisicao {

    private static final int LIMITE_CORPO = 16 * 1024;

    private final String metodo;
    private final Map<String, String> parametros;

    private Requisicao(String metodo, Map<String, String> parametros) {
        this.metodo = metodo;
        this.parametros = parametros;
    }

    static Requisicao de(HttpExchange troca) throws IOException {
        Map<String, String> params = new HashMap<>();
        decodificar(troca.getRequestURI().getRawQuery(), params);
        if ("POST".equals(troca.getRequestMethod())) {
            try (InputStream in = troca.getRequestBody()) {
                byte[] corpo = in.readNBytes(LIMITE_CORPO + 1);
                if (corpo.length > LIMITE_CORPO) {
                    throw new IllegalArgumentException("Formulário muito grande.");
                }
                decodificar(new String(corpo, StandardCharsets.UTF_8), params);
            }
        }
        return new Requisicao(troca.getRequestMethod(), params);
    }

    public static Requisicao de(String metodo, Map<String, String> parametros) {
        return new Requisicao(metodo, new HashMap<>(parametros));
    }

    private static void decodificar(String bruto, Map<String, String> destino) {
        if (bruto == null || bruto.isEmpty()) {
            return;
        }
        for (String par : bruto.split("&")) {
            if (par.isEmpty()) {
                continue;
            }
            int i = par.indexOf('=');
            String chave = URLDecoder.decode(i < 0 ? par : par.substring(0, i), StandardCharsets.UTF_8);
            String valor = i < 0 ? "" : URLDecoder.decode(par.substring(i + 1), StandardCharsets.UTF_8);
            destino.put(chave, valor);
        }
    }

    public String metodo() {
        return metodo;
    }

    public String param(String nome) {
        return parametros.get(nome);
    }

    public String param(String nome, String padrao) {
        String v = parametros.get(nome);
        return v == null || v.isBlank() ? padrao : v;
    }
}
