package dev.abykov.streamtemplate;

import java.util.Objects;
import java.util.function.Supplier;

final class Placeholder {

    private final String pattern;
    private final Supplier<String> replacementSupplier;

    Placeholder(String pattern, Supplier<String> replacementSupplier) {
        this.pattern = Objects.requireNonNull(pattern);
        this.replacementSupplier = Objects.requireNonNull(replacementSupplier);
    }

    String pattern() {
        return pattern;
    }

    String replacement() {
        return Objects.requireNonNull(
                replacementSupplier.get(),
                "replacement supplier must not return null"
        );
    }
}
