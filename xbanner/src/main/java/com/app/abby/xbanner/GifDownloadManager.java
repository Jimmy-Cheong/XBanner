package com.app.abby.xbanner;

import android.widget.ImageView;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Abby on 12/2/2017.
 */

public class GifDownloadManager {


    public interface ProgressListener{
        void showProgress(int index,int progress);
    }

    public static void downloagGif(String url, GifImageView gifImageView, ImageView.ScaleType scaleType){
        GifDownloader.displayImage(url,gifImageView,scaleType);
    }



}
