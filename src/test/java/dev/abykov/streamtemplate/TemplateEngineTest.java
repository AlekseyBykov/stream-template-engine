package dev.abykov.streamtemplate;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemplateEngineTest {

    @Test
    void shouldReplaceSinglePlaceholder() {
        TemplateEngine engine = TemplateEngine.builder()
                .replace("${name}", "Alexey")
                .build();

        ByteArrayInputStream input = new ByteArrayInputStream(
                "Hello, ${name}!".getBytes(StandardCharsets.UTF_8)
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        engine.process(input, output);

        assertEquals(
                "Hello, Alexey!",
                output.toString(StandardCharsets.UTF_8)
        );
    }
}
