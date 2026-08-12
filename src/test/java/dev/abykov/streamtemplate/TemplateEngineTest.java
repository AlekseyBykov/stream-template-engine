package dev.abykov.streamtemplate;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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

    @Test
    void shouldPreserveContentWhenPlaceholderIsAbsent() {
        TemplateEngine engine = TemplateEngine.builder()
                .replace("${name}", "Alexey")
                .build();

        byte[] source = "Hello, world!".getBytes(StandardCharsets.UTF_8);

        ByteArrayInputStream input = new ByteArrayInputStream(source);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        engine.process(input, output);

        assertArrayEquals(source, output.toByteArray());
    }

    @Test
    void shouldReplaceMultiplePlaceholders() throws IOException {
        TemplateEngine engine = TemplateEngine.builder()
                .replace("$name", "Alexey")
                .replace("$project", "stream-template-engine")
                .build();

        ByteArrayInputStream input =
                new ByteArrayInputStream(
                        "Hello $name! Welcome to $project."
                                .getBytes(StandardCharsets.UTF_8)
                );

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        engine.process(input, output);

        assertEquals(
                "Hello Alexey! Welcome to stream-template-engine.",
                output.toString(StandardCharsets.UTF_8)
        );
    }

    @Test
    void shouldUseLongestMatchingPlaceholder() {
        TemplateEngine engine = TemplateEngine.builder()
                .replace("$id", "short")
                .replace("$identifier", "long")
                .build();

        ByteArrayInputStream input =
                new ByteArrayInputStream(
                        "$identifier".getBytes(StandardCharsets.UTF_8)
                );

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        engine.process(input, output);

        assertEquals(
                "long",
                output.toString(StandardCharsets.UTF_8)
        );
    }

    @Test
    void shouldPreservePartialPlaceholderMatch() {
        TemplateEngine engine = TemplateEngine.builder()
                .replace("$project", "stream-template-engine")
                .build();

        ByteArrayInputStream input = new ByteArrayInputStream(
                "Hello $proX world".getBytes(StandardCharsets.UTF_8)
        );

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        engine.process(input, output);

        assertEquals(
                "Hello $proX world",
                output.toString(StandardCharsets.UTF_8)
        );
    }

    @Test
    void shouldReplaceRepeatedPlaceholder() {
        TemplateEngine engine = TemplateEngine.builder()
                .replace("$name", "Alexey")
                .build();

        ByteArrayInputStream input = new ByteArrayInputStream(
                "$name $name $name".getBytes(StandardCharsets.UTF_8)
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        engine.process(input, output);

        assertEquals(
                "Alexey Alexey Alexey",
                output.toString(StandardCharsets.UTF_8)
        );
    }

    @Test
    void shouldPreservePlaceholderPrefixAtEndOfInput() {
        TemplateEngine engine = TemplateEngine.builder()
                .replace("$project", "stream-template-engine")
                .build();

        ByteArrayInputStream input = new ByteArrayInputStream(
                "Hello $pro".getBytes(StandardCharsets.UTF_8)
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        engine.process(input, output);

        assertEquals(
                "Hello $pro",
                output.toString(StandardCharsets.UTF_8)
        );
    }

    @Test
    void shouldProcessEmptyInput() {
        TemplateEngine engine = TemplateEngine.builder()
                .replace("$name", "Alexey")
                .build();

        ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        engine.process(input, output);

        assertEquals(0, output.size());
    }

    @Test
    void shouldCopyInputWhenNoPlaceholdersConfigured() {
        TemplateEngine engine = TemplateEngine.builder()
                .build();

        byte[] source = "Hello, world!".getBytes(StandardCharsets.UTF_8);

        ByteArrayInputStream input = new ByteArrayInputStream(source);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        engine.process(input, output);

        assertArrayEquals(source, output.toByteArray());
    }
}
