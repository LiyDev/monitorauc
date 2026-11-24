package br.ucsal.monitorauc.servico;

import br.ucsal.monitorauc.dominio.Reserva;

import java.time.LocalDateTime;

public record EventoReserva(Tipo tipo, Reserva reserva, LocalDateTime quando) {

    public enum Tipo {
        CRIADA("Nova reserva"),
        CANCELADA("Reserva cancelada"),
        ATENDIDA("Atendimento concluído");

        private final String rotulo;

        Tipo(String rotulo) {
            this.rotulo = rotulo;
        }

        public String rotulo() {
            return rotulo;
        }
    }
}
