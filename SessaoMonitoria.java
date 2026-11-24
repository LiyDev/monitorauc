package br.ucsal.monitorauc.dominio;

import java.time.LocalDateTime;
import java.util.Objects;

public record SessaoMonitoria(
        String id,
        Disciplina disciplina,
        Monitor monitor,
        LocalDateTime inicio,
        int duracaoMinutos,
        String local,
        int capacidade) {

    public SessaoMonitoria {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(disciplina, "disciplina");
        Objects.requireNonNull(monitor, "monitor");
        Objects.requireNonNull(inicio, "inicio");
        Objects.requireNonNull(local, "local");
        if (capacidade <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser positiva");
        }
        if (duracaoMinutos <= 0) {
            throw new IllegalArgumentException("Duração deve ser positiva");
        }
    }

    public LocalDateTime fim() {
        return inicio.plusMinutes(duracaoMinutos);
    }

    public boolean aceitaReservasEm(LocalDateTime agora) {
        return agora.isBefore(inicio);
    }
}
