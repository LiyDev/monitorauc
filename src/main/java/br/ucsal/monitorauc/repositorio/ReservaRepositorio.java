package br.ucsal.monitorauc.repositorio;

import br.ucsal.monitorauc.dominio.Reserva;

import java.util.List;
import java.util.Optional;

public interface ReservaRepositorio {
    void salvar(Reserva reserva);
    Optional<Reserva> buscarPorCodigo(String codigo);
    List<Reserva> listarPorSessao(String sessaoId);
}
