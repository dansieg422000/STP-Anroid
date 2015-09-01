package com.z.stproperty.adapter;

/**
 * @author EVVOLUTIONS
 * 
 * Description:
	* (Adapter for ListView which is used in property news)
	* 
	* Input variables:
	* ArrayList<HashMap<String,String>> a1(which contains the basic information for property news, like title, description,author, and photo)
	* 
	* Output variables:
	* null
	* 
 * Customizes adapter is used in list-view display
 * 
 * The base adapter or default adapter are available in android are looks ugly 
 * In-order to show user friendly view and more informations on the view we need a customized adapter like this
 * 
 *  ImageLoader ::
 *  	Is another class running in background to load the images into views
 *  	1. This will check the url path in cache
 *  		if it is available then the image is loaded from cache
 *  	2. Otherwise it will download the image in background and updates the view
 *  		2.1 After updating, images are cached (feature use)
 *  	3. The default image like loading is displayed in view (before loading image)
 *  Sharing
 *  	1. Twitter sharing
 *  		1.1 login authentication is needed
 *  	2. FaceBook
 *  		2.1 login authentication is needed
 *  	3. Email
 *  		3.1 Share via email
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.z.stproperty.R;
import com.z.stproperty.shared.ImageLoader;

public class ArticleListAdapter extends BaseAdapter {
	private static Activity activity;
    private static LayoutInflater inflater=null;
    private ImageLoader imageLoader;
    private List<HashMap<String, String>> menuItems;
	
	    /**
	     * 
	     * @param baseActivity :: BaseActivity (For application context purpose)
	     * @param arrayList :: The array list (Needs to be displayed in list-view)
	     */
	    public ArticleListAdapter(Activity baseActivity, List<HashMap<String,String>> arrayList) {
		  	activity = baseActivity;
		  	menuItems=arrayList;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        imageLoader=new ImageLoader(activity.getApplicationContext());
	    }
    /**
     * @return :: Count of array or array-list 
     */
    public int getCount() {
    	return menuItems.size();
    }
    /**
     * @return :: Object at position (List-view position)
     */
    public Object getItem(int position) {
        return position;
    }
    /**
     * @return :: Item's position in list-view
     */
    public long getItemId(int position) {
        return position;
    }
    /**
	  * Child View
	  * @param position :: current position
	  * @param convertView :: Current View
	  * @param parent :: Parent View
	  * 
	  * convertView is null on first time 
	  * if convertView is null then we will inflate the view with action inflater
	  * 
	  * convertView Not null 
	  * 	The values are assigned
	  * 	And the convertView is returned back
	  * 
	  * (Hidden)
	  * On-click of share button the dialog will appear to choose an option 
	  * then the activity share options are called to share the property
	  * 
	  *    The view shows like 
	  *    LeftView :: Image and share icon
	  *    Right View :: title, 
	  *    				date, 
	  *    				author, 
	  *    				description etc...
	  *    
	  */
    public View getView(final int position, View conView, ViewGroup parent) {
    	View convertView;
    	if(menuItems.get(position)==null){
    		convertView = inflater.inflate(R.layout.loading, null);
    	}else{
    		convertView = inflater.inflate(R.layout.propertynewsrows, null);
            Map<String, String> item = new HashMap<String, String>();
            item = menuItems.get(position);
         
            ((TextView) convertView.findViewById(R.id.articleDate)).setText(item.get("date"));
            ((TextView) convertView.findViewById(R.id.articlDescription)).setText(item.get("articleblurb"));
            ((TextView) convertView.findViewById(R.id.artileAuthor)).setText(item.get("author"));
            ((TextView) convertView.findViewById(R.id.articleTitle)).setText(item.get("title"));
            ImageView share=(ImageView) convertView.findViewById(R.id.articlePhoto);
            imageLoader.displayImage(item.get("photo"), share);
    	}
        return convertView;
    }

}