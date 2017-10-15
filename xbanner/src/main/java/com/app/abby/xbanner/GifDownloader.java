package com.app.abby.xbanner;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Abby on 10/6/2017.
 * @author Abby
 */

public class GifDownloader {


    private static Handler mHandler;
    private static Runnable mRunnable;
    private static String path;
    private static  ThreadPoolExecutor threadExecutor;

    private static final int CORE_POOL_SIZE=5;
    private static final int MAX_POOL_SIZE=10;
    private static final int THREAD_ALIVE_TIME=10;
    private static final int QUEUE_SIZE=64;
    private static final int SHORTEST_FILE_NAME=5;
    private static final String TEM_FILE_END=".tmp";
    //delete the cache in the root path
    private static String deletePath;
    public static void displayImage(final String url, final GifImageView gifView, final ImageView.ScaleType scaleType){
        String md5Url = getMd5(url);
        path = gifView.getContext().getCacheDir().getAbsolutePath()+"/"+md5Url;  //file with .tmp still downloading
        deletePath=gifView.getContext().getCacheDir().getAbsolutePath();
        Log.d("GifHelper","the cache dir is"+path);
        final File cacheFile = new File(path);

        final WeakReference<GifImageView> gifImage=new WeakReference<>(gifView);

        //if the file was cached before
        //then we load it from the local file
        if(cacheFile.exists()){
            displayImage(cacheFile,gifImage.get(),scaleType);
            Log.d("GifHelper","the gif "+path+ " was cached before");
            return;
        }

        final File newFile=new File(path+".tmp");
        startDownLoad(url, newFile, new AbstractDownLoadTask() {
            @Override
            void onStart() {
                Log.d("GifDownloader","now start download");
            }
            @Override
            void onLoading(long total, long current) {

                int progress=(int)(current*100/total);
                Log.d("GifDownloader","now the download of "+path+ " has finished "+progress+"%");
                showFirstFrame(newFile,gifImage.get());
            }

            @Override
            void onSuccess(File file) {
                if(file==null) {
                    return;
                }
                String path = file.getAbsolutePath();
                if(path==null || path.length()<SHORTEST_FILE_NAME){
                    return;
                }
                File downloadFile = new File(path);
                File renameFile = new File(path.substring(0,path.length()-4));//remove .tmp
                Log.d("GifHelper","download success,the path of file is "+path+" rename to be "+renameFile.getAbsolutePath());
                if(path.endsWith(TEM_FILE_END)){
                    downloadFile.renameTo(renameFile);
                }
                if(gifImage.get()!=null){
                    displayImage(renameFile,gifImage.get(),scaleType);
                }
            }
            @Override
            void onFailure(Throwable e) {
                e.printStackTrace();
                Log.d("GifDownloader","image download failed");
            }
        });

    }

