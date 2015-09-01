package com.z.stproperty.directory;

/**
 * StrictMode thread policy for all thread executions 
 * Initial step the favorites list are shown as list-view (previous screen)
 * 
 * On selecting of list options the corresponding property details are populated in another activity with two tabs 
 * called INFO and MAP
 * 
 * INFO tab
 * 		contains all details about the property(Description)
 * 
 * MAP Tab
 * 		will locate the exact location of the property in GEO map
		 * User can able to calculate the premium amount for properties (Only for Sale)
		 * The calculator options wont be visible for for-rent and room rental
 * 
 * Details Tab
 * 		The details like
 * 		1. Thumb-nail image
 * 		2. district
 * 		3. Tenure
 * 		4. type etc ...
 * 
 * Amenities Tab
 * 		Amenities (Additional information) about the property
 * 
	 *  1. NearBy Schools
	 *  	1. School names and distance is shown in ListView
	 *  2. Nearest MRT Stations
	 *  	1. MRT Stations names and distance is shown in ListView
	 *  3. Nearest Shopping Malls
	 *  	1. Shopping Malls names and distance is shown in ListView
 *  
 * On-click on the image the gallery will open and shows all images as pager.
 */

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.evvolutions.android.http.AsyncHttpResponseHandler;
import com.z.stproperty.R;
import com.z.stproperty.shared.ConnectionCheck;
import com.z.stproperty.shared.ConnectionManager;
import com.z.stproperty.shared.ConnectionManager.ConnectionType;
import com.z.stproperty.shared.PagerSlidingTabStrip;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.UrlUtils;

public class DirectoryDetail extends FragmentActivity {
	private String response = "";
	private ProgressDialog dialog;
	private int position = 0;
	private OnPageChangeListener pageListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			//  Auto-generated method stub
			onPageChanged(arg0);
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
	/**
	 * @param arg0 :: Page number to highlight
	 */
	private void onPageChanged(int arg0){
		try{
			position = arg0;
			String screenName = getIntent().getStringExtra("typeName").replace(" ", "_");
			screenName = screenName + "::" + screenName; 
			switch (arg0) {
			case 1:
				screenName = screenName + "_Ad_Map";
				break;
			case 2:
				screenName = screenName + "_Ad_Description";
				break;
			default:
				screenName = screenName + "_Ad_Detail";
				break;
			}
			SharedFunction.sendATTagging(getApplicationContext(), screenName, 6, null);
			SharedFunction.sendGA(DirectoryDetail.this, screenName);
			((TextView) findViewById(R.id.HeaderText)).setText(getIntent().getStringExtra("typeName").replace("Directory", "") + "Ad Details");
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
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
			((Button) findViewById(R.id.ShareIcon)).setVisibility(View.GONE);
			dialog = ProgressDialog.show(DirectoryDetail.this,"", "Loading. Please wait...", true);
			checkUpdate();
		}catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * 
	 * This will get the required contents from the URL with the provided
	 * resources to the JSONObject. First it will perform an Internet connection
	 * check and will prompt the user if there is no Internet connection. If
	 * there is Internet connection, the URL is loaded to ConnectionHandler
	 * which processes the URL and returns the response.If the response status
	 * is not fail, the addFacilityDetail(), addAdditionalInfo() and adMapPins()
	 * are called.
	 * 
	 * 
	 */
	private void checkUpdate() {
		try {
			String directoryId = getIntent().getStringExtra("id");
			String type = getIntent().getStringExtra("type");
			String url = UrlUtils.URL_DIRECOTRYDETAIL+ "&type=" + type
					+ "&hash=" + SharedFunction.getHashKey() + "&id=" + directoryId;
			if(ConnectionCheck.checkOnline(DirectoryDetail.this)){
				 ConnectionManager test = new ConnectionManager();
				 test.connectionHandler(DirectoryDetail.this, null, url, ConnectionType.CONNECTIONTYPE_GET, null, new AsyncHttpResponseHandler(){
					 
					 @Override
					 public void onSuccess(String responseStr) {
						 dialog.dismiss();
						 response = responseStr;
					 }

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						dialog.dismiss();
						Toast.makeText(DirectoryDetail.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFinish() {
						displayDirectory();
					}
				});
			 }else{
				 dialog.dismiss();
				 Toast.makeText(DirectoryDetail.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
			 }
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * Load the directory detail into pager or adapter
	 */
	private void displayDirectory(){
		try{
			JSONObject json = new JSONObject(response);
			if (("fail").equals(json.getString("status"))) {
				Toast.makeText(DirectoryDetail.this, "Please try again", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				JSONObject detailJson = json.getJSONObject("result");
				ViewPager mPager = (ViewPager)findViewById(R.id.PropertyDtailsPager);
				PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
			    MyDetailAdapter  adapter = new MyDetailAdapter(getSupportFragmentManager(), detailJson);
			    mPager.setAdapter(adapter);
			    tabs.setViewPager(mPager);
			    tabs.setOnPageChangeListener(pageListener);
			    pageListener.onPageSelected(0);
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	
	/**
	  * 
	  * Class		: MyPagerAdapter
	  * Type		: FragmentPagerAdapter
	  * Date		: 25 Jan 2013
	  * 
	  * This is the PagerAdapter to load the directory contents. It calls
	  * the new instances of AgentFragment and DirectoryFragments in it's
	  * getItem method.
	  * 
	  * 
	  * @author Evvolutions
	  *
	  */
	public class MyDetailAdapter extends FragmentPagerAdapter {

		private String[] tilesTab = { "Details", "Map", "Description"};
		private JSONObject detailsJson;
		public MyDetailAdapter(FragmentManager fm, JSONObject detailJson) {
			super(fm);
			detailsJson = detailJson;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tilesTab[position];
		}

		@Override
		public int getCount() {
			return tilesTab.length;
		}

		@Override
		public Fragment getItem(int position) {
			return DirectoryDetailFragment.newInstance(position, detailsJson, getIntent().getStringExtra("type"));
		}
	}
	/**
	 * Set AT Internet hitting
	 */
	@Override
	protected void onResume() {
		//  Auto-generated method stub
		super.onResume();
		if(pageListener!=null){
			pageListener.onPageSelected(position);
		}
	}
}