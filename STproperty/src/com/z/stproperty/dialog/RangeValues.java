package com.z.stproperty.dialog;

/***
 * 
 * OnCreate() is called when the activity is created.
 * It sets the range_values_dialog layout to visibility in the screen.
 * 
 * fromId and toId are assigned from string variables
 *  fromValue and toValue in received intent, respectively
 *  
 *  Text View rangeTitle is set with text from string variable 
 *  "Title " in received intent
 *  
 *   fromarrayId and toarrayId are assigned from string variables
 *  fromArrayId and toArrayId in received intent, respectively
 *  
 *  fromadapter is an ArrayWheelAdapter<String> with array value 
 *  as the fromarrayId in strings.xml
 *  text size for values in fromadapter is set to 14
 *  
 *  fromValue is a wheel view for which fromadapter is set as ViewAdapter
 *  in fromValue fromId position is selected as current item  
 *  
 *  toadapter is an ArrayWheelAdapter<String> with array value 
 *  as the toarrayId in strings.xml
 *  text size for values in toadapter is set to 14
 *  
 *  toValue is a wheel view for which toadapter is set as ViewAdapter
 *  in toValue toId position is selected as current item  
 *  
 *  fromscrollListener and toscrollListener are OnWheelScrollListener
 *  where fromId is assigned in fromscrollListener
 *  and toId is assigned in toscrollListener
 *  
 *  fromValue is added with fromscrollListener 
 *  toValue is added with toscrollListener
 *  
 *  An on click listener is set for done Button where
 *  an Intent is created and set with a result RESULT_OK
 *  The intent is appended with extra values fromId and toId
 *  and this activity is finished.
 *  
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.evvo.wheelscroller.OnWheelScrollListener;
import com.evvo.wheelscroller.WheelView;
import com.z.stproperty.R;
import com.z.stproperty.adapter.ArrayWheelAdapter;
import com.z.stproperty.fonts.HelveticaBold;

public class RangeValues extends Activity{
	private int fromId = 0,toId=0;
	private HelveticaBold filteredRange;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.range_values_dialog);
			final String[] fromArray = getIntent().getStringArrayExtra("fromArray");
			final String[] toArray = getIntent().getStringArrayExtra("toArray");
			fromId=getIntent().getIntExtra("fromValue",0);
			toId=getIntent().getIntExtra("toValue",0);
			filteredRange = (HelveticaBold)findViewById(R.id.FilteredRange);
			filteredRange.setText(fromArray[0]+" - "+toArray[0]);
			((HelveticaBold)findViewById(R.id.rangeTitle)).setText(getIntent().getStringExtra("title"));
			ArrayWheelAdapter<String> fromadapter = new ArrayWheelAdapter<String>(this, fromArray);
			fromadapter.setTextSize(14);
			WheelView fromValue = (WheelView) findViewById(R.id.fromVaue);
			fromValue.setVisibleItems(10);
			fromValue.setViewAdapter(fromadapter);
			fromValue.setCurrentItem(fromId);
			ArrayWheelAdapter<String> toAdapter =new ArrayWheelAdapter<String>(this, toArray);
			toAdapter.setTextSize(14);
			final WheelView toValue = (WheelView) findViewById(R.id.toValue);
			toValue.setVisibleItems(10);
			toValue.setViewAdapter(toAdapter);
			toValue.setCurrentItem(toId);
			OnWheelScrollListener fromscrollListener = new OnWheelScrollListener() {
                public void onScrollingStarted(WheelView wheel) {
                	// Method declarations
                }
                public void onScrollingFinished(WheelView wheel) {
                	fromId = wheel.getCurrentItem();
                	filteredRange.setText(fromArray[fromId]+" - "+toArray[toId]);
                }
	        };
	        OnWheelScrollListener toscrollListener = new OnWheelScrollListener() {
	            public void onScrollingStarted(WheelView wheel) { 
	            	// Method declarations
	            }
	            public void onScrollingFinished(WheelView wheel) {
	            	toId = wheel.getCurrentItem();
	            	filteredRange.setText(fromArray[fromId]+" - "+toArray[toId]);
	            }
	        };
	        fromValue.addScrollingListener(fromscrollListener);
	        toValue.addScrollingListener(toscrollListener);
	        ((Button)findViewById(R.id.donebtn)).setOnClickListener(new  OnClickListener() {
				@Override
				public void onClick(View v) {
					if(toId<=fromId){
						Intent intent = new Intent(getApplicationContext(), STPAlertDialog.class);
						intent.putExtra("message", "Please check your MIN and MAX range values.");
						startActivity(intent);
					}else{
						Intent returnIntent = new Intent();
						returnIntent.putExtra("fromId",fromId);
						returnIntent.putExtra("toId",toId);
						setResult(RESULT_OK,returnIntent);
						finish();
					}
				}
			});
		}catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
		}
	}
}