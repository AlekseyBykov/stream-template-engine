package dev.abykov.streamtemplate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TemplateEngineTest {

    @TempDir
    Path tempDir;

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

    @Test
    void shouldProcessFiles() throws IOException {
        TemplateEngine engine = TemplateEngine.builder()
                .replace("$name", "Alexey")
                .replace("$project", "stream-template-engine")
                .build();

        Path input = tempDir.resolve("input.txt");
        Path output = tempDir.resolve("output.txt");

        Files.writeString(
                input,
                "Hello $name! Welcome to $project.",
                StandardCharsets.UTF_8
        );

        engine.process(input, output);

        assertEquals(
                "Hello Alexey! Welcome to stream-template-engine.",
                Files.readString(output, StandardCharsets.UTF_8)
        );
    }

    @Test
    void shouldWrapIOExceptionWhenInputFileDoesNotExist() {
        TemplateEngine engine = TemplateEngine.builder()
                .replace("$name", "Alexey")
                .build();

        Path input = tempDir.resolve("missing.txt");
        Path output = tempDir.resolve("output.txt");

        assertThrows(
                UncheckedIOException.class,
                () -> engine.process(input, output)
        );
    }

    @Test
    void shouldNotCloseProvidedStreams() {
        TemplateEngine engine = TemplateEngine.builder()
                .replace("$name", "Alexey")
                .build();

        CloseTrackingInputStream input = new CloseTrackingInputStream(
                "Hello $name".getBytes(StandardCharsets.UTF_8)
        );

        CloseTrackingOutputStream output = new CloseTrackingOutputStream();

        engine.process(input, output);

        assertFalse(input.isClosed());
        assertFalse(output.isClosed());
    }

    @Test
    void shouldEvaluateSupplierLazilyForEachOccurrence() {
        AtomicInteger counter = new AtomicInteger();

        TemplateEngine engine = TemplateEngine.builder()
                .replace(
                        "$value",
                        () -> "value-" + counter.incrementAndGet()
                )
                .build();

        ByteArrayInputStream input = new ByteArrayInputStream(
                "$value $value $value".getBytes(StandardCharsets.UTF_8)
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        engine.process(input, output);

        assertEquals(
                "value-1 value-2 value-3",
                output.toString(StandardCharsets.UTF_8)
        );

        assertEquals(3, counter.get());
    }

    @Test
    void shouldKeepSupplierUnevaluatedWhenPlaceholderIsAbsent() {
        AtomicInteger counter = new AtomicInteger();

        TemplateEngine engine = TemplateEngine.builder()
                .replace(
                        "$value",
                        () -> {
                            counter.incrementAndGet();
                            return "replacement";
                        }
                )
                .build();

        ByteArrayInputStream input = new ByteArrayInputStream(
                "Hello, world!".getBytes(StandardCharsets.UTF_8)
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        engine.process(input, output);

        assertEquals(0, counter.get());
    }
}
