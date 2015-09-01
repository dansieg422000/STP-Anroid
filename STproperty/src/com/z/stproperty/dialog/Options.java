package com.z.stproperty.dialog;

/*******************************************************************************************
 * Class	: Options
 * Type		: Activity
 * Date		: 19 02 2014
 * 
 * General Description:
 * 
 * Will displayed as selection dialog with few options as list
 * Based on user selection the result is returned back to calling function
 * 
 *******************************************************************************************/

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.z.stproperty.R;

public class Options extends Activity{

	private boolean withPhoto = false, withVideo = false, newLaunch = false;
	/**
	 * onClick	:: OnClickListener
	 * 
	 * Is common on-click listener for all the buttons and images in home screen
	 * This is grouped into single listener to make easier to alter the code and reduce the 
	 * line of code.
	 * 
	 * This will check the View ID to match with the predefined View-ID to identify
	 * which view is clicked 
	 * 
	 * And highlights the selected on 
	 */
	private OnClickListener onClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			performClickAction(v);
		}
	};
	private void performClickAction(View v){
		switch (v.getId()) {
		case R.id.WithPhotosLayout:
			withPhoto = !withPhoto;
			makeSekection((ImageView)findViewById(R.id.withPhoto), withPhoto);
			break;
		case R.id.WithVideosLayout:
			withVideo = !withVideo;
			makeSekection((ImageView)findViewById(R.id.withVideo), withVideo);
			break;
		case R.id.NewLaunchLayout:
			newLaunch = ! newLaunch;
			makeSekection((ImageView)findViewById(R.id.newLaunches), newLaunch);
			break;
		case R.id.DoneBtn:
			Intent intent = new Intent();
			String value = (withPhoto ? "&withphotos=1" : "") +
							(withVideo ? "&withvideos=1" : "") +
							(newLaunch ? "&newlaunches=1" : "");
			String options = (withPhoto ? "With Photos, " : "") +
					(withVideo ? "With Videos, " : "") +
					(newLaunch ? "New Launches, " : "");
			intent.putExtra("value", value);
			intent.putExtra("options", options.equals("") ? "Select" : options.substring(0, options.length()-2));
			setResult(RESULT_OK, intent);
			finish();
			break;
		default:
			break;
	}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.options_layout);
			((RelativeLayout)findViewById(R.id.WithPhotosLayout)).setOnClickListener(onClick);
			((RelativeLayout)findViewById(R.id.WithVideosLayout)).setOnClickListener(onClick);
			((RelativeLayout)findViewById(R.id.NewLaunchLayout)).setOnClickListener(onClick);
			((Button)findViewById(R.id.DoneBtn)).setOnClickListener(onClick);
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
		}
	}
	/**
	 * 
	 * @param selectView	:: View 
	 * @param flag	:: True or false (Selected)
	 */
	private void makeSekection(ImageView selectView, boolean flag){
		selectView.setBackgroundResource(flag ? R.drawable.chked : R.drawable.unchked);
	}
}
