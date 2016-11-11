package com.felipeacerbi.nfctest.models;

import android.location.Location;

public class Path {

    private Location from;
    private Location to;
    private String duration;

    public Path() {
    }

    public Path(Location from, Location to, String duration) {
        this.from = from;
        this.to = to;
        this.duration = duration;
    }

    public Location getFrom() {
        return from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
