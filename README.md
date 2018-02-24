[中文版](README_CN.md) | **English** </br>
XBanner
=========
This is a banner suppoted gifs and images.无限轮播的广告控件，提供gif支持。
## Introduction
A banner for showing Ads with the Viewpager and supports automatic play in an infinite loop.With XBanner you can customize the style of this banner.XBanner also provides gif supprot,and we use the 3rd party dependency to load gifs for better perfoemance. 

## Effects
#### The final effect is as the gif below.

<div align=center><img width="316" height="584" src="https://github.com/AbbyJM/XBanner/raw/master/gif/demo1.gif"/></div>  

#### Customize your banner style now.
<a href="gif/demo2.gif"><img src="gif/demo2.gif" width="30%"/></a> <a href="gif/demo3.gif"><img src="gif/demo3.gif" width="30%"/></a> <a href="gif/demo4.gif"><img src="gif/demo4.gif" width="30%"/></a> 

## Gradle 
```java
dependencies{

  compile 'com.abby.app:xbanner:1.5.5' //the latest version
  
  //Thans koral-- for android-gif-drawable,it's a good solution for showing gif images.It is high performance.
  //Instead of using ImageView+Glide,we use gif-drawable for better performance
  //The dependency 'pl.droidsonroids.gif:android-gif-drawable:1.2.8' was added in this project.
}
```
    
## Usage
 #### A simple way to use XBanner is like below. releaseBanner() must be called when the view is destroying

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
xbanner.releaseBanner(); // releaseBanner() must be called when the view is destroying
}
```    
   
 #### A simple way to use XBanner with titles.***BannerType must be set to TITLE_TYPES when using titles.***
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
    xbanner.releaseBanner();// releaseBanner() must be called when the view is destroying
}
```
      
#### If you want to load images from the url,an ImageLoader will be needed.A simple way to use ImageLoader is like below.
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
    xbanner.releaseBanner();// releaseBanner() must be called when the view is destroying
}
```

### A simple way to load gifs
```java
@Override
public void onCreate(Bundle savedInstanceState) {
    banner.setImageUrls(gifUrls)
    .asGif()
    .setLoadingProgressType(XBanner.CIRCLE_PROGRESS)  //now you can apply a loading progress view here
    .start();
}

@Override
public void onDestroy(){  
   super.onDestroy();  
    xbanner.releaseBanner();// releaseBanner() must be called when the view is destroying
}
```
     
## Feature(Methods)
|name|description|params
|----|-----|----|
|setBannerTypes|set the type of banner,CIRCLEINDICATOR by default|CIRCLE_INDICATOR,***CIRCLE_INDICATOR_TITLE***,CUBE_INDICATOR,***NUM_INDICATOR,NUM_INDICATOR_TITLE***|
|setIndicatorGravity|set the gravity of indicators,___CENTER by default___|INDICATOR_START,***INDICATOR_CENTER***,INDICATOR_END|
|setUpIndicatorSize|set up the size of indicators,***IN PX,BOTH SELECTED AND UNSELECTED will be set***|int indicatorSize|
|setUpIndicators|set up the indicator resID|int res_selected,int res_unselected|
|setEllipsizeType|set the ellipsizetype of the title text,,___END by default___|ELLIPSIZE_END,***ELLIPSIZE_MARQUEE***|
|setImageScaleType|set the scale type of the image,___this method must be called before setting up the images___,___FIT_XY by default___|ImageView.ScaleType scaleType|
|setScroller|set the scroller of the viewpager|the scroller to be applied
|setTitlebgAlpha|set the title background to be alpha|no params|
|setImageRes|set the image resource|int[] res|
|setImageUrls|set the image urls|List< String > urls|
|setTitles|set the titles|List< String > titles|
|setImageResAndTitles|set the imageres and titles|int[] res,List< String > titles|
|setImageUrlsAndTitles|set the image urls and titles|List< String > urls,List< String > titles|
|setPageTransformer|set the transformer for the viewpager|PageTransformer transformer|
|setDelay|set the interval of the banner when playing,***IN MS***|int delay|
|isAutoPlay|indicate if the banner is automatically playing|boolean is autoPlay|
|setTransformerSpeed|set the transform speed when idle,***IN MS***|int speed|
|setTitleHeight|set the height of title,***IN PX***|int height|
|setBannerPageListener|set the banner listener|BannerPageListener listener|
|getViewPager|get the viewPager of the banner|no params|
|asGif|indicate the urls are gif urls|no params|
|clearGifCache|clear the cache of gifs|no params|
|autoDeleteGifCache|automatically delete the cache of gifs when reach the the given size|int sizeInMB|
|start|start the banner,***must be set to start the banner***|no params|
|setLoadingProgressType|set a progress view when loading gifs|CIRCLE_PROGRESS or TEXT_PROGRESS|
|notifyDataSetChanged|refresh data after recalling setImageUrls and setImageRes|no params| 
## Notice
* []() if the images do not display,check out your network permission and your image if they are configured correctly
* []() must set the banner type to TITLE TYPE when we set titles to avoid some logic errors
* []() setImageScaleType must be called before we set the images
* []() set an ImageLoader when loading images from url,but if you are loading gifs,you don't really have to 
* []() must call asGif() after you set the Url with setImageUrls method when loading gif,no need to set an ImageLoader if so
* []() must call releaseBanner() in onDestroy()
* []() to get a better visaul effect,indicator gravity is set to END when in TITLE mode
* []() just confirm that your gif is in a correct format when you are downloading gif,or it won't be displayed
* []() when refreshing data at run time,you should call notifyDataSetChanged() and you should reconfig the bannerPageListener because the listener is base on the position not the image itself,***only image supports refresh,gif does not beacuse the cost is huge*** 
* []() supports API 19 and above


## Update
#### v1.5.2:
* []() new method setLoadingProgressType(int type) to apply a progress view when loading gifs,while it needs a long time load gifs.
* []() optimize the memory consumption and the process of downloading gifs
 
#### v1.5.3:
* []() fix a problem that the gif may not be displayed correctly

#### v1.5.5:
* []() suppot data refresh at runtime,by calling notifyDataSetChanged() you can refresh data after you reset the dataset
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
