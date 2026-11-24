package br.ucsal.monitorauc;

import br.ucsal.monitorauc.dominio.Reserva;
import br.ucsal.monitorauc.dominio.StatusReserva;
import br.ucsal.monitorauc.fila.AgrupadaPorTopico;
import br.ucsal.monitorauc.servico.EventoReserva;
import br.ucsal.monitorauc.servico.ReservaRecusadaException;
import br.ucsal.monitorauc.servico.ReservaService;
import br.ucsal.monitorauc.validacao.ErrosDeValidacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static br.ucsal.monitorauc.Cenario.pedido;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservaServiceTest {

    private Cenario cenario;
    private ReservaService servico;

    @BeforeEach
    void preparar() {
        cenario = new Cenario();
        servico = cenario.servico;
    }

    @Test
    void reservaValidaGeraCodigoEOcupaVaga() {
        Reserva r = servico.reservar(pedido("S1", "123456", "Lia", "SOLID"));

        assertTrue(r.codigo().matches("MON-[A-Z2-9]{5}"), "formato do código: " + r.codigo());
        assertEquals(StatusReserva.CONFIRMADA, r.status());
        assertEquals(2, servico.vagasRestantes(servico.sessao("S1")));
        assertEquals(r.codigo(), servico.consultar(r.codigo().toLowerCase()).codigo(), "consulta ignora maiúsculas");
    }

    @Test
    void dadosInvalidosRetornamTodasAsMensagens() {
        ErrosDeValidacao e = assertThrows(ErrosDeValidacao.class,
                () -> servico.reservar(pedido("S1", "12ab", "", "")));

        assertEquals(3, e.mensagens().size(), e.mensagens().toString());
        assertEquals(3, servico.vagasRestantes(servico.sessao("S1")), "nenhuma vaga consumida");
    }

    @Test
    void topicoForaDaDisciplinaEhRecusado() {
        assertThrows(ReservaRecusadaException.class,
                () -> servico.reservar(pedido("S1", "123456", "Lia", "Cálculo")));
    }

    @Test
    void mesmaMatriculaNaoReservaDuasVezesNaMesmaSessao() {
        servico.reservar(pedido("S1", "123456", "Lia", "SOLID"));

        ReservaRecusadaException e = assertThrows(ReservaRecusadaException.class,
                () -> servico.reservar(pedido("S1", "123456", "Lia", "Streams")));
        assertTrue(e.getMessage().contains("já possui"));
    }

    @Test
    void sessaoLotadaRecusaNovasReservas() {
        servico.reservar(pedido("S1", "100001", "Ana", "SOLID"));
        servico.reservar(pedido("S1", "100002", "Bia", "SOLID"));
        servico.reservar(pedido("S1", "100003", "Caio", "SOLID"));

        ReservaRecusadaException e = assertThrows(ReservaRecusadaException.class,
                () -> servico.reservar(pedido("S1", "100004", "Duda", "SOLID")));
        assertTrue(e.getMessage().contains("lotada"));
    }

    @Test
    void sessaoQueJaComecouNaoAceitaReserva() {
        assertThrows(ReservaRecusadaException.class,
                () -> servico.reservar(pedido("PASSADA", "123456", "Lia", "SOLID")));
        assertTrue(servico.sessoesDisponiveis().stream().noneMatch(s -> s.id().equals("PASSADA")));
    }

    @Test
    void cancelarLiberaAVaga() {
        servico.reservar(pedido("S1", "100001", "Ana", "SOLID"));
        servico.reservar(pedido("S1", "100002", "Bia", "SOLID"));
        Reserva r = servico.reservar(pedido("S1", "100003", "Caio", "SOLID"));
        assertEquals(0, servico.vagasRestantes(servico.sessao("S1")));

        servico.cancelar(r.codigo(), "100003");

        assertEquals(StatusReserva.CANCELADA, r.status());
        assertEquals(1, servico.vagasRestantes(servico.sessao("S1")));
        servico.reservar(pedido("S1", "100004", "Duda", "SOLID"));
    }

    @Test
    void cancelarExigeAMatriculaDoDono() {
        Reserva r = servico.reservar(pedido("S1", "100001", "Ana", "SOLID"));

        assertThrows(ReservaRecusadaException.class, () -> servico.cancelar(r.codigo(), "999999"));
        assertEquals(StatusReserva.CONFIRMADA, r.status());
    }

    @Test
    void monitorConcluiAtendimentoESaiDaFila() {
        Reserva r1 = servico.reservar(pedido("S1", "100001", "Ana", "SOLID"));
        servico.reservar(pedido("S1", "100002", "Bia", "Streams"));

        servico.marcarAtendida("S1", r1.codigo());

        ReservaService.PainelSessao painel = servico.painel("S1", new AgrupadaPorTopico());
        assertEquals(1, painel.pendentes());
        assertEquals(1, painel.atendidas());
        assertEquals(1, painel.vagasRestantes(), "atendida continua ocupando a vaga");
        assertThrows(ReservaRecusadaException.class, () -> servico.marcarAtendida("S1", r1.codigo()));
        assertThrows(ReservaRecusadaException.class, () -> servico.cancelar(r1.codigo(), "100001"));
    }

    @Test
    void naoConcluiReservaDeOutraSessao() {
        Reserva r = servico.reservar(pedido("S1", "100001", "Ana", "SOLID"));
        assertThrows(ReservaRecusadaException.class, () -> servico.marcarAtendida("GRANDE", r.codigo()));
    }

    @Test
    void observadoresRecebemOsEventos() {
        List<EventoReserva.Tipo> recebidos = new ArrayList<>();
        servico.registrar(e -> recebidos.add(e.tipo()));

        Reserva r = servico.reservar(pedido("S1", "100001", "Ana", "SOLID"));
        servico.cancelar(r.codigo(), "100001");

        assertEquals(List.of(EventoReserva.Tipo.CRIADA, EventoReserva.Tipo.CANCELADA), recebidos);
    }

    @Test
    void reservasSimultaneasNuncaUltrapassamACapacidade() throws Exception {
        int tentativas = 200;
        AtomicInteger sucessos = new AtomicInteger();
        AtomicInteger lotadas = new AtomicInteger();
        CountDownLatch largada = new CountDownLatch(1);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < tentativas; i++) {
                String matricula = String.valueOf(200000 + i);
                executor.submit(() -> {
                    largada.await();
                    try {
                        servico.reservar(pedido("GRANDE", matricula, "Aluno", "Streams"));
                        sucessos.incrementAndGet();
                    } catch (ReservaRecusadaException e) {
                        lotadas.incrementAndGet();
                    }
                    return null;
                });
            }
            largada.countDown();
        }

        assertEquals(5, sucessos.get());
        assertEquals(tentativas - 5, lotadas.get());
        assertEquals(0, servico.vagasRestantes(servico.sessao("GRANDE")));
    }
}
