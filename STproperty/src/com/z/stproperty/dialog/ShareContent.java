package com.z.stproperty.dialog;

/*******************************************************************************************
 * Class	: ShareContent
 * Type		: Activity
 * Date		: 19 02 2014
 * 
 * General Description:
 * 
 * Will displayed as selection dialog with few options as list
 * Based on user selection the result is returned back to calling function
 * 
 * Like 
 * 1. FaceBook
 * 2. Twitter
 * 3. Email
 *******************************************************************************************/

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.z.stproperty.R;

public class ShareContent extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.list_picker);
			((TextView)findViewById(R.id.pickerTitle)).setText("Share");
			ListView pickerList = (ListView)findViewById(R.id.pickerList);
			String[] array = new String[]{"Facebook","Twitter","Email"};
			pickerList.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_textivew, R.id.textView1, array));
			pickerList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Intent data = new Intent();
					data.putExtra("id", arg2);
					setResult(RESULT_OK, data);
					finish();
				}
			});
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
		}
	}
}
