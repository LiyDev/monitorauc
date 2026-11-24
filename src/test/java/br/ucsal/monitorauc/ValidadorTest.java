package br.ucsal.monitorauc;

import br.ucsal.monitorauc.dominio.PedidoReserva;
import br.ucsal.monitorauc.validacao.ErrosDeValidacao;
import br.ucsal.monitorauc.validacao.Validador;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidadorTest {

    @Test
    void pedidoValidoPassaSemErros() {
        PedidoReserva p = new PedidoReserva("S1", "20261234", "Maria José", "SOLID", "Dúvida sobre LSP");
        assertEquals(p, Validador.validar(p));
    }

    @Test
    void leAnotacoesPorReflexaoEAcumulaErros() {
        PedidoReserva p = new PedidoReserva("", "abc", "M", "SOLID", "x".repeat(281));

        ErrosDeValidacao e = assertThrows(ErrosDeValidacao.class, () -> Validador.validar(p));

        assertEquals(4, e.mensagens().size(), e.mensagens().toString());
        assertTrue(e.mensagens().contains("Informe a sessão."));
        assertTrue(e.mensagens().contains("A matrícula deve conter apenas números (6 a 12 dígitos)."));
        assertTrue(e.mensagens().contains("O primeiro nome deve ter entre 2 e 30 caracteres."));
        assertTrue(e.mensagens().contains("A descrição da dúvida deve ter no máximo 280 caracteres."));
    }

    @Test
    void campoOpcionalVazioNaoGeraErro() {
        Validador.validar(new PedidoReserva("S1", "123456", "Lia", "SOLID", null));
    }

    @Test
    void nomeComNumerosEhRecusado() {
        ErrosDeValidacao e = assertThrows(ErrosDeValidacao.class,
                () -> Validador.validar(new PedidoReserva("S1", "123456", "L1a", "SOLID", "")));
        assertEquals(1, e.mensagens().size());
    }
}
