package com.z.stproperty.search;

/***************************************************************
 * Class name:
 * 
 * (Search)
 * 
 * Description:
 * 
 * (Users can check the saved search in this UI)
 * 
 * 
 * Input variables:
 * 
 * SharedPreferences mPrefs(contains the whole information of saved search details)
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
 *  onRestart() :: 
 *  	On restart of these activities it will check for back options and reload
 *  
 *  * 
 * INFO tab contains all properties list (favorites)
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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.z.stproperty.BaseActivity;
import com.z.stproperty.R;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.UrlUtils;

public class SearchPropertyList extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			// addListAdd true will make the add visible on properties list
			addListAdd = true;
			tabtitle = "Search";
			page = 1;
			setContentLayout();
			Intent curIntent = getIntent();
			Bundle curExtra = curIntent.getExtras();
			if (curIntent.hasExtra("url")) {
				url = curExtra.getString("url");
				url = UrlUtils.URL_SEARCH + url + "&limit=25&page=" + page;
			}
			gaSearchScreenName = curExtra.getString("screenName");
			checkUpdate();
			ArrayAdapter<String> sortByAdapter = new ArrayAdapter<String>(this, R.layout.spinner_textivew, Constants.SORTBYARRAY);
			final Spinner sort = (Spinner) findViewById(R.id.SortBySpin);
			sort.setAdapter(sortByAdapter);
			sort.setOnItemSelectedListener(onItemClick);
			((Button)findViewById(R.id.SortTextView)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					sortClick = true;
					sort.performClick();
				}
			});
			postAnalytics();
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	
	private void postAnalytics(){
		// Screen Tracking
		 SharedFunction.sendGA(getApplicationContext(), "Property_Search::Property_Search_Result_Page");
        // AT Internet tracking
        SharedFunction.sendATTagging(getApplicationContext(), "Property_Search::Property_Search_Result_Page", 3, null);
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
	
	public void checkUpdate() {
		getServerValues();
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
}
