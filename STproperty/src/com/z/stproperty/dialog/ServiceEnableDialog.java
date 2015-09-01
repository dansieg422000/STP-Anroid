package com.z.stproperty.dialog;

/*********************************************************************************************************
 * Class	: ServiceEnableDialog
 * Type		: Dialog
 * Date		: 19 Set 2013
 * -------------------------------------------------------------------------------------------------------
 * 
 * Description:
 * 
 * To use this application the user must have the internet connection
 * 
 * If there is no network connection this will show a dialog-box 
 * to update the network status
 * 
 * IF user turned off his GPS service and tried to see the nearby properties 
 * then the GPS setting dialog will appears
 * 
 * If there is any network failure on connected network then also this dialog 
 * will ask user to check his network connections
 * 
 * After successful enable this will reload the corresponding screen
 * 
 * like
 * 
 * 1. district listing
 * 2. property listing
 * 3. property details
 * 4. article listing
 * 5. directory listing
 * 6. directory details
 * etc...
 * 
 * If user is not enabled and come back then 
 * tried again then this will ask again and again to turn on
 * 
 * If user click No on this dialog then the correspoding dialog will gets closed
 * 
 * dialog messages 
 * ---------------
 * 
 * lowinternet
 * 		:: Please check your WiFi/3G network settings and try again.
 * 
 * network
 * 		::  App requires network connection.\nDo you want to enable it?
 * 
 * gpsenable
 * 		::  App requires GPS service.\nDo you want to enable it?
 * 
 * ********************************************************************************************************/

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.z.stproperty.R;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.shared.Constants;

public class ServiceEnableDialog extends Activity{
	private boolean lowinternet = false; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_dialog);
		
		lowinternet = getIntent().getBooleanExtra(Constants.LOWINTERNETSTR, false);
		if(lowinternet){
			((Helvetica)findViewById(R.id.BodyText)).setText("Please check your WiFi/3G network settings and try again.");
			((Button)findViewById(R.id.NoExit)).setText("Cancel");
			((Button)findViewById(R.id.YesBtn)).setText("Try Again");
		}else if(!getIntent().getBooleanExtra("gpsenable", false)){
			((Helvetica)findViewById(R.id.BodyText)).setText("App requires network connection.\nDo you want to enable it?");
		} 
		
		((Button)findViewById(R.id.NoExit)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setresultBack(RESULT_CANCELED);
			}
		});
	 	((Button)findViewById(R.id.YesBtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(lowinternet){
					setresultBack(RESULT_OK);
				}else if(getIntent().getBooleanExtra("gpsenable", false)){
					startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),0);
				}else{
					startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS),0);
				}
			}
		});
	}
	/***
	 *  *
	 * @requestCode
	 *  Identifies for which request the result is returned in activity
	 *  
	 * @responseCode
	 *  Identifies if the response was successful or cancelled
	 *  
	 * @data
	 *  carries any data that is sent as a result
	 *  
	 *  if request code is REQUESTCODE_SETTINGS && response code is checked for 'RESULT_CANCELED'
	 *   activity is finished.
	 *  else the response code is checked for 'RESULT_OK'
	 *   if yes then
	 *     previous activity is called 
	 *   
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		setresultBack(RESULT_OK);
	}
	/***
	 * Creates an intent and sends a result to previous activity
	 * 
	 * @param resultcode
	 *  value result-code is sent to the previous activity
	 *  
	 */
	private void setresultBack(int resultcode){
		try{
			Intent data = new Intent();
			data.putExtra("result", 0);
			setResult(resultcode, data);
			finish();
		}catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
		}
	}
}
