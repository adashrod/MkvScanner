package com.adashrod.mkvscanner.util;

/**
 * Immutable result of running an external process: its exit value and the full contents of its stdout and stderr
 * streams.
 */
public class ProcessResult {
    private final int exitValue;
    private final String stdout;
    private final String stderr;

    public ProcessResult(final int exitValue, final String stdout, final String stderr) {
        this.exitValue = exitValue;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public int getExitValue() {
        return exitValue;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }
}
