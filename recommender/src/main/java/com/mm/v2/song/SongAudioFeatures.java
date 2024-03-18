package com.mm.v2.song;

public class SongAudioFeatures {

    private float acousticness;
    private String analysis_url;
    private float danceability;
    private int duration_ms;
    private float energy;
    private String id;
    private float instrumentalness;
    private int key;
    private float liveness;
    private float loudness;
    private int mode;
    private float speechiness;
    private float tempo;
    private int time_signature;
    private String track_href;
    private String type;
    private String uri;
    private float valence;

    public String getAcousticness() {
        return Float.toString(this.acousticness);
    }

    public String getDanceability() {
        return Float.toString(this.danceability);
    }

    public String getDuration() {
        return Integer.toString(this.duration_ms);
    }

    public String getEnergy()   {
        return Float.toString(this.energy);
    }

    public String getID()   {
        return this.id;
    }

    public String getInstrumentalness() {
        return Float.toString(this.instrumentalness);
    }

    public String getKey()  {
        return Integer.toString(this.key);
    }

    public String getLiveness() {
        return Float.toString(this.liveness);
    }

    public String getLoudness() {
        return Float.toString(this.liveness);
    }

    public String getMode() {
        return Integer.toString(this.mode);
    }

    public String getSpeechiness()  {
        return Float.toString(this.speechiness);
    }

    public String getTempo()    {
        return Float.toString(this.tempo);
    }

    public String getTimeSignature()    {
        return Integer.toString(this.time_signature);
    }

    public String getValence()  {
        return Float.toString(this.valence);
    }
    
}
