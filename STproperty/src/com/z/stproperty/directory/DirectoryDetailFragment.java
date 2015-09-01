package com.z.stproperty.directory;

/*********************************************************************************************************
 * Class : DirectoryDetailFragment 
 * Type : Fragment
 * Date : 26 01 2014
 * 
 * General Description:
 * 
 * Called by DirectoryFragment class.
 * 
 * Will load the directory details.
 * 
 * This class is responsible for the following :
 * 
 * - Displaying Directory details. 
 * - Displaying nearby amenities 
 * - Displaying map with nearby amenities
 * 
 * Few important layouts 
 * directories_list :: ListView that is displays the agent list.
 * 
 * 
 * Description :
 * 
 * onCreateView()
 * 
 * This sets the UI for the fragment. It also handles the operations for search
 * EditText, grid button and search button. This also sets the adapter for the
 * directories ListView.
 * 
 * loadDirectoryDetails()
 * 
 * This will get the required contents from the URL with the provided resources
 * to the JSONObject. First it will perform an Internet connection check and
 * will prompt the user if there is no Internet connection. If there is Internet
 * connection, the URL is loaded to ConnectionHandler which processes the URL
 * and returns the response.
 * 
 * addFacilityDetail()
 * 
 * This method will add the facility details by calling both loadLandDisplay()
 * and loadPortraitDisplay(). It will check weather there is any facility or not
 * and if there is no facility, then noFacilites TextView is made visible. Else,
 * the TextView is made gone and directory_facilities_row layout is inflated and
 * is added to facilityParent.
 * 
 * loadPortraitDisplay()
 * 
 * This method is called when the device orientation is portrait. This will get
 * the JSONArray from detailsJson and it will place the values to the
 * corresponding TextViews. It will also performs the null checks for all
 * required data and will replace those data by "-" value. In the case where
 * there is no photos to display, it will display a default no-photo image.
 * 
 * addMapPins()
 * 
 * This will add the nearby contents along with the map pins to the list. First
 * it will make all the nearby TextViews gone. Then, using the JSONObject, it
 * will check weather there is any amenities nearby and if there is nearby
 * amenities, it will make the corresponding TextView and layout visible and
 * will will pass the details to addNearByView() to display it in list.
 * 
 * 
 * ********************************************************************************************************/

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.z.stproperty.Photos;
import com.z.stproperty.R;
import com.z.stproperty.URLImageParser;
import com.z.stproperty.adapter.PhotoGallery;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.fonts.HelveticaBold;
import com.z.stproperty.shared.ImageLoader;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.UrlUtils;

