package br.ucsal.monitorauc.demo;

import br.ucsal.monitorauc.dominio.Disciplina;
import br.ucsal.monitorauc.dominio.Monitor;
import br.ucsal.monitorauc.dominio.PedidoReserva;
import br.ucsal.monitorauc.dominio.SessaoMonitoria;
import br.ucsal.monitorauc.repositorio.SessaoRepositorio;
import br.ucsal.monitorauc.servico.ReservaService;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public final class DadosDemonstracao {

    public static final Disciplina POOA = new Disciplina("POOA", "Programação Orientada a Objetos Avançada",
            List.of("Herança e polimorfismo", "Padrões de projeto (GoF)", "SOLID", "Collections e Streams",
                    "Exceções", "Threads e concorrência", "Servlets e web", "Outro"));
    public static final Disciplina ED = new Disciplina("ED", "Estrutura de Dados",
            List.of("Listas encadeadas", "Pilhas e filas", "Árvores", "Ordenação", "Recursão", "Outro"));
    public static final Disciplina BD = new Disciplina("BD", "Banco de Dados",
            List.of("Modelagem ER", "Normalização", "SQL - consultas", "SQL - joins", "Outro"));
    public static final Disciplina CALC = new Disciplina("CALC1", "Cálculo I",
            List.of("Limites", "Derivadas", "Regra da cadeia", "Integrais", "Outro"));

    private DadosDemonstracao() {
    }

    public static void carregar(SessaoRepositorio repositorio, Clock relogio) {
        LocalDate hoje = LocalDate.now(relogio);
        LocalDate d1 = proximoDiaUtil(hoje.plusDays(1));
        LocalDate d2 = proximoDiaUtil(d1.plusDays(1));
        LocalDate d3 = proximoDiaUtil(d2.plusDays(1));

        Monitor ana = new Monitor("m1", "Ana (monitora fictícia)");
        Monitor bruno = new Monitor("m2", "Bruno (monitor fictício)");
        Monitor carla = new Monitor("m3", "Carla (monitora fictícia)");
        Monitor davi = new Monitor("m4", "Davi (monitor fictício)");

        List.of(
                sessao("S1", POOA, ana, d1, 14, 0, 90, "Laboratório 3 - Bloco B", 8),
                sessao("S2", POOA, ana, d2, 18, 30, 60, "Sala 205 - Bloco A", 6),
                sessao("S3", ED, bruno, d1, 10, 0, 60, "Laboratório 1 - Bloco B", 6),
                sessao("S4", BD, carla, d2, 15, 0, 60, "Online (link no Classroom)", 10),
                sessao("S5", CALC, davi, d3, 9, 0, 90, "Sala 110 - Bloco C", 3)
        ).forEach(repositorio::salvar);
    }

    public static void carregarReservasExemplo(ReservaService servico) {
        servico.reservar(new PedidoReserva("S1", "100000001", "Lucas", "Padrões de projeto (GoF)",
                "Quando usar Strategy em vez de if/else?"));
        servico.reservar(new PedidoReserva("S1", "100000002", "Marina", "Collections e Streams",
                "groupingBy com mais de um nível"));
        servico.reservar(new PedidoReserva("S1", "100000003", "Pedro", "Padrões de projeto (GoF)",
                "Diferença entre Observer e callback"));
        servico.reservar(new PedidoReserva("S5", "100000004", "Júlia", "Derivadas", ""));
        servico.reservar(new PedidoReserva("S5", "100000005", "Rafael", "Regra da cadeia", ""));
    }

    private static SessaoMonitoria sessao(String id, Disciplina d, Monitor m, LocalDate dia,
                                          int hora, int minuto, int duracao, String local, int capacidade) {
        LocalDateTime inicio = LocalDateTime.of(dia, LocalTime.of(hora, minuto));
        return new SessaoMonitoria(id, d, m, inicio, duracao, local, capacidade);
    }

    private static LocalDate proximoDiaUtil(LocalDate data) {
        LocalDate d = data;
        while (d.getDayOfWeek() == DayOfWeek.SATURDAY || d.getDayOfWeek() == DayOfWeek.SUNDAY) {
            d = d.plusDays(1);
        }
        return d;
    }
}
