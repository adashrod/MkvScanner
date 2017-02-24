package com.adashrod.mkvscanner.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A thread that can be used to ingest all of the characters from a stream and save them in a string. When using
 * {@link java.lang.Runtime#exec(String)} or {@link ProcessBuilder#start()}, the JVM will hang if the stdout and stderr
 * streams are not emptied. Their buffers will fill up and then the program and the external process will be in deadlock.
 * This prevents that scenario while allowing the user to get the content of the stream afterward.
 */
public class StreamConsumer extends Thread {
    private final Logger logger = Logger.getLogger(StreamConsumer.class);

    private InputStream inputStream = null;
    private final StringBuilder streamContent = new StringBuilder();

    public StreamConsumer(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        String line;
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = reader.readLine()) != null) {
                streamContent.append(line).append('\n');
            }
        } catch (final IOException ioe) {
            logger.error(ioe);
        }
    }

    public String getStreamContent() {
        return streamContent.toString();
    }

    // todo: could make a return for a list as well, then you wouldn't need to pass the result to new StringLineIterator()
}
