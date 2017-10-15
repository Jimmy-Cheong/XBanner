package com.app.abby.xbanner;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Abby on 9/22/2017.
 */

public  interface ImageLoader {
    void loadImages(Context context, String url, ImageView image);
    void loadGifs(String url, GifImageView gifImageView,ImageView.ScaleType scaleType);
}
