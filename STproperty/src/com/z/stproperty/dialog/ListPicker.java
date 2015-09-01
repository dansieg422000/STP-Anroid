package com.z.stproperty.dialog;

/*******************************************************************************************
 * Class	: ListPicker
 * Type		: Activity
 * Date		: 19 02 2014
 * 
 * General Description:
 * 
 * Will displayed as selection dialog with few options as list
 * Based on user selection the result is returned back to calling function
 * 
 *******************************************************************************************/

import com.z.stproperty.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListPicker  extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.list_picker);
			Bundle extras = getIntent().getExtras();
			((TextView)findViewById(R.id.pickerTitle)).setText(extras.getString("title"));
			ListView pickerList = (ListView)findViewById(R.id.pickerList);
			pickerList.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_textivew, R.id.textView1, getIntent().getStringArrayExtra("array")));
			pickerList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					try{
						Intent data = new Intent();
						data.putExtra("id", arg2);
						setResult(RESULT_OK, data);
						finish();
					}catch(Exception e){
						Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
					}
				}
			});
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
			finish();
		}
	}
}
