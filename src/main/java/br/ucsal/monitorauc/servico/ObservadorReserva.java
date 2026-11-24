package br.ucsal.monitorauc.servico;

@FunctionalInterface
public interface ObservadorReserva {
    void aoOcorrer(EventoReserva evento);
}
