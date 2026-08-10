package dev.abykov.streamtemplate;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

public final class TemplateEngine {

    private final List<Placeholder> placeholders;

    TemplateEngine(List<Placeholder> placeholders) {
        this.placeholders = List.copyOf(placeholders);
    }

    public static TemplateEngineBuilder builder() {
        return new TemplateEngineBuilder();
    }

    public void process(InputStream input, OutputStream output) {
    }

    public void process(Path input, Path output) {
    }
}
