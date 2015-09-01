
package com.z.stproperty.search;

/***************************************************************
 * Class name:
 * (ViewSavedsearch)
 * 
 * Description:
 * (Users can check the saved search in this UI)
* 
* The SavedSearch that made from this device is shown
* 	If the same user log-in to some other device or web and made any saved search
* 	That wont get updated here
* 	This works based on local database values
* 
* 
* 
* The Saved Search are shown as list-view with basic information
* 
* OnSelect of any  
* 	The detailed view is shown in next screen
 * 
 * Input variables:
 * SharedPreferences mPrefs(contains the whole information of saved search details)
 * 
 * Output variables:
 * null
 ****************************************************************/

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.google.analytics.tracking.android.EasyTracker;
import com.z.stproperty.R;
import com.z.stproperty.adapter.CustomExpandAdapter;
import com.z.stproperty.bean.ExpandAdapterBean;
import com.z.stproperty.database.DatabaseHelper;
import com.z.stproperty.dialog.ConfirmDialog;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.SharedFunction;

public class ViewSavedsearch extends Activity {
	private List<ExpandAdapterBean> menuItems;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.savedsearch);
			ExpandableListView list = (ExpandableListView) findViewById(R.id.SavedSearchList);
			DatabaseHelper data = new DatabaseHelper(getApplicationContext());
			menuItems = data.getSearchSummary();
			
			if(menuItems.isEmpty()){
				((Helvetica)findViewById(R.id.errorTxt)).setVisibility(View.VISIBLE);
			}
			CustomExpandAdapter searchlistAdapter = new CustomExpandAdapter(this, menuItems, false);
			list.setAdapter(searchlistAdapter);
			((Button) findViewById(R.id.ClearHistory)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!menuItems.isEmpty()){
						Intent intent  =new Intent(getApplicationContext(), ConfirmDialog.class);
						intent.putExtra("acitivty", "SavedSearch");
						startActivityForResult(intent, Constants.REQUESTCODE_CONFIRM);
					}
				}
			});
			// AT Internet tracking
	        SharedFunction.sendATTagging(getApplicationContext(), "History", 10, null);
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			DatabaseHelper dataBase = new DatabaseHelper(getApplicationContext());
			dataBase.deleteSearch();
			startActivity(getIntent());
			finish();
		}
	}
	/**
	 * Used to start the session for this activity 
	 * in Google Analytics Screen capture
	*/
	 @Override
	 public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance(this).activityStart(this);
	 }
	 /**
	 * Used to End the session for this activity 
	 * in Google Analytics Screen capture
	 */
	 @Override
	 public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this);
	 }
}
