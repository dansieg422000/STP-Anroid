package com.z.stproperty.dialog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.z.stproperty.R;
import com.z.stproperty.application.GlobalClass;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.fonts.HelveticaBold;

/*******************************************************************************************
 * Class	: VersionUpdate
 * Type		: Dialog Activity
 * Date		: 07 03 2014
 * 
 * General Description:
 * 
 * Will displayed as confirm dialog with some message to confirm the user
 * Based on user confirmation the result is returned back to calling function
 * Whether to update the application or not
 *******************************************************************************************/

public class VersionUpdate extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.exit_dialog);
			String content = "New version available for download. Please proceed to update the application for better performance.";
			((Helvetica)findViewById(R.id.MessageText)).setText(content);
			((HelveticaBold)findViewById(R.id.haderText)).setText("New Version Alert!");
			Button update = (Button)findViewById(R.id.ExitDialog);
			update.setText("Update");
			update.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					updateVersion(true);
				}
			});
			Button cancel = (Button)findViewById(R.id.NoExit);
			cancel.setText("Cancel");
			cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					updateVersion(false);
				}
			});
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * @param flag	:: Flag true to update the application 
	 * 					False for not to update
	 * If there is no play store installed in the device the catch block will open the link in browser
	 * If available then this will redirects user to play sotre to update the application
	 */
	private void updateVersion(boolean flag){
		try{
			GlobalClass global = (GlobalClass) getApplication();
			global.setVersionUpdate(true);
			if(flag){
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.z.stproperty")));
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.z.stproperty")));
		}
		finish();
	}
}
