[English](https://github.com/AbbyJM/XBanner) | **中文版** </br>
XBanner
=========
支持无限轮播的广告插件，提供gif支持。
## 简介
XBanner是一个支持无限轮播的广告插件，主要通过viewpager实现。你可以自定义指示器的风格。XBanner提供了gif支持，为了提供更好的性能，采用了第三方依赖库android-gif-drawable。
## 效果
#### 最终的效果
<div align=center><img width="316" height="584" src="https://github.com/AbbyJM/XBanner/raw/master/gif/demo1.gif"/></div> 
 
<a href="gif/demo2.gif"><img src="gif/demo2.gif" width="30%"/></a><a href="gif/demo3.gif"><img src="gif/demo3.gif" width="30%"/></a><a href="gif/demo4.gif"><img src="gif/demo4.gif" width="30%"/></a> 

## Gradle 
```java
dependencies{
  compile 'com.abby.app:xbanner:1.0.1' //这是支持gif的版本，如果你想要使用gif图片，请在gradle中添加依赖
  compile 'com.abby.app:xbanner:1.5.1' //无gif版本，去除了gif-drawable的依赖，如果你不使用gif图片，请在gradle中添加此依赖


  //非常感谢 koral--的android-gif-drawable,这是一个高性能的gif加载依赖库
  //使用这个库的原因是ImageView配合Glide加载gif图片的性能并不是很好
  //android-gif-drawable已经添加在gif版本中   
}
```   
    
## 用法
 #### 一个简单的用法如下所示 //注意，必须在onDestroy()中调用releaseBanner()来释放回调
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
xbanner.releaseBanner(); //必须在onDestroy()中调用releaseBanner()
}
```    
   
 #### 一个简单的使用标题的例子 ***注意，使用标题时一定要将Bannertype设置成TITLE对应的风格***
```java
@Override
public void onCreate(Bundle savedInstanceState) {
int[] res={R.drawable.pic1,R.drawable.pic2,R.drawable.pic3,R.drawable.pic4,R.drawable.pic5};
List<String> titles=Arrays.asList("This is the picture 1","This is the picture 2","This is the picture 3","This is the picture 4","This is the picture 5");
  xbanner.isAutoPlay(false)
         .setBannerTypes(XBanner.NUM_INDICATOR_TITLE)//使用标题时一定要将Bannertype设置成TITLE对应的风格以避免一些逻辑错误
         .setDelay(3000)
         .setImageRes(res)
         .setTitles(titles)// 也可以使用setImageResAndTitles(res,titles)
         .start(); 
 }

@Override
public void onDestroy(){
    super.onDestroy();     
    xbanner.releaseBanner();// 必须在onDestroy()中调用releaseBanner()
}
```
      
#### 如果从网络中获取图像时，必须设置一个Imageloader，但如果加载的是gif图片，可以使用内置的loader
```java
@Override
public void onCreate(Bundle savedInstanceState) {
List<String> imageUrls=new ArrayList<>();
imageUrls.add("http://img2.tgbusdata.cn/v2/thumb/jpg/YzQ0NSw4MzAsNjQwLDQsMywxLC0xLDAscms1MA==/u/olpic.tgbusdata.cn/uploads/allimg/160324/238-160324160031.jpg");
imageUrls.add("http://img2.tgbusdata.cn/v2/thumb/jpg/ZjliZCw4MzAsNjQwLDQsMywxLC0xLDAscms1MA==/u/olpic.tgbusdata.cn/uploads/allimg/160324/238-160324160031-50.jpg");
xbanner.isAutoPlay(false)
     .setDelay(3000)
     .setImageUrls(imageUrls)
     //.asGif() 如果加载的是gif则调用此方法
     .setImageLoader(new AbstractUrlLoader() {
      @Override
      /**
       *简单Glide使用方法，当然你可以自己自定义
       *加载gif图片时，可以不用设置loader而使用内置的loader，当然也可以自己定义
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
    xbanner.releaseBanner();// 一定要在onDestoy()中调用releaseBanner()来释放回调
}
```
     
## 特性(主要方法)
|名称|描述|参数
|----|-----|----|
|setBannerTypes|设置banner类型,默认的类型是CIRCLE_INDICATOR|CIRCLE_INDICATOR,***CIRCLE_INDICATOR_TITLE***,CUBE_INDICATOR,***NUM_INDICATOR,NUM_INDICATOR_TITLE***|
|setIndicatorGravity|设置指示器的重心方向,___默认的类型是 CENTER___|INDICATOR_START,***INDICATOR_CENTER***,INDICATOR_END|
|setUpIndicatorSize|设置指示器的尺寸,***单位为像素，选中和未选中的尺寸都会同时设定***|int size|
|setUpIndicators|设置指示器选中和未选中时的图片ResourceID|int res_selected,int res_unselected|
|setEllipsizeType|设置标题文本的ellipsizetype,___默认的类型END___|ELLIPSIZE_END,***ELLIPSIZE_MARQUEE***|
|setImageScaleType|设置图片的缩放类型,___注意，这个方法必须在图片设置之前调用，否则会没有效果___,___默认的缩放类型是FIT_XY___|ImageView.ScaleType scaletype|
|setScroller|设置scroller|将要设置的scroller
|setTitlebgAlpha|设置标题的背景为透明|不需要参数|
|setImageRes|设置图片的resourceID|int[] res|
|setImageUrls|设置图片的url|List< String > urls|
|setTitles|设置标题|List< String > titles|
|setImageResAndTitles|设置图片的resourceID和标题|int[] res,List< String > titles|
|setImageUrlsAndTitles|设置图片的url和标题|List< String > urls,List< String > titles|
|setPageTransformer|设置一个transformer给viewpager|PageTransformer transformer|
|setDelay|设置播放的延迟,***单位是毫秒***|int delay|
|isAutoPlay|指出banner是否是自动播放，***默认情况下不自动播放***|boolean is autoPlay|
|setTransformerSpeed,***默认是600毫秒***|设置未拖拽情况下transformer的切换延迟|int speed|
|setTitleHeight|设置标题的高度,***单位是像素***|int height|
|setBannerPageListener|设置banner的监听器|BannerPageListener listener|
|getViewPager|获得banner的viewpager|不需要参数|
|asGif|指出当前加载的url是不是来自gif图片|不需要参数|
|clearGifCache|清楚gif的缓存|不需要参数|
|autoDeleteGifCache|当达到一个阈值时自动清除gif缓存|int sizeInMB|
|start|banner开始播放,***一定要调用这个方法来开始播放***|不需要参数|
 
## 注意
* []() 一定要将banner的类型设置为TITLE类型以避免一些不必要的逻辑错误
* []() 一定要在图片设置完之前调用setImageScaleType
* []() 必须设置一个Imageloader如果当前加载的图片来自于网络，但是如果加载的是git图片，可以不设置，将使用内置的加载器
* []() 一定要调用asGif()如果当前加载的图片是gif图片 
* []() 一定要在onDestroy()中调用releaseBanner()来释放回调
* []() 为了获取更好的视觉效果，当我们使用标题的时候指示器重心将设置在末端
* []() 支持API 19以及更高版本，因为某些API需要更高的版本支持
 
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
