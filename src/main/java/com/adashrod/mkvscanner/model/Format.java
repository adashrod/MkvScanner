package com.adashrod.mkvscanner.model;

/**
 * Created by aaron on 2016-01-09.
 */
public class Format {
    public static final Format OGM_CHAPTERS;

    static {
        OGM_CHAPTERS = new Format();
        OGM_CHAPTERS.setFormatType(FormatType.CHAPTERS);
        OGM_CHAPTERS.setName("OGM Chapters");
    }

    private String name;
    /**
     * todo: replace with enum
     */
    private FormatType formatType;

    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }
    public FormatType getFormatType() {
        return formatType;
    }
    public void setFormatType(final FormatType formatType) {
        this.formatType = formatType;
    }

    @Override
    public String toString() {
        return String.format("Format[name=%s, formatType=%s]", name, formatType);
    }
}
