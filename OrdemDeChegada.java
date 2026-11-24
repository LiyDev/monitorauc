package br.ucsal.monitorauc.fila;

import br.ucsal.monitorauc.dominio.Reserva;

import java.util.List;

public class OrdemDeChegada implements PoliticaFila {

    @Override
    public String id() {
        return "chegada";
    }

    @Override
    public String descricao() {
        return "Ordem de chegada";
    }

    @Override
    public List<GrupoAtendimento> organizar(List<Reserva> pendentes) {
        return pendentes.stream()
                .map(r -> new GrupoAtendimento(r.topico(), List.of(r)))
                .toList();
    }
}
