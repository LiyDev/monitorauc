package br.ucsal.monitorauc.web;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Servidor implements AutoCloseable {

    private final HttpServer http;
    private final ExecutorService executor;

    public Servidor(int porta, List<PaginaBase> paginas) throws IOException {
        this.http = HttpServer.create(new InetSocketAddress(porta), 0);
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        http.setExecutor(executor);
        paginas.forEach(p -> http.createContext(p.caminho(), p));
    }

    public Servidor iniciar() {
        http.start();
        return this;
    }

    public int porta() {
        return http.getAddress().getPort();
    }

    @Override
    public void close() {
        http.stop(0);
        executor.close();
    }
}
