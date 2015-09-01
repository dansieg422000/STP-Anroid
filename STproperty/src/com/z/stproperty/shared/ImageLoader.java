package com.z.stproperty.shared;

/***************************************************************
* Class name:
* (ImageLoader)
* 
* Description:
* (Load Images through url, and display it into imageviews)
* 
* 
* Input variables:
* String url(Image url, which need to be displayed)
* ImageView I (ImageView which will display the image)
* 
* Output variables:
* null
* 
****************************************************************/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.z.stproperty.R;

public class ImageLoader {
    
	private MemoryCache memoryCache=new MemoryCache();
	private FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService; 
    
    public ImageLoader(Context context){
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }
    
    private static final int STUBID=R.drawable.black;
    public void displayImage(String url, ImageView imageView) {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
        }else {
            queuePhoto(url, imageView);
            imageView.setImageResource(STUBID);
        }
    }
    
    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }
    
    private Bitmap getBitmap(String url)  {
        File f=fileCache.getFile(url);
        
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null){
            return b;
        }
        //from web
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            UrlUtils.copyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception e){
        	Log.e("getBitmap", e.getLocalizedMessage(), e);
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            
            //Find the correct scale value. It should be the power of 2.
            final int requiredSize=200;
            int widthTmp=o.outWidth, heightTmp=o.outHeight;
            while(true){
                if(widthTmp/2<requiredSize || heightTmp/2<requiredSize){
                    break;
                }
                widthTmp/=2;
                heightTmp/=2;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=1;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (Exception e) {
        	//  Exception not needed
//        	Log.e("File not found", e.getLocalizedMessage(), e);
        }
        return null;
    }
    
    //Task for the queue
    private class PhotoToLoad {
        private String url;
        private ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url=u; 
            imageView=i;
        }
    }
    
    private class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            if(imageViewReused(photoToLoad)){
                return;
            }
            Bitmap bmp=getBitmap(photoToLoad.url);
            try {
				memoryCache.put(photoToLoad.url, bmp);
			} catch (Exception e) {
				Log.e("photosloader", e.getLocalizedMessage(), e);
			}
            if(imageViewReused(photoToLoad)){
                return;
            }
            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url)){
            return true;
        }
        return false;
    }
    
    //Used to display bitmap in the UI thread
    private class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){
        	bitmap=b;
        	photoToLoad=p;
        }
        public void run() {
            if(imageViewReused(photoToLoad)){
                return;
            }
            if(bitmap!=null){
                photoToLoad.imageView.setImageBitmap(bitmap);
            }else{
                photoToLoad.imageView.setImageResource(STUBID);
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}
