/***************************************************************
* Class name:
* (MemoryCache)
* 
* Description:
* (Store the loaded images. Once they are called again, they will be displayed directly(no need loading time))
* 
* 
* Input variables:
* String url(Image url which is going to be loaded)
* 
* Output variables:
* null
****************************************************************/


package com.z.stproperty.shared;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class MemoryCache {
	//Last argument true for LRU ordering
    private Map<String, Bitmap> cache=Collections.synchronizedMap(
            new LinkedHashMap<String, Bitmap>(10,1.5f,true));
    //current allocated size
    private long size=0;
    //max memory in bytes
    private long limit=1000000;

    public MemoryCache(){
        //use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory()/4);
    }
    
    public void setLimit(long newLimit){
        limit=newLimit;
    }

    public Bitmap get(String id){
        try{
            if(!cache.containsKey(id)){
                return null;
            }
            return cache.get(id);
        }catch(Exception e){
        	Log.e("getBitmap", e.getLocalizedMessage(), e);
            return loadBitmap(id);
        }
    }
    public static Bitmap loadBitmap(String url) {
        Bitmap bitmap = null;
        try {
        	  bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
    	} catch (IOException e) {
    		Log.e("loadBitmap", e.getLocalizedMessage(), e);
    	}

        return bitmap;
    }
    public void put(String id, Bitmap bitmap){
        try{
            if(cache.containsKey(id)){
                size-=getSizeInBytes(cache.get(id));
            }
            cache.put(id, bitmap);
            size+=getSizeInBytes(bitmap);
            checkSize();
        }catch(Exception e){
        	Log.e("loadBitmap", e.getLocalizedMessage(), e);
        }
    }
    
    private void checkSize() {
        if(size>limit){
        	//least recently accessed item will be the first one iterated
            Iterator<Entry<String, Bitmap>> iter=cache.entrySet().iterator();  
            while(iter.hasNext()){
                Entry<String, Bitmap> entry=iter.next();
                size-=getSizeInBytes(entry.getValue());
                iter.remove();
                if(size<=limit){
                    break;
                }
            }
        }
    }

    public void clear() {
        cache.clear();
    }

    long getSizeInBytes(Bitmap bitmap) {
        if(bitmap==null){
            return 0;
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}