package br.ucsal.monitorauc.repositorio;

import br.ucsal.monitorauc.dominio.SessaoMonitoria;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessaoRepositorioEmMemoria implements SessaoRepositorio {

    private final Map<String, SessaoMonitoria> sessoes = new ConcurrentHashMap<>();

    @Override
    public void salvar(SessaoMonitoria sessao) {
        sessoes.put(sessao.id(), sessao);
    }

    @Override
    public Optional<SessaoMonitoria> buscar(String id) {
        return id == null ? Optional.empty() : Optional.ofNullable(sessoes.get(id));
    }

    @Override
    public List<SessaoMonitoria> listarTodas() {
        return sessoes.values().stream()
                .sorted(Comparator.comparing(SessaoMonitoria::inicio).thenComparing(SessaoMonitoria::id))
                .toList();
    }
}
