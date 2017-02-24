package com.adashrod.mkvscanner.util;

import java.util.Iterator;

/**
 * An implementation of an {@link java.util.Iterator} that splits a string on line-breaks. Each call to next() will
 * return the next "line" in the string.
 */
public class StringLineIterator implements Iterator<String>, Iterable<String> {
    private final String string;
    private int cursor;

    public StringLineIterator(final String string) {
        this.string = string;
    }

    @Override
    public Iterator<String> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return cursor < string.length();
    }

    @Override
    public String next() {
        final int carriageReturn = string.indexOf('\r', cursor);
        final int lineFeed = string.indexOf('\n', cursor);
        final int pos;
        int increment = 1;
        if (carriageReturn == -1) {
            if (lineFeed == -1) {
                pos = string.length(); // last line
            } else {
                pos = lineFeed; // Unix
            }
        } else {
            if (lineFeed == -1) {
                pos = carriageReturn; // Mac
            } else {
                pos = Math.min(carriageReturn, lineFeed); // Windows
                increment = 2; // since there were two characters "\r\n" delimiting lines
            }
        }
        final String result = string.substring(cursor, pos);
        cursor = pos + increment;
        return result;
    }

    public String getString() {
        return string;
    }
}
