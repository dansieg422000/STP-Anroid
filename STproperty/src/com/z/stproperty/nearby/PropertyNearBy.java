package com.z.stproperty.nearby;

/***************************************************************
 * Class name:
 * 
 * (PropertyNearBy)
 * 
 * Description:
 * 
 * (Show The Properties near by user's current location)
 * 
 * 
 * Input variables:
 * 
 * ArrayList<HashMap<String, String>> menuItems(Need to get latitude and longitude from it)
 *
 *  
 * Output variables:
 * 
 *  null
 *  
 * INFO tab contains all properties list (NearBy Properties)
 * 
 * MAP will locate the exact location of the property in GEO map
 * 
 * LocationChangeListener 
 * 	
 * 	On change of location this listener gets activated and 
 * 	Updates the latitude and longitude values
 * 	Up-to this the spinner will be on screen
 * Variables :: total : property count
 * 
 * 				mPrefs : contains all the values (favorites list)
 * 
 * 				title, classification,type,price and pID contains the property basic values
 * 
 * 				Latitude and longitude will have the location details of the property 
 ****************************************************************/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.evvolutions.android.http.AsyncHttpResponseHandler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.z.stproperty.PropertyDetail;
import com.z.stproperty.PropertyListOnMap;
import com.z.stproperty.R;
import com.z.stproperty.adapter.PropertyListAdapter;
import com.z.stproperty.dialog.ServiceEnableDialog;
import com.z.stproperty.fonts.HelveticaBold;
import com.z.stproperty.shared.ConnectionCheck;
import com.z.stproperty.shared.ConnectionManager;
import com.z.stproperty.shared.ConnectionManager.ConnectionType;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.UrlUtils;

public class PropertyNearBy extends FragmentActivity implements OnMyLocationChangeListener {
	
	private List<HashMap<String, String>> menuItems;
	private ListView list;
	private PropertyListAdapter nearbyAdapter;
	private HelveticaBold pager;
	private String sorturl = "";
	private String tabtitle = "Properties Nearby";
	private int page = 1, type1;
	private String response = "", curLatitude, curLongitude;
	private int total = 0;
	private Spinner sort;
	private boolean locationFlag = false, loadMore = false, sortClick = false;
	private ProgressDialog dialog;
	private GoogleMap googleMap;

