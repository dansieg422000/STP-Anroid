package com.z.stproperty;

/*********************************************************************************************************
 * Class : PropertyDetailFragment 
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
 * addProjectDetail()
 * 
 * This will get the required contents from the URL with the provided resources
 * to the JSONObject. First it will perform an Internet connection check and
 * will prompt the user if there is no Internet connection. If there is Internet
 * connection, the URL is loaded to ConnectionHandler which processes the URL
 * and returns the response.
 * 
 * FACILITIES
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

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.z.stproperty.adapter.PhotoGallery;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.fonts.HelveticaBold;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.ImageLoader;
import com.z.stproperty.shared.SharedFunction;

public class PropertyDetailFragment extends Fragment {
	private static final String ARG_POSITION = "position";
	private int curPosition;
	private static JSONObject detailsJson;
	private ScrollView projectDetailScrollView;
	private ImageLoader imageLoader;
	private LayoutInflater inflater;
	private static ScrollView mapScrollView;
	private static Context mContext;
	private String price = "", title = "", mobileNoStr="", agentEmail="", prurl="";
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
	 * Based on the view id this will perform different functionalities 
	 */
	private OnClickListener onClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			performClickAction(v);
		}
	};
	/**
	 * 
	 * @param position	:: Current page position
	 * @param detailJson	:: Property details JSON
	 * @param context 
	 * @return	:: Return fragment for pager
	 */
	public static PropertyDetailFragment newInstance(int position, JSONObject detailJson, Context context) {
		detailsJson = detailJson;
		mContext = context;
		PropertyDetailFragment f = new PropertyDetailFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		if(detailJson!=null){
		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		mPrefs.edit().putString("PropertyDetailJson",detailJson.toString()).commit();
		}
		return f;
		
	}
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
	 * Based on the view id this will perform different functionalities 
	 */
	private void performClickAction(View v){
		try{
			switch (v.getId()) {
			case R.id.LoanCalculator:
				Intent calIntent = new Intent(getActivity(), LoanCalculator.class);
				calIntent.putExtra("price", price);
				startActivity(calIntent);
				break;
			case R.id.AgentMobile:
				String url = "tel:" + mobileNoStr;
				url = url.replace("+65-", "");
				Intent callIntent = new Intent( Intent.ACTION_CALL);
				callIntent.setData(Uri.parse(url));
				startActivity(callIntent);
				break;
			case R.id.SendEmailBtn:
				String text = "I'm interested in your property advertised on STProperty\n\n" + "PROPERTY TITLE:"
						+ title + "\n" + "PRICE:" + price + "\n\nClick on the following link to view more details about the property\n\n" + prurl;

				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("plain/text");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[] { agentEmail });
				intent.putExtra(Intent.EXTRA_SUBJECT, "Enquiry");
				intent.putExtra(Intent.EXTRA_TEXT, text);
				SharedFunction.postAnalytics(PropertyDetailFragment.this.getActivity(), "Lead", "Successful Email Enquiry",  title);
				startActivity(Intent.createChooser(intent, ""));
				break;
			/**case R.id.SendEnquiryBtn:
				Intent enqInt = new Intent(getActivity(), SaleEnquiry.class);
				enqInt.putExtra("propertyId", detailsJson.getString("id"));
				enqInt.putExtra("agentId", detailsJson.getJSONObject("seller_info").getString("agent_cea_reg_no"));
				startActivity(enqInt);
				break;*/
			default:
				break;
			}
		}catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		curPosition = getArguments().getInt(ARG_POSITION);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		imageLoader = new ImageLoader(getActivity().getApplicationContext());
		try{
			if(curPosition == 0){
				return addProjectDetail();
			}else if(curPosition ==1){
				if(detailsJson == null){
					SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
						detailsJson = new JSONObject(mPrefs.getString("PropertyDetailJson", null));
				} else {
					SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
					mPrefs.edit().putString("PropertyDetailJson", detailsJson.toString()).commit();
				}
				if(detailsJson.getString("map_latitude").equalsIgnoreCase("null")){
					return addAdditonalInfo();
				}else{
					return addMapPins();
				}
			}else{
				return addAdditonalInfo();
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			return null;
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
	private View addProjectDetail(){
		View view = inflater.inflate(R.layout.property_details, null);
		try{
			projectDetailScrollView = (ScrollView) view.findViewById(R.id.DetailsScroll);
			if(detailsJson == null){
				SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
					detailsJson = new JSONObject(mPrefs.getString("PropertyDetailJson", null));
			} else {
				SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
				mPrefs.edit().putString("PropertyDetailJson", detailsJson.toString()).commit();
			}
			try{
			((HelveticaBold)view.findViewById(R.id.propertyTitle)).setText(detailsJson.getString("property_title"));
			((HelveticaBold)view.findViewById(R.id.projectName)).setText(detailsJson.getString("project_name"));
			((HelveticaBold)view.findViewById(R.id.Classification)).setText(detailsJson.getString("property_classification"));
			((HelveticaBold)view.findViewById(R.id.Tenure)).setText(detailsJson.getString("tenure").replace("null", "-"));
			((HelveticaBold)view.findViewById(R.id.propertyType)).setText(detailsJson.getString("property_type"));
			} catch(Exception e){
				Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			}
			String floorArea = "-";
			if(!detailsJson.getString("builtin_area").equals("null")){
				floorArea = detailsJson.getString("builtin_area") + " sqft";
			}
			((HelveticaBold)view.findViewById(R.id.FloorArea)).setText(floorArea);
			
			((HelveticaBold)view.findViewById(R.id.Address)).setText(detailsJson.getString("address"));
			((HelveticaBold)view.findViewById(R.id.PostalCode)).setText(detailsJson.getString("postal_code").replace("null", "-"));
			String bedRoom = detailsJson.getString("bedrooms");
			String bathRoom = detailsJson.getString("bathroom");
			((HelveticaBold)view.findViewById(R.id.Bedrooms)).setText(bedRoom.equals("null") || bedRoom.equals("0") ? "-" : bedRoom);
			((HelveticaBold)view.findViewById(R.id.Bathrooms)).setText(bathRoom.equals("null") || bathRoom.equals("0") ? "-" : bathRoom);
			String psf = detailsJson.getString("psf");
			psf = psf.equals("null") || psf.equals("0") ? "-" : "SGD " + psf;
			((HelveticaBold)view.findViewById(R.id.Psf)).setText(psf);
			String district = detailsJson.get("property_district").toString();
			String estate = detailsJson.get("property_estate").toString();
			if (!estate.equals("")) {
				district = estate;
			}
			((HelveticaBold)view.findViewById(R.id.district)).setText(district);
			price = detailsJson.getString("price").replace("null", "");
			price = price.equals("") || price.equalsIgnoreCase("price on ask") ? price 
							: "SGD " + SharedFunction.getPriceWithComma(detailsJson.getString("price"));
			view.findViewById(R.id.PSFDetailLayout).setVisibility(psf.equals("-") ? View.GONE : View.VISIBLE);
			if(price.equals("")){
				((LinearLayout)view.findViewById(R.id.PriceLayout)).setVisibility(View.GONE);
				((LinearLayout)view.findViewById(R.id.PSFDetailLayout)).setVisibility(View.GONE);
			}else{
				((HelveticaBold)view.findViewById(R.id.Price)).setText(price);
			}
			((HelveticaBold)view.findViewById(R.id.Psf)).setText(psf);
			title = detailsJson.getString("property_title");
			prurl = detailsJson.getString("url");
			JSONObject sellerJson = detailsJson.getJSONObject("seller_info");
			String registerNoStr = sellerJson.getString("agent_cea_reg_no").replace("null", "-");
			String licenceNoStr = sellerJson.getString("agent_cea_license_no").replace("null", "-");
			String agentImageUrl = sellerJson.getString("agent_image");
			agentEmail = sellerJson.getString("agent_email");
			mobileNoStr = sellerJson.getString("agent_mobile");
			((HelveticaBold)view.findViewById(R.id.AgentName)).setText("Agent Name : "+sellerJson.getString("agent_name"));
			((HelveticaBold)view.findViewById(R.id.AgentMobile)).setText(sellerJson.getString("agent_mobile"));
			((HelveticaBold)view.findViewById(R.id.RegisterNo)).setText(registerNoStr);
			((HelveticaBold)view.findViewById(R.id.LicenceNo)).setText(licenceNoStr);
			((HelveticaBold)view.findViewById(R.id.agentEmail)).setText(sellerJson.getString("agent_email"));
			((HelveticaBold)view.findViewById(R.id.Agency)).setText(sellerJson.getString("agency_name"));
			imageLoader.displayImage(agentImageUrl, (ImageView)view.findViewById(R.id.AgentImage));
			
			if (detailsJson.getString("property_for").equalsIgnoreCase("for sale") && !price.equalsIgnoreCase("price on ask")
					&& !price.equalsIgnoreCase("")) {
				((Button)view.findViewById(R.id.LoanCalculator)).setOnClickListener(onClick);
			}else{
				((Button)view.findViewById(R.id.LoanCalculator)).setBackgroundResource(R.drawable.green_btn_disables);
				((Button)view.findViewById(R.id.LoanCalculator)).getBackground().setAlpha(128);
			}
			final TextView imageCount = (TextView) view.findViewById(R.id.imageCount);
			view.findViewById(R.id.AgentMobile).setOnClickListener(onClick);
			view.findViewById(R.id.SendEmailBtn).setOnClickListener(onClick);
			view.findViewById(R.id.SendEnquiryBtn).setOnClickListener(onClick);
			ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
			
			PhotoGallery gallery = new PhotoGallery();
			pager.setAdapter(gallery);
			JSONArray galery = detailsJson.getJSONArray("photos");
			final String[] photosArray = new String[galery.length()];
			imageCount.setText("1/"+photosArray.length);
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
							projectDetailScrollView.requestDisallowInterceptTouchEvent(true);
					    } else if (event.getAction() == MotionEvent.ACTION_UP) {
					    	projectDetailScrollView.requestDisallowInterceptTouchEvent(false);
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
					// Auto-generated method stub
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
					// Auto-generated method stub
				}
			});
			gallery.notifyDataSetChanged();
			view.findViewById(R.id.BedBathLayout).setVisibility(
					Arrays.asList(Constants.RESIDENTIAL_TYPE).contains(detailsJson.getString("property_type")) 
					? View.VISIBLE : View.GONE);
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
			String content = detailsJson.getString("additional_info");
			if(content.equals("") || content.equals("null")){
				content = "No Data.";
			}
			content = content.replace("\n", " <br /> ");
			TextView addTxt = ((Helvetica)view.findViewById(R.id.AdditionalInfo));
			URLImageParser p = new URLImageParser(addTxt, this.getActivity());
			Spanned htmlSpan = Html.fromHtml(content, p, null);
			addTxt.setText(htmlSpan);
			//.setText(Html.fromHtml(content));
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
			mapScrollView.setSmoothScrollingEnabled(true);
			SupportMapFragment supportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager() .findFragmentById(R.id.mapview);
			GoogleMap googleMap = supportMapFragment.getMap();
			SharedFunction.addMapPins(getActivity(), inflater, view, googleMap, detailsJson, mapScrollView);
		}catch(Exception e){
			Toast.makeText(getActivity(), "Property map details not found.", Toast.LENGTH_LONG).show();
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
