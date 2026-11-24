package br.ucsal.monitorauc.fila;

import br.ucsal.monitorauc.dominio.Reserva;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AgrupadaPorTopico implements PoliticaFila {

    @Override
    public String id() {
        return "topico";
    }

    @Override
    public String descricao() {
        return "Agrupar por tópico";
    }

    @Override
    public List<GrupoAtendimento> organizar(List<Reserva> pendentes) {
        Map<String, List<Reserva>> porTopico = pendentes.stream()
                .collect(Collectors.groupingBy(Reserva::topico, LinkedHashMap::new, Collectors.toList()));

        return porTopico.entrySet().stream()
                .map(e -> new GrupoAtendimento(e.getKey(), e.getValue()))
                .sorted(Comparator
                        .comparingInt((GrupoAtendimento g) -> g.reservas().size()).reversed()
                        .thenComparingLong(g -> g.reservas().getFirst().sequencia()))
                .toList();
    }
}
