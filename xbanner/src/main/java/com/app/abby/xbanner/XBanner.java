package com.app.abby.xbanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;


import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;


import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.animation.DecelerateInterpolator;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;



import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Abby on 9/22/2017.
 * @author Abby
 * email: 1358890401@qq.com
 *
 */


public class XBanner extends RelativeLayout{

    public  int delayTime=3000;
    public  int titleTextSize=16;
    public int sizeNumIndicator=12;
    public  int indicatorMargin=10;
    public int titleHeight=100;
    public int titleMarginStart=20;
    public int widthIndicatorCube=40;
    public int heightIndicatorCube=10;

    public int pageTransformerDelayIdle=600;

    //transform delay set to 250ms when dragging
    public static final int PAGE_TRANSFORM_DELAY_DRAGGING=250;

    //indicator gravity types
    public static final int INDICATOR_START=0;
    public static final int INDICATOR_CENTER=1;
    public static final int INDICATOR_END=2;

    //banner styles
    public static final int CIRCLE_INDICATOR=0;
    public static final int CIRCLE_INDICATOR_TITLE=1;
    public static final int CUBE_INDICATOR=2;
    public static final int NUM_INDICATOR=3;
    public static final int NUM_INDICATOR_TITLE=4;

    //ellipsize types
    public static final int ELLIPSIZE_END=0;
    public static final int ELLIPSIZE_MARQUEE=1;

    //loaing progress view types
    public static final int TEXT_PROGRESS=0;
    public static final int CIRCLE_PROGRESS=1;



    private Context mContext;
    private int mTitleHeight;
    private int mTitleWidth;
    private int mIndicatorSize;
    private int mIndicatorHeight;
    private int mIndicatorWidth;
    private int mIndicatorSelected;
    private int mIndicatorUnselected;
    private int mDelayTime;
    private boolean mIsAutoPlay;
    private boolean mIsPlaying;
    private boolean mIsGif;
    private boolean mIsTitlebgAlpha;
    private boolean mIndicatorSet;
    private int mSizeTitleText;
    private int mColorTitle;
    private int mGravity;
    private int mImageCount;
    private int mBannerType;
    private int mProgressType;
    private int mEllipsizeType;
    private RelativeLayout mBannerParentView;
    private ImageView.ScaleType mScaleType;
    private DisplayMetrics dm;

    private ViewPager mViewPager;
    private XBPagerAdapter mAdapter;
    private ViewPager.PageTransformer mViewPageTransformer;
    private TextView mBannerTitle;
    private TextView mTextProgress;
    private RingProgressBar mCircleProgress;
    private TextView mNumIndicator;
    private List<ImageView> mIndicators;
    private List<View> mBannerImages;
    private List<String> mTitles;
    private List<String> mUrls;
    private LinearLayout mIndicatorContainer;
    private BannerPageListener mBannerPageListner;
    private ImageLoader mImageLoader;

    private static Handler mHandler=new Handler();
    private Scroller mScroller;
    //xbannerScroller will be applied by default
    private  XBannerScroller xbannerScroller;
    private ViewPagerRunnable mRunnable;

    private int mSelectedIndex=1;


    private boolean[] downloadFinished;

    public XBanner(Context context){
        super(context);
        mContext=context;
        initValues();
    }


    public XBanner(Context context, AttributeSet attrs){
        super(context,attrs);
        mContext=context;
        initValues();
        getTypeArrayValue(context,attrs);
    }

    public XBanner(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
        mContext=context;
        initValues();
        getTypeArrayValue(context,attrs);


    }

    /**
     * Set the gravity of the indicators,START by default
     * Gravity will be END when with a title inside
     * to change the gravity of the indicator
     * Use {@link #setIndicatorGravity(int gravity)}
     */
    @IntDef({INDICATOR_START,INDICATOR_CENTER,INDICATOR_END})
    @Retention(RetentionPolicy.SOURCE)
    public @interface INDICATOR_GRAVITY{};
    public XBanner setIndicatorGravity(@INDICATOR_GRAVITY int gravity){
        mGravity=gravity;
        return this;
    }

