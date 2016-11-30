package com.pvnptl.exploringreddit.activity;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.davemorrissey.labs.subscaleview.decoder.DecoderFactory;
import com.davemorrissey.labs.subscaleview.decoder.ImageDecoder;
import com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder;
import com.pvnptl.exploringreddit.PicassoDecoder;
import com.pvnptl.exploringreddit.PicassoRegionDecoder;
import com.pvnptl.exploringreddit.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;

public class FullScreenImageActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String IMAGE_URL = "image-url";
    @Bind(R.id.full_screen_imageView)
    public SubsamplingScaleImageView mScaleImageView;
    @Bind(R.id.progress_bar)
    public ProgressBar mProgressBar;
    private String mImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ButterKnife.bind(this);

        mImageUrl = getIntent().getStringExtra(IMAGE_URL);

        if (mImageUrl == null) {
            finish();
        }

        mProgressBar.setVisibility(View.VISIBLE);
        loadImageByUrl(mImageUrl, new OkHttpClient());
    }

    public void loadImageByUrl(final String url, final OkHttpClient okHttpClient) {
        mScaleImageView.setMaxScale(5.0f);
        final Picasso picasso = Picasso.with(mScaleImageView.getContext());

        mScaleImageView.setBitmapDecoderFactory(new DecoderFactory<ImageDecoder>() {
            @Override
            public ImageDecoder make() throws IllegalAccessException, java.lang.InstantiationException {

                return new PicassoDecoder(url, picasso);
            }
        });

        mScaleImageView.setRegionDecoderFactory(new DecoderFactory<ImageRegionDecoder>() {
            @Override
            public ImageRegionDecoder make() throws IllegalAccessException, java.lang.InstantiationException {
                return new PicassoRegionDecoder(okHttpClient);
            }
        });

        mScaleImageView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
            @Override
            public void onReady() {

            }

            @Override
            public void onImageLoaded() {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPreviewLoadError(Exception e) {

            }

            @Override
            public void onImageLoadError(Exception e) {

            }

            @Override
            public void onTileLoadError(Exception e) {

            }

            @Override
            public void onPreviewReleased() {

            }
        });
        mScaleImageView.setImage(ImageSource.uri(url));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
