package com.z.stproperty.profile;

/***************************************************************
 * Class name:
 * (UserProfile)
 * 
 * Description:
 * User details are shown n dialog box
 * 
 * 
 * Input variables:
 * null
 * 
 ****************************************************************/


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.z.stproperty.R;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.shared.SharedFunction;

public class UserProfile extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.user_profile);
			SharedPreferences profile = PreferenceManager.getDefaultSharedPreferences(UserProfile.this);
			((Helvetica) findViewById(R.id.userName)).setText(profile.getString("userid", ""));
			String[] name = profile.getString("name", "").split("\\s");
			((Helvetica) findViewById(R.id.FirstName)).setText(name[0]);
			((Helvetica) findViewById(R.id.LastName)).setText(name.length>1 ? name[1] : "");
			((Helvetica) findViewById(R.id.ContactNumber)).setText(profile.getString("phone", ""));
			((Helvetica) findViewById(R.id.Email)).setText(profile.getString("st_email", ""));
			SharedFunction.postAnalytics(UserProfile.this, "Account", "Profile", profile.getString("userid", ""));
			// AT Internet tracking
		    SharedFunction.sendATTagging(getApplicationContext(), "Profile", 10, null);
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
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
