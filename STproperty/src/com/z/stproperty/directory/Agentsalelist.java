package com.z.stproperty.directory;

/**
 * @author EVVOLUTIONS
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
 * The agents List shown as ListView
 * ListView contains 
 * 		1. Photo (Left)
 * 		1.1 Call button (below photo)
 * 		2. Name (Right)
 * 		3. Contact Number (Right)
 * 		4. Company Name (Right)
 * 		5. Designation (Right)
 * On-Click of call button the agent gets a call from user
 * 
 * The top header will have Caption and the sort button
 * 
 * SORTING
 * 
 * On-Click of the sort button GridView will get focus
 * The user can able to sort the agents based of alphabets (a-z)
 * On-Selection from gridview the center View (ListView) is reloaded.
 * 
 * PAGINATION
 * 
 * Pagination count is 25 per page
 * User can navigate from one to next 25 or to previous all values
 * 
 * The next page values are auto loaded into list once the user reaches to last one.
 * 
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.z.stproperty.BaseActivity;
import com.z.stproperty.R;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.SharedFunction;

public class Agentsalelist extends BaseActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		tabtitle = "Directories";
		page = 1;
		setContentLayout();
		Intent extraIntent = getIntent();
		Bundle extras = extraIntent.getExtras();
		url = extras.getString("url") + "&hash=" + SharedFunction.getHashKey() + "&page=" + page;
		
		final Spinner sort = (Spinner) findViewById(R.id.SortBySpin);
		ArrayAdapter<String> sortByAdapter = new ArrayAdapter<String>(this, R.layout.spinner_textivew, Constants.SORTBYARRAY);
		sort.setAdapter(sortByAdapter);
		sort.setOnItemSelectedListener(onItemClick);
		((TextView)findViewById(R.id.SortTextView)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sortClick = true;
				sort.performClick();
			}
		});
		
		checkUpdate();
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
		}
	}
	/**
	 * This is overridden abstract method for FatherActivity
	 * The common method "getServerValues();" is called in this method to reduce the code duplication.
	 */
	public void checkUpdate() {
		getServerValues();
	}
	/**
	 * if the user gives cancel On settings then close the activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_CANCELED && requestCode==Constants.REQUESTCODE_SETTINGS){
			finish();
		}else if(resultCode == RESULT_OK){
			dialog.show();
			checkUpdate();
		}
	}
}
