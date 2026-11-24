package br.ucsal.monitorauc;

import br.ucsal.monitorauc.dominio.Reserva;
import br.ucsal.monitorauc.fila.AgrupadaPorTopico;
import br.ucsal.monitorauc.fila.CatalogoPoliticas;
import br.ucsal.monitorauc.fila.GrupoAtendimento;
import br.ucsal.monitorauc.fila.OrdemDeChegada;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PoliticaFilaTest {

    private static final LocalDateTime T = LocalDateTime.of(2026, 11, 23, 9, 0);

    private static Reserva r(long seq, String nome, String topico) {
        return new Reserva(seq, "MON-" + seq, "S1", "10000" + seq, nome, topico, "", T.plusMinutes(seq));
    }

    private final List<Reserva> fila = List.of(
            r(1, "Ana", "Streams"),
            r(2, "Bia", "SOLID"),
            r(3, "Caio", "SOLID"),
            r(4, "Duda", "Outro"),
            r(5, "Edu", "SOLID"),
            r(6, "Fabi", "Streams"));

    @Test
    void agrupaPorTopicoComMaiorGrupoPrimeiro() {
        List<GrupoAtendimento> grupos = new AgrupadaPorTopico().organizar(fila);

        assertEquals(List.of("SOLID", "Streams", "Outro"), grupos.stream().map(GrupoAtendimento::titulo).toList());
        assertEquals(List.of("Bia", "Caio", "Edu"),
                grupos.getFirst().reservas().stream().map(Reserva::primeiroNome).toList(),
                "dentro do grupo mantém a ordem de chegada");
    }

    @Test
    void empateDeTamanhoFavoreceQuemChegouPrimeiro() {
        List<GrupoAtendimento> grupos = new AgrupadaPorTopico().organizar(List.of(
                r(1, "Ana", "Streams"), r(2, "Bia", "SOLID")));
        assertEquals("Streams", grupos.getFirst().titulo());
    }

    @Test
    void ordemDeChegadaMantemUmPorGrupo() {
        List<GrupoAtendimento> grupos = new OrdemDeChegada().organizar(fila);

        assertEquals(6, grupos.size());
        assertEquals(List.of("Ana", "Bia", "Caio", "Duda", "Edu", "Fabi"),
                grupos.stream().map(g -> g.reservas().getFirst().primeiroNome()).toList());
    }

    @Test
    void catalogoUsaPrimeiraPoliticaQuandoIdDesconhecido() {
        CatalogoPoliticas catalogo = CatalogoPoliticas.padrao();
        assertEquals("topico", catalogo.porId("inexistente").id());
        assertEquals("chegada", catalogo.porId("chegada").id());
    }
}