    /**
     * display image
     */
    public static boolean displayImage(File localFile,GifImageView gifImageView,ImageView.ScaleType scaleType){
        if(localFile==null || gifImageView==null){
            return false;
        }
        GifDrawable gifFrom;
        try {
            gifFrom = new GifDrawable(localFile);
            gifImageView.setImageDrawable(gifFrom);
            gifImageView.setScaleType(scaleType);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * get the md5 of a file
     * @param str
     * @return
     */
    public static String getMd5(String str) {
        int shortestMd5=24;
        if(str==null || str.length()<1){
            return "no_image.gif";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(str.getBytes());
            StringBuilder sb = new StringBuilder(40);
            for(byte x:bs) {
                if((x & 0xff)>>4 == 0) {
                    sb.append("0").append(Integer.toHexString(x & 0xff));
                } else {
                    sb.append(Integer.toHexString(x & 0xff));
                }
            }
            if(sb.length()<shortestMd5){
                return sb.toString();
            }
            return sb.toString().substring(8,24);//for the better performance
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "no_image.gif";
        }
    }


    /**
     *
     */
    public static abstract class AbstractDownLoadTask{
        abstract void onStart();
        abstract void onLoading(long total, long current);
        abstract void onSuccess(File target);
        abstract void onFailure(Throwable e);
    }

    /**
     * start download in the work thread
     */
    public static void startDownLoad(final String uri, final File targetFile, final AbstractDownLoadTask task){
        mHandler=new Handler();
        mRunnable=new Runnable() {
            @Override
            public void run() {
                downloadToStream(uri, targetFile,task,mHandler);
            }
        };
        //new Thread(mRunnable).start();  avoid using this method to create a thread,may reduce performance
        initThreadPool();
    }

    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "Runnable #" + mCount.getAndIncrement());
        }
    };


    private static void initThreadPool(){
        if(threadExecutor==null){
            threadExecutor=new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE,THREAD_ALIVE_TIME, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(QUEUE_SIZE),THREAD_FACTORY);
        }
        threadExecutor.execute(mRunnable);
        Log.d("GifDownloader","thread size is "+threadExecutor.getPoolSize());
        Log.d("GifDownloader","tasks being executing count "+threadExecutor.getQueue().size());
        Log.d("GifDownloader","tasks run complete count "+threadExecutor.getCompletedTaskCount());
    }


    public static void shutdownThreadPool(){
        if(threadExecutor!=null&&!threadExecutor.isShutdown()){
            threadExecutor.shutdown();
        }
    }


    private static long downloadToStream(String uri, final File targetFile, final AbstractDownLoadTask task, Handler handler) {

        HttpURLConnection httpURLConnection = null;
        BufferedInputStream bis = null;
        OutputStream outputStream = null;

        long result = -1;
        long fileLen = 0;
        long currCount = 0;
        try {
            try {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.onStart();
                    }
                });

                final URL url = new URL(uri);
                outputStream = new FileOutputStream(targetFile);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setReadTimeout(10000);

                final int responseCode = httpURLConnection.getResponseCode();
                if (HttpURLConnection.HTTP_OK == responseCode) {
                    Log.d("GifDownload","response OK");
                    bis = new BufferedInputStream(httpURLConnection.getInputStream());
                    result = httpURLConnection.getExpiration();
                    result = result < System.currentTimeMillis() ? System.currentTimeMillis() + 40000 : result;
                    fileLen = httpURLConnection.getContentLength();
                } else {
                    return -1;
                }
            } catch (final Exception ex) {
                task.onFailure(ex);
                return -1;
            }

            byte[] buffer = new byte[4096];//update progress per 4k
            int len = 0;
            BufferedOutputStream out = new BufferedOutputStream(outputStream);
            while ((len = bis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                currCount+=len;
                final long finalFileLen=fileLen;
                final long finalCurrCount=currCount;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.onLoading(finalFileLen,finalCurrCount);
                    }
                });
            }
            out.flush();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    task.onSuccess(targetFile);

                }
            });

        } catch (Throwable e) {
            result = -1;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (final Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * load the first frame of the gif as the placeholder
     */
    private static void showFirstFrame(File gifFile, GifImageView imageView){
        if(imageView==null){
            return;
        }
        if(imageView.getTag(R.style.AppTheme) instanceof Integer){
            return;//the first frame was showed before
        }
        try {
            GifDrawable gifFromFile = new GifDrawable(gifFile);
            boolean canSeekForward = gifFromFile.canSeekForward();
            if(!canSeekForward){
                return;
            }
            Log.d("GifHelper","the first frame can be show");
            gifFromFile.seekToFrame(0);
            gifFromFile.pause();
            imageView.setImageDrawable(gifFromFile);
            imageView.setTag(R.style.AppTheme,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * delete the given file or dir
     */
    private  static boolean delete(File file){

            if (file.isFile()) {
                return file.delete();
            }
            if (file.isDirectory()) {
                File[] childFiles = file.listFiles();
                if (childFiles == null || childFiles.length == 0) {
                    return file.delete();
                }

                for (File childFile : childFiles) {
                    delete(childFile);
                }
                return file.delete();
            }
            return false;
    }

    /**
     * clear the cache
     */
    public static boolean clearGifCache(){
        return delete(new File(deletePath));
    }


    /**
     * automatically delete the gif cache
     */
    public static boolean autoDeleteGifCache(int sizeMB) {

        double size=FileSizeUtil.getFileOrFilesSize(deletePath,3);
        Log.d("GifDownloader","the cache size is "+size+" MB");
        if(size>=sizeMB){
            Log.d("GifDownloader","cache cleared,total size: "+size+"MB");
           return delete(new File(deletePath));
        }
        return false;
    }


}
