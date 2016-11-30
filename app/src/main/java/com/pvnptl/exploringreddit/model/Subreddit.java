package com.pvnptl.exploringreddit.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pvnptl on 29/11/16.
 */
public class Subreddit extends RealmObject {
    @PrimaryKey
    private String subredditName;

    private RealmList<Link> links = new RealmList<>();

    private String after;

    public String getSubredditName() {
        return subredditName;
    }

    public void setSubredditName(String subredditName) {
        this.subredditName = subredditName;
    }

    public RealmList<Link> getLinks() {
        return links;
    }

    public void setLinks(RealmList<Link> links) {
        this.links = links;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }
}
