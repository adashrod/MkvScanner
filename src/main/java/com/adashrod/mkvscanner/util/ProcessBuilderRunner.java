package com.adashrod.mkvscanner.util;

import java.io.IOException;
import java.util.List;

/**
 * Production {@link ProcessRunner} that launches the process with a {@link ProcessBuilder} and drains its stdout and
 * stderr with {@link StreamConsumer} threads to avoid the buffer-deadlock described in {@link StreamConsumer}.
 */
public class ProcessBuilderRunner implements ProcessRunner {
    @Override
    public ProcessResult run(final List<String> command) throws IOException, InterruptedException {
        final ProcessBuilder builder = new ProcessBuilder(command);
        Process process = null;
        try {
            process = builder.start();
            final StreamConsumer stdOut = new StreamConsumer(process.getInputStream());
            final StreamConsumer stdErr = new StreamConsumer(process.getErrorStream());
            stdOut.start();
            stdErr.start();
            final int exitValue = process.waitFor();
            return new ProcessResult(exitValue, stdOut.getStreamContent(), stdErr.getStreamContent());
        } catch (final InterruptedException ie) {
            process.destroy();
            throw ie;
        }
    }
}
