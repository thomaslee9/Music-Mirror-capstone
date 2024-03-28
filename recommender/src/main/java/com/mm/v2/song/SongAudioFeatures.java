package com.mm.v2.song;

public class SongAudioFeatures {

    public static String[] base_features = new String[] { "acousticness", "danceability", "energy",
                                                            "instrumentalness", "liveness", "loudness", 
                                                            "speechiness", "tempo", "valence" };

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

    public static Double getFeatureValue(String f, SongAudioFeatures features)   {

        Double result = 0.0;

        if      (f.equals("acousticness"))   {
            result = Double.parseDouble(features.getAcousticness());
        }
        else if (f.equals("danceability"))  {
            result = Double.parseDouble(features.getDanceability());
        }
        else if (f.equals("energy"))    {
            result = Double.parseDouble(features.getEnergy());
        }
        else if (f.equals("instrumentalness"))  {
            result = Double.parseDouble(features.getInstrumentalness());
        }
        else if (f.equals("liveness"))  {
            result = Double.parseDouble(features.getLiveness());
        }
        else if (f.equals("loudness"))  {
            result = Double.parseDouble(features.getLoudness());
        }
        else if (f.equals("speechiness"))  {
            result = Double.parseDouble(features.getSpeechiness());
        }
        else if (f.equals("tempo"))  {
            result = Double.parseDouble(features.getTempo());
        }
        else if (f.equals("valence"))  {
            result = Double.parseDouble(features.getValence());            
        }

        return result;

    }

    public static void setFeatureValue(String f, SongAudioFeatures features, float value)   {

        if      (f.equals("acousticness"))   {
            features.setAcousticness(value);
        }
        else if (f.equals("danceability"))  {
            features.setDanceability(value);
        }
        else if (f.equals("energy"))    {
            features.setEnergy(value);
        }
        else if (f.equals("instrumentalness"))  {
            features.setInstrumentalness(value);
        }
        else if (f.equals("liveness"))  {
            features.setLiveness(value);
        }
        else if (f.equals("loudness"))  {
            features.setLoudness(value);
        }
        else if (f.equals("speechiness"))  {
            features.setSpeechines(value);
        }
        else if (f.equals("tempo"))  {
            features.setTempo(value);
        }
        else if (f.equals("valence"))  {
            features.setValence(value);
        }

    }

    public String getAcousticness() {
        return Float.toString(this.acousticness);
    }

    public void setAcousticness(float acousticness) {
        this.acousticness = acousticness;
    }

    public String getDanceability() {
        return Float.toString(this.danceability);
    }

    public void setDanceability(float danceability) {
        this.danceability = danceability;
    }

    public String getDuration() {
        return Integer.toString(this.duration_ms);
    }

    public void setDuration(int duration) {
        this.duration_ms = duration;
    }

    public String getEnergy()   {
        return Float.toString(this.energy);
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public String getID()   {
        return this.id;
    }

    public String getInstrumentalness() {
        return Float.toString(this.instrumentalness);
    }

    public void setInstrumentalness(float instrumentalness) {
        this.instrumentalness = instrumentalness;
    }

    public String getKey()  {
        return Integer.toString(this.key);
    }

    public String getLiveness() {
        return Float.toString(this.liveness);
    }

    public void setLiveness(float liveness) {
        this.liveness = liveness;
    }

    public String getLoudness() {
        return Float.toString(this.loudness);
    }

    public void setLoudness(float loudness) {
        this.loudness = loudness;
    }

    public String getMode() {
        return Integer.toString(this.mode);
    }

    public String getSpeechiness()  {
        return Float.toString(this.speechiness);
    }

    public void setSpeechines(float speechiness)    {
        this.speechiness = speechiness;
    }

    public String getTempo()    {
        return Float.toString(this.tempo);
    }

    public void setTempo(float tempo)  {
        this.tempo = tempo;
    }

    public String getTimeSignature()    {
        return Integer.toString(this.time_signature);
    }

    public String getValence()  {
        return Float.toString(this.valence);
    }

    public void setValence(float valence)   {
        this.valence = valence;
    }

    public void Print() {
        System.out.println("--- SONG FEATURES: ---");
        System.out.println("Acousticness: " + this.acousticness);
        System.out.println("Danceability: " + this.danceability);
        System.out.println("Energy: " + this.energy);        
        System.out.println("Instrumentalness: " + this.instrumentalness);
        System.out.println("Liveness: " + this.liveness);
        System.out.println("Loudness: " + this.loudness);
        System.out.println("Speechiness: " + this.speechiness);
        System.out.println("Tempo: " + this.tempo);
        System.out.println("Valence: " + this.valence);
        System.out.println("----------------------");

    }
    
}
