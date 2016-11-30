package com.pvnptl.exploringreddit.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pvnptl.exploringreddit.CallInterface;
import com.pvnptl.exploringreddit.ProjectUtils;
import com.pvnptl.exploringreddit.R;
import com.pvnptl.exploringreddit.RedditDataLoader;
import com.pvnptl.exploringreddit.adapter.PostsAdapter;
import com.pvnptl.exploringreddit.listener.OnViewHolderInteractionListener;
import com.pvnptl.exploringreddit.model.Link;
import com.pvnptl.exploringreddit.model.Subreddit;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.realm.Case;
import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubredditFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubredditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubredditFragment extends RealmBaseFragment
        implements CallInterface, OnViewHolderInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SUBREDDIT_NAME = "subreddit-name";

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;
    @Bind(R.id.load_more_progress_bar)
    SmoothProgressBar mLoadMoreProgressBar;

    private String mSubredditName;

    private OnFragmentInteractionListener mListener;
    private Realm mRealm;
    private RedditDataLoader mRedditDataLoader;

    private List<Link> mData;
    private PostsAdapter mPostsAdapter;

    public SubredditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param subredditName Subreddit Name
     * @return A new instance of fragment SubredditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubredditFragment newInstance(String subredditName) {
        SubredditFragment fragment = new SubredditFragment();
        Bundle args = new Bundle();
        args.putString(SUBREDDIT_NAME, subredditName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSubredditName = getArguments().getString(SUBREDDIT_NAME).toLowerCase();
        }
        mRedditDataLoader = new RedditDataLoader(this);
        mRealm = Realm.getInstance(getRealmConfig());
        initDataset();
    }

    private void initDataset() {
        // Initialising empty view for first time
        mData = new ArrayList<>();
        mPostsAdapter = new PostsAdapter(this, mData);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mSubredditName = savedInstanceState.getString("subredditname");
            refreshPosts();
        } else {
            if (ProjectUtils.isOnline(getContext())) {
                setRefreshing(true);
                mRedditDataLoader.loadDataFromNetwork(mSubredditName, mRealm, "");
            } else {
                refreshPosts();
            }
        }
    }

    private boolean loading = true;
    int pastVisiblesItems;
    int visibleItemCount;
    int totalItemCount;

    private void refreshPosts() {
        Subreddit subreddit = mRealm.where(Subreddit.class)
                .equalTo("subredditName", mSubredditName, Case.INSENSITIVE)
                .findFirst();

        if (subreddit != null) {
            mData = subreddit.getLinks();
            mPostsAdapter.setPostsList(mData);
            mPostsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_subreddit, container, false);

        ButterKnife.bind(this, rootView);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mPostsAdapter);

        // Load more listener
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            if (ProjectUtils.isOnline(getContext())) {
                                loading = false;
                                String after = mRealm.where(Subreddit.class)
                                        .equalTo("subredditName", mSubredditName, Case.INSENSITIVE)
                                        .findFirst().getAfter();
                                setLoadMore(true);
                                mRedditDataLoader.loadDataFromNetwork(mSubredditName.toLowerCase(), mRealm, after);
                            } else {
                                loading = true;
                            }
                        }
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) getContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mListener = null;
    }

    @Override
    public void onDataLoaded() {

        String after = mRealm.where(Subreddit.class)
                .equalTo("subredditName", mSubredditName)
                .findFirst().getAfter();

        if (after != null) {
            loading = true;
        } else {
            loading = false;
            mListener.onError(getString(R.string.no_more_posts));
        }
        refreshPosts();
        setLoadMore(false);
        setRefreshing(false);
    }

    @Override
    public void onFailure(String message) {
        setRefreshing(false);
        if (message.equalsIgnoreCase("forbidden")) {
            mListener.onError(getString(R.string.private_sub));
        } else if (message.equalsIgnoreCase("Not found")) {
            mListener.onError(getString(R.string.sub_does_not_exist));
        } else if (message.equalsIgnoreCase("timeout")) {
            mListener.onError(getString(R.string.something_went_wrong));
            loading = true;
        } else if (message.equalsIgnoreCase("canceled")) {
            // Cancelled intentionally
        } else {
            //showSnackbarMessage(getString(R.string.something_went_wrong));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mRedditDataLoader.cancelAllRequests();
        mRedditDataLoader = null;
        mRealm.close();
        mRealm = null;
    }

    @Override
    public void onViewHolderInteraction(String action, Object data) {
        if (action.equalsIgnoreCase(PostsAdapter.ACTION_IMAGE_CLICKED)) {
            mListener.onFragmentInteraction(mData.get((int) data).URL);
        } else {
            // other actions
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String imageURL);

        void onError(String message);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("subredditname", mSubredditName);
    }

    private void setRefreshing(boolean isRefreshing) {
        mProgressBar.setVisibility(isRefreshing ? View.VISIBLE : View.GONE);
    }

    private void setLoadMore(boolean isLoadingMore) {
        mLoadMoreProgressBar.setVisibility(isLoadingMore ? View.VISIBLE : View.GONE);
    }
}
