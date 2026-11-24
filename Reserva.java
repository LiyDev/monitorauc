package br.ucsal.monitorauc.dominio;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Reserva {

    private final long sequencia;
    private final String codigo;
    private final String sessaoId;
    private final String matricula;
    private final String primeiroNome;
    private final String topico;
    private final String descricao;
    private final LocalDateTime criadaEm;
    private volatile StatusReserva status;

    public Reserva(long sequencia, String codigo, String sessaoId, String matricula, String primeiroNome,
                   String topico, String descricao, LocalDateTime criadaEm) {
        this.sequencia = sequencia;
        this.codigo = Objects.requireNonNull(codigo);
        this.sessaoId = Objects.requireNonNull(sessaoId);
        this.matricula = Objects.requireNonNull(matricula);
        this.primeiroNome = Objects.requireNonNull(primeiroNome);
        this.topico = Objects.requireNonNull(topico);
        this.descricao = descricao == null ? "" : descricao;
        this.criadaEm = Objects.requireNonNull(criadaEm);
        this.status = StatusReserva.CONFIRMADA;
    }

    public synchronized void cancelar() {
        exigirStatus(StatusReserva.CONFIRMADA, "cancelar");
        status = StatusReserva.CANCELADA;
    }

    public synchronized void marcarAtendida() {
        exigirStatus(StatusReserva.CONFIRMADA, "marcar como atendida");
        status = StatusReserva.ATENDIDA;
    }

    private void exigirStatus(StatusReserva esperado, String acao) {
        if (status != esperado) {
            throw new IllegalStateException(
                    "Não é possível " + acao + " uma reserva " + status.rotulo().toLowerCase());
        }
    }

    public long sequencia() { return sequencia; }
    public String codigo() { return codigo; }
    public String sessaoId() { return sessaoId; }
    public String matricula() { return matricula; }
    public String primeiroNome() { return primeiroNome; }
    public String topico() { return topico; }
    public String descricao() { return descricao; }
    public LocalDateTime criadaEm() { return criadaEm; }
    public StatusReserva status() { return status; }
}
