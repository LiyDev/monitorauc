package br.ucsal.monitorauc;

import br.ucsal.monitorauc.dominio.Disciplina;
import br.ucsal.monitorauc.dominio.Monitor;
import br.ucsal.monitorauc.dominio.PedidoReserva;
import br.ucsal.monitorauc.dominio.SessaoMonitoria;
import br.ucsal.monitorauc.repositorio.ReservaRepositorioEmMemoria;
import br.ucsal.monitorauc.repositorio.SessaoRepositorioEmMemoria;
import br.ucsal.monitorauc.servico.ReservaService;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

final class Cenario {

    static final Instant AGORA = Instant.parse("2026-11-23T12:00:00Z");
    static final Disciplina POOA = new Disciplina("POOA", "POO Avançada",
            List.of("SOLID", "Streams", "Outro"));

    final SessaoRepositorioEmMemoria sessoes = new SessaoRepositorioEmMemoria();
    final ReservaRepositorioEmMemoria reservas = new ReservaRepositorioEmMemoria();
    final ReservaService servico;

    Cenario(Clock relogio) {
        servico = new ReservaService(sessoes, reservas, relogio);
        LocalDateTime hoje = LocalDateTime.ofInstant(AGORA, ZoneOffset.UTC);
        Monitor ana = new Monitor("m1", "Ana");
        sessoes.salvar(new SessaoMonitoria("S1", POOA, ana, hoje.plusHours(2), 60, "Lab 1", 3));
        sessoes.salvar(new SessaoMonitoria("PASSADA", POOA, ana, hoje.minusHours(1), 60, "Lab 1", 3));
        sessoes.salvar(new SessaoMonitoria("GRANDE", POOA, ana, hoje.plusDays(1), 60, "Lab 2", 5));
    }

    Cenario() {
        this(Clock.fixed(AGORA, ZoneOffset.UTC));
    }

    static PedidoReserva pedido(String sessao, String matricula, String nome, String topico) {
        return new PedidoReserva(sessao, matricula, nome, topico, "");
    }
}
