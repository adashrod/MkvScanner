package com.adashrod.mkvscanner.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaron on 2016-01-09.
 */
public class Video {
    private final List<Track> tracks = new ArrayList<>();

    public void addTrack(final Track track) {
        tracks.add(track);
    }

    public List<Track> getTracks() {
        return new ArrayList<>(tracks);
    }

    @Override
    public String toString() {
        return String.format("Video[tracks = %s]", tracks);
    }
}
