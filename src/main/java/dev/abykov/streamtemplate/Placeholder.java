package dev.abykov.streamtemplate;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Supplier;

final class Placeholder {

    private final String pattern;
    private final byte[] patternBytes;
    private final Supplier<String> replacementSupplier;

    Placeholder(
            String pattern,
            Supplier<String> replacementSupplier
    ) {
        this.pattern = Objects.requireNonNull(pattern);
        this.patternBytes = pattern.getBytes(StandardCharsets.UTF_8);
        this.replacementSupplier = Objects.requireNonNull(replacementSupplier);
    }

    String pattern() {
        return pattern;
    }

    byte[] patternBytes() {
        return patternBytes;
    }

    String replacement() {
        return replacementSupplier.get();
    }
}
