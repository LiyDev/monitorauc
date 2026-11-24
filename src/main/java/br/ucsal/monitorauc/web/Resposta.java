package br.ucsal.monitorauc.web;

public sealed interface Resposta {

    record Pagina(int status, String titulo, String corpo) implements Resposta {
    }

    record Redirecionar(String destino) implements Resposta {
    }

    static Resposta ok(String titulo, String corpo) {
        return new Pagina(200, titulo, corpo);
    }

    static Resposta redirecionar(String destino) {
        return new Redirecionar(destino);
    }
}
