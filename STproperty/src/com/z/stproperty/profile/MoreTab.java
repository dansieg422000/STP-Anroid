package com.z.stproperty.profile;

/***************************************************************
 * Class name:
 * (tab5)
 * 
 * Description:
 * (Users can check more information in this UI)
 * 
 * 
 * Input variables:
 * null
 * Output variables:
 * null
 * 
 *  * *  FatherActivity
 * 
 * 	FatherActivity has few common functions like 
 * 
 * 	isApplicationBroughtToBackground() :: 
 * 
 * 		this will check for whether app in running as background process or not
 * 
 *  onRestart() :: 
 *  
 *  	On restart of these activities it will check for back options and reload
 *  
 *  ShortCuts
 *  
 *   1. Search
 *   2. Different property types like condo, commercial etc...
 *   3. To list directories
 *   4. Article listing (Latest News)
 *   5. More Tab 
 *   	5.1 enquiry
 *   	5.2 saved search list
 *   	5.3 outbox
 *   	5.4 contact-us (feedback)
 *   6. Favorites Tab
 *   7. Login
 *   8. Register
 *   
 *   User can choose their options from this screen to view their required properties
 * 
 ****************************************************************/

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.z.stproperty.MainActivity;
import com.z.stproperty.R;
import com.z.stproperty.article.Category;
import com.z.stproperty.dialog.ConfirmDialog;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.history.RecentlyViewed;
import com.z.stproperty.search.ViewSavedsearch;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.SharedFunction;

public class MoreTab extends Activity {
	private TextView titleText;
	private SharedPreferences mPrefs;
	/**
	 * onClick	:: OnClickListener
	 * 
	 * Is common on-click listener for all the buttons and images in home screen
	 * This is grouped into single listener to make easier to alter the code and reduce the 
	 * line of code.
	 * 
	 * This will check the View ID to match with the predefined View-ID to identify
	 * which view is clicked
	 * 
	 * Login button only visible on unsuccessful login
	 * If user already logged-in then login button visibility set to false 
	 */
	private OnClickListener onClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// Auto-generated method stub
			performClickAction(v);
		}
	};
	private void performClickAction(View v){
		Intent i = new Intent(getBaseContext(), Login.class);
		boolean startActivity  = true;
		switch (v.getId()) {
		case R.id.SignInLayout:
			if (!mPrefs.contains("userid")) {
				i = new Intent(getBaseContext(), Login.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			} else {
				startActivity = false;
				i  =new Intent(getApplicationContext(), ConfirmDialog.class);
				i.putExtra("acitivty", "signout");
				startActivityForResult(i, Constants.REQUESTCODE_CONFIRM);
			}
			break;
		case R.id.RegisterLayout:
			if (mPrefs.contains("userid")) {
				i = new Intent(getBaseContext(), UserProfile.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}else{
				i = new Intent(getBaseContext(), Register.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}
			break;
		case R.id.ArticleLayout:
			i = new Intent(getBaseContext(), Category.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			break;
		case R.id.OutBoxLayout:
			i = new Intent(getBaseContext(), OutBox.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			break;
		case R.id.SavedSearchLayout:
			i = new Intent(getBaseContext(), ViewSavedsearch.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			break;
		case R.id.HistoryLayout:
			i = new Intent(getBaseContext(), RecentlyViewed.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			break;
		default:
			break;
		}
		if(startActivity){
			startActivity(i);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.more_tapcontent);
			mPrefs = PreferenceManager.getDefaultSharedPreferences(MoreTab.this);
			titleText = (TextView) findViewById(R.id.textView1);
			/**
			 * FeecBack option
			 */
			((LinearLayout) findViewById(R.id.ContactLayout)).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(getBaseContext(), ContactUs.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(i, 0);
				}
			});
	        //Screen tracking ends
			((LinearLayout) findViewById(R.id.SignInLayout)).setOnClickListener(onClick);
			/**
			 * Registration option
			 */
			((LinearLayout) findViewById(R.id.RegisterLayout)).setOnClickListener(onClick);
			/**
			 * Latest News or
			 * Article listing
			 */
			((LinearLayout) findViewById(R.id.ArticleLayout)).setOnClickListener(onClick);
			/**
			 * OutBox listing option
			 */
			((LinearLayout) findViewById(R.id.OutBoxLayout)).setOnClickListener(onClick);
			/**
			 * To List Saved Search
			 */
			((LinearLayout) findViewById(R.id.SavedSearchLayout)).setOnClickListener(onClick);
			
			// History
			((LinearLayout) findViewById(R.id.HistoryLayout)).setOnClickListener(onClick);
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * There is an option to user can log-in from home screen and come back
	 * When ever this screen resume focus this will check user login details 
	 * from shared preferences 
	 * If user logged-in then the sign-out option is enabed and the remaing option 
	 * are disabled
	 */
	@Override
	protected void onResume() {
		// Auto-generated method stub
		super.onResume();
		// GA net-rating
		SharedFunction.sendGA(MoreTab.this, "More");
        // AT Internet tracking
        SharedFunction.sendATTagging(getApplicationContext(), "More", 10, null);
		setLogin();
	}
	/**
	 * User login status checking
	 * If logged-in then login buttons visibility set to false
	 */
	private void setLogin(){
		if (mPrefs.contains("userid")) {
			((Helvetica)findViewById(R.id.RegisterProfile)).setText("Profile");
			titleText.setText("Sign Out");
		} else if (!mPrefs.contains("userid")) {
			titleText.setText("Sign In");
			((Helvetica)findViewById(R.id.RegisterProfile)).setText("Register");
		}
	}
	/**
	 * On back press the app will launch the home screen as fresh activity
	 * 
	 * clears all the top saved instances
	 * and launches the new activity
	 * 
	 */
	@Override
	 public void onBackPressed() {
	 	 try{
	 		Intent intent = new Intent(getApplicationContext(),MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
	 	 }catch (Exception e) {
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
			mPrefs.edit().remove("userid").commit();
			setLogin();
		}
	}
}