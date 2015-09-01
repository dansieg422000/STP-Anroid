package com.z.stproperty;

/***************************************************************
 * Class name:
 * (PropertyDetail)
 * 
 * Description:
 * (Show each Property Details)
 * 
 * 
 * Input variables:
 * ArrayList<HashMap<String, String>> menuItems(Stored the basic information of Property Detail)
 *
 *  
 * Output variables:
 *  ArrayList<HashMap<String, String>> menuItems1(Basic Information which need to pass to map, Info, and Amenities)
 *  
 *  Tabs 
 *  1. Details
 *  2. Map
 *  3. Info
 *  4. Amenities
 *  
 * StrictMode thread policy for all thread executions 
 * Initial step the favorites list are shown as listview (previous screen)
 * 
 * On selecting of list options the corresponding property details are populated in another activity with two tabs 
 * called INFO and MAP
 * 
 * INFO tab contains all details about the property
 * 
 * MAP will locate the exact location of the property in GEO map
 * User can able to calculate the premium amount for properties (Only for Sale)
 * The calculator options wont be visible for for-rent and room rental
 * 
 * The photo gallery also available
 * Onclick on the image the gallery will open and shows all images as pager.
 * 
 * Email Sharing Format
 * 
 *  Kyn Jyn

	 65
	
	 PROPERTY TITLE:Hotel Shopping Mall (Ex-Bukit
	 Merah Safra) Mall Shop Retail 5200 Jalan
	 Bukit Merah For Sale
	
	 PRICE:
	
	 Click on the following link to view more
	 details about the property
	
	 http://www.stproperty.sg/7-for-sale/23-for-sale/hotel-shopping-mall-%2528ex-bukit-merah-safra%2529-/1238930
 ****************************************************************/

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.evvo.twitter.twittclass.Twitt;
import com.evvolutions.android.http.AsyncHttpResponseHandler;
import com.facebook.android.ShareWithfacebook;
import com.z.stproperty.database.DatabaseHelper;
import com.z.stproperty.dialog.ServiceEnableDialog;
import com.z.stproperty.dialog.ShareContent;
import com.z.stproperty.shared.ClearCache;
import com.z.stproperty.shared.ConnectionCheck;
import com.z.stproperty.shared.ConnectionManager;
import com.z.stproperty.shared.ConnectionManager.ConnectionType;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.PagerSlidingTabStrip;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.UrlUtils;

