package com.pvnptl.exploringreddit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.realm.RealmObject;

/**
 * Created by pvnptl on 28/11/16.
 * Reddit Base Class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Thing<T> {

    @JsonProperty("kind")
    public String kind;

    @JsonProperty("data")
    public T data;

}
