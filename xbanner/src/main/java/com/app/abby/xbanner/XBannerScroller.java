package com.app.abby.xbanner;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by Abby on 10/4/2017.
 */

public class XBannerScroller extends Scroller {

    private int mDuration=200;


    public XBannerScroller(Context context){
        super(context);
    }

    public XBannerScroller(Context context, Interpolator interpolator){
        super(context,interpolator);
    }

    public XBannerScroller(Context context,Interpolator interpolator,boolean flywheel){
        super(context,interpolator,flywheel);
    }


    public void setDuration(int duration){
        mDuration=duration;
    }

    @Override
    public void startScroll(int startX,int startY,int dx,int dy,int duration){
        super.startScroll(startX,startY,dx,dy,mDuration);
    }

    @Override
    public void startScroll(int startX,int startY,int dx,int dy){
        super.startScroll(startX,startY,dx,dy);
    }

}
