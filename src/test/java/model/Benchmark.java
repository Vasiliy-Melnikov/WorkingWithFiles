package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Benchmark {

    public String game;
    public int fps;
    public String preset;
}