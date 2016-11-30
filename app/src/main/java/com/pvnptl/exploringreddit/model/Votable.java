package com.pvnptl.exploringreddit.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by pvnptl on 28/11/16.
 */
public class Votable {
    @JsonProperty("ups")
    public int ups;

    @JsonProperty("downs")
    public int downs;

    // We are not using this.
    /*@JsonProperty("likes")
    public boolean likes;*/

}
