package dev.abykov.streamtemplate;

public final class TemplateEngineBuilder {

    public TemplateEngineBuilder replace(
            String placeholder,
            String value
    ) {
        return this;
    }

    public TemplateEngine build() {
        return new TemplateEngine();
    }
}
