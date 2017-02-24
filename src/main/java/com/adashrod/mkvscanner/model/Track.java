package com.adashrod.mkvscanner.model;

/**
 * Created by aaron on 2016-01-09.
 */
public class Track {
    private int number;
    private String name;
    private Format format;
    private String language;

    public int getNumber() {
        return number;
    }
    public void setNumber(final int number) {
        this.number = number;
    }
    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }
    public Format getFormat() {
        return format;
    }
    public void setFormat(final Format format) {
        this.format = format;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(final String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return String.format("Track[number=%d, name=%s, format=%s, language=%s]", number, name, format, language);
    }
}
