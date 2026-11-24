package br.ucsal.monitorauc.validacao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class Validador {

    private static final Map<String, Pattern> PADROES = new ConcurrentHashMap<>();

    private Validador() {
    }

    public static <T extends Record> T validar(T objeto) {
        List<String> erros = new ArrayList<>();
        for (RecordComponent componente : objeto.getClass().getRecordComponents()) {
            Object valor = ler(componente, objeto);
            String texto = valor == null ? null : valor.toString().strip();

            Obrigatorio obrigatorio = componente.getAnnotation(Obrigatorio.class);
            if (obrigatorio != null && (texto == null || texto.isEmpty())) {
                erros.add("Informe " + obrigatorio.rotulo() + ".");
                continue;
            }
            if (texto == null || texto.isEmpty()) {
                continue;
            }

            Tamanho tamanho = componente.getAnnotation(Tamanho.class);
            if (tamanho != null && (texto.length() < tamanho.min() || texto.length() > tamanho.max())) {
                erros.add(tamanho.min() > 0
                        ? tamanho.rotulo() + " deve ter entre " + tamanho.min() + " e " + tamanho.max() + " caracteres."
                        : tamanho.rotulo() + " deve ter no máximo " + tamanho.max() + " caracteres.");
            }

            Padrao padrao = componente.getAnnotation(Padrao.class);
            if (padrao != null && !PADROES.computeIfAbsent(padrao.regex(), Pattern::compile).matcher(texto).matches()) {
                erros.add(padrao.mensagem());
            }
        }
        if (!erros.isEmpty()) {
            throw new ErrosDeValidacao(erros);
        }
        return objeto;
    }

    private static Object ler(RecordComponent componente, Object objeto) {
        try {
            return componente.getAccessor().invoke(objeto);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Não foi possível ler " + componente.getName(), e);
        }
    }
}
