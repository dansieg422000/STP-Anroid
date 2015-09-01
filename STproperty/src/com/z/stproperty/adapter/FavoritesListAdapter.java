package com.z.stproperty.adapter;

/***************************************************************
 * Class name:
 * (LazyAdapter6)
 * 
 * Description:
 * (Adapter for ListView which is used in added favourite properties)
 * 
 * 
 * Input variables:
 * ArrayList<HashMap<String,String>> a1(which contains the basic information for property news, like title, description,author, and photo)
 * 
 * Output variables:
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
 *  
 ****************************************************************/

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.z.stproperty.R;
import com.z.stproperty.database.DatabaseHelper;
import com.z.stproperty.favorites.Addedfav;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.fonts.HelveticaBold;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.ImageLoader;
import com.z.stproperty.shared.SharedFunction;

public class FavoritesListAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;
	private ImageLoader imageLoader;
	private List<HashMap<String, String>> menuItems;
	private DatabaseHelper database;
	/**
	 * @param a
	 *            :: BaseActivity (For application context purpose)
	 * @param propertyList
	 *            :: The array list (Needs to be displayed in list-view)
	 */
	public FavoritesListAdapter(Activity activity,
			List<HashMap<String, String>> propertyList) {
		menuItems = propertyList;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
		database = new DatabaseHelper(activity.getApplicationContext());
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
	 * 
	 * @param position
	 *            :: current position
	 * @param convertView
	 *            :: Current View
	 * @param parent
	 *            :: Parent View
	 * 
	 *            convertView is null on first time if convertView is null then
	 *            we will inflate the view with action inflater
	 * 
	 *            convertView Not null The values are assigned And the
	 *            convertView is returned back
	 * 
	 *            On-click of share button the dialog will appear to choose an
	 *            option then the activity share options are called to share the
	 *            property
	 * 
	 *            The view shows like LeftView :: Image and share icon Right
	 *            View :: Name, price, PSF, bed-room count, bath-room count
	 *            etc...
	 * 
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {
		View curView = null;
		try{
			if(convertView==null){
				curView = inflater.inflate(R.layout.addfavrow, null);
			}else{
				curView = convertView;
			}
			
			Map<String, String> property = new HashMap<String, String>();
			property = menuItems.get(position);
			String price = property.get("price");
			String psf = property.get("psf");
			((LinearLayout) curView.findViewById(R.id.PSFLayout)).setVisibility(psf.equals("-") ? View.GONE : View.VISIBLE);
			if (!price.equalsIgnoreCase("price on ask") && !price.equalsIgnoreCase("")) {
				price = "SGD " + SharedFunction.getPriceWithComma(price);
			}else{
				((LinearLayout) curView.findViewById(R.id.PSFLayout)).setVisibility(View.GONE);
			}
			String priceOption = property.get("price_option");
			((Helvetica)curView.findViewById(R.id.PriceOption)).setText("( "+priceOption+" )");
			if(price.contains("SGD") && priceOption.equalsIgnoreCase("Price on Ask")){
				((Helvetica)curView.findViewById(R.id.PriceOption)).setText("");
			}
			((Helvetica)curView.findViewById(R.id.PriceOption)).setVisibility(View.VISIBLE);
			final String prpertyId = property.get("pID");
			
			((Helvetica)curView.findViewById(R.id.PriceOfPsf)).setText(": "+psf);
			((Helvetica)curView.findViewById(R.id.DatePosted)).setText(": "+property.get("dateposted"));
			((HelveticaBold)curView.findViewById(R.id.BedRoom)).setText(property.get("bedroom"));
			((Helvetica)curView.findViewById(R.id.PropertyType)).setText(": "+property.get("type"));
			((Helvetica)curView.findViewById(R.id.FloorArea)).setText(": "+property.get("floorarea") + " sqft");
			((HelveticaBold)curView.findViewById(R.id.PriceValue)).setText(price);
			curView.findViewById(R.id.PriceValue).setVisibility(price.equals("") ? View.GONE : View.VISIBLE);
			((HelveticaBold)curView.findViewById(R.id.Shower)).setText(property.get("shower"));
			((Helvetica)curView.findViewById(R.id.Classification)).setText(": "+property.get("classification"));
			((HelveticaBold)curView.findViewById(R.id.propertyTitle)).setText(property.get("title"));
			LinearLayout bedBathLayout = (LinearLayout)curView.findViewById(R.id.bedBathLayout);
			bedBathLayout.setVisibility(Arrays.asList(Constants.RESIDENTIAL_TYPE).contains(property.get("property_type")) ? View.VISIBLE : View.INVISIBLE);
			ImageView delete = (ImageView) curView.findViewById(R.id.DeeteFavIcon);
			delete.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					database.deleteFavorite(prpertyId);
					menuItems.remove(position);
					notifyDataSetChanged();
					Addedfav.resetFavCount(menuItems.size());
				}
			});
			ImageView thumb = (ImageView) curView.findViewById(R.id.ThumbImage);
			imageLoader.displayImage(property.get("photo"), thumb);
			((ImageView)curView.findViewById(R.id.PriorityImage)).setImageResource(
					SharedFunction.getPriorityImage(property.get("property_highlights")));
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
		}
		return curView;
	}
}