package com.pvnptl.exploringreddit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.realm.RealmObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Listing<T> {

    @JsonProperty("modhash")
    public String modhash;

    @JsonProperty("children")
    public T children;

    @JsonProperty("after")
    public String after;

    @JsonProperty("before")
    public String before;
}
