package br.ucsal.monitorauc.dominio;

public enum StatusReserva {
    CONFIRMADA("Confirmada"),
    ATENDIDA("Atendida"),
    CANCELADA("Cancelada");

    private final String rotulo;

    StatusReserva(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public boolean ocupaVaga() {
        return this != CANCELADA;
    }
}
