package com.z.stproperty.directory;

import com.z.stproperty.R;
import com.z.stproperty.dialog.ListPicker;
import com.z.stproperty.shared.Constants;

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

public class DirectoryFilter  extends Activity{
	private int position = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.list_picker);
			((TextView)findViewById(R.id.pickerTitle)).setText("Filter By");
			ListView pickerList = (ListView)findViewById(R.id.pickerList);
			position = getIntent().getIntExtra("position", 0);
			String[] array = getArray();
			pickerList.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_textivew, R.id.textView1, array));
			pickerList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// Auto-generated method stub
					filterParam(arg2);
				}
			});
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * @param selPosition :: Filter applied position (Selection option)
	 * Based on the directory current positions and the user filer selection the filter 
	 * parameter is returned to the calling activity
	 */
	private void filterParam(int selPosition){
		String[] array = getArray();
		if(array[selPosition].startsWith("Show All")){
			returnResult("All");
		}else if(array[selPosition].startsWith("New")){
			returnResult("&isnew=1");
		}else if(array[selPosition].startsWith("Popular")){
			returnResult("&ispopular=1");
		}else if(array[selPosition].startsWith("Featured")){
			returnResult("&isfeatured=1");
		}else if(array[selPosition].startsWith("By")){
			// by alphabets
			Intent intent = new Intent(getApplicationContext(), ListPicker.class);
			intent.putExtra("title",array[selPosition]);
			intent.putExtra("array",Constants.BY_ALPHABETS);
			startActivityForResult(intent, Constants.REQUEST_FILTER);
		}else{
			// by Districts
			Intent intent = new Intent(getApplicationContext(), ListPicker.class);
			intent.putExtra("title",array[selPosition]);
			intent.putExtra("array",Constants.DISTRICTS);
			startActivityForResult(intent, Constants.REQUEST_DISTRICT);
		}
	}
	/**
	 * @return	:: Based on current directory list position this will 
	 * 				return the array for filters
	 */
	private String[] getArray(){
		String[] array = null;
		 switch (position) {
		 case 0:
			 array = Constants.AGENT_FILTER;
			 break;
		 case 1:
			 array = Constants.CONDO_FILTER;
			 break;
		 case 2:
			 array = Constants.COMMERCIAL_FILTER;
			 break;
		 case 3:
			 array = Constants.INTUSTRIAL_FILTER;
			 break;
		 default:
			 break;
		 }
		 return array;
	}
	/***
	 * 
	 * @param paramer :: String values
	 * 				Filter Parameter
	 */
	private void returnResult(String paramer){
		Intent data = new Intent();
		data.putExtra("filterParam", paramer);
		setResult(RESULT_OK, data);
		finish();
	}
	/***
	 * @Param requestCode : Request code to identify the calling function
	 * @param resultCode
	 *            : Response returned from the called function RESULT_OK or
	 *            RESULT_CANCEL
	 * @param data
	 *            : Is an intent extra values passed from called function
	 * 
	 *            Exit dialog return values handled
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			int id = data.getIntExtra("id", 0);
			switch (requestCode) {
			case Constants.REQUEST_FILTER:
				returnResult("&beginwith=" + Constants.BY_ALPHABETS[id]);
				break;

			case Constants.REQUEST_DISTRICT:
				returnResult("&district="+(id+1));
				break;
			default:
				 break;
			}
		}
	}
}
