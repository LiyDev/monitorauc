package br.ucsal.monitorauc.repositorio;

import br.ucsal.monitorauc.dominio.Reserva;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ReservaRepositorioEmMemoria implements ReservaRepositorio {

    private final Map<String, Reserva> porCodigo = new ConcurrentHashMap<>();

    @Override
    public void salvar(Reserva reserva) {
        porCodigo.put(reserva.codigo(), reserva);
    }

    @Override
    public Optional<Reserva> buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(porCodigo.get(codigo.strip().toUpperCase(Locale.ROOT)));
    }

    @Override
    public List<Reserva> listarPorSessao(String sessaoId) {
        return porCodigo.values().stream()
                .filter(r -> r.sessaoId().equals(sessaoId))
                .sorted(Comparator.comparingLong(Reserva::sequencia))
                .toList();
    }
}
