package com.z.stproperty.adapter;

/**
 * 
 * @author EVVOLUTIONS
 * 
 * Article ListView - Customizes adapter is used in list-view display 
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
import android.widget.TextView;

import com.z.stproperty.R;

public class ArticleCategoryAdapter extends BaseAdapter {
    private static LayoutInflater inflater=null;
    private List<HashMap<String, String>> menuItems;
    /**
     * 
     * @param a :: BaseActivity (For application context purpose)
     * @param a1 :: The array list (Needs to be displayed in list-view)
     */
    public ArticleCategoryAdapter(Activity activity, List<HashMap<String,String>> artilceList) {
	  	menuItems = artilceList;
	  	inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	  *    The view shows like 
	  *    LeftView :: Image and share icon
	  *    Right View :: category_blurb, 
	  *    				article_category_count, 
	  *    				name etc...
	  *    
	  */
    	public View getView(int position, View view, ViewGroup parent) {
    		View convertView;
    		if(view==null){
    			convertView = inflater.inflate(R.layout.categoryrows, null);
    		}else{
    			convertView = view;
    		}
    		Map<String, String> item = new HashMap<String, String>();
    		item = menuItems.get(position);
    		TextView articleName=(TextView) convertView.findViewById(R.id.articleName); 
    		articleName.setText(item.get("name"));
    		TextView categoryBlurb =(TextView) convertView.findViewById(R.id.articleBlrb); 
    		categoryBlurb.setText(item.get("category_blurb"));
    		TextView articleCount=(TextView) convertView.findViewById(R.id.articleCount);
    		articleCount.setText(item.get("article_category_count"));
    		return convertView;
        }
    }
   
    
    
