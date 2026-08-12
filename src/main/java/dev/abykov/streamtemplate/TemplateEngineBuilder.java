package dev.abykov.streamtemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class TemplateEngineBuilder {

    private final List<Placeholder> placeholders = new ArrayList<>();

    TemplateEngineBuilder() {
    }

    public TemplateEngineBuilder replace(
            String placeholder,
            String value
    ) {
        Objects.requireNonNull(value);

        return replace(placeholder, () -> value);
    }

    public TemplateEngineBuilder replace(
            String placeholder,
            Supplier<String> supplier
    ) {
        Objects.requireNonNull(placeholder, "placeholder must not be null");
        Objects.requireNonNull(supplier, "replacement supplier must not be null");

        if (placeholder.isEmpty()) {
            throw new IllegalArgumentException("placeholder must not be empty");
        }

        if (containsPlaceholder(placeholder)) {
            throw new IllegalArgumentException(
                    "placeholder is already registered: " + placeholder
            );
        }

        placeholders.add(new Placeholder(placeholder, supplier));

        return this;
    }

    private boolean containsPlaceholder(String placeholder) {
        return placeholders.stream()
                .anyMatch(existing -> existing.pattern().equals(placeholder));
    }

    public TemplateEngine build() {
        return new TemplateEngine(placeholders);
    }
}
