package com.adashrod.mkvscanner.model;

/**
 * Created by aaron on 2016-01-09.
 */
public class Format {
    private String name;
    private String formatType;

    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }
    public String getFormatType() {
        return formatType;
    }
    public void setFormatType(final String formatType) {
        this.formatType = formatType;
    }

    @Override
    public String toString() {
        return String.format("Format[name=%s, formatType=%s]", name, formatType);
    }
}
