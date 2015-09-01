package com.z.stproperty.adapter;

/***************************************************************
 * Class name:
 * (LazyAdapter3)
 * 
 * Description:
 * (Adapter for ListView which is used in property Listing)
 * 
 * 
 * Input variables:
 * ArrayList<HashMap<String,String>> a1(which contains the basic information for property list, like title, description,classification, and photo)
 * 
 * Output variables:
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
 ****************************************************************/

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.z.stproperty.R;
import com.z.stproperty.database.DatabaseHelper;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.fonts.HelveticaBold;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.ImageLoader;
import com.z.stproperty.shared.SharedFunction;

public class PropertyListAdapter extends BaseAdapter {
	private static Activity activity;
	private static LayoutInflater inflater = null;
	private ImageLoader imageLoader;
	private List<HashMap<String, String>> menuItems;
	private DatabaseHelper database;
	private String propertyAdd = "file:///android_asset/openXCondoIphone.html";
	/**
	 * 
	 * @param baseActivity
	 *            :: BaseActivity (For application context purpose)
	 * @param propertyList
	 *            :: The array list (Needs to be displayed in list-view)
	 */
	public PropertyListAdapter(Activity baseActivity, List<HashMap<String, String>> propertyList, int propertyType) {
		activity = baseActivity;
		menuItems = propertyList;
		propertyAdd = assignAdd(propertyType);
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
		database = new DatabaseHelper(activity.getApplicationContext());
	}
	private String assignAdd(int type){
		switch (type) {
		case 1:
			return "file:///android_asset/openXCondoIphone.html";
		case 2:
			return "file:///android_asset/openXHDBIphone.html";
		case 5:
			return "file:///android_asset/openXLandedIphone.html";
		default:
			return "file:///android_asset/openXCommercialIphone.html";
		}
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
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi"})
	public View getView(final int position, View convertView, ViewGroup parent) {
		View curView = null;
		try{
			if(convertView == null){
				curView = inflater.inflate(R.layout.propertylistrows, null);
			}else{
				curView = convertView;
			}
			if(menuItems.get(position) == null){
				curView.findViewById(R.id.AddDetailLayout).setVisibility(View.GONE);
				curView.findViewById(R.id.WedAddLayout).setVisibility(View.VISIBLE);
				WebView webview= (WebView) curView.findViewById(R.id.addWeb);
	  		    webview.getSettings().setLoadWithOverviewMode(true);
	  		    webview.getSettings().setJavaScriptEnabled(true);
	  	        webview.getSettings().setUseWideViewPort(true);
				webview.loadUrl(propertyAdd);
				webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
				webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				webview.setFocusable(false);
				webview.setClickable(false);
			}else{
				curView.findViewById(R.id.AddDetailLayout).setVisibility(View.VISIBLE);
				curView.findViewById(R.id.WedAddLayout).setVisibility(View.GONE);
				Map<String, String> property = new HashMap<String, String>();
				property = menuItems.get(position);
				final String propertyId = property.get("pID");
				
				((LinearLayout) curView.findViewById(R.id.PSFLayout)).setVisibility(property.get("psf").equals("-") ? View.GONE : View.VISIBLE);
				String price = property.get("price");
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
				String psfString = property.get("psf");
				if(property.get("psf").contains("SGD")){
					psfString = "SGD " + String.format("%.2f", Float.parseFloat(psfString.replace("SGD", "").trim()));
				}
				((Helvetica)curView.findViewById(R.id.PriceOfPsf)).setText(": "+psfString);
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
				ImageView fav = (ImageView) curView.findViewById(R.id.FavIcon);
				if(database.isFavorite(propertyId)){
					fav.setImageDrawable(activity.getResources().getDrawable(R.drawable.fav_s));
				}else{
					fav.setImageDrawable(activity.getResources().getDrawable(R.drawable.fav));
				}
				fav.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(database.isFavorite(propertyId)){
							database.deleteFavorite(propertyId);
							((ImageView) v).setImageDrawable(activity.getResources().getDrawable(R.drawable.fav));
						}else{
							((ImageView) v).setImageDrawable(activity.getResources().getDrawable(R.drawable.fav_s));
							addIntoFavorite(position);
						}
					}
				});
				ImageView thumb = (ImageView) curView.findViewById(R.id.ThumbImage);
				imageLoader.displayImage(property.get("photo"), thumb);
				((ImageView)curView.findViewById(R.id.PriorityImage)).setImageResource(
						SharedFunction.getPriorityImage(property.get("property_highlights")));
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
		return curView;
	}
	/**
	 * @param propertyDetails : Json value that contains the details about the property
	 * 
	 * The required fields to store in history are passed to the database and stored there with current date time stamp 
	 */
	private void addIntoFavorite(int position){
		try{
			Map<String, String> propertyDetails = menuItems.get(position);
			Map<String, String> property = new HashMap<String, String>();
			property.put("type", propertyDetails.get("property_type"));
			property.put("propertyFor", propertyDetails.get("type"));
			property.put("propertyId", propertyDetails.get("pID"));
			property.put("propertyTitle", propertyDetails.get("title"));
			property.put("price", propertyDetails.get("price"));
			property.put("latitude", propertyDetails.get("latitude"));
			property.put("longitude", propertyDetails.get("longitude"));
			property.put("psf", propertyDetails.get("psf"));
			property.put("bedRooms", propertyDetails.get("bedroom"));
			property.put("bathRooms", propertyDetails.get("shower"));
			property.put("builtinArea", propertyDetails.get("floorarea"));
			property.put("property_highlights", propertyDetails.get("property_highlights"));
			property.put("datePosted", propertyDetails.get("dateposted"));
			property.put("thumbnail", propertyDetails.get("photo"));
			property.put("classification", propertyDetails.get("classification"));
			property.put("propertyName", "");
			property.put("priceOption", propertyDetails.get("price_option"));
			database.addFavOrHistory(property, false);
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
}