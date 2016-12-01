package com.pvnptl.exploringreddit;

import android.text.TextUtils;
import android.util.Log;

import com.pvnptl.exploringreddit.model.Link;
import com.pvnptl.exploringreddit.model.Listing;
import com.pvnptl.exploringreddit.model.Subreddit;
import com.pvnptl.exploringreddit.model.Thing;
import com.pvnptl.exploringreddit.service.RedditService;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RedditDataLoader {

    public static final String BASE_URL = "https://www.reddit.com/";

    private RedditService mRedditService;
    private Call<Thing<Listing<List<Thing<Link>>>>> mListCall;
    Retrofit mRetrofit;
    CallInterface mCallInterface;
    OkHttpClient mOkhttpClient;
    private long mTimeStamp;

    public RedditDataLoader(CallInterface callInterface) {
        mOkhttpClient = new OkHttpClient();
        mRetrofit = new Retrofit.Builder()
                .client(mOkhttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        mRedditService = mRetrofit.create(RedditService.class);
        mCallInterface = callInterface;
        mTimeStamp = System.currentTimeMillis();
    }

    public void cancelAllRequests() {
        mOkhttpClient.dispatcher().cancelAll();
    }

    boolean isFirstCall = false;

    public void loadDataFromNetwork(final String subredditName, final Realm realm, final String after) {
        mListCall =
                mRedditService.hot(subredditName, after);
        mListCall.enqueue(new Callback<Thing<Listing<List<Thing<Link>>>>>() {
            @Override
            public void onResponse(Call<Thing<Listing<List<Thing<Link>>>>> call, Response<Thing<Listing<List<Thing<Link>>>>> response) {
                if (response.isSuccessful()) {
                    final Thing<Listing<List<Thing<Link>>>> body = response.body();

                    if (body.data.children.isEmpty()) {
                        mCallInterface.onFailure("Not found");
                        return;
                    }

                    isFirstCall = TextUtils.isEmpty(after);

                    processAndAddData(realm, body.data.children, subredditName, body.data.after);
                } else {
                    mCallInterface.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<Thing<Listing<List<Thing<Link>>>>> call, Throwable t) {
                mCallInterface.onFailure(t.getMessage());
            }
        });
    }

    private void processAndAddData(Realm realm, List<Thing<Link>> things, final String subredditName, final String after) {
        // Process posts

        if (isFirstCall) {
            // This is first call. So we clear any persisted data.
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Subreddit subreddit = realm.where(Subreddit.class)
                            .equalTo("subredditName", subredditName, Case.INSENSITIVE)
                            .findFirst();
                    if (subreddit != null) {
                        subreddit.deleteFromRealm();
                    }
                }
            });
        }

        final List<Link> postList = new ArrayList<>();
        for (Thing<Link> thing : things) {
            if (acceptContent(thing.data)) {
                // add only filtered content
                postList.add(thing.data);
            }
        }

        if (after != null && postList.isEmpty()) {
            loadDataFromNetwork(subredditName, realm, after);
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Subreddit subreddit = realm.where(Subreddit.class)
                            .equalTo("subredditName", subredditName)
                            .findFirst();

                    if (subreddit == null) {
                        subreddit = new Subreddit();
                        subreddit.setSubredditName(subredditName);
                    }
                    subreddit.setAfter(after);
                    subreddit.getLinks().addAll(postList);
                    realm.copyToRealmOrUpdate(subreddit);
                }
            });
            mCallInterface.onDataLoaded();
        }
    }

    private boolean acceptContent(Link link) {
        if (link.postHint != null && link.postHint.equalsIgnoreCase("image") && isImageExtension(link.URL)) {
            // Image filter
            return true;
        }

        return false;
    }

    String[] mImageExtensions = new String[]{
            ".jpg",
            ".jpeg",
            ".png"
    };

    private boolean isImageExtension(String url) {
        if (url.contains(".")) {
            String extension = url.substring(url.lastIndexOf("."));
            for (int i = 0; i < mImageExtensions.length; i++) {
                if (extension.equalsIgnoreCase(mImageExtensions[i])) {
                    return true;
                }
            }
        }
        return false;
    }

}
