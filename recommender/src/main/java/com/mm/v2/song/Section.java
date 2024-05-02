package com.mm.v2.song;

public class Section {
    private float start;
    private float duration;
    private float tempo;


    public Section(float start, float duration, float tempo) {
        this.start = start;
        this.duration = duration;
        this.tempo = tempo;
    }

    public float getStart() {
        return this.start;
    }

    public float getDuration() {
        return this.duration;
    }

    public float getTempo() {
        return this.tempo;
    }
}
