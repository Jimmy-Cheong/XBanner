package com.app.abby.xbannerdemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.widget.ImageView;
import android.widget.Toast;


import com.app.abby.xbanner.AbstractUrlLoader;
import com.app.abby.xbanner.XBanner;
import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;


/**
 * Created by Abby on 10/6/2017.
 * @author Abby
 */
public class MainActivity extends AppCompatActivity {

    private XBanner banner1;
    private XBanner banner2;
    private XBanner banner3;

    List<String> mdata= Arrays.asList("http://flv2.bn.netease.com/videolib3/1604/28/fVobI0704/SD/fVobI0704-mobile.mp4","http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
    int[] res={R.drawable.pic1,R.drawable.pic2,R.drawable.pic3,R.drawable.pic4,R.drawable.pic5};
    List<String> titles=Arrays.asList("This is the picture 1","This is the picture 2","This is the picture 3","This is the picture 4","This is the picture 5");
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> gifUrls=new ArrayList<>();
        List<String> imageUrls=new ArrayList<>();
        imageUrls.add("http://img2.tgbusdata.cn/v2/thumb/jpg/YzQ0NSw4MzAsNjQwLDQsMywxLC0xLDAscms1MA==/u/olpic.tgbusdata.cn/uploads/allimg/160324/238-160324160031.jpg");
        imageUrls.add("http://img2.tgbusdata.cn/v2/thumb/jpg/ZjliZCw4MzAsNjQwLDQsMywxLC0xLDAscms1MA==/u/olpic.tgbusdata.cn/uploads/allimg/160324/238-160324160031-50.jpg");
        imageUrls.add("http://img2.tgbusdata.cn/v2/thumb/jpg/OTg2YSw4MzAsNjQwLDQsMywxLC0xLDAscms1MA==/u/olpic.tgbusdata.cn/uploads/allimg/160324/238-160324160031-51.jpg");
        imageUrls.add("http://img2.tgbusdata.cn/v2/thumb/jpg/MmNjZSw4MzAsNjQwLDQsMywxLC0xLDAscms1MA==/u/olpic.tgbusdata.cn/uploads/allimg/160324/238-160324160031-52.jpg");
        imageUrls.add("http://img2.tgbusdata.cn/v2/thumb/jpg/MzQ2ZCw4MzAsNjQwLDQsMywxLC0xLDAscms1MA==/u/olpic.tgbusdata.cn/uploads/allimg/160324/238-160324160032.jpg");


        gifUrls.add("https://imgsa.baidu.com/forum/w%3D580/sign=ef7fb6fd912bd40742c7d3f54b889e9c/415cefc4b74543a9647dd21918178a82b8011442.jpg");
        gifUrls.add("https://imgsa.baidu.com/forum/w%3D580/sign=fa476d68af773912c4268569c8188675/f04d7ff40ad162d9fe9749c917dfa9ec8b13cdab.jpg");
        gifUrls.add("https://imgsa.baidu.com/forum/w%3D580/sign=8444d11918178a82ce3c7fa8c602737f/4adcad014c086e061bf7149e04087bf40bd1cb49.jpg");
        gifUrls.add("https://imgsa.baidu.com/forum/w%3D580/sign=3df3a931b51c8701d6b6b2ee177e9e6e/0546958fa0ec08fa42bd04725fee3d6d54fbda6f.jpg");
        gifUrls.add("https://imgsa.baidu.com/forum/w%3D580/sign=c125c9878335e5dd902ca5d746c4a7f5/2f11524e9258d1093d398067d758ccbf6e814dd6.jpg");


        banner2=(XBanner)findViewById(R.id.banner2);
        banner2.setBannerTypes(XBanner.NUM_INDICATOR_TITLE)
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setImageRes(res)
                .setTitles(titles)
                .setEllipsizeType(XBanner.ELLIPSIZE_MARQUEE)
                .isAutoPlay(true)
                .setDelay(7000)
                .setBannerPageListener(new XBanner.BannerPageListener() {
                    @Override
                    public void onBannerClick(int item) {
                        int index=item+1;
                        Toast.makeText(MainActivity.this,"You clicked the picture "+index,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onBannerDragging(int item) {

                    }

                    @Override
                    public void onBannerIdle(int item) {

                    }
                })
                .start();





        banner1=(XBanner)findViewById(R.id.banner);
        banner1  .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setImageUrls(imageUrls)
                .setImageLoader(new AbstractUrlLoader() {
                    @Override
                    public void loadImages(Context context, String url, ImageView image) {
                        Glide.with(context)
                                .load(url)
                                .into(image);
                    }

                    @Override
                    public void loadGifs(String url, GifImageView gifImageView, ImageView.ScaleType scaleType) {

                    }
                })
                .setBannerTypes(XBanner.CIRCLE_INDICATOR_TITLE)
                .setIndicatorGravity(XBanner.INDICATOR_START)
                .setTitles(titles)
                .setDelay(3000)
                 .setUpIndicators(R.drawable.ic_selected,R.drawable.ic_unselected)
                .setUpIndicatorSize(20,20)
                .isAutoPlay(true)
                .start();




        banner3=(XBanner)findViewById(R.id.banner3);
        banner3.setImageUrls(gifUrls)
                .setBannerTypes(XBanner.CIRCLE_INDICATOR_TITLE)
                .asGif()
                .setTitles(titles)
                .isAutoPlay(true)
                .setDelay(10000)
                .setBannerPageListener(new XBanner.BannerPageListener() {
                    @Override
                    public void onBannerClick(int item) {
                        int index =item+1;
                        Toast.makeText(MainActivity.this,"You clicked the gif "+index,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onBannerDragging(int item) {

                    }

                    @Override
                    public void onBannerIdle(int item) {

                    }
                })
                .start();


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        banner1.releaseBanner();
        banner3.releaseBanner();
        banner2.releaseBanner();

    }


}








