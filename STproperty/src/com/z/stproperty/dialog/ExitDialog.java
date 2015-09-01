package com.z.stproperty.dialog;
/*******************************************************************************************
 * Class	: ExitDialog
 * Type		: Dialog Activity
 * Date		: 19 02 2014
 * 
 * General Description:
 * 
 * Will displayed as confirm dialog with some message to confirm the user
 * Based on user confirmation the result is returned back to calling function
 * 
 * EditDialog	:: this will give option to user to stay in or exit the application
 * This has two buttons 
 * one for to exit (yes)
 * one for to stay (no)
 * 
 *******************************************************************************************/
import com.z.stproperty.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ExitDialog extends Activity{

	/***
	 * ExitDialog	:: this will give option to user to stay in or exit the application
	 * This has two buttons 
	 * one for to exit (yes)
	 * one for to stay (no)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exit_dialog);
		((Button)findViewById(R.id.ExitDialog)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				retrunResult(true);
			}
		});
	 	((Button)findViewById(R.id.NoExit)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				retrunResult(false);
			}
		});
	}
	/***
	 * 
	 * @param exit :: boolean values
	 * 				True	:: to exit application
	 * 				False	:: Stay in application (don't exit)
	 */
	private void retrunResult(boolean exit){
		Intent data = new Intent();
		data.putExtra("exit", exit);
		setResult(Activity.RESULT_OK, data);
		finish();
	}
}
