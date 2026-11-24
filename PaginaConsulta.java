package br.ucsal.monitorauc.web;

public class PaginaConsulta extends PaginaBase {

    public PaginaConsulta() {
        super("/consultar");
    }

    @Override
    protected Resposta get(Requisicao req) {
        return Resposta.ok("Minha reserva", """
                <h1>Consultar minha reserva</h1>
                <p class="sub">Digite o código recebido ao reservar (ex.: MON-AB12C).</p>
                <form class="linha" method="get" action="/reserva">
                  <div style="flex:1;min-width:200px">
                    <label for="codigo">Código da reserva</label>
                    <input id="codigo" name="codigo" required autocomplete="off">
                  </div>
                  <button type="submit">Consultar</button>
                </form>
                """);
    }
}
