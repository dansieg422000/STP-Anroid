package com.z.stproperty;

/***************************************************************
 * Class name:
 * (PropertyList)
 * 
 * Description:
 * (Display Properties By using listView,user can browser properties list in this Interface)
 * 
 * 
 * Input variables:
 * ArrayList<HashMap<String, String>> menuItems(Stored the basic information of each Property)
 *
 *  
 * Output variables:
 *  ArrayList<HashMap<String, String>> menuItems1(Basic Information which need to pass to map, Info, and Amenities)
 *  
 * FatherActivity
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
 *  onRestart() :: 
 *  	On restart of these activities it will check for back options and reload
 *  
 *  * 
 * INFO tab contains all properties list (favorites)
 * 
 * MAP will locate the exact location of the property in GEO map
 * 
 * Pagination count is 25 per page
 * User can navigate from one to next 25 or to previous all values
 * 
 * The next page values are auto loaded into list once the user reaches to last one.

 ****************************************************************/

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.z.stproperty.dialog.ServiceEnableDialog;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.UrlUtils;

public class PropertyList extends BaseActivity {

	public static int wantfor, propertyTypes;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		try {
			// addListAdd true will make the add visible on properties list
			addListAdd = true;
			Intent intent = getIntent();
			Bundle extras = intent.getExtras();
			tabtitle = extras.getString("type");
			page = 1;
			setContentLayout();
			final Spinner sort = (Spinner) findViewById(R.id.SortBySpin);
			((Button)findViewById(R.id.SortTextView)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					sortClick = true;
					sort.performClick();
				}
			});
			ArrayAdapter<String> sortByAdapter = new ArrayAdapter<String>(this, R.layout.spinner_textivew, Constants.SORTBYARRAY);
			sort.setAdapter(sortByAdapter);
			sort.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					if(sortClick){
						page = 1;
						loadMore = false;
						dialog.show();
						menuItems.clear();
						propertyListAdapter.notifyDataSetChanged();
						url = UrlUtils.URL_LISTING + "&type=" + (propertyTypes == 3 ? "3,4,6,7" : propertyTypes)
								+ "&for=" + wantfor + "&hash=" + SharedFunction.getHashKey() + "&page=" + page + "&limit=25&sortby="+SharedFunction.getSortBy(position);
						checkUpdate();
					}
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// Auto-generated method stub
				}
			});
			
			propertyTypes = extras.getInt("type1");
	
			if (intent.hasExtra("wantfor")) {
				wantfor = extras.getInt("wantfor");
			}
			if (intent.hasExtra("url")) {
				url = extras.getString("url");
			} else {
				url = UrlUtils.URL_LISTING + "&type=" + (propertyTypes == 3 ? "3,4,6,7" : propertyTypes)
						+ "&for=" + wantfor + "&hash=" + SharedFunction.getHashKey() + "&page=" + page + "&limit=25";
			}
			checkUpdate();
			postAnalytics();
		} catch (Exception e2) {
			Log.e(this.getClass().getSimpleName(), e2.getLocalizedMessage(), e2);
		}
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
	private void postAnalytics(){
		// Screen Tracking
        gaScreenName = wantfor == 2 ? "Properties_For_Sale" : (wantfor == 1 ? "Properties_For_Rent" : "Room_For_Rent");
        gaScreenName = gaScreenName + "_" + (propertyTypes == 1 ? "Condominium" : (propertyTypes == 2 ? "HDB" : 
        	(propertyTypes == 5 ? "Landed" : "Business_Space")));
        SharedFunction.sendGA(PropertyList.this, gaScreenName + "::" + gaScreenName + "_Listing");
        // AT Internet tracking
        int level2Id = wantfor == 2 ? 7 :(wantfor == 1 ? 8 : 9);
        SharedFunction.sendATTagging(getApplicationContext(), gaScreenName + "::" + gaScreenName + "_Listing", level2Id, null);
		// Screen Tracking ends
	}
	/**
	 * Base class abstract method to load properties
	 */
	public void checkUpdate() {
		getServerValues();
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
	 * if the user gives cancel On settings then close the activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_CANCELED && requestCode == Constants.REQUESTCODE_SETTINGS) {
			finish();
		} else if(resultCode == RESULT_OK){
			dialog.show();
			checkUpdate();
		}
	}
}
