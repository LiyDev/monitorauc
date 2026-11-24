package br.ucsal.monitorauc.fila;

import br.ucsal.monitorauc.dominio.Reserva;

import java.util.List;

public record GrupoAtendimento(String titulo, List<Reserva> reservas) {
    public GrupoAtendimento {
        reservas = List.copyOf(reservas);
    }
}
