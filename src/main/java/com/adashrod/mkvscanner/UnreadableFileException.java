package com.adashrod.mkvscanner;

/**
 * Tried to scan a file as an mkv that wasn't a video container
 */
public class UnreadableFileException extends DemuxerException {
    public UnreadableFileException(final String filename, final String arguments, final String demuxerOutput) {
        super(filename, arguments, demuxerOutput);
    }
}
