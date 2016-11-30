package com.pvnptl.exploringreddit.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by pvnptl on 28/11/16.
 */
public class Created {
    @JsonProperty("created")
    public long created;

    @JsonProperty("created_utc")
    public long createdUtc;

}
