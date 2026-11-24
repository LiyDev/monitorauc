package br.ucsal.monitorauc.validacao;

import java.util.List;

public class ErrosDeValidacao extends RuntimeException {

    private final List<String> mensagens;

    public ErrosDeValidacao(List<String> mensagens) {
        super(String.join("; ", mensagens));
        this.mensagens = List.copyOf(mensagens);
    }

    public List<String> mensagens() {
        return mensagens;
    }
}
