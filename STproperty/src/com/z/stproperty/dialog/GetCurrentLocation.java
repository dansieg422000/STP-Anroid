package com.z.stproperty.dialog;

/*******************************************************************************************
 * Class	: GetCurrentLocation
 * Type		: FragmentActivity
 * InterFace: OnMyLocationChangeListener
 * Date		: 19 02 2014
 * 
 * General Description:
 * 
 * Gets the user current location to search property near-by their location
 * 
 * After getting the location this will return the latitude and longitude values back
 * to calling function
 *******************************************************************************************/

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.z.stproperty.R;

public class GetCurrentLocation extends FragmentActivity implements OnMyLocationChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_location);
		SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);
		GoogleMap googleMap = supportMapFragment.getMap();
		googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationChangeListener(this);
	}
	@Override
	public void onMyLocationChange(Location arg0) {
		sendLocation(arg0);
	}
	/**
	 * @param currentLocation :: Location of user
	 * 
	 * Sends back the user current location to calling activity to show near by properties
	 */
	private void sendLocation(Location currentLocation){
		Intent intent = new Intent();
		intent.putExtra("latitude", currentLocation.getLatitude()+"");
		intent.putExtra("longitude", currentLocation.getLongitude()+"");
		setResult(RESULT_OK, intent);
		finish();
	}
}
