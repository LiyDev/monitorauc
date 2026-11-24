package br.ucsal.monitorauc.repositorio;

import br.ucsal.monitorauc.dominio.SessaoMonitoria;

import java.util.List;
import java.util.Optional;

public interface SessaoRepositorio {
    void salvar(SessaoMonitoria sessao);
    Optional<SessaoMonitoria> buscar(String id);
    List<SessaoMonitoria> listarTodas();
}
