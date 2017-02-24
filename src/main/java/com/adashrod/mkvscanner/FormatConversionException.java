package com.adashrod.mkvscanner;

/**
 * Thrown when an attempt is made to demux a track and convert it to a format whose conversion is not supported.
 */
public class FormatConversionException extends DemuxerException {
    public FormatConversionException(final String filename, final String arguments, final String demuxerOutput) {
        super(filename, arguments, demuxerOutput);
    }
}
