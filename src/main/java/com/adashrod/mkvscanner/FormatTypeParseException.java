package com.adashrod.mkvscanner;

/**
 * Thrown when the library can't parse the format type of a track. This could be thrown if the format of the track
 * metadata can't be parsed.
 */
public class FormatTypeParseException extends Exception {
    private final String formatTypeToken;

    public FormatTypeParseException(final String message, final String formatTypeToken) {
        super(message);
        this.formatTypeToken = formatTypeToken;
    }

    public String getFormatTypeToken() {
        return formatTypeToken;
    }
}
