package com.z.stproperty.dialog;

/*********************************************************************************************************
 * Class	: STPAlertDialog
 * Type		: Dialog
 * Date		: 16 DEC 2013
 * 
 * Description:
 * 
 * This is an alert dialog for user 
 * This will display the empty messages of corresponding screen
 * 
 * ********************************************************************************************************/

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.z.stproperty.R;
import com.z.stproperty.fonts.Helvetica;

public class STPAlertDialog extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog);
		((Helvetica)findViewById(R.id.MessageText)).setText(getIntent().getStringExtra("message"));
		((Button)findViewById(R.id.ConfirmDialog)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
