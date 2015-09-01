package com.z.stproperty.history;

/***************************************************************
 * Class name:
 * 
 * (Search)
 * 
 * Description:
 * 
 * (Will loads uses recently viewed properties)
 * 
 * 
 * Input variables:
 * 
 * null
 * 
 * Output variables:
 * 
 * null
 * 
 * FatherActivity
 * 
 * 	FatherActivity has few common functions like 
 * 
 * 	setContentLayout() :: 
 * 		will set the layout and initializes few variables as well
 *  onScrolled()
 *  	Scroll listener to auto-load the next set of values into existing list
 *  onItemClick
 *  	Common click listener for views
 *  loadListValuesToArray()
 *  	Loads the json values into array (HashMap)
 *  getServerValues()
 *  	Tries to get values from server with given url
 *  
 *  
 *  * 
 * INFO tab contains all properties list (History)
 * 
 * MAP will locate the exact location of the property in GEO map
 * 
 * Variables :: total : property count
 * 				mPrefs : contains all the values (favorites list)
 * 				title, classification,type,price and pID contains the property basic values
 * 				Latitude and longitude will have the location details of the property  
 * 
 * Pagination count is 25 per page
 * User can navigate from one to next 25 or to previous all values
 * 
 * The next page values are auto loaded into list once the user reaches to last one.
 ****************************************************************/

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.z.stproperty.BaseActivity;
import com.z.stproperty.R;
import com.z.stproperty.adapter.PropertyListAdapter;
import com.z.stproperty.database.DatabaseHelper;
import com.z.stproperty.dialog.ConfirmDialog;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.SharedFunction;

public class RecentlyViewed extends BaseActivity {

	/**
	 * Variables :: total : property count mPrefs : contains all the values
	 * (favorites list) title, classification,type,price and pID contains the
	 * property basic values Latitude and longitude will have the location
	 * details of the property
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		try {
			tabtitle = "Recently Viewed";
			setContentLayout();
			Button clearBtn = (Button) findViewById(R.id.ClearHistory);
			clearBtn.setVisibility(View.VISIBLE);
			clearBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!menuItems.isEmpty()){
						Intent intent  =new Intent(getApplicationContext(), ConfirmDialog.class);
						intent.putExtra("acitivty", "recently viewed");
						startActivityForResult(intent, Constants.REQUESTCODE_CONFIRM);
					}
				}
			});
			checkUpdate();
			postAnalytics();
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	private void postAnalytics(){
		// Screen Tracking
        SharedFunction.sendGA(getApplicationContext(), "Recently Viewed");
        // AT Internet tracking
        SharedFunction.sendATTagging(getApplicationContext(), "Recently Viewed", 10, null);
		// Screen Tracking ends
	}
	/**
	 * Specifically we called this for favorites 
	 * If user favorites or un-favorite the property in detail page and comes back to list screen
	 *  then that has to be reflected in the list-view as well
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if(propertyListAdapter!=null){
			propertyListAdapter.notifyDataSetChanged();
			postAnalytics();
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
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			DatabaseHelper data = new DatabaseHelper(getApplicationContext());
			data.clearHistory();
			Toast.makeText(getApplicationContext(), "History cleared successfully.", Toast.LENGTH_LONG).show();
			menuItems.clear();
			propertyListAdapter.notifyDataSetChanged();
			findViewById(R.id.noFavorites).setVisibility(View.VISIBLE);
		}
	}
	/**
	 * this is a thread that will load all the data from database and 
	 * 	add into map and then converted into view
	 * the thread handler will be called once this done it's job
	 */
	public void checkUpdate() {
		try {
			dialog.dismiss();
			DatabaseHelper database = new DatabaseHelper(getApplicationContext());
			List<HashMap<String, String>> propertyArray = database.getFavOrHistory(true);
			if (!propertyArray.isEmpty()) {
				total = propertyArray.size();
				for (int i = 0; i < total; i++) {
					menuItems.add((HashMap<String, String>)SharedFunction.getProperty(new JSONObject(propertyArray.get(i)), 0));
				}
			}else{
				findViewById(R.id.noFavorites).setVisibility(View.VISIBLE);
				propertyList.setVisibility(View.GONE);
			}
			propertyListAdapter = new PropertyListAdapter(RecentlyViewed.this, menuItems, 0);
			propertyList.setAdapter(propertyListAdapter);
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
}
