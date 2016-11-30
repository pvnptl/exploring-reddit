package com.pvnptl.exploringreddit.service;

import com.pvnptl.exploringreddit.model.Link;
import com.pvnptl.exploringreddit.model.Listing;
import com.pvnptl.exploringreddit.model.Thing;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface RedditService {

    @GET("r/{subreddit}/.json")
    Call<Thing<Listing<List<Thing<Link>>>>> hot(
            @Path("subreddit") String subreddit,
            @Query("after") String after);

}