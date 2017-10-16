[中文版](README_CN.md) | **English** </br>
XBanner
=========
This is a banner suppoted gifs and images.无限轮播的广告插件，提供gif支持。
## Introduction
A banner for showing Ads with the Viewpager and supports automatic play in an infinite loop.With XBanner you can customize the style of this banner.XBanner also provides gif supprot,and we use the 3rd party dependency to load gifs for batter perfoemance. 

## Effects
#### The final effect is as the gif below.

<div align=center><img width="316" height="584" src="https://github.com/AbbyJM/XBanner/raw/master/gif/demo1.gif"/></div>  

#### Customize your banner style now.
<a href="gif/demo2.gif"><img src="gif/demo2.gif" width="30%"/></a> <a href="gif/demo3.gif"><img src="gif/demo3.gif" width="30%"/></a> <a href="gif/demo4.gif"><img src="gif/demo4.gif" width="30%"/></a> 

## Gradle 
```java
dependencies{

  compile 'com.abby.app:xbanner:1.0.1' //Gif supported version.If you use gif,contain this in your project.
  compile 'com.abby.app:xbanner:1.5.1' //Version without gif dependency.If you don't use gif,use this version.
  
  
  //Thans koral-- for android-gif-drawable,it's a good solution for showing gif images.It is high performance.
  //Instead of using ImageView+Glide,we use gif-drawable for better performance
  //The dependency 'pl.droidsonroids.gif:android-gif-drawable:1.2.8' was added in the gif version.
}
```
    
## Usage
 #### A simple way to use XBanner is like below. //Attension,releaseBanner() must be called in onDestroy()

```java
@Override
public void onCreate(Bundle savedInstanceState) {
   int[] res={R.drawable.pic1,R.drawable.pic2,R.drawable.pic3,R.drawable.pic4,R.drawable.pic5};
    xbanner.isAutoPlay(false)
           .setDelay(3000)
           .setImageRes(res)
           .start(); 
   }

@Override
public void onDestroy(){
super.onDestroy();     
xbanner.releaseBanner(); //releaseBanner() must be called in onDestroy()
}
```    
   
 #### A simple way to use XBanner with titles.***Attension,BannerType must be set to TITLE_TYPES when using titles.***
```java
@Override
public void onCreate(Bundle savedInstanceState) {
int[] res={R.drawable.pic1,R.drawable.pic2,R.drawable.pic3,R.drawable.pic4,R.drawable.pic5};
List<String> titles=Arrays.asList("This is the picture 1","This is the picture 2","This is the picture 3","This is the picture 4","This is the picture 5");
  xbanner.isAutoPlay(false)
         .setBannerTypes(XBanner.NUM_INDICATOR_TITLE)//must set banner type to title type to avoid some logic error
         .setDelay(3000)
         .setImageRes(res)
         .setTitles(titles)// setImageResAndTitles(res,titles) also OK
         .start(); 
 }

@Override
public void onDestroy(){
    super.onDestroy();     
    xbanner.releaseBanner();// releaseBanner() must be called in onDestroy()
}
```
      
#### If you want to load images from the url,an ImageLoader will be needed.A simple ImageLoader is like below.
```java
@Override
public void onCreate(Bundle savedInstanceState) {
List<String> imageUrls=new ArrayList<>();
imageUrls.add("http://img2.tgbusdata.cn/v2/thumb/jpg/YzQ0NSw4MzAsNjQwLDQsMywxLC0xLDAscms1MA==/u/olpic.tgbusdata.cn/uploads/allimg/160324/238-160324160031.jpg");
imageUrls.add("http://img2.tgbusdata.cn/v2/thumb/jpg/ZjliZCw4MzAsNjQwLDQsMywxLC0xLDAscms1MA==/u/olpic.tgbusdata.cn/uploads/allimg/160324/238-160324160031-50.jpg");
xbanner.isAutoPlay(false)
     .setDelay(3000)
     .setImageUrls(imageUrls)
     //.asGif() if you want to load gif,call it
     .setImageLoader(new AbstractUrlLoader() {
      @Override
      /**
       *the one using Glide,you can customize your ImageLoader
       *when loading gifs,a default ImageLoader will be applied,which you don't need to add one
       *but if you want to customize your loader,do it
       */
      public void loadImages(Context context, String url, ImageView image) {
          Glide.with(context)   
                  .load(url)
                  .into(image);
      }

      @Override
      public void loadGifs(String url, GifImageView gifImageView, ImageView.ScaleType scaleType) {

      }
}).start(); 
}

@Override
public void onDestroy(){  
   super.onDestroy();  
    xbanner.releaseBanner();// releaseBanner() must be called in onDestroy()
}
```
     
## Feature(Methods)
|name|description|params
|----|-----|----|
|setBannerTypes|set bannertype|CIRCLE_INDICATOR,***CIRCLE_INDICATOR_TITLE***,CUBE_INDICATOR,***NUM_INDICATOR,NUM_INDICATOR_TITLE***,the default is CIRCLE_INDICATOR|
|setIndicatorGravity|set the gravity of indicators|INDICATOR_START,***INDICATOR_CENTER***,INDICATOR_END,___the default is CENTER___|
|setUpIndicatorSize|set up the size of indicators|int size,***IN PX,BOTH SELECTED AND UNSELECTED will be set***|
|setUpIndicators|set up the indicator res|int res_selected,int res_unselected|
|setEllipsizeType|set the ellipsizetype of the title text|ELLIPSIZE_END,***ELLIPSIZE_MARQUEE***,___the default is END___|
|setImageScaleType|set the scale type of the image|no params,___Attension,this method must be called before setting up the images___,___the default is FIT_XY___|
|setScroller|set the scroller of the viewpager|the scroller to be applied
|setTitlebgAlpha|set the title background to be alpha|no params|
|setImageRes|set the image resource|int[] res|
|setImageUrls|set the image urls|List< String > urls|
|setTitles|set the titles|List< String > titles|
|setImageResAndTitles|set the imageres and titles|int[] res,List< String > titles|
|setImageUrlsAndTitles|set the image urls and titles|List< String > urls,List< String > titles|
|setPageTransformer|set the transformer for the viewpager|PageTransformer transformer|
|setDelay|set the interval of the banner when playing|int delay,***IN MS***|
|isAutoPlay|indicate if the banner is automatically playing|boolean is autoPlay|
|setTransformerSpeed|set the transform speed when idle|int speed,***IN MS***|
|setTitleHeight|set the height of title|int height,***IN PX***|
|setBannerPageListener|set the banner listener|BannerPageListener listener|
|getViewPager|get the viewPager of the banner|no params|
|asGif|indicate the urls are gif urls|no params|
|clearGifCache|clear the cache of gifs|no params|
|autoDeleteGifCache|automatically delete the cache of gifs when reach the the given size|int sizeInMB|
|start|start the banner|no params,***must be set to start the banner***|
 
## Notice
* []() must set the banner type to TITLE TYPE when we set titles to avoid some logic errors
* []() setImageScaleType must be called before we set the images
* []() set an ImageLoader when loading images from url,but if you are loading gifs,you don't really have to 
* []() must call asGif() when loading gif,no need to set an ImageLoader if so
* []() must call releaseBanner() in onDestroy()
* []() to get a better visaul effect,indicator gravity is set to END when in TITLE mode
* []() supports API 19 and above


# License
      Copyright 2017 AbbyJM

      Licensed under the Apache License, Version 2.0 (the "License");
      you may not use this file except in compliance with the License.
      You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

      Unless required by applicable law or agreed to in writing, software
      distributed under the License is distributed on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
      See the License for the specific language governing permissions and
      limitations under the License.
