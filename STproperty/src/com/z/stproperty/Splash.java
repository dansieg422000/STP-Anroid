
package com.z.stproperty;

/***************************************************************
 * Class name:
 * 
 * (Splash)
 * 
 * Description:
 * 
 * (Splash screen)
 * 
 * 
 * Input variables:
 * 
 * null
 * Output variables:
 * 
 *  Called when the activity is first created.
	 * 
	 * Timer task countDown OnClick of this advertisement this will stop the
	 * timer and the web is loaded
	 * 
	 * On successful completion of timer the home page is loaded
	 * 
	 * OnClick of Skip button The home page is loaded
 * 
 ****************************************************************/

import com.google.analytics.tracking.android.EasyTracker;
import com.z.stproperty.shared.SharedFunction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

public class Splash extends Activity {

	private static final int DELAY = 400;

	/**
	 * Start activity to show the home screen
	 * 
	 */
	void showMainMenu() {
		Intent i = new Intent();
		i.setClassName(this, "com.z.stproperty.Advertisement");
		startActivity(i);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		Thread splashThread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (waited < DELAY) {
						sleep(400);
						waited += 100;
					}
				} catch (InterruptedException e) {
					// do nothing
				} finally {
					finish();
					showMainMenu();
				}
			}
		};
		splashThread.start();
		SharedFunction.sendATTagging(getApplicationContext(), "App_Launch", 1, null);
	}
	/**
	 * Override the default back to avoid back option
	 */
	@Override
	public void onBackPressed() {
		// Auto-generated method stub
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