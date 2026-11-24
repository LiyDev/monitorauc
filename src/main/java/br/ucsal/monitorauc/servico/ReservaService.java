package br.ucsal.monitorauc.servico;

import br.ucsal.monitorauc.dominio.PedidoReserva;
import br.ucsal.monitorauc.dominio.Reserva;
import br.ucsal.monitorauc.dominio.SessaoMonitoria;
import br.ucsal.monitorauc.dominio.StatusReserva;
import br.ucsal.monitorauc.fila.GrupoAtendimento;
import br.ucsal.monitorauc.fila.PoliticaFila;
import br.ucsal.monitorauc.repositorio.ReservaRepositorio;
import br.ucsal.monitorauc.repositorio.SessaoRepositorio;
import br.ucsal.monitorauc.validacao.Validador;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class ReservaService {

    private static final char[] ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    private final SessaoRepositorio sessoes;
    private final ReservaRepositorio reservas;
    private final Clock relogio;
    private final List<ObservadorReserva> observadores = new CopyOnWriteArrayList<>();
    private final Map<String, Object> travasPorSessao = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();
    private final SecureRandom aleatorio = new SecureRandom();

    public ReservaService(SessaoRepositorio sessoes, ReservaRepositorio reservas, Clock relogio) {
        this.sessoes = Objects.requireNonNull(sessoes);
        this.reservas = Objects.requireNonNull(reservas);
        this.relogio = Objects.requireNonNull(relogio);
    }

    public void registrar(ObservadorReserva observador) {
        observadores.add(observador);
    }

    public List<SessaoMonitoria> sessoesDisponiveis() {
        LocalDateTime agora = agora();
        return sessoes.listarTodas().stream()
                .filter(s -> s.aceitaReservasEm(agora))
                .toList();
    }

    public SessaoMonitoria sessao(String id) {
        return sessoes.buscar(id)
                .orElseThrow(() -> new ReservaRecusadaException("Sessão de monitoria não encontrada."));
    }

    public int vagasRestantes(SessaoMonitoria sessao) {
        return sessao.capacidade() - ocupadas(sessao.id());
    }

    public Reserva reservar(PedidoReserva pedidoBruto) {
        PedidoReserva pedido = Validador.validar(pedidoBruto.normalizado());
        SessaoMonitoria sessao = sessao(pedido.sessaoId());

        if (!sessao.disciplina().possuiTopico(pedido.topico())) {
            throw new ReservaRecusadaException("Escolha um tópico da lista da disciplina.");
        }

        Reserva reserva;
        synchronized (travaDa(sessao.id())) {
            LocalDateTime agora = agora();
            if (!sessao.aceitaReservasEm(agora)) {
                throw new ReservaRecusadaException("As reservas para esta sessão já foram encerradas.");
            }
            List<Reserva> daSessao = reservas.listarPorSessao(sessao.id());
            boolean jaReservou = daSessao.stream()
                    .anyMatch(r -> r.status().ocupaVaga() && r.matricula().equals(pedido.matricula()));
            if (jaReservou) {
                throw new ReservaRecusadaException("Esta matrícula já possui uma reserva nesta sessão.");
            }
            long ocupadas = daSessao.stream().filter(r -> r.status().ocupaVaga()).count();
            if (ocupadas >= sessao.capacidade()) {
                throw new ReservaRecusadaException("Sessão lotada. Escolha outro horário.");
            }
            reserva = new Reserva(sequencia.incrementAndGet(), novoCodigo(), sessao.id(),
                    pedido.matricula(), pedido.primeiroNome(), pedido.topico(), pedido.descricao(), agora);
            reservas.salvar(reserva);
        }
        publicar(EventoReserva.Tipo.CRIADA, reserva);
        return reserva;
    }

    public Reserva consultar(String codigo) {
        return reservas.buscarPorCodigo(codigo)
                .orElseThrow(() -> new ReservaRecusadaException("Reserva não encontrada. Confira o código."));
    }

    public Reserva cancelar(String codigo, String matricula) {
        Reserva reserva = consultar(codigo);
        if (matricula == null || !reserva.matricula().equals(matricula.strip())) {
            throw new ReservaRecusadaException("A matrícula não corresponde a esta reserva.");
        }
        synchronized (travaDa(reserva.sessaoId())) {
            if (reserva.status() != StatusReserva.CONFIRMADA) {
                throw new ReservaRecusadaException("Só é possível cancelar reservas confirmadas.");
            }
            reserva.cancelar();
        }
        publicar(EventoReserva.Tipo.CANCELADA, reserva);
        return reserva;
    }

    public Reserva marcarAtendida(String sessaoId, String codigo) {
        Reserva reserva = consultar(codigo);
        if (!reserva.sessaoId().equals(sessaoId)) {
            throw new ReservaRecusadaException("A reserva não pertence a esta sessão.");
        }
        synchronized (travaDa(reserva.sessaoId())) {
            if (reserva.status() != StatusReserva.CONFIRMADA) {
                throw new ReservaRecusadaException("Esta reserva não está aguardando atendimento.");
            }
            reserva.marcarAtendida();
        }
        publicar(EventoReserva.Tipo.ATENDIDA, reserva);
        return reserva;
    }

    public PainelSessao painel(String sessaoId, PoliticaFila politica) {
        SessaoMonitoria sessao = sessao(sessaoId);
        List<Reserva> daSessao = reservas.listarPorSessao(sessaoId);
        List<Reserva> pendentes = daSessao.stream()
                .filter(r -> r.status() == StatusReserva.CONFIRMADA)
                .toList();
        List<GrupoAtendimento> grupos = politica.organizar(pendentes);
        long atendidas = daSessao.stream().filter(r -> r.status() == StatusReserva.ATENDIDA).count();
        return new PainelSessao(sessao, grupos, pendentes.size(), (int) atendidas, vagasRestantes(sessao), politica);
    }

    private int ocupadas(String sessaoId) {
        return (int) reservas.listarPorSessao(sessaoId).stream()
                .filter(r -> r.status().ocupaVaga())
                .count();
    }

    private Object travaDa(String sessaoId) {
        return travasPorSessao.computeIfAbsent(sessaoId, id -> new Object());
    }

    private String novoCodigo() {
        String codigo;
        do {
            StringBuilder sb = new StringBuilder("MON-");
            for (int i = 0; i < 5; i++) {
                sb.append(ALFABETO[aleatorio.nextInt(ALFABETO.length)]);
            }
            codigo = sb.toString();
        } while (reservas.buscarPorCodigo(codigo).isPresent());
        return codigo;
    }

    private void publicar(EventoReserva.Tipo tipo, Reserva reserva) {
        EventoReserva evento = new EventoReserva(tipo, reserva, agora());
        observadores.forEach(o -> o.aoOcorrer(evento));
    }

    private LocalDateTime agora() {
        return LocalDateTime.now(relogio);
    }

    public record PainelSessao(SessaoMonitoria sessao, List<GrupoAtendimento> grupos,
                               int pendentes, int atendidas, int vagasRestantes, PoliticaFila politica) {
    }
}
