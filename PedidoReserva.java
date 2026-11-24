package br.ucsal.monitorauc.dominio;

import br.ucsal.monitorauc.validacao.Obrigatorio;
import br.ucsal.monitorauc.validacao.Padrao;
import br.ucsal.monitorauc.validacao.Tamanho;

public record PedidoReserva(
        @Obrigatorio(rotulo = "a sessão")
        String sessaoId,

        @Obrigatorio(rotulo = "a matrícula")
        @Padrao(regex = "\\d{6,12}", mensagem = "A matrícula deve conter apenas números (6 a 12 dígitos).")
        String matricula,

        @Obrigatorio(rotulo = "o primeiro nome")
        @Tamanho(rotulo = "O primeiro nome", min = 2, max = 30)
        @Padrao(regex = "[\\p{L}' -]+", mensagem = "O primeiro nome deve conter apenas letras.")
        String primeiroNome,

        @Obrigatorio(rotulo = "o tópico da dúvida")
        String topico,

        @Tamanho(rotulo = "A descrição da dúvida", max = 280)
        String descricao) {

    public PedidoReserva normalizado() {
        return new PedidoReserva(strip(sessaoId), strip(matricula), strip(primeiroNome), strip(topico), strip(descricao));
    }

    private static String strip(String s) {
        return s == null ? null : s.strip();
    }
}
