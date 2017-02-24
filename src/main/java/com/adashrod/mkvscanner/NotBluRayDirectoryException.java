package com.adashrod.mkvscanner;

/**
 * Tried to scan a dir that isn't a BD dir
 */
public class NotBluRayDirectoryException extends DemuxerException {
    public NotBluRayDirectoryException(final String filename, final String arguments, final String demuxerOutput) {
        super(filename, arguments, demuxerOutput);
    }
}
