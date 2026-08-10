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
        placeholders.add(
                new Placeholder(
                        Objects.requireNonNull(placeholder),
                        Objects.requireNonNull(supplier)
                )
        );

        return this;
    }

    public TemplateEngine build() {
        return new TemplateEngine(placeholders);
    }
}
