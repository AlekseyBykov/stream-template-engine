package dev.abykov.streamtemplate;

import java.util.function.Supplier;

public final class TemplateEngineBuilder {

    TemplateEngineBuilder() {
    }

    public TemplateEngineBuilder replace(
            String placeholder,
            String value
    ) {
        return this;
    }

    public TemplateEngineBuilder replace(
            String placeholder,
            Supplier<String> supplier
    ) {
        return this;
    }

    public TemplateEngine build() {
        return new TemplateEngine();
    }
}
