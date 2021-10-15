package com.emi.jsontest.models;

import com.emi.jsontest.data.CompatibleDateAdapter;
import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class FilmModel {

    @SerializedName("title")
    public String title;

    @SerializedName("episode_id")
    public int episode_id;
    @SerializedName("opening_crawl")
    public String opening_crawl;
    @SerializedName("director")
    public String director;
    @SerializedName("producer")
    public String producer;
    @SerializedName("release_date")
    public Date release_date;

    @SerializedName("characters")
    public JsonArray characters;
    @SerializedName("planets")
    public JsonArray planets;
    @SerializedName("starships")
    public JsonArray starships;
    @SerializedName("vehicles")
    public JsonArray vehicles;
    @SerializedName("species")
    public JsonArray species;

    @SerializedName("created")
    public Date created;

    @SerializedName("edited")
    public Date edited;

    @SerializedName("url")
    public String url;
}
