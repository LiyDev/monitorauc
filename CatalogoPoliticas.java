package br.ucsal.monitorauc.fila;

import java.util.List;

public final class CatalogoPoliticas {

    private final List<PoliticaFila> politicas;

    public CatalogoPoliticas(List<PoliticaFila> politicas) {
        if (politicas.isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos uma política");
        }
        this.politicas = List.copyOf(politicas);
    }

    public static CatalogoPoliticas padrao() {
        return new CatalogoPoliticas(List.of(new AgrupadaPorTopico(), new OrdemDeChegada()));
    }

    public List<PoliticaFila> todas() {
        return politicas;
    }

    public PoliticaFila porId(String id) {
        return politicas.stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .orElse(politicas.getFirst());
    }
}
