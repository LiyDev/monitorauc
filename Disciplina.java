package br.ucsal.monitorauc.dominio;

import java.util.List;
import java.util.Objects;

public record Disciplina(String codigo, String nome, List<String> topicos) {

    public Disciplina {
        Objects.requireNonNull(codigo, "codigo");
        Objects.requireNonNull(nome, "nome");
        topicos = List.copyOf(topicos);
        if (topicos.isEmpty()) {
            throw new IllegalArgumentException("A disciplina precisa de ao menos um tópico");
        }
    }

    public boolean possuiTopico(String topico) {
        return topicos.contains(topico);
    }
}
