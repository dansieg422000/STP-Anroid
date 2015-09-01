package com.z.stproperty.profile;

/***************************************************************
 * Class name:
 * (OutBox)
 * 
 * Description:
 * (Display the basic information of users' enquiries)
 * 
 * The enquiry that made from this device is shown
 * 	If the same user log-in to some other device or web and made any queries
 * 	That wont get updated here
 * 	This works based on local database values
 * 
 * The Saved Enquiries are shown as list-view with basic information
 * 
 * OnSelect of any enquiry 
 * 	The detailed view of the enquiry is shown in next screen
 * 
 * 
 * Input variables:
 * null
 * 
 * Output variables:
 * null
 ****************************************************************/

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class OutBox extends Activity {

	private SharedPreferences mPrefs;
	private List<ExpandAdapterBean> menuItems;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.outbox);
			mPrefs = PreferenceManager.getDefaultSharedPreferences(OutBox.this);
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * If user already logged in 
	 * then this will displays the enquiry details to user 
	 * But this is not based on the user selection rather this is based on device database
	 */
	private void displayOutbox(){
		ExpandableListView list = (ExpandableListView) findViewById(R.id.EnquiryList);
		DatabaseHelper data = new DatabaseHelper(getApplicationContext());
		menuItems = data.getEnquirySummary();
		CustomExpandAdapter listAdapter = new CustomExpandAdapter(this, menuItems, true);
		list.setAdapter(listAdapter);
		if(menuItems.isEmpty()){
			((Helvetica)findViewById(R.id.errorTxt)).setText("No Data");
			((Helvetica)findViewById(R.id.errorTxt)).setVisibility(View.VISIBLE);
		}
		((Button) findViewById(R.id.ClearHistory)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!menuItems.isEmpty()){
					Intent intent  =new Intent(getApplicationContext(), ConfirmDialog.class);
					intent.putExtra("acitivty", "outbox");
					startActivityForResult(intent, Constants.REQUESTCODE_CONFIRM);
				}
			}
		});
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
			dataBase.clearEnquiry();
			startActivity(getIntent());
			finish();
		}
	}
	/**
	 * OnResume on this activity 
	 * this will check for user login status 
	 * 	if the user already logged in then the outbox will be displayed to the user
	 * 	otherwise the dialog box will open with option 
	 * 		Login, Register, Cancel to choose
	 */
	protected void onResume() {
		mPrefs = PreferenceManager.getDefaultSharedPreferences(OutBox.this);
		if (!mPrefs.contains("userid")) {
			((ExpandableListView)findViewById(R.id.EnquiryList)).setVisibility(View.GONE);
			((Helvetica)findViewById(R.id.errorTxt)).setVisibility(View.VISIBLE);
			((Button)findViewById(R.id.ClearHistory)).setVisibility(View.GONE);
			Dialog dialog = userDialogBox();
			dialog.show();
		}else{
			((ExpandableListView)findViewById(R.id.EnquiryList)).setVisibility(View.VISIBLE);
			((Button)findViewById(R.id.ClearHistory)).setVisibility(View.VISIBLE);
			((Helvetica)findViewById(R.id.errorTxt)).setVisibility(View.GONE);
			 SharedFunction.postAnalytics(OutBox.this, "Engagement", "Outbox", "Logged In");
			displayOutbox();
			// AT Internet tracking
	        SharedFunction.sendATTagging(getApplicationContext(), "Outbox", 10, null);
		}
		super.onResume();
	}
	/**
	 * In-order to post an enquiry 
	 * the user must logged-in already otherwise
	 * Dialog box will ask user to login 
	 * If the user doesn't have account the they can go with option register
	 * or can cancel the euquiry option
	 * 
	 * @return Dialog box for user with options to 
	 * 
	 * 1. Register 
	 * 2. Login
	 * 3. Cancel dialog 
	 */
	private AlertDialog userDialogBox() {
		AlertDialog myQuittingDialogBox =
		new AlertDialog.Builder(this)
				// set message, title, and icon
				.setTitle("Login Authentication")
				.setMessage("Please log in to continue")
				.setIcon(R.drawable.appicon)
				// set three option buttons
				.setPositiveButton("Sign In",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// whatever should be done when answering "YES"
							// goes here
							Intent i1 = new Intent(getBaseContext(), Login.class);
							i1.putExtra("TYPE", "Outbox");
							i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivityForResult(i1, 0);
						}
					})
				// set Positive Button
				.setNeutralButton("Register",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// whatever should be done when answering "NO"
							// goes here
							Intent i = new Intent(getBaseContext(), Register.class);
							i.putExtra("TYPE", "Outbox");
							i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivityForResult(i, 0);

						}
					})
				// set Negative Button
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// cancel button stuff 
							SharedFunction.postAnalytics(OutBox.this, "Engagement", "Outbox", "Cancel");
						}
					})
				.create();
		return myQuittingDialogBox;
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
