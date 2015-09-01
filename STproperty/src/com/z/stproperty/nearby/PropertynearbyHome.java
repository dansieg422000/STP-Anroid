package com.z.stproperty.nearby;

/***************************************************************
* Class name:
* (beforpropertynearby)
* 
* Description:
* (Show different options After user clicked property nearby)
* 
* 
* Input variables:
* String type(to indicate which type of property they chose(Residential or Commercial))
* String for1(to indicate what user want to(Buy,Rent,Rent a Room))
* String newlaunches(to check whether user need to view new launch properties)
* Output variables:
* all input values will be passed to property nearby as url 
*  
*  Filtering properties based
 *  
 * 1. ForSale
 * 2. ForRent
 * 3. RoomRental
 * 4. New Launches 
 * 
 * Net-Rating is sent to server
 * 		Net-Rating is done like
 * 		-residential-for-rent
		-residential-for-sale
		-residential-room-rental
		-residential-new-launches
 * 		-business-space-for-sale
 * 		-business-space-for-sale
		-business-space-room-rental
		-business-space-new-launches
****************************************************************/

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.z.stproperty.PropertyList;
import com.z.stproperty.R;
import com.z.stproperty.shared.SharedFunction;

public class PropertynearbyHome extends Activity {
	private TextView roomrental,newproperties;
	private Button com,resi;
	private String type="&ptype=1,2,5";
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
	 * Based on the view id this will launch new activity with filter values 
	 */
	private OnClickListener onClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			performViewClick(v);
		}
	};
	private void performViewClick(View v){
		Intent i =new Intent(getBaseContext(), PropertyNearBy.class);
		String filterParam="";
  	  	String analytics = "";
		switch (v.getId()) {
		case R.id.ForSale:
			analytics = "For_Sale";
			filterParam="&for=2";
        	PropertyList.wantfor=2;
			break;
		case R.id.ForRent:
			analytics = "For_Rent";
			filterParam="&for=1";
        	PropertyList.wantfor=1;		
			break;
		case R.id.ForRental:
			analytics = "Room_Rental";
			filterParam="&for=3";
        	PropertyList.wantfor=3;
			break;
		case R.id.NewProperties:
			analytics = "New";
			filterParam="&newlaunches=1";
			break;
		default:
			break;
		}
		i.putExtra("analytics", "Properties_Nearby_"+ (type.contains("3") ? "Business_Space_" : "Residential_") + analytics);
		i.putExtra("filterParam", filterParam);
		i.putExtra("listType", type);
		startActivity(i);
	}
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      try{
	      setContentView(R.layout.nearby_home);
	      com = (Button) findViewById(R.id.BusinessSpace);
	      resi = (Button) findViewById(R.id.Redidential);
	      TextView forsale = (TextView) findViewById(R.id.ForSale);
	      TextView forrent = (TextView) findViewById(R.id.ForRent);
	      roomrental = (TextView) findViewById(R.id.ForRental);
	      newproperties=(TextView) findViewById(R.id.NewProperties);
	      com.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	        	  roomrental.setEnabled(false);
	        	  roomrental.setTextColor(Color.parseColor("#c3c3c3"));
	        	  type="&ptype=3,4,6,7";
	        	  PropertyList.propertyTypes=3;
	        	  com.setBackgroundDrawable(PropertynearbyHome.this.getResources().getDrawable(R.drawable.roundcorners3));
	        	  resi.setBackgroundDrawable(PropertynearbyHome.this.getResources().getDrawable(R.drawable.roundcorners2));
	         }
	       });
	      resi.setOnClickListener(new OnClickListener() {
	          public void onClick(View v) {
	        	  roomrental.setEnabled(true);
	        	  roomrental.setTextColor(Color.parseColor("#000000"));
	        	  type="&ptype=1,2,5";
	        	  PropertyList.propertyTypes=1;
	        	  com.setBackgroundDrawable(PropertynearbyHome.this.getResources().getDrawable(R.drawable.roundcorners1));
	        	  resi.setBackgroundDrawable(PropertynearbyHome.this.getResources().getDrawable(R.drawable.roundcorners));
	         }
	       });
	      forsale.setOnClickListener(onClick);
	      forrent.setOnClickListener(onClick);
	      roomrental.setOnClickListener(onClick);
	      newproperties.setOnClickListener(onClick);
	      
      }catch (Exception e) {
		Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
      }
	}
	@Override
	protected void onResume() {
		super.onResume();
		// Screen Tracking
	    SharedFunction.sendGA(getApplicationContext(), "Properties_Nearby_Home");
	    // AT Internet tracking
	    SharedFunction.sendATTagging(getApplicationContext(), "Properties_Nearby_Home", 5, null);
	    // Screen Tracking ends
	}
	
}
