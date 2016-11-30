package com.pvnptl.exploringreddit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pvnptl on 28/11/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Link extends RealmObject {

    @JsonProperty("domain")
    public String domain;

    @JsonProperty("subreddit")
    public String subreddit;

    @JsonProperty("id")
    @PrimaryKey
    public String id;

    @JsonProperty("gilded")
    public int gilded;

    @JsonProperty("author")
    public String author;

    @JsonProperty("score")
    public int score;

    @JsonProperty("over_18")
    public boolean isNSFW;

    @JsonProperty("thumbnail")
    public String thumbnailURL;

    @JsonProperty("post_hint")
    public String postHint;

    @JsonProperty("hide_score")
    public boolean isScoreHidden;

    @JsonProperty("url")
    public String URL;

    @JsonProperty("title")
    public String title;

    @JsonProperty("link_flair_text")
    public String linkFlairText;

    @JsonProperty("distinguished")
    public String distinguished;

    @JsonProperty("num_comments")
    public int numOfComments;

    // Votable
    @JsonProperty("ups")
    public int ups;

    @JsonProperty("downs")
    public int downs;

    // We are not using this.
    /*@JsonProperty("likes")
    public boolean likes;*/

    @JsonProperty("created")
    public long created;

    @JsonProperty("created_utc")
    public long createdUtc;

}
