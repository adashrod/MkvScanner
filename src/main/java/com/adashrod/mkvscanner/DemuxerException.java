package com.adashrod.mkvscanner;

/**
 * Generic class for exceptions generated by the demuxer/parent class for more specific exceptions
 */
public class DemuxerException extends Exception {
    private final String filename;
    private final String arguments;
    private final String demuxerOutput;

    public DemuxerException(final String filename, final String arguments, final String demuxerOutput) {
        // todo: change this behavior, or maybe make another constructor so that the short form of the error msg (probably
        // the stderr of eac3to) is all that's in the message field
        super(String.format("file=%s, arguments={%s}, truncated demuxerOutput={%s}", filename, arguments, demuxerOutput.length() < 100 ?
            demuxerOutput: demuxerOutput.substring(0, 100)));
        this.filename = filename;
        this.arguments = arguments;
        this.demuxerOutput = demuxerOutput;
    }

    public String getFilename() {
        return filename;
    }
    public String getArguments() {
        return arguments;
    }
    public String getDemuxerOutput() {
        return demuxerOutput;
    }
}
