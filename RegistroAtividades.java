package br.ucsal.monitorauc.servico;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class RegistroAtividades implements ObservadorReserva {

    private final Deque<EventoReserva> eventos = new ConcurrentLinkedDeque<>();
    private final int limite;

    public RegistroAtividades(int limite) {
        this.limite = limite;
    }

    @Override
    public void aoOcorrer(EventoReserva evento) {
        eventos.addFirst(evento);
        while (eventos.size() > limite) {
            eventos.pollLast();
        }
    }

    public List<EventoReserva> recentesDaSessao(String sessaoId, int quantidade) {
        List<EventoReserva> resultado = new ArrayList<>();
        for (EventoReserva e : eventos) {
            if (e.reserva().sessaoId().equals(sessaoId)) {
                resultado.add(e);
                if (resultado.size() == quantidade) {
                    break;
                }
            }
        }
        return resultado;
    }
}
