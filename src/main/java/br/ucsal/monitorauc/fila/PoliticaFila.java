package br.ucsal.monitorauc.fila;

import br.ucsal.monitorauc.dominio.Reserva;

import java.util.List;

public interface PoliticaFila {

    String id();

    String descricao();

    List<GrupoAtendimento> organizar(List<Reserva> pendentes);
}