    /**
     * Set the indicator type here
     */
    @IntDef({CIRCLE_INDICATOR,CIRCLE_INDICATOR_TITLE,CUBE_INDICATOR,NUM_INDICATOR,NUM_INDICATOR_TITLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface  BANNER_TYPE{};
    public XBanner setBannerTypes(@BANNER_TYPE int bannerType){
        mBannerType=bannerType;
        return this;
    }

    /**
     *Set the ellipsize type here
     */
    @IntDef({ELLIPSIZE_END,ELLIPSIZE_MARQUEE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ELLIPSIZE_TYPE{};
    public XBanner setEllipsizeType(@ELLIPSIZE_TYPE int ellipsizeType){
        mEllipsizeType=ellipsizeType;
        return this;
    }


    private void getTypeArrayValue(Context context,AttributeSet attr){
        if(attr==null){
            return;
        }
        TypedArray typedArray=context.obtainStyledAttributes(attr,R.styleable.XBanner);

        mIndicatorWidth=typedArray.getDimensionPixelSize(R.styleable.XBanner_indicator_width,mIndicatorSize);
        mTitleHeight=typedArray.getDimensionPixelSize(R.styleable.XBanner_title_height,titleHeight);
        mDelayTime=typedArray.getInteger(R.styleable.XBanner_delay_time,delayTime);
        mIsAutoPlay=typedArray.getBoolean(R.styleable.XBanner_is_auto_play,false);
        mSizeTitleText=typedArray.getInteger(R.styleable.XBanner_size_title_text,titleTextSize);
        mGravity=typedArray.getInteger(R.styleable.XBanner_indicator_gravity,INDICATOR_CENTER);
        mColorTitle=typedArray.getColor(R.styleable.XBanner_color_title, Color.WHITE);
        typedArray.recycle();

    }

    private void initValues(){

        dm= mContext.getResources().getDisplayMetrics();
        mIndicatorSize= dm.widthPixels/80;
        mIndicatorHeight=mIndicatorSize;
        mIndicatorWidth=mIndicatorSize;
        mTitleWidth= dm.widthPixels*3/4;
        mImageCount=0;
        mDelayTime=4000;


        mBannerType=CIRCLE_INDICATOR;
        mColorTitle=Color.WHITE;
        mGravity=INDICATOR_CENTER;
        mScaleType=ImageView.ScaleType.FIT_XY;

        mIndicators=new ArrayList<>();
        mBannerImages=new ArrayList<>();
        mTitles=new ArrayList<>();
        mUrls=new ArrayList<>();

        mIsPlaying=false;
        mIsGif=false;
        mIsTitlebgAlpha=false;
        mIndicatorSet=false;

        mProgressType=-1;

    }

    private void initView(){

        bindView();
        //Need to set banner type to title types to avoid some logic errors

        String exceptionTitle="XBanner: "+"Banner type must be set to CIRCLE_INDICATOR_TITLE or NUM_INDICATOR_TITLE to set titles"+",the default banner type is set to CIRCLE_INDICATOR.";
        initScroller();
        if(mBannerType==CIRCLE_INDICATOR_TITLE||mBannerType==NUM_INDICATOR_TITLE){
           initViewforTitleType();
        } else if(mTitles.size()>0){
            throw new RuntimeException(exceptionTitle);
        }
        initIndicatorContainer();
        initViewPagerAdapter();
    }

    private void initViewforTitleType(){
        setIndicatorGravity(INDICATOR_END);
        initBannerTitle();
        addView(mBannerTitle);
    }


    /**
     * init Scroller
     * Change the speed of the scroller
     * to apply your scroller
     * use {@link #setScroller(Scroller scroller)}
     *
     */


    private void initScroller(){

        try {
            Field xScroller=ViewPager.class.getDeclaredField("mScroller");
            xScroller.setAccessible(true);
            if(mScroller==null){
                xbannerScroller =new XBannerScroller(mContext,new DecelerateInterpolator());
                xbannerScroller.setDuration(600);
                xScroller.set(mViewPager,xbannerScroller);
            }else {
                xScroller.set(mViewPager,mScroller);
            }

        }catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
    }


    private void removeScroller(){
        try {
            Field xScroller=ViewPager.class.getDeclaredField("mScroller");
            xScroller.setAccessible(true);
            xScroller.set(mViewPager,null);
            xbannerScroller=null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public XBanner setScroller(Scroller scroller){
        mScroller=scroller;
        return this;
    }

    /**
     * As we call this method,we new a GifImage loader to loader our gifs
     * but if you need to customize your gif loader,do it
     * use {@link #setImageLoader(ImageLoader imageloader)}
     * remember to call asGif() before you set your gif loader
     *
     */
    public XBanner asGif( ){
        mIsGif=true;
        //apply our default gif loader here
        mImageLoader=new GifImageLoader();
        return this;
    }

    @IntDef({TEXT_PROGRESS,CIRCLE_PROGRESS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PROGRESS_TYPE{}
    public XBanner setLoadingProgressType(@PROGRESS_TYPE int type){
        mProgressType=type;
        downloadFinished=new boolean[mImageCount+1];
        for(int i=0;i<mImageCount;i++){
            downloadFinished[i]=false;
        }
        return this;
    }

    /**
     *Called to make the title background to be alpha
     */
    public XBanner setTitlebgAlpha(){
        mIsTitlebgAlpha=true;
        return this;
    }

    /**
     * Attention,this method must be called before the images set
     * or it does not work
     * the default scale type is FIT_XY
     *
     */
    public XBanner setImageScaleType(ImageView.ScaleType scaleType){
        mScaleType=scaleType;
        return this;
    }


    /**
     * Create indicators,must be called after banner images set
     * @return ImageView list of indicators
     */
    private List<ImageView> createIndicators(){
        List<ImageView> images=new ArrayList<>();
            for(int i=0;i<mImageCount;i++){
                images.add(newIndicator(i));
            }
        return images;
    }



    /**
     * new an indicator with the given resId
     * @param index the index of the Image
     */
    private ImageView newIndicator(int index){
        ImageView indicator=new ImageView(mContext);
        LinearLayout.LayoutParams params;

            params=new LinearLayout.LayoutParams(mIndicatorWidth,mIndicatorHeight);
            if(mIndicatorSet){
                indicator.setImageResource(index==0?mIndicatorSelected:mIndicatorUnselected);
            }else if(mBannerType==CIRCLE_INDICATOR_TITLE||mBannerType==CIRCLE_INDICATOR){
                indicator.setImageResource(index==0?R.drawable.indicator_selected:R.drawable.indicator_unselected);
            }else if(mBannerType==CUBE_INDICATOR){
                indicator.setImageResource(index==0?R.drawable.indicator_cube_selected:R.drawable.indicator_cube_unselected);
            }

        if(mBannerType==CUBE_INDICATOR){
            params.height=heightIndicatorCube;
            params.width=widthIndicatorCube;
        }
        params.leftMargin=indicatorMargin;
        params.rightMargin=indicatorMargin;
        indicator.setLayoutParams(params);
        return indicator;
    }

    private TextView createNumIndicator(){
        TextView indicator=new TextView(mContext);
        indicator.setTextSize(sizeNumIndicator);
        indicator.setText("1/"+mImageCount);
        indicator.setTextColor(Color.WHITE);
        return indicator;
    }


    public XBanner setImageRes(int[] images){

        mImageCount=images.length;
        if(mImageCount>1){
            mBannerImages.add(newImageFromRes(images[mImageCount-1]));
            for(int i=0;i<mImageCount;i++){
               mBannerImages.add(newImageFromRes(images[i]));
            }
            mBannerImages.add(newImageFromRes(images[0]));
        }else {
            mBannerImages.add(newImageFromRes(images[0]));
        }

        return this;
    }


    /**
     * load gifs from the resourceID
     * @param gifs the gif resource id
     */
    public XBanner setGifRes(int[] gifs){
        mImageCount=gifs.length;
        if(mImageCount>1){
            mBannerImages.add(newGifImageFroRes(gifs[mImageCount-1]));
            for(int i=0;i<mImageCount;i++){
                mBannerImages.add(newGifImageFroRes(gifs[i]));
            }
            mBannerImages.add(newGifImageFroRes(gifs[0]));
        }else {
            mBannerImages.add(newGifImageFroRes(gifs[0]));
        }
        return this;
    }




    private GifImageView newGifImageFroRes(int res){
        GifImageView gif=new GifImageView(mContext);
        gif.setImageResource(res);
        gif.setScaleType(mScaleType);
        return gif;
    }

    private ImageView newImageFromRes(int res){
        ImageView image=new ImageView(mContext);
        image.setImageResource(res);
        image.setScaleType(mScaleType);
        return image;
    }

    public XBanner setTitles(List<String> titles){
        mTitles.addAll(titles);
        return this;
    }



    public XBanner setImageUrls(List<String> urls){
        mUrls.addAll(urls);
        if(mImageCount==0){
            mImageCount=urls.size();
        }
        return this;
    }

    public XBanner setImageResAndTitles(int[] imageRes,List<String> titles){
        setImageRes(imageRes);
        setTitles(titles);
        return this;
    }

    public XBanner setGifImageResAndTitles(int[] gifs,List<String> titles){
        setGifRes(gifs);
        setTitles(titles);
        return this;
    }

    public XBanner setImageUrlsAndTitles(List<String> url,List<String> titles){
        setImageUrls(url);
        setTitles(titles);
        return this;
    }

    /**
     * Apply your transformer here
     * @param transformer the transformer to be applied
     */
    public XBanner setPageTransformer(ViewPager.PageTransformer transformer){
        mViewPageTransformer=transformer;
        return this;
    }

    public XBanner setDelay(int delay){
        //default delay time is 3000ms
        if(mDelayTime<0){
            mDelayTime=titleTextSize;
        }else {
            mDelayTime=delay;
        }
        return this;
    }

    /**
     * Set the speed of transformer when idle,default to be 600ms
     * but when dragging,it should be a smaller number
     * if you prefer to set your scroller
     * use {@link #setScroller(Scroller scroller)}
     *
     */

    public XBanner setTransformerSpeed(int speed){
        if(speed<0){
            speed=600;
        }
        pageTransformerDelayIdle=speed;
        return this;
    }


    /**
     * Set up the height of the indicator container,which is the title background
     * @param height the height of the title background in px
     */
    public XBanner setTitleHeight(int height){
        if(height<0){
            height=0;
        }else {
            mTitleHeight=height;
        }
        return this;
    }

    /**
     * Set the res of indicator manually
     * the width and height can be set
     * @param selected the res of indicator when selected
     * @param unselected the res of indicator when unselected
     *
     */

    public XBanner setUpIndicators(int selected,int unselected){
        mIndicatorSelected=selected;
        mIndicatorUnselected=unselected;
        mIndicatorSet=true;
        return this;
    }


    /**
     *Set up the indicator size
     * @param width width of the indicator
     * @param height height of the indicator
     */
    public XBanner setUpIndicatorSize(int width,int height){
        mIndicatorWidth=width;
        mIndicatorHeight=height;
        return this;
    }


    public XBanner isAutoPlay(boolean isAutoPlay){
        mIsAutoPlay=isAutoPlay;
        return this;
    }

    /**
     *Get the viewpager
     */
    public ViewPager getViewPager(){
        return mViewPager;
    }

    private void showIndicators(){

        if(mBannerType==NUM_INDICATOR||mBannerType==NUM_INDICATOR_TITLE){
            mNumIndicator=createNumIndicator();
            if(mBannerType==NUM_INDICATOR){
               applyIndicatorGravity();
            }else {
                mIndicatorContainer.setGravity(Gravity.CENTER_VERTICAL|Gravity.END);
            }
            mIndicatorContainer.addView(mNumIndicator);

        }else {
            applyIndicatorGravity();
            mIndicators.addAll(createIndicators());
            //applyIndicatorGravity();
            for(int i=0;i<mIndicators.size();i++){
                mIndicatorContainer.addView(mIndicators.get(i));
            }
        }

    }

    private void applyIndicatorGravity(){
        if(mGravity==INDICATOR_START){
            mIndicatorContainer.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
        }else if(mGravity==INDICATOR_CENTER){
            mIndicatorContainer.setGravity(Gravity.CENTER|Gravity.CENTER_VERTICAL);
        } else if (mGravity == INDICATOR_END) {
            mIndicatorContainer.setGravity(Gravity.END|Gravity.CENTER_VERTICAL);
        }
    }


    private void initBannerTitle(){

        mBannerTitle=new TextView(mContext);
        mBannerTitle.setTextColor(mColorTitle);
        mBannerTitle.setText(mTitles.get(0));
        mBannerTitle.setGravity(Gravity.CENTER_VERTICAL);
        mBannerTitle.setSingleLine();
        mBannerTitle.setTextSize(mSizeTitleText);

        if(mEllipsizeType==ELLIPSIZE_END){
            mBannerTitle.setEllipsize(TextUtils.TruncateAt.END);
        } else if (mEllipsizeType == ELLIPSIZE_MARQUEE) {
            mBannerTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mBannerTitle.setMarqueeRepeatLimit(-1);
            mBannerTitle.setFocusable(true);
            mBannerTitle.setFocusableInTouchMode(true);
            mBannerTitle.setSelected(true);
        }

        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(mTitleWidth,mTitleHeight);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.leftMargin=titleMarginStart;
        mBannerTitle.setLayoutParams(params);

    }

    private void initIndicatorContainer(){
        RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)mIndicatorContainer.getLayoutParams();
        params.height=mTitleHeight;
    }

    private void bindView(){
        mBannerParentView=(RelativeLayout)LayoutInflater.from(mContext).inflate(R.layout.xbanner,this,true);
        mViewPager=(ViewPager)mBannerParentView.findViewById(R.id.viewpager);
        mIndicatorContainer =(LinearLayout)mBannerParentView.findViewById(R.id.indicator_container);
        if(mIsTitlebgAlpha){
            mIndicatorContainer.setBackgroundColor(Color.TRANSPARENT);
        }

    }


    private void initViewPagerAdapter(){

        if(mAdapter==null){
            mAdapter= new XBPagerAdapter(mBannerPageListner,mImageCount);
        }
        mViewPager.setAdapter(mAdapter);
        mAdapter.setData(mBannerImages);
        if(mImageCount>1){
            mViewPager.setCurrentItem(1);
        }else {
            mViewPager.setCurrentItem(0);
            mSelectedIndex=0;
        }



        if(mViewPageTransformer!=null){
            mViewPager.setPageTransformer(false,mViewPageTransformer);
        }


    }

    /**
     * Get the true position of the indicator
     * @param pos the pos of the viewpager now
     */
    private  int getTruePos(int pos){
        //get the position of the indicator
        int truepos=(pos-1)%mImageCount;
        if(truepos<0){
            truepos=mImageCount-1;
        }
        return truepos;
    }



    private int getindexforProgress(int pos){
        int index=pos;
        if(pos==mImageCount+1){
            index=1;
        }
        if(pos==0){
            index=mImageCount;
        }

        return index;

    }

    /**
     * Config the viewpager listener to the viewpager
     * Only if the image count is greater than 1
     */
    private void applyViewPagerAdapterListener(){


            if(mImageCount<=1){
                return;
            }

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    onIndicatorChange(position);
                    mSelectedIndex=getindexforProgress(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                    int current=mViewPager.getCurrentItem();

                    switch (state){
                        case ViewPager.SCROLL_STATE_DRAGGING:
                            if(mBannerPageListner!=null){
                                mBannerPageListner.onBannerDragging(current);
                            }
                            if(xbannerScroller!=null){
                                xbannerScroller.setDuration(PAGE_TRANSFORM_DELAY_DRAGGING);
                            }
                            if(current==0){
                                mViewPager.setCurrentItem(mImageCount,false);
                            }
                            if(current==mImageCount+1){
                                mViewPager.setCurrentItem(1,false);
                            }
                            break;
                        case ViewPager.SCROLL_STATE_IDLE:
                            if(mBannerPageListner!=null){
                                mBannerPageListner.onBannerIdle(current);
                            }
                            if(xbannerScroller!=null){
                                xbannerScroller.setDuration(pageTransformerDelayIdle);
                            }
                            if(current==mImageCount+1){
                                mViewPager.setCurrentItem(1,false);
                            }else if(current==0){
                                mViewPager.setCurrentItem(mImageCount,false);
                            }

                            break;
                        default:
                            break;
                    }
                }
            });


    }

    private void onIndicatorChange(int position){

        switch (mBannerType){
            case CIRCLE_INDICATOR:
                onCircleIndicatorChange(position);
                break;

            case CIRCLE_INDICATOR_TITLE:
                onCircleIndicatorChange(position);
                mBannerTitle.setText(mTitles.get(getTruePos(position)));
                break;

            case CUBE_INDICATOR:
                onCubeIndicatorChange(position);
                break;

            case NUM_INDICATOR:
                onNumIndicatorChange(position);
                break;

            case NUM_INDICATOR_TITLE:
                onNumIndicatorChange(position);
                mBannerTitle.setText(mTitles.get(getTruePos(position)));
                break;
            default:
                break;
        }
    }


    private void onCircleIndicatorChange(int position){

        mIndicators.get(getTruePos(position)).setImageResource(mIndicatorSet?mIndicatorSelected:R.drawable.indicator_selected);
        for(int i=0;i<mIndicators.size();i++){
            if(i!=getTruePos(position)){
                mIndicators.get(i).setImageResource(mIndicatorSet?mIndicatorUnselected:R.drawable.indicator_unselected);
            }
        }
    }

    private void onCubeIndicatorChange(int position){

        mIndicators.get(getTruePos(position)).setImageResource(mIndicatorSet?mIndicatorSelected:R.drawable.indicator_cube_selected);
        for(int i=0;i<mIndicators.size();i++){
            if(i!=getTruePos(position)){
                mIndicators.get(i).setImageResource(mIndicatorSet?mIndicatorUnselected:R.drawable.indicator_cube_unselected);
            }
        }
    }


    private void onNumIndicatorChange(int position){
        int i=position;
        if(i==0){
            i=mImageCount;
        }
        if(position>mImageCount){
            i=1;
        }
        mNumIndicator.setText(i+"/"+mImageCount);
    }

    /**
     * start this banner
     */
    public void start(){

        checkImageAndTitleNum();
        loadFromUrlsIfNeeded();
        initView();
        if(mIsGif&&mProgressType!=-1){
            setUpGifProgress();
        }
        showIndicators();
        applyViewPagerAdapterListener();
        startPlayIfNeeded();

    }


    private void startPlay(){
        if(mIsAutoPlay){
            mIsPlaying=true;
            mRunnable=new ViewPagerRunnable(mViewPager,mImageCount,mDelayTime);

            mHandler.postDelayed(mRunnable,mDelayTime);
        }
    }

    private void startPlayIfNeeded(){
        if(mImageCount>1&& mIsAutoPlay){
            startPlay();
        }
    }

    private void setUpGifProgress(){

        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(dm.widthPixels/12, dm.widthPixels/12);
        params.addRule(CENTER_HORIZONTAL);
        params.addRule(CENTER_VERTICAL);
        if(mProgressType==TEXT_PROGRESS){
            mTextProgress=new TextView(mContext);
            mTextProgress.setTextColor(Color.WHITE);
            mBannerParentView.addView(mTextProgress,params);
            mTextProgress.setVisibility(GONE);
        }else if(mProgressType==CIRCLE_PROGRESS){
            mCircleProgress=new RingProgressBar(mContext);
            mCircleProgress.setStyle(RingProgressBar.FILL);
            mCircleProgress.setRingColor(Color.WHITE);
            mBannerParentView.addView(mCircleProgress,params);
            mCircleProgress.setVisibility(GONE);
        }

        GifDownloader.setProgressListener(new GifDownloadManager.ProgressListener() {
            /**
             * Show the progress according to the download thread name.
             * Since we download all the image synchronously,we will probably hava more than one progress.
             * The progressView can be accessed and written in multiple threads,so no need to use synchronized.
             * but we just show the progress of the current item
             */
            @Override
            public void showProgress(final int index, final int progress) {


                //we need to update view in the main thread
                if(index==mSelectedIndex){
                    if(mProgressType==TEXT_PROGRESS){
                        mTextProgress.post(new Runnable() {
                            @Override
                            public void run() {
                                mTextProgress.setText(progress+"%");
                                if(downloadFinished[index] || progress==100){
                                    mTextProgress.setVisibility(GONE);
                                }else {
                                    mTextProgress.setVisibility(VISIBLE);
                                }
                            }
                        });
                    }else if(mProgressType==CIRCLE_PROGRESS){

                        mCircleProgress.post(new Runnable() {
                            @Override
                            public void run() {
                                mCircleProgress.setProgress(progress);
                                if(progress==100 || downloadFinished[index]){
                                    mCircleProgress.setVisibility(GONE);
                                }else{
                                    mCircleProgress.setVisibility(VISIBLE);
                                }
                            }
                        });
                    }

                }

                if(progress==100){
                    downloadFinished[index]=true;
                }

            }
        });
    }



    /**
     * check the number of titles and images
     * titles and images must have the same size to avoid some logic error
     */
    private void checkImageAndTitleNum(){

        if(mImageCount!=mTitles.size()&&(mBannerType==CIRCLE_INDICATOR_TITLE||mBannerType==NUM_INDICATOR_TITLE)){
            throw new RuntimeException("image size and title size is not the same\n"
                        +"size of images: "+mImageCount+"\n"
                        +"size of titles: "+mTitles.size()+"\n"
                        +"if you do Not need titles,please set the banner type to non-title type");
        }
    }


    /**
     * Load image from urls
     * need to apply an imageloader,see{@link ImageLoader}
     */
    private void loadFromUrlsIfNeeded(){

        if(mImageLoader==null){
            return;
        }

        if(mBannerImages.isEmpty()&&!mUrls.isEmpty()){
            if(mImageCount>1){
                mBannerImages.add(mIsGif?newGifFromUrl(mImageCount-1):newImageFroUrl(mImageCount-1));
                for(int i=0;i<mUrls.size();i++){
                    mBannerImages.add(mIsGif?newGifFromUrl(i):newImageFroUrl(i));
                }
                mBannerImages.add(mIsGif?newGifFromUrl(0):newImageFroUrl(0));
            }else{
                mBannerImages.add(mIsGif?newGifFromUrl(0):newImageFroUrl(0));
            }

        }

    }


    /**
     * New an image or gif with the given url
     *@param index the index of the Image
     */
    private ImageView newImageFroUrl(int index){
        ImageView image=new ImageView(mContext);
        image.setScaleType(mScaleType);
        mImageLoader.loadImages(mContext,mUrls.get(index),image);
        return image;
    }


    private GifImageView newGifFromUrl(int index){
        GifImageView gif=new GifImageView(mContext);
        gif.setScaleType(mScaleType);
        mImageLoader.loadGifs(mUrls.get(index),gif,mScaleType);
        return gif;
    }


    /**
     * the listener interface for banner event,includeing clicked,dragging and idled
     * the index starts from 0,which item's value starts from 0
     */
    public interface BannerPageListener{
        void onBannerClick(int item);
        void onBannerDragging(int item);
        void onBannerIdle(int item);
    }



    public XBanner setBannerPageListener(BannerPageListener listener){
        mBannerPageListner=listener;
        return this;
    }

    public XBanner setImageLoader(ImageLoader imageLoader){
        mImageLoader=imageLoader;
        return this;
    }


    private static class ViewPagerRunnable implements Runnable{
        //avoid memory leak
        private WeakReference<ViewPager> mViewPager;
        int count;
        int delaytime;

        ViewPagerRunnable(ViewPager viewPager, int imagecount, int delay){
            mViewPager=new WeakReference<>(viewPager);
            count=imagecount;
            delaytime=delay;
        }

        @Override
        public void run(){
            if(count>1){
                if(mViewPager.get()!=null){
                    int current=mViewPager.get().getCurrentItem();
                    if(current==count+1){
                        mViewPager.get().setCurrentItem(1,false);
                        mHandler.post(this);
                    }else {
                        mViewPager.get().setCurrentItem(current+1);
                        mHandler.postDelayed(this,delaytime);
                    }
                }

            }

        }



    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        super.dispatchTouchEvent(me);
        if(me.getAction()==MotionEvent.ACTION_DOWN){
            if(mIsPlaying){
                mHandler.removeCallbacks(mRunnable);
                mIsPlaying=false;
            }
        }

        if(me.getAction()==MotionEvent.ACTION_UP||me.getAction()==MotionEvent.ACTION_CANCEL
                ||me.getAction()==MotionEvent.ACTION_OUTSIDE&&!mIsPlaying&&mIsAutoPlay){
            mIsPlaying=true;
            startPlay();
        }
        return true;
    }

    //release banner here
    public void releaseBanner(){

        if(mBannerImages!=null){
            mBannerImages.clear();
        }

        if(mIndicators!=null){
            mIndicators.clear();
        }

        if(mBannerParentView!=null){
            mBannerParentView.removeAllViews();
            mBannerParentView=null;
        }
        mHandler.removeCallbacks(mRunnable);
        mRunnable=null;

        removeScroller();
        if(mIsGif){
            GifDownloader.releaseDownloader();
        }

        mAdapter.releaseAdapter();
        mAdapter=null;
        mBannerPageListner=null;
        mImageLoader=null;


        mViewPager.setAdapter(null);

        mCircleProgress=null;
        mTextProgress=null;
        try {
            Field f=ViewPager.class.getDeclaredField("mOnPageChangeListeners");
            f.setAccessible(true);
            f.set(mViewPager,null);

        }catch (Exception e){
            e.printStackTrace();
        }


        mViewPager.removeAllViews();
        mViewPager=null;


        //Since there may be a number of images or gif in the memory,
        //consider starting a gc to clean the images in the memory,
        //before calling a gc,we need to remove the reference of images manually
        //and we also do something else to release the objects
        System.gc();
        System.runFinalization();

    }



    /**
     * clear cache
     */
    public void clearGifCache(){
        GifDownloader.clearGifCache();
    }

    /**
     *auto delete the cache when cache size is greater than sizeMB
     * @param sizeMB the top size of cache
     *
     */
    public void autoDeleteGifCache(int sizeMB){
        GifDownloader.autoDeleteGifCache(sizeMB);
    }



}