	/**
	 * OnScrolled :: is an interface 
	 * This will monitor the ListView items focus
	 * Once the last view gets focus then this will check for load-more flag
	 * If this flag is set then this will load next set of properties into the current list-view
	 * and the map pins are redrawn 
	 */
	private OnScrollListener onScrolled = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// Auto-generated method stub
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if((firstVisibleItem + visibleItemCount) == totalItemCount && loadMore && !menuItems.isEmpty()){
				dialog.show();
				loadMore = false;
				page = page + 1;
				checkUpdate();
			}
		}
	};
	
	/**
	 * On Create of this screen this will update the listener to track user current location
	 * once it detects then this will get removed from map.
	 * 
	 * in-order to get this location we are having dummy fragment on layout design with
	 * width and height as 0
	 * 
	 * clears all the top saved instances and launches the new activity
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.propertynb);
			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}
			if (!((LocationManager)getSystemService(LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				Toast.makeText(this, "please check your gps settings.", Toast.LENGTH_LONG).show();
				finish();
			}else if(!ConnectionCheck.checkOnline(getApplicationContext())){
				Toast.makeText(this, "please check your internet settings.", Toast.LENGTH_LONG).show();
				finish();
			}
			// To get user current location
			SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);
			googleMap = supportMapFragment.getMap();
			googleMap.setMyLocationEnabled(true);
	        googleMap.setOnMyLocationChangeListener(this);
	        
			sort = (Spinner) findViewById(R.id.SortBySpin);
			ArrayAdapter<String> sortBy = new ArrayAdapter<String>(this, R.layout.spinner_textivew, Constants.SORTBYARRAY);
			sort.setAdapter(sortBy);
			
			Intent i = getIntent();
			Bundle extras1 = i.getExtras();
			pager = (HelveticaBold) findViewById(R.id.PropertyCount);
			page = 1;
			if (i.hasExtra("page")) {
				page = extras1.getInt("page");
			}
			Button mapBtn = (Button) findViewById(R.id.propertiesOnMap);
			menuItems = new ArrayList<HashMap<String, String>>();
			list = (ListView) findViewById(R.id.propertyListView);
			
			mapBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (total != 0) {
						Intent i = new Intent(getBaseContext(), PropertyListOnMap.class);
						i.putExtra("menuItems", (ArrayList<HashMap<String, String>>)menuItems);
						i.putExtra("tabtitle", tabtitle);
						i.putExtra("count", pager.getText().toString());
						i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						startActivityForResult(i, 0);
					}
				}
			});
			sort.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					if(sortClick){
						page = 1;
						loadMore = false;
						menuItems.clear();
						nearbyAdapter.notifyDataSetChanged();
						sorturl = "&sortby="+SharedFunction.getSortBy(position);
						checkUpdate();
					}
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// Auto-generated method stub
				}
			});
			// sort text-view
			((Button)findViewById(R.id.SortTextView)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					sortClick = true;
					sort.performClick();
				}
			});
			dialog = ProgressDialog.show(PropertyNearBy.this, "", "Loading. Please wait...", true);
			
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					Intent i = new Intent(getBaseContext(), PropertyDetail.class);
					i.putExtra("propertyDetail", menuItems.get(position));
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(i, 0);
				}
			});
			list.setOnScrollListener(onScrolled);
			postAnalytics();
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	private void postAnalytics(){
		// Screen Tracking
        String gaScreenName = getIntent().getStringExtra("analytics");
//        SharedFunction.sendGA(getApplicationContext(), gaScreenName);
        // AT Internet tracking
        SharedFunction.sendATTagging(getApplicationContext(), gaScreenName, 5, null);
		// Screen Tracking ends
	}
	/**
	 * Loads the properties near by user current location with asynchronous
	 * Not to disturb the main thread
	 */
	private void checkUpdate() {
		try {
			dialog.show();
			if(ConnectionCheck.checkOnline(PropertyNearBy.this)){
				 ConnectionManager test = new ConnectionManager();
				 String url = UrlUtils.URL_NEARBY + "&hash=" + SharedFunction.getHashKey() + "&latitude="
							+ curLatitude + "&longitude=" + curLongitude + "&limit=25&page=" + page
							+ getIntent().getStringExtra("listType") + getIntent().getStringExtra("filterParam") + sorturl;
				 Log.d("url", url);
				 
				 test.connectionHandler(PropertyNearBy.this, null, url, ConnectionType.CONNECTIONTYPE_GET, null, new AsyncHttpResponseHandler(){
					 
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
						loadNearByListValues();
						dialog.dismiss();
					}
				});
			 }else{
				 dialog.dismiss();
				 updateSettings(Constants.NETWORKSTR);
			 }
		} catch (Exception e) {
			dialog.dismiss();
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	private void loadNearByListValues(){
		try{
			JSONObject json = new JSONObject(response);
			if (!json.getString("status").equals("fail")) {
				JSONObject fbfeed = json.getJSONObject("result");
				total = fbfeed.getInt("total_properties");
				for (int i = 0; fbfeed.has("" + i); i++) {
					menuItems.add((HashMap<String, String>)SharedFunction.getProperty(fbfeed.getJSONObject(i+""), type1));
				}
				loadMore = page * 25 < total;
				if(nearbyAdapter==null){
					nearbyAdapter = new PropertyListAdapter(PropertyNearBy.this, menuItems, 0);
					list.setAdapter(nearbyAdapter);
				}else{
					nearbyAdapter.notifyDataSetChanged();
				}
				String pagerStr = (page * 25 <= total ? page * 25 : total) + " of " + total + " properties";
				pager.setText(pagerStr);
			}else{
				updateSettings(Constants.LOWINTERNETSTR);
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * Listener :: OnMyLocationChangeListener
	 * 
	 * onMyLocationChange() is an over-ridden method 
	 * 	gets called automatically when the location is gets changed
	 *  
	 *  curLatitude = "1.324076";
		curLongitude = "103.956205";
	 * 
	 * GPS will update this function periodically 
	 */
	@Override
	public void onMyLocationChange(Location arg0) {
		if(!locationFlag){
			locationFlag = true;
			dialog.dismiss();
			curLatitude = Double.toString(arg0.getLatitude());
			curLongitude = Double.toString(arg0.getLongitude());
			checkUpdate();
			googleMap.setMyLocationEnabled(false);
			googleMap.setOnMyLocationChangeListener(null);
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(nearbyAdapter!=null){
			nearbyAdapter.notifyDataSetChanged();
			postAnalytics();
		}
	}
	/**
	 * if the user gives cancel On settings then close the activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_CANCELED && requestCode==Constants.REQUESTCODE_SETTINGS) {
			finish();
		} else if(resultCode == RESULT_OK){
			dialog.show();
			checkUpdate();
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
}
