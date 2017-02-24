package com.adashrod.mkvscanner;

/**
 * Blu-ray dir that's still encrypted or otherwise unreadable
 */
public class CorruptBluRayStructureException extends DemuxerException {
    public CorruptBluRayStructureException(final String filename, final String arguments, final String demuxerOutput) {
        super(filename, arguments, demuxerOutput);
    }
}
