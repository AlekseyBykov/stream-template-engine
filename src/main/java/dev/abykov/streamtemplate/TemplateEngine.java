package dev.abykov.streamtemplate;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public final class TemplateEngine {

    public static TemplateEngineBuilder builder() {
        return new TemplateEngineBuilder();
    }

    public void process(
            InputStream in,
            OutputStream out
    ) {
    }

    public void process(
            Path input,
            Path output
    ) {
    }
}
