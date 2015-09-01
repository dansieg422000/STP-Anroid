package com.z.stproperty.shared;
 /************************************************************************************************
 * CLASS NAME	: ClearCache
 * TYPE 		: AsyncTask
 * Date 		: 23 - 09 - 2013
 * 
 * Description 
 * 
   * in this application we have images quality with more than 2 MB to load in the application
   * and the cache size also will increase on page navigation
   * Means screen navigation 
   * 
   * In-order to avoid the force close issue we need to clear the cache memory 
   * by manually 
 * 
 ***********************************************************************************************/
import java.io.File;

import android.os.AsyncTask;

public class ClearCache extends AsyncTask<File ,Void, Void>{

	@Override
	protected Void doInBackground(File... params) {
		deleteDir(params[0]);
		return null;
	}
	/**
	 * 
	 * @param dir :: cache directory
	 * @return :: true on success false on failure
	 * 
	 * We will delete all cache files 
	 * except DATABASE
	 * 
	 * DATABASE is also an cache data but if we try to remove this and load the 
	 * application again then this tme we will get only the default data
	 * not the updated data's (update data will be lost)
	 * 
	 * In-order to retain those updates we are not deleting the database caches
	 */
	public boolean deleteDir(File dir) {
      if (dir != null && dir.isDirectory()) {
         String[] children = dir.list();
         for (int i = 0; i < children.length; i++) {
        	 if(!dir.getAbsolutePath().contains("/databases")){
        	    boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	               return false;
	            }
        	 }else{
        		 return true;
        	 }
         }
      }
      // The directory is now empty so delete it
      return dir.delete();
   }
}
