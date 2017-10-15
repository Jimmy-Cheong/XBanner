package com.app.abby.xbanner;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Abby on 10/14/2017.
 */

public abstract class AbstractUrlLoader implements ImageLoader {

    @Override
    public abstract void loadImages(Context context, String url, ImageView image);


}