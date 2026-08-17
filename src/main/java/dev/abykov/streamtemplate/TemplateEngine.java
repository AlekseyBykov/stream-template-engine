package dev.abykov.streamtemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class TemplateEngine {

    private final List<Placeholder> placeholders;

    TemplateEngine(List<Placeholder> placeholders) {
        this.placeholders = List.copyOf(placeholders);
    }

    public static TemplateEngineBuilder builder() {
        return new TemplateEngineBuilder();
    }

    /**
     * Processes data from the given input stream and writes the result
     * to the given output stream.
     *
     * <p>This method does not close either stream.
     */
    public void process(InputStream input, OutputStream output) {
        try {
            processStream(input, output);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void process(Path input, Path output) {
        Objects.requireNonNull(input, "input must not be null");
        Objects.requireNonNull(output, "output must not be null");

        try (
                InputStream inputStream = Files.newInputStream(input);
                OutputStream outputStream = Files.newOutputStream(output)
        ) {
            processStream(inputStream, outputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void processStream(
            InputStream input,
            OutputStream output
    ) throws IOException {

        Objects.requireNonNull(input, "input must not be null");
        Objects.requireNonNull(output, "output must not be null");

        if (placeholders.isEmpty()) {
            input.transferTo(output);
            return;
        }

        ByteArrayOutputStream pending = new ByteArrayOutputStream();

        int current;

        while ((current = input.read()) != -1) {
            pending.write(current);
            processPending(pending, output, false);
        }

        processPending(pending, output, true);
    }

    private void processPending(
            ByteArrayOutputStream pending,
            OutputStream output,
            boolean endOfInput
    ) throws IOException {
        while (pending.size() > 0) {
            byte[] candidate = pending.toByteArray();

            Placeholder exactMatch = findExactMatch(candidate);
            boolean longerMatchPossible = hasLongerMatchingPrefix(candidate);

            if (exactMatch != null && (!longerMatchPossible || endOfInput)) {
                writeReplacement(output, exactMatch);
                pending.reset();
                return;
            }

            if (hasMatchingPrefix(candidate) && !endOfInput) {
                return;
            }

            Placeholder longestMatch = findLongestMatchingPrefix(candidate);

            if (longestMatch != null) {
                byte[] pattern = longestMatch.patternBytes();

                writeReplacement(output, longestMatch);

                pending.reset();
                pending.write(
                        candidate,
                        pattern.length,
                        candidate.length - pattern.length
                );

                continue;
            }

            output.write(candidate[0]);

            pending.reset();
            pending.write(candidate, 1, candidate.length - 1);
        }
    }

    private Placeholder findExactMatch(byte[] candidate) {
        for (Placeholder placeholder : placeholders) {
            byte[] pattern = placeholder.patternBytes();

            if (pattern.length == candidate.length
                && startsWith(pattern, candidate)) {
                return placeholder;
            }
        }

        return null;
    }

    private boolean hasMatchingPrefix(byte[] candidate) {
        for (Placeholder placeholder : placeholders) {
            byte[] pattern = placeholder.patternBytes();

            if (candidate.length <= pattern.length
                && startsWith(pattern, candidate)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasLongerMatchingPrefix(byte[] candidate) {
        for (Placeholder placeholder : placeholders) {
            byte[] pattern = placeholder.patternBytes();

            if (candidate.length < pattern.length
                && startsWith(pattern, candidate)) {
                return true;
            }
        }

        return false;
    }

    private Placeholder findLongestMatchingPrefix(byte[] candidate) {
        Placeholder longestMatch = null;
        int longestMatchLength = 0;

        for (Placeholder placeholder : placeholders) {
            byte[] pattern = placeholder.patternBytes();

            if (pattern.length <= candidate.length
                && startsWith(candidate, pattern)
                && pattern.length > longestMatchLength) {

                longestMatch = placeholder;
                longestMatchLength = pattern.length;
            }
        }

        return longestMatch;
    }

    private boolean startsWith(byte[] value, byte[] prefix) {
        if (prefix.length > value.length) {
            return false;
        }

        for (int i = 0; i < prefix.length; i++) {
            if (value[i] != prefix[i]) {
                return false;
            }
        }

        return true;
    }

    private void writeReplacement(
            OutputStream output,
            Placeholder placeholder
    ) throws IOException {
        output.write(
                toBytes(placeholder.replacement())
        );
    }

    private byte[] toBytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
