package com.z.stproperty;

/***************************************************************
 * Class name:
 * (Map)
 * 
 * Description:
 * (Show Map view of listing properties and mark the different property on map)
 * 
 * 
 * Input variables:
 * String latitude,longitude(properties' location and user's current location)
 * 
 * Output variables:
 * null
 * 
 * Google Map Api
 * 	Key version used is 2
 * 	Plotting all the latitude and longitude values as marker in the map
 * 	All markers are brought to view with 200 x 200 
 * 		so that all marker can be visible in device 
 * 	Animating the camera position to focus the markers
 * 	
 * 	OnClick of the marker the property name is shown in default display
 * 	 
 ****************************************************************/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.fonts.HelveticaBold;
import com.z.stproperty.shared.SharedFunction;

public class PropertyListOnMap extends FragmentActivity {
	private List<HashMap<String, String>> menuItems;
	private String longitude="103.956205", latitude="1.324076", tabtitle = "";
	private GoogleMap googleMap;
	private Marker marker;
	/**
	 * Override the info window 
	 * to show custom title and on-click
	 */
	private InfoWindowAdapter infoWinow = new InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker arg0) {
            return null;
        }
        @Override
        public View getInfoContents(Marker marker) {
            View myContentView = getLayoutInflater().inflate(R.layout.marker_info_window, null);
            Helvetica tvTitle = (Helvetica) myContentView.findViewById(R.id.propertyTitle);
            String[] titlearray = marker.getTitle().split("\\$\\#");
            tvTitle.setText(titlearray[1]);
            return myContentView;
        }
    };
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		try {
			SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);
			googleMap = supportMapFragment.getMap();
			CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude))).zoom(10)
                    .tilt(12).build();
			// Creates a CameraPosition from the builder
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			HelveticaBold titleView = (HelveticaBold) findViewById(R.id.headerTitle);

			Bundle extras = getIntent().getExtras();
			if (getIntent().hasExtra("menuItems")) {
				menuItems = (ArrayList<HashMap<String, String>>) extras.get("menuItems");
			}
			if (getIntent().hasExtra("tabtitle")) {
				tabtitle = extras.getString("tabtitle");
			}
			Button list = (Button) findViewById(R.id.propertyOnList);
			list.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String  gaSearchScreenName = getIntent().hasExtra("gaSearchScreenName") ? 
							getIntent().getStringExtra("gaSearchScreenName") : "";
					if(!gaSearchScreenName.equals("")){
						SharedFunction.sendGA(PropertyListOnMap.this, gaSearchScreenName + "::" + gaSearchScreenName + "_Listing");
					}
					onBackPressed();
				}
			});
			if(getIntent().getStringExtra("count").equals("")){
				((HelveticaBold)findViewById(R.id.PropertyCount)).setVisibility(View.GONE);
			}else{
				((HelveticaBold)findViewById(R.id.PropertyCount)).setText(getIntent().getStringExtra("count"));
			}
			titleView.setText(tabtitle);
			
			googleMap.setInfoWindowAdapter(infoWinow);
			googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				
				@Override
				public void onInfoWindowClick(Marker arg0) {
					showMap(arg0.getTitle());
				}
			});
			loadPinsOnMap();
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * Loads the Different pins based on the property type like condo, HADB etc...
	 * 
	 * if (menuItems.size() < 2) {
			builder.include(new LatLng( marker.getPosition().latitude + 0.093, marker .getPosition().longitude + 0.093));
		}
	 */
	private void loadPinsOnMap(){
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		if (!menuItems.isEmpty()) {
			try {
				for (int is = 0; is < menuItems.size(); is++) {
					Map<String, String> item = new HashMap<String, String>();
					item = menuItems.get(is);
					// List will contain null for add display so need to check before processing it
					if (item!=null && !item.get("latitude").equals("null")) {
						Bitmap icon = BitmapFactory.decodeResource(PropertyListOnMap.this.getResources(), SharedFunction.loadMapPin(item.get("property_type")));
						latitude = item.get("latitude");
						longitude = item.get("longitude");
						String title = is+"$#"+item.get("title");
						marker = googleMap.addMarker(new MarkerOptions()
										.title(title)
										.position( new LatLng( Double.parseDouble(latitude), Double.parseDouble(longitude)))
										.icon(BitmapDescriptorFactory .fromBitmap(icon)));
						builder.include(marker.getPosition());
						icon.recycle();
					}
				}
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			}
		}
		LatLngBounds bounds = builder.build();
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 400, 400, 0);
		googleMap.animateCamera(cu);
	}
	/**
	 * 
	 * @param title	:: Title of marker info-window to show
	 */
	private void showMap(String title){
		try{
			String[] titlearray = title.split("\\$\\#");
			Intent i = new Intent(getBaseContext(), PropertyDetail.class);
			i.putExtra("propertyDetail", menuItems.get(Integer.parseInt(titlearray[0])));
			i.putExtra("tabtitle", tabtitle);
			if(getIntent().hasExtra("gaScreenName")){
				i.putExtra("gaScreenName", getIntent().getStringExtra("gaScreenName"));
			}
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
    private void postAnalytics(){
    	// Screen Tracking Starts
		if(getIntent().hasExtra("gaScreenName") && !getIntent().getStringExtra("gaScreenName").equals("")){
			String gaScreenName = getIntent().getStringExtra("gaScreenName");
			int level2Id = gaScreenName.contains("For_Sale") ? 7 :(gaScreenName.contains("Room_For_Rent") ? 9 : 8);
			SharedFunction.sendGA(PropertyListOnMap.this, gaScreenName + "::" + gaScreenName + "_Map_Listing");
	        // AT Internet tracking
	        SharedFunction.sendATTagging(getApplicationContext(), gaScreenName + "::" + gaScreenName + "_Map_Listing", level2Id, null);
		}
		String  gaSearchScreenName = getIntent().hasExtra("gaSearchScreenName") ? 
				getIntent().getStringExtra("gaSearchScreenName") : "";
		if(!gaSearchScreenName.equals("")){
			SharedFunction.sendGA(PropertyListOnMap.this, gaSearchScreenName + "::" + gaSearchScreenName + "_Map_Listing");
		}
		// Screen Tracking ends
    }
    @Override
	protected void onResume() {
		// Auto-generated method stub
		super.onResume();
		postAnalytics();
	}
}