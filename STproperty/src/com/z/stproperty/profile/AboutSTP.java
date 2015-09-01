package com.z.stproperty.profile;

/***************************************************************
 * Class name:
 * (About STP)
 * 
 * Description:
 * (About stp is displayed in webview)
 * 
 * 
 * Input variables:
 * null
 * 
 ****************************************************************/

import com.google.analytics.tracking.android.EasyTracker;
import com.z.stproperty.R;
import com.z.stproperty.shared.SharedFunction;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class AboutSTP extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_stp);
		try{
			((WebView)findViewById(R.id.aboutStp)).loadUrl("file:///android_asset/act0.html");
			// AT Internet tracking
	        SharedFunction.sendATTagging(getApplicationContext(), "About_Us", 10, null);
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
