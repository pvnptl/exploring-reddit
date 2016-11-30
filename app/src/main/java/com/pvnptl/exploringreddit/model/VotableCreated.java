package com.pvnptl.exploringreddit.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by pvnptl on 28/11/16.
 * This class will be base class when both Votable and Created implementations has to be inherited.
 */
public class VotableCreated extends Created{

    // Votable
    @JsonProperty("ups")
    public int ups;

    @JsonProperty("downs")
    public int downs;

    // We are not using this.
    /*@JsonProperty("likes")
    public boolean likes;*/
}
