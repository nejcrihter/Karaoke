package com.example.karaoke;

import java.time.Duration;

class Subtitle {
    Duration start;
    Duration end;
    String text;

    Subtitle(Duration start, Duration end, String text) {
        this.start = start;
        this.end = end;
        this.text = text;
    }

    public Duration getStart() {
        return start;
    }

    public void setStart(Duration start) {
        this.start = start;
    }

    public Duration getEnd() {
        return end;
    }

    public void setEnd(Duration end) {
        this.end = end;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
