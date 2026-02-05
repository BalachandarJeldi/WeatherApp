package com.balu.weatherapp.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
public class WeatherResponse {

    // OpenWeather uses 'cod' for the internal status code (e.g., 200, 404, 401)
    @SerializedName("cod")
    public int cod;
    @SerializedName("main")
    public Main main;
    @SerializedName("weather")
    public List<Weather> weather;

    @SerializedName("name")
    public String city;

    @SerializedName("coord")
    public Coord coord;

    public static class Main{
        @SerializedName("temp")
        public double temp;
        @SerializedName("humidity")
        public double humidity;
    }
    public static class Weather{
        @SerializedName("description")
        public String description;
        @SerializedName("icon")
        public String icon;
        @SerializedName("main")
        public String main;
    }

    public static class Coord{
        @SerializedName("lon")
        public String lon;
        @SerializedName("lat")
        public String lat;
    }

}
