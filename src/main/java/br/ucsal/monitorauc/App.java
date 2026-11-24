package br.ucsal.monitorauc;

import br.ucsal.monitorauc.demo.DadosDemonstracao;
import br.ucsal.monitorauc.fila.CatalogoPoliticas;
import br.ucsal.monitorauc.repositorio.ReservaRepositorioEmMemoria;
import br.ucsal.monitorauc.repositorio.SessaoRepositorio;
import br.ucsal.monitorauc.repositorio.SessaoRepositorioEmMemoria;
import br.ucsal.monitorauc.servico.RegistroAtividades;
import br.ucsal.monitorauc.servico.ReservaService;
import br.ucsal.monitorauc.web.PaginaConsulta;
import br.ucsal.monitorauc.web.PaginaInicio;
import br.ucsal.monitorauc.web.PaginaMonitor;
import br.ucsal.monitorauc.web.PaginaPainel;
import br.ucsal.monitorauc.web.PaginaReserva;
import br.ucsal.monitorauc.web.PaginaSessao;
import br.ucsal.monitorauc.web.Servidor;

import java.io.IOException;
import java.time.Clock;
import java.util.List;

public final class App {

    private App() {
    }

    public static Servidor criar(int porta, Clock relogio, boolean comReservasExemplo) throws IOException {
        SessaoRepositorio sessoes = new SessaoRepositorioEmMemoria();
        ReservaService servico = new ReservaService(sessoes, new ReservaRepositorioEmMemoria(), relogio);
        RegistroAtividades atividades = new RegistroAtividades(500);
        servico.registrar(atividades);

        DadosDemonstracao.carregar(sessoes, relogio);
        if (comReservasExemplo) {
            DadosDemonstracao.carregarReservasExemplo(servico);
        }

        return new Servidor(porta, List.of(
                new PaginaInicio(servico),
                new PaginaSessao(servico),
                new PaginaReserva(servico),
                new PaginaConsulta(),
                new PaginaMonitor(sessoes, servico),
                new PaginaPainel(servico, CatalogoPoliticas.padrao(), atividades)));
    }

    public static void main(String[] args) throws IOException {
        int porta = 8080;
        boolean exemplos = true;
        for (String a : args) {
            if (a.equals("--vazio")) {
                exemplos = false;
            } else {
                porta = Integer.parseInt(a);
            }
        }
        String env = System.getenv("PORT");
        if (env != null && !env.isBlank() && args.length == 0) {
            porta = Integer.parseInt(env);
        }

        Servidor servidor = criar(porta, Clock.systemDefaultZone(), exemplos).iniciar();
        Runtime.getRuntime().addShutdownHook(new Thread(servidor::close));
        System.out.println("MonitoraUC rodando em http://localhost:" + servidor.porta());
        System.out.println("Estudante: http://localhost:" + servidor.porta() + "/");
        System.out.println("Monitor:   http://localhost:" + servidor.porta() + "/monitor");
        System.out.println("Pressione Ctrl+C para encerrar.");
    }
}
