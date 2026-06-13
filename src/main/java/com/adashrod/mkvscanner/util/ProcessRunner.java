package com.adashrod.mkvscanner.util;

import java.io.IOException;
import java.util.List;

/**
 * An abstraction over launching an external process and capturing its output. This is the seam that allows the process
 * execution to be mocked in tests so that fake stdout/exit codes can be fed to the code that parses and interprets
 * scanner output.
 */
public interface ProcessRunner {
    /**
     * Runs the given command, blocks until it exits, and returns its exit value and captured stdout/stderr.
     * @param command the command and its arguments (e.g. as passed to {@link ProcessBuilder})
     * @return the result of running the process
     * @throws IOException          if the process can't be started or an I/O error occurs
     * @throws InterruptedException if the current thread is interrupted while waiting for the process to finish
     */
    ProcessResult run(List<String> command) throws IOException, InterruptedException;
}
