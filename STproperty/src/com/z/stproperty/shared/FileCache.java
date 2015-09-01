package com.z.stproperty.shared;

/***************************************************************
* Class name:
* (FileCache)
* 
* Description:
* (store the image file when they are loading)
* 
* 
* Input variables:
* String url(image url which need to be stored)
* 
* Output variables:
* File f(return the image files)
* 
****************************************************************/

import java.io.File;
import android.content.Context;

public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"LazyList");
        }else{
            cacheDir=context.getCacheDir();
        }
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
    }
    
    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        return new File(cacheDir, filename);
        
    }
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null){
            return;
        }
        for(File f:files){
            f.delete();
        }
    }

}