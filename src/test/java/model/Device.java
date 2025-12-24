package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Device {

    public String model;
    public int year;
    public String os;
    public int ramGb;
    public List<Benchmark> benchmarks;
}