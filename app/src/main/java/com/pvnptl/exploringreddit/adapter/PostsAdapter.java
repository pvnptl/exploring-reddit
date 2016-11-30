package com.pvnptl.exploringreddit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pvnptl.exploringreddit.ProjectUtils;
import com.pvnptl.exploringreddit.R;
import com.pvnptl.exploringreddit.listener.OnViewHolderInteractionListener;
import com.pvnptl.exploringreddit.model.Link;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String ACTION_IMAGE_CLICKED = "image-clicked";
    private final OnViewHolderInteractionListener mCallback;

    private List<Link> mPostsList;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ImagePostViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        @Bind(R.id.post_image_view)
        ImageView postImageView;

        @Bind(R.id.title_text_view)
        TextView titleTextView;

        @Bind(R.id.meta_data_text_view)
        TextView metaDataTextView;

        @Bind(R.id.score_text_view)
        TextView scoreTextView;

        @Bind(R.id.comment_count_text_view)
        TextView commentCountTextView;

        private ImagePostViewHolderClickListerner mListener;

        public ImagePostViewHolder(View v, final ImagePostViewHolderClickListerner listener) {
            super(v);
            mListener = listener;
            ButterKnife.bind(this, v);
            // Set click listeners for different ui elements
            postImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onPostImageClick(getAdapterPosition());
        }

        public interface ImagePostViewHolderClickListerner {
            void onPostImageClick(int position);
        }
    }
    // END_INCLUDE(recyclerViewNotificationViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param postsList List<Link> containing the texts to populate views to be used by RecyclerView.
     */
    public PostsAdapter(OnViewHolderInteractionListener callback,
                        List<Link> postsList) {
        mCallback = callback;
        mPostsList = postsList;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_image_post_view, viewGroup, false);
        return new ImagePostViewHolder(v, new ImagePostViewHolder.ImagePostViewHolderClickListerner() {
            @Override
            public void onPostImageClick(int position) {
                mCallback.onViewHolderInteraction(ACTION_IMAGE_CLICKED, position);
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        final Link link = mPostsList.get(position);
        String postHint = link.postHint;
        Context context = viewHolder.itemView.getContext();
        if (postHint != null) {
            if (postHint.equalsIgnoreCase("image")) {
                Picasso.with(context).load(link.URL).fit().centerCrop().into(((ImagePostViewHolder) viewHolder).postImageView);
                ((ImagePostViewHolder) viewHolder).titleTextView.setText(link.title);
                ((ImagePostViewHolder) viewHolder).metaDataTextView.setText(createBulletFormattedText(link));
                ((ImagePostViewHolder) viewHolder).scoreTextView.setText(link.score + " points");
                ((ImagePostViewHolder) viewHolder).commentCountTextView.setText(link.numOfComments + " comments");
            } else {
                // Other post types
            }
        }
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mPostsList.size();
    }

    public void setPostsList (List<Link> postsList) {
        mPostsList = postsList;
    }

    private String createBulletFormattedText(Link link) {
        StringBuilder builder = new StringBuilder();
        if (link.isNSFW) {
            builder.append("NSFW").append(" • ");
        }

        if (link.linkFlairText != null && !TextUtils.isEmpty(link.linkFlairText)) {
            builder.append(link.linkFlairText).append(" • ");
        }

        builder.append(link.author).append(" • ");
        builder.append(ProjectUtils.getElapsedTimeString(link.createdUtc * 1000)).append(" • ");
        builder.append(link.subreddit).append("(").append(link.domain).append(")");

        if (link.gilded > 0) {
            builder.append(" • ").append("★ ").append(link.gilded);
        }

        return builder.toString();

    }

}