public class PropertyDetail extends FragmentActivity {
	private String shareImageLink;
	private String response = "", price = "", title = "", prurl="", description="";
	private static String propertyId;
	private static Map<String, String> pripertyDetail = new HashMap<String, String>();
	private ProgressDialog dialog;
	private String gaScreenName = "";
	private int level2Id = 7, position = 0;
	private List<String> customVariables;
	private OnPageChangeListener onPageListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int arg0) {
			performPageSelectAction(arg0);
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			//  Auto-generated method stub
		}
		@Override
		public void onPageScrollStateChanged(int arg0) {
			//  Auto-generated method stub
		}
	};
	private void performPageSelectAction(int arg0){
		position = arg0;
		List<String> cusVariables = null;
		String screenName = "";
		switch (arg0) {
		case 1:
			String lat = pripertyDetail.get("longitude");
			screenName = gaScreenName + "::" + gaScreenName + 
					(lat.equalsIgnoreCase("null") ? "_Ad_Description" : "_Ad_Map");
			break;
		case 2:
			screenName = gaScreenName + "::" + gaScreenName + "_Ad_Description";
			break;
		default:
			screenName = gaScreenName + "::" + gaScreenName + "_Ad_Detail";
			cusVariables = customVariables;
			break;
		}
		SharedFunction.sendGA(PropertyDetail.this, gaScreenName);
        // AT Internet tracking
        SharedFunction.sendATTagging(getApplicationContext(), screenName, level2Id, cusVariables);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pdetail);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		try{
			pripertyDetail = (HashMap<String, String>) getIntent().getExtras().get("propertyDetail");
			title = pripertyDetail.get("title");
			propertyId = pripertyDetail.get("pID");
				SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
				mPrefs.edit().putString("propertyId", propertyId).commit();
			dialog = ProgressDialog.show(PropertyDetail.this,"", "Loading. Please wait...", true);
			
			checkUpdate();
	        ((Button) findViewById(R.id.ShareIcon)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), ShareContent.class);
					startActivityForResult(intent, Constants.REQUEST_SHARE);
				}
			});
	        trimCache();
		}catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * Loads the property details from server with asynchronous
	 * without disturbing the main thread
	 */
	private void checkUpdate() {
		try {
			if(ConnectionCheck.checkOnline(PropertyDetail.this)){
				 ConnectionManager test = new ConnectionManager();
				 if(propertyId == null){
						SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
						propertyId = mPrefs.getString("propertyId", null);
					} else {
						SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
						mPrefs.edit().putString("propertyId", propertyId).commit();
					}
				 test.connectionHandler(PropertyDetail.this, null, UrlUtils.URL_AD_DETAILS + SharedFunction.getHashKey() 
						 + "&property_id=" + propertyId, ConnectionType.CONNECTIONTYPE_GET, null, new AsyncHttpResponseHandler(){
					 
					 @Override
					 public void onSuccess(String responseStr) {
						 response = responseStr;
					 }

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						dialog.dismiss();
						updateSettings(Constants.LOWINTERNETSTR);
					}

					@Override
					public void onFinish() {
						displayDetails();
					}
				});
			 }else{
				 dialog.dismiss();
				 updateSettings(Constants.NETWORKSTR);
			 }
		} catch (Exception e) {
			updateSettings(Constants.LOWINTERNETSTR);
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * Displays the property details 
	 * and the gallery images in small view
	 */
	private void displayDetails(){
		try{
			JSONObject json = new JSONObject(response);
			if (json.getString("status").equals("fail")) {
				String errorMsg = json.getString("error").split("-")[1];
				Toast.makeText(PropertyDetail.this, errorMsg, Toast.LENGTH_SHORT).show();
				finish();
			} else {
				JSONObject detailJson = json.getJSONObject("result");
				prurl = detailJson.getString("url");
				price = detailJson.getString("price").equals("null") || detailJson.getString("price_option").equalsIgnoreCase("price on ask") ? "Price on Ask" :
					"$" + SharedFunction.getPriceWithComma(detailJson.getString("price"));
				shareImageLink = detailJson.getJSONArray("photos").getString(0).replace("_S.", "_L.");
				description = detailJson.getString("additional_info");
				storeHistory(detailJson);
				ViewPager mPager = (ViewPager)findViewById(R.id.PropertyDtailsPager);
				PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
			    MyDetailAdapter  adapter = new MyDetailAdapter(getSupportFragmentManager(), detailJson);
			    mPager.setAdapter(adapter);
			    tabs.setViewPager(mPager);
			    
		        // Screen Tracking Starts
				// AT Internet tracking with Custom variables
				customVariables = new ArrayList<String>();
				customVariables.add(detailJson.getString("property_title"));
				customVariables.add(detailJson.getString("property_type"));
				customVariables.add(detailJson.getString("property_classification"));
				customVariables.add(detailJson.getString("address"));
				customVariables.add(detailJson.getString("property_district"));
				customVariables.add(detailJson.getString("project_name"));
				customVariables.add(detailJson.getJSONObject("seller_info").getString("agent_cea_license_no"));
				customVariables.add(detailJson.getString("id"));
				String propertyFor = detailJson.getString("property_for").equals("Room Rental") ? "Room For Rent" : ("Properties_" + detailJson.getString("property_for"));
				gaScreenName = (propertyFor + "_" + getListingType(detailJson.getString("property_type"))).replace(" ", "_");
				level2Id = gaScreenName.contains("Sale") ? 7 :(gaScreenName.contains("Room_For") ? 9 : 8);
				
				SharedFunction.sendATTagging(getApplicationContext(), gaScreenName + "::" + gaScreenName + "_Ad_Detail", level2Id, customVariables);
		        tabs.setOnPageChangeListener(onPageListener);
				// Screen Tracking ends
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
		dialog.dismiss();
	}
	private String getListingType(String type){
		if(type.contains("Cond")){
			return "Condominium";
		} else if(type.equals("Landed")){
			return "Landed";
		}else if(type.equalsIgnoreCase("hdb") || type.equalsIgnoreCase("hudc") || type.equalsIgnoreCase("hdb/hudc")){
			return "HDB";
		}else{
			return "Business_Space";
		}
	}
	/**
     * 
     * @param type :: Type network failure 	
     * 					1. No internet connection
     * 					2. Low internet connection
     * Will pop-up an dialog box to check their network status.
     */
    protected void updateSettings(String type){
    	Intent intent = new Intent(getApplicationContext(),ServiceEnableDialog.class);
    	intent.putExtra(type, true);
    	startActivityForResult(intent, Constants.REQUESTCODE_SETTINGS);
    }
	/**
	 * @param propertyDetails : Json value that contains the details about the property
	 * 
	 * The required fields to store in history are passed to the database and stored there with current date time stamp 
	 */
	private void storeHistory(JSONObject propertyDetails){
		try{
			DatabaseHelper data = new DatabaseHelper(getApplicationContext());
			Map<String, String> property = new HashMap<String, String>();
			property.put("type", propertyDetails.getString("property_type"));
			property.put("propertyFor", propertyDetails.getString("property_for"));
			property.put("propertyId", propertyDetails.getString("id"));
			property.put("propertyName", propertyDetails.getString("project_name"));
			property.put("propertyTitle", propertyDetails.getString("property_title"));
			property.put("priceOption", propertyDetails.getString("price_option"));
			property.put("price", propertyDetails.getString("price"));
			property.put("latitude", propertyDetails.getString("map_latitude"));
			property.put("longitude", propertyDetails.getString("map_longitude"));
			property.put("psf", propertyDetails.getString("psf"));
			property.put("bedRooms", propertyDetails.getString("bedrooms"));
			property.put("bathRooms", propertyDetails.getString("bathroom"));
			property.put("builtinArea", propertyDetails.getString("builtin_area"));
			property.put("datePosted", propertyDetails.getString("date_posted"));
			property.put("thumbnail", propertyDetails.getJSONArray("photos").getString(0));
			property.put("classification", propertyDetails.getString("property_classification"));
			
			property.put("property_highlights", pripertyDetail.get("property_highlights"));
			data.addFavOrHistory(property, true);
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * User can share this property details with their friends through email as well
	 * and can choose other options like whats-app, drop-box etc as well
	 */
	private void shareWithEmail(){
		try {
			String path = Images.Media.insertImage(getContentResolver(), SharedFunction.loadBitmap(shareImageLink), "title", null);
			Uri screenshotUri = Uri.parse(path);
			String shareContent = title + "\n\n" + prurl + "\n\n" + price + "\n\n";
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("image/png");
			intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
			intent.putExtra(Intent.EXTRA_SUBJECT, "Property to share with you");
			intent.putExtra(Intent.EXTRA_TEXT, "I think you might be interested in a property advertised on STProperty \n\n" + shareContent);
			intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
			SharedFunction.postAnalytics(PropertyDetail.this, "Lead", "Email Share",  title);
			startActivity(Intent.createChooser(intent, ""));
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
     * 
     * @param shareTxt :: share text (property details)
     * 
     * This will ask user authentication and login details to share the property
     */
    public void shareWithTwitter(){
    	Twitt twitt = new Twitt(PropertyDetail.this, Constants.CONSUMERKEY, Constants.CONSUMERSECRET);
    	SharedFunction.postAnalytics(PropertyDetail.this, "Lead", "Twitter Share",  title);
		twitt.shareToTwitter(title + " " + prurl);
    }
    /**
     * 
     * @author 	:: Evvolutions
     * Class 	:: MyDetailAdapter
     * Type		:: FragmentPagerAdapter
     * 
     * Will load the property details 
     * and Map and
     * Additional information in single fragment-pager for user to easily check the details 
     * with swipe option
     *
     */
	public class MyDetailAdapter extends FragmentPagerAdapter {

		private String[] tabTitles = null;
		private JSONObject detailsJson;
		public MyDetailAdapter(FragmentManager fm, JSONObject detailJson) {
			super(fm);
			detailsJson = detailJson;
			try {
				if(detailsJson.getString("map_latitude").equalsIgnoreCase("null")){
					tabTitles = new String[] { "Details", "Description"};
				}else{
					tabTitles = new String[] { "Details", "Map", "Description"};
				}
			} catch (JSONException e) {
				Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tabTitles[position];
		}

		@Override
		public int getCount() {
			return tabTitles.length;
		}

		@Override
		public Fragment getItem(int position) {
			return PropertyDetailFragment.newInstance(position, detailsJson, PropertyDetail.this);
		}

	}
	/***
	 * @Param requestCode : Request code to identify the calling function
	 * @param resultCode
	 *            : Response returned from the called function RESULT_OK or
	 *            RESULT_CANCEL
	 * @param data
	 *            : Is an intent extra values passed from called function
	 * 
	 *            Exit dialog return values handled
	 * 
	 * For sharing and settings this will get called on activity returns the result 
	 * back to this activity on successful completion
	 */
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if(arg1==RESULT_CANCELED &&  arg0 == Constants.REQUESTCODE_SETTINGS) {
			finish();
		} else if(arg1 == RESULT_OK){
			if(arg0 == Constants.REQUESTCODE_SETTINGS){
				dialog.show();
				checkUpdate();
			}else{
				int id = arg2.getIntExtra("id", 0);
				switch (id) {
				case 0:
					new ShareWithfacebook(PropertyDetail.this, title, shareImageLink, description, prurl);
					SharedFunction.postAnalytics(PropertyDetail.this, "Lead", "Facebook Share",  title);
					break;
				case 1:
					shareWithTwitter();
					break;
				case 2:
					shareWithEmail();
					break;
				default:
					break;
				}
			}
		}
	}
	/**
	 * Set AT Internet hitting
	 */
	@Override
	protected void onResume() {
		//  Auto-generated method stub
		super.onResume();
		if(!gaScreenName.equals("")){
			onPageListener.onPageSelected(position);
		}
	}
	/**
	   * In -our application we have images quality with more than 2 mb to load in the application
	   * and the cache size also will increase on page navigation
	   * Means screen navigation 
	   * 
	   * In-order to avoid the force close issue we need to clear the cache memory 
	   * by manually 
	   */
	public void trimCache() {
     try {
        File dir = this.getCacheDir();
        File appDir = new File(dir.getParent());
        if (appDir != null && appDir.isDirectory()) {
           new ClearCache().execute(appDir);
        }
     } catch (Exception e) {
         // handle exception
    	 Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
     }
  }
}
