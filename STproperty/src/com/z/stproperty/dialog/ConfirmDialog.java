package com.z.stproperty.dialog;
/*******************************************************************************************
 * Class	: ConfirmDialog
 * Type		: Dialog Activity
 * Date		: 19 02 2014
 * 
 * General Description:
 * 
 * Will displayed as confirm dialog with some message to confirm the user
 * Based on user confirmation the result is returned back to calling function
 *******************************************************************************************/
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.z.stproperty.R;
import com.z.stproperty.fonts.Helvetica;

public class ConfirmDialog extends Activity{
	/***
	 * ConfirmDialog	:: this will give option to user to confirm yes or no
	 * This has two buttons 
	 * one for to exit (yes)
	 * one for to stay (no)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.exit_dialog);
			String content = "Are you sure you want to clear all?";
			String activity = getIntent().getStringExtra("acitivty");
			if(activity.equalsIgnoreCase("signout")){
				content = "Do you really want to sign out?";
			}
			((Helvetica)findViewById(R.id.MessageText)).setText(content);
			((Button)findViewById(R.id.ExitDialog)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					retrunResult(Activity.RESULT_OK);
				}
			});
		 	((Button)findViewById(R.id.NoExit)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					retrunResult(Activity.RESULT_CANCELED);
				}
			});
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/***
	 * 
	 * @param result :: boolean values
	 * 				True	:: to exit application
	 * 				False	:: Stay in application (don't exit)
	 */
	private void retrunResult(int result){
		Intent data = new Intent();
		setResult(result, data);
		finish();
	}
}
