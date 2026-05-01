package br.com.fiap.techchallenge.feedbackplatform.domain.rules;

import java.text.Normalizer;
import java.util.Objects;
import java.util.Set;

public class CriticalKeywordMatcher {

    private final Set<String> palavrasCriticas;

    public CriticalKeywordMatcher() {
        this(Set.of(
                "erro",
                "travando",
                "bug",
                "pessimo",
                "horrivel",
                "nao funciona",
                "reclamacao",
                "insuportavel"
        ));
    }

    public CriticalKeywordMatcher(Set<String> palavrasCriticas) {
        this.palavrasCriticas = Objects.requireNonNull(palavrasCriticas)
                .stream()
                .map(CriticalKeywordMatcher::normalizar)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    public boolean contemPalavraCritica(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            return false;
        }

        String descricaoNormalizada = normalizar(descricao);
        return palavrasCriticas.stream().anyMatch(descricaoNormalizada::contains);
    }

    private static String normalizar(String valor) {
        String semAcentos = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return semAcentos.toLowerCase().trim();
    }
}