public class DirectoryDetailFragment extends Fragment {
	private static final String ARG_POSITION = "position";
	private int curPosition;
	private static JSONObject detailsJson;
	private ScrollView directoryDetailScrollView;
	private ImageLoader imageLoader;
	private LayoutInflater inflater;
	private static ScrollView mapScrollView;
	private String propertyname = "";
	private static String directoryType;
	/**
	 * onClickListner :: Interface
	 * 
	 * This is common click listener for all option click buttons
	 * Based on user selection corresponding functionality or screen display is done
	 */
	private OnClickListener onClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			performViewClick(v);
		}
	};
	/**
	 * 
	 * @param position :: Position of the fragment (view with photos, Map, Info)
	 * @param detailJson :: Json values
	 * @param type	:: Type like condo, commercial etc..
	 * @return :: Fragment to adapter
	 */
	public static DirectoryDetailFragment newInstance(int position, JSONObject detailJson, String type) {
		detailsJson = detailJson;
		DirectoryDetailFragment f = new DirectoryDetailFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		directoryType = type;
		return f;
		
	}
	private void performViewClick(View v){
		try {
			String url = UrlUtils.URL_LISTING
					+ "&project=" + propertyname + "&limit=25&userid="+detailsJson.getString("id")
					+"&type=" + directoryType;
		
			switch (v.getId()) {
			case R.id.ForSale:
				url = url + "&for=2";
				break;
			case R.id.ForRent:
				url = url + "&for=1";
				break;
			default:
				// Room rental
				url = url + "&for=3";
				break;
			}
			url = url.replace(" ", "%20");
			url = url.replace("+", "");
			Intent intent = new Intent(getActivity(), Agentsalelist.class);
			intent.putExtra("url", url);
			getActivity().startActivity(intent);
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * @param savedInstanceState
	 *            :: If the activity is being re-initialized after previously
	 *            being shut down then this Bundle contains the data it most
	 *            recently supplied in onSaveInstanceState(android.os.Bundle)
	 * 
	 * Called when the activity is starting.  This is where most
	 * initialization should go: calling setContentView(int) to
	 * inflate the activity's UI, using findViewById(int) to
	 * programmatically interact with widgets in the UI. 
	 * 
	 * Here, the onclickListeners() for all tabs buttons are assigned.
	 * The orientation is also checked and the corresponding display
	 * is set.
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		curPosition = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		imageLoader = new ImageLoader(getActivity().getApplicationContext());
		if(curPosition == 0){
			return addDirectoryDetail();
		}else if(curPosition ==1){
			return addMapPins();
		}else{
			return addAdditonalInfo();
		}
	}
	/**
	 * PHOTOS
	 * 
	 * photos are shown in pager and when the user touches the pager 
	 * then scroll stops to scroll vertically
	 * 
	 * DETAILS
	 * 
	 * Will show all the details related to the property or directory
	 * like type,
	 * title,
	 * subtype,
	 * district etc..
	 * 
	 * FACILITIES
	 * 
	 * This method will add the facility details by calling both
	 * loadLandDisplay() and loadPortraitDisplay(). It will check weather there
	 * is any facility or not and if there is no facility, then noFacilites
	 * TextView is made visible. Else, the TextView is made gone and
	 * directory_facilities_row layout is inflated and is added to
	 * facilityParent.
	 * 
	 */
	private View addDirectoryDetail(){
		View view = inflater.inflate(R.layout.directorydetail, null);
		try{
			directoryDetailScrollView = (ScrollView) view.findViewById(R.id.DetailsScroll);
			
			((HelveticaBold)view.findViewById(R.id.DirectoryTitle)).setText(detailsJson.getString("name"));
			((HelveticaBold)view.findViewById(R.id.district)).setText(detailsJson.getString("district"));
			JSONArray developerArray = detailsJson.getJSONArray("developers");
			String developerStr = "-";
			for(int devindex = 0; devindex<developerArray.length();devindex++){
				developerStr = (!developerStr.equals("-") ? ", " : "") + developerStr + developerArray.getString(devindex);
			}
			propertyname = detailsJson.getString("name");
			((HelveticaBold)view.findViewById(R.id.Developer)).setText(developerStr);
			((HelveticaBold)view.findViewById(R.id.Tenure)).setText(detailsJson.getString("tenure"));
			((HelveticaBold)view.findViewById(R.id.TopYear)).setText(detailsJson.getString("top"));
			((HelveticaBold)view.findViewById(R.id.ForSaleCount)).setText(detailsJson.getString("forsalecount"));
			((HelveticaBold)view.findViewById(R.id.ForRentCount)).setText(detailsJson.getString("forrentcount"));
			((HelveticaBold)view.findViewById(R.id.ForRentalCount)).setText(detailsJson.getString("forroomrentalcount"));
			String noOfFloor = "-";
			if(!detailsJson.isNull("nooffloors")){
				noOfFloor = detailsJson.getString("nooffloors");
			}
			String noOfUnits = "-";
			if(!detailsJson.isNull("nooffloors")){
				noOfUnits = detailsJson.getString("noofunits");
			}
			((HelveticaBold)view.findViewById(R.id.NoOfFloor)).setText(noOfFloor);
			((HelveticaBold)view.findViewById(R.id.NoOfUnits)).setText(noOfUnits);
			((HelveticaBold)view.findViewById(R.id.Classification)).setText(detailsJson.getString("class"));
			if(detailsJson.has("facilities") && !detailsJson.getString("facilities").equals("[\"\"]")){
				JSONArray facilities = detailsJson.getJSONArray("facilities");
				LinearLayout facilityParent = (LinearLayout) view.findViewById(R.id.FacilityLayout);
				facilityParent.removeAllViews();
				for(int index=0; index<facilities.length() && !facilities.getString(index).equals("");index++){
					View child = inflater.inflate(R.layout.directory_facilities_row, null);
					facilityParent.addView(child);
					((Helvetica)child.findViewById(R.id.facilityName)).setText(facilities.getString(index));
				}
			}
			ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
			
			final TextView imageCount = (TextView) view.findViewById(R.id.imageCount);
			PhotoGallery gallery = new PhotoGallery();
			pager.setAdapter(gallery);
			JSONArray galery = detailsJson.getJSONArray("photos");
			if(galery.length()==0){
				galery.put("http://www.stproperty.sg/images/no-photo_S.jpg");
			}
			final String[] photosArray = new String[galery.length()];
			imageCount.setText("1/"+photosArray.length);
			Button forSale = (Button)view.findViewById(R.id.ForSale);
			Button forRent = (Button)view.findViewById(R.id.ForRent);
			if(detailsJson.getInt("forsalecount") == 0){
				forSale.setBackgroundResource(R.drawable.green_btn_disables);
				forSale.getBackground().setAlpha(128);
			}else{
				forSale.setOnClickListener(onClick);
			}
			if(detailsJson.getInt("forrentcount") == 0){
				forRent.setBackgroundResource(R.drawable.green_btn_disables);
				forRent.getBackground().setAlpha(128);
			}else{
				forRent.setOnClickListener(onClick);
			}
			Button forRental = (Button)view.findViewById(R.id.RoomRental);
			if(detailsJson.getInt("forroomrentalcount") == 0){
				forRental.setBackgroundResource(R.drawable.green_btn_disables);
				forRental.getBackground().setAlpha(128);
			}else{
				forRental.setOnClickListener(onClick);
			}
			for(int index=0; index<galery.length();index++){
				View imageLoading = inflater.inflate(R.layout.image_with_loading, null);
				ImageView image = (ImageView) imageLoading.findViewById(R.id.galleryPhoto);
				photosArray[index] = galery.getString(index).replace("_S.", "_L.");
	        	imageLoader.displayImage(galery.getString(index).replace("_S.", "_L."), image);
	        	final int curIndex = index;
	        	imageLoading.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							directoryDetailScrollView.requestDisallowInterceptTouchEvent(true);
					    } else if (event.getAction() == MotionEvent.ACTION_UP) {
					    	directoryDetailScrollView.requestDisallowInterceptTouchEvent(false);
					    }
						return false;
					}
				});
	        	imageLoading.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent galleryIntent = new Intent(getActivity(),Photos.class);
						galleryIntent.putExtra("photos1", photosArray);
						galleryIntent.putExtra("selected", curIndex);
						startActivity(galleryIntent);
					}
				});
	        	gallery.addView(imageLoading);
			}
			pager.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					imageCount.setText((arg0+1)+"/"+photosArray.length);
				}
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// on page scrolled
				}
				@Override
				public void onPageScrollStateChanged(int arg0) {
					// On scroll changed do here
				}
			});
			gallery.notifyDataSetChanged();
			
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
		return view;
	}
	/**
	 * 
	 * This method will check the detailsJson and the value is added to content.
	 * If the content is blank, "No Data" is added to the content. Then, the
	 * content is added to AdditionalInfo TextView.
	 * 
	 */
	private View addAdditonalInfo(){
		View view = inflater.inflate(R.layout.info, null);
		try{
			String content = detailsJson.getString("intro");
			if(content.equals("") || content.equals("null")){
				content = "No Data.";
			}
			TextView addTxt = ((Helvetica)view.findViewById(R.id.AdditionalInfo));
			URLImageParser p = new URLImageParser(addTxt, this.getActivity());
			Spanned htmlSpan = Html.fromHtml(content, p, null);
			Log.d("url - add info", content);
			addTxt.setText(htmlSpan);
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
		return view;
	}
	
	/**
	 * 
	 * This will add the nearby contents along with the map pins to the list.
	 * First it will make all the nearby TextViews gone. Then, using the
	 * JSONObject, it will check weather there is any amenities nearby and if
	 * there is nearby amenities, it will make the corresponding TextView and
	 * layout visible and will will pass the details to addNearByView() to
	 * display it in list.
	 * 
	 */
	private View addMapPins(){
		View view = inflater.inflate(R.layout.nearby_map_pins, null);
		try{
			mapScrollView = (ScrollView) view.findViewById(R.id.MapScrollView);
			SupportMapFragment supportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapview);
			GoogleMap googleMap = supportMapFragment.getMap();
			SharedFunction.addMapPins(getActivity(), inflater, view, googleMap, detailsJson, mapScrollView);
			
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
		return view;
	}
	/**
	 * 
	 * @param flag	:: True or false
	 * based on flag this will all scroll to scroll or not-to-scroll
	 */
	public static void mapZoomControls(boolean flag){
		mapScrollView.requestDisallowInterceptTouchEvent(flag);
	}
}
