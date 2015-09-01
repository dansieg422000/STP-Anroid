package com.z.stproperty.search;

/***************************************************************
 * Class name:
 * (tab2)
 * 
 * Description:
 * (Users can search in this UI)
 * 
 * 
 * Input variables:
 * null
 * Output variables:
 * null
 * *  FatherActivity
 * 
 * 	FatherActivity has few common functions like 
 * 
 * 	isApplicationBroughtToBackground() :: 
 * 
 * 		this will check for whether app in running as background process or not
 * 
 *  onRestart() :: 
 *  
 *  	On restart of these activities it will check for back options and reload
 *  
 *  ShortCuts
 *  
 *   1. Search
 *   2. Different property types like condo, commercial etc...
 *   3. To list directories
 *   4. Article listing (Latest News)
 *   5. More Tab 
 *   	5.1 enquiry
 *   	5.2 saved search list
 *   	5.3 outbox
 *   	5.4 contact-us (feedback)
 *   6. Favorites Tab
 *   7. Login
 *   8. Register
 *   
 *   User can choose their options from this screen to view their required properties
 *  
 ****************************************************************/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evvolutions.android.http.AsyncHttpResponseHandler;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.z.stproperty.BaseActivity;
import com.z.stproperty.MainActivity;
import com.z.stproperty.PropertyList;
import com.z.stproperty.R;
import com.z.stproperty.database.DatabaseHelper;
import com.z.stproperty.dialog.GetCurrentLocation;
import com.z.stproperty.dialog.ListPicker;
import com.z.stproperty.dialog.Options;
import com.z.stproperty.dialog.RangeValues;
import com.z.stproperty.dialog.STPAlertDialog;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.shared.ConnectionCheck;
import com.z.stproperty.shared.ConnectionManager;
import com.z.stproperty.shared.ConnectionManager.ConnectionType;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.UrlUtils;

public class SearchTab extends BaseActivity{
	private String bed = "", bath = "", tennure = "", maxpsf = "",
			minpsf = "", top = "", listedon = "", sortby = "";
	private String propertyWant = "2", nearByUrl = "",
			district = "", minprice = "", maxprice = "", minBuiltArea = "",
			maxBuiltArea = "", leaseTerm = "", covMin = "", covMax = "", roomType = "", hdbScheme = "";
	private static String[] LOCATIONS = {"Current Location", "By District", "Near School", "Near MRT", "Near Shopping Mall", "Near Child Care Center"};
	private String propertyType="", clasi = "", options="",searchLatitude="", searchLongitude="";
	private Button sale, forrent, forRental, saveSearch;
	private EditText keyword;
	private int nearByPosition = -1;
	protected String[] searchedTmpList;
	private List<LocationHelper> locationDetails = new ArrayList<SearchTab.LocationHelper>();
	/**
	 * Based on the user selected button this will highlights the button
	 * and its corresponding selection values are gets cleared.
	 * 
	 */
	private OnClickListener onClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			onViewClick(v);
		}
	};
	/**
	 * Based on the user selected button this will highlights the button
	 * and its corresponding selection values are gets cleared.
	 * 
	 */
	private void onViewClick(View v){
		switch (v.getId()) {
		case R.id.ForSaleBtn:
			highLightSale();
			break;
		case R.id.ForRentBtn:
			highlightRent();
			break;
		case R.id.ForRentalBtn:
			clearValuesForRental();
			break;
		case R.id.SearchBtn:
			performSearch();
			break;
		case R.id.SaveSearchList:
			Intent i = new Intent(getBaseContext(), ViewSavedsearch.class);
			startActivity(i);
			break;
		default:
			break;
		}
	}
	/**
	 * Common listener for view click event
	 * based the layout id this will perform or launch the new activity
	 * with some set of values as  well for user selection
	 * 
	 * like 
	 * 
	 * 1. Property type
	 * 2. Property subtype
	 * 3. Location
	 * 4. Bedroom and bathroom
	 * 5. Price range
	 * 6. Land size or floor area etc...
	 */
	private OnClickListener onPickerClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			onPickerViewClickEvent(v);
		}
	};
	private void onPickerViewClickEvent(View v){
		Intent intent = new Intent(getApplicationContext(), ListPicker.class);
		int requestCode = Constants.REQUEST_PROPERTYTYPE;
		switch (v.getId()) {
			case R.id.PropertyType:
				String[] propertyArray = propertyWant.equals("3") ? Constants.RENTAL_PROPERTYTYPE : Constants.PROPERTYTYPE;
				intent.putExtra("title", "Property Type");
				intent.putExtra("array", propertyArray);
				break;
			case R.id.PriceLayout:
				intent = new Intent(getApplicationContext(), RangeValues.class);
				intent.putExtra("title", "Price Range");
				intent.putExtra("fromArray", propertyWant.equals("2") ? Constants.SALEMINPRICE : (propertyWant.equals("1") ? Constants.RENTMINPRICE : Constants.RENTAL_MINPRICE));
				intent.putExtra("toArray", propertyWant.equals("2") ? Constants.SALEMAXPRICE : (propertyWant.equals("1") ? Constants.RENTMAXPRICE : Constants.RENTAL_MAXPRICE));
				requestCode = Constants.REQUEST_PRICE;
				break;
			case R.id.LandSizeLayout:
					intent = new Intent(getApplicationContext(), RangeValues.class);
					String[] floorValue = propertyType.equals("2") ? Constants.FLOORAREA : Constants.LANDAREA;
					intent.putExtra("title", "Floor Area");
					intent.putExtra("fromArray", floorValue);
					intent.putExtra("toArray", floorValue);
					requestCode = Constants.REQUEST_LANDSIZE;
				break;
			case R.id.ClassificationLayout:
				if(propertyType.equals("")){
					intent = new Intent(getApplicationContext(), STPAlertDialog.class);
					intent.putExtra("message", "Please select property type.");
				}else{
					intent.putExtra("title", "Property SubType");
					String[] array = SharedFunction.getClasification(Integer.parseInt(propertyType));
					intent.putExtra("array", array);
					requestCode = Constants.REQUEST_CLASSIFICATION;
				}
				break;
			case R.id.LocationLayout:
				intent.putExtra("title", "Locations");
				ArrayList<String> locationArray = new ArrayList<String>();
				locationArray.addAll(Arrays.asList(LOCATIONS));
				if(propertyType.equals("2")){
					locationArray.set(1, "By Estate");
				} else {
					locationArray.set(1, "By District");
				}
				String[] tmpList = new String[locationArray.size()];
				intent.putExtra("array", locationArray.toArray(tmpList));
				requestCode = Constants.REQUEST_LOCATION;
				break;
				
			case R.id.BedroomLayout:
				intent.putExtra("title", "Bed Rooms");
				intent.putExtra("array", Constants.BEDROOMS);
				requestCode = Constants.REQUEST_BEDROOM;
				break;
			case R.id.BathroomLayout:
				intent.putExtra("title", "Bath Rooms");
				intent.putExtra("array", Constants.BATHROOMS);
				requestCode = Constants.REQUEST_BATHROOM;
				break;
			case R.id.TenureLayout:
				if(propertyType.equals("")){
					intent = new Intent(getApplicationContext(), STPAlertDialog.class);
					intent.putExtra("message", "Please select property type.");
				}else{
					intent.putExtra("title", "Tenure");
					String[] tenurearray = (propertyType.equals("1") || propertyType.equals("2") ||propertyType.equals("5")) ?
							Constants.TENURERENT : Constants.TENURESALE;
					intent.putExtra("array", tenurearray);
					requestCode = Constants.REQUEST_TENURE;
				}
				break;
			case R.id.PsfLayout:
				intent = new Intent(getApplicationContext(), RangeValues.class);
				intent.putExtra("title", "PSF");
				intent.putExtra("fromArray", Constants.PSFMIN);
				intent.putExtra("toArray", Constants.PSFMAX);
				requestCode = Constants.REQUEST_PSF;
				break;
			case R.id.TopLayout:
				intent.putExtra("title", "Top");
				intent.putExtra("array", Constants.TOP);
				requestCode = Constants.REQUEST_TOP;
				break;
			case R.id.ListedOnLayout:
				intent.putExtra("title", "Listed On");
				intent.putExtra("array", Constants.LISTEDON);
				requestCode = Constants.REQUEST_LISTEDON;
				break;
			case R.id.OptionsLayout:
				intent = new Intent(getApplicationContext(), Options.class);
				requestCode = Constants.REQUEST_OPTIONS;
				break;
			case R.id.SortByLayout:
				intent.putExtra("title", "Sort By");
				intent.putExtra("array", Constants.SORTBYARRAY);
				requestCode = Constants.REQUEST_SORT;
				break;
			case R.id.HdbSchemeLayout:
				intent.putExtra("title", "HDB Scheme");
				intent.putExtra("array", Constants.HDBSCHEME);
				requestCode = Constants.REQUEST_HDBSCHEME;
				break;
			case R.id.HdbCOVLayout:
				intent = new Intent(getApplicationContext(), RangeValues.class);
				intent.putExtra("title", "HDB COV");
				intent.putExtra("fromArray", Constants.COVMIN);
				intent.putExtra("toArray", Constants.COVMAX);
				requestCode = Constants.REQUEST_HDBCOV;
				break;
			case R.id.RoomTypeLayout:
				intent.putExtra("title", "Room Type");
				intent.putExtra("array", Constants.ROOMTYPE);
				requestCode = Constants.REQUEST_ROOMTYPE;
				break;
			case R.id.LeaseTermLayout:
				intent.putExtra("title", "Lease Term");
				intent.putExtra("array", Constants.LEASETERM);
				requestCode = Constants.REQUEST_LEASETERM;
				break;
			default:
				break;
		}
		startActivityForResult(intent, requestCode);
	}
	/**
	 * @param visible	:: Show or hide sale/rent parameters
	 */
	private void clearSearchValues(int visible){
		((LinearLayout)findViewById(R.id.SaleRentLayoutParam)).setVisibility(visible);
		((LinearLayout)findViewById(R.id.RentalSearchParam)).setVisibility(visible == View.GONE ? View.VISIBLE : View.GONE);
		((Helvetica)findViewById(R.id.PriceRange)).setText("Select");
		((Helvetica)findViewById(R.id.LandSize)).setText("Select");
		((Helvetica)findViewById(R.id.PropertyTypeSpinner)).setText("Select");
		((Helvetica)findViewById(R.id.PropertySubType)).setText("Select");
		((Helvetica)findViewById(R.id.Bedroom)).setText("Select");
		((Helvetica)findViewById(R.id.Bathroom)).setText("Select");
		((Helvetica)findViewById(R.id.Tenure)).setText("Select");
		((Helvetica)findViewById(R.id.Top)).setText("Select");
		((Helvetica)findViewById(R.id.Psf)).setText("Select");
		((Helvetica)findViewById(R.id.LeaseTerm)).setText("Select");
		((Helvetica)findViewById(R.id.HdbCOV)).setText("Select");
		((Helvetica)findViewById(R.id.RoomType)).setText("Select");
		((Helvetica)findViewById(R.id.HdbScheme)).setText("Select");
		((Helvetica)findViewById(R.id.ListedOn)).setText("Select");
		((Helvetica)findViewById(R.id.SortBy)).setText("Select");
		((Helvetica)findViewById(R.id.LocationTv)).setText("Select");
		((Helvetica)findViewById(R.id.Options)).setText("Select");
		((LinearLayout)findViewById(R.id.HdbSchemeLayout)).setVisibility(View.GONE);
		((LinearLayout)findViewById(R.id.HdbCOVLayout)).setVisibility(View.GONE);
		propertyType = "";
		minprice = "";
		maxprice = "";
		minBuiltArea = "";
		maxBuiltArea = "";
		clasi = "";
		leaseTerm = "";
		covMin = "";
		covMax = "";
		roomType = "";
		hdbScheme = "";
		bed = "";
		bath = "";
		top = "";
		tennure = "";
		minpsf = "";
		maxpsf = "";
		listedon = "";
		sortby = "";
		options = "";
		district = "";
		searchLatitude = "";
		searchLongitude = "";
	}
	/**
	 * Clear the parameter layouts for room rental search
	 * like bedroom, bathroom etc...
	 */
	private void clearValuesForRental(){
		clearSearchValues(View.GONE);
		sale.setBackgroundResource(R.drawable.un_selected_tab);
		forrent.setBackgroundResource(R.drawable.un_selected_tab);
		forRental.setBackgroundResource(R.drawable.selected_tab);
		propertyWant = "3";
		PropertyList.wantfor = 3;
	}
	
	/**
	 * High lights the sale button and clear all the values
	 *  from parameters and views
	 */
	private void highLightSale(){
		clearSearchValues(View.VISIBLE);				
		sale.setBackgroundResource(R.drawable.selected_tab);
		forrent.setBackgroundResource(R.drawable.un_selected_tab);
		forRental.setBackgroundResource(R.drawable.un_selected_tab);
		propertyWant = "2";
		PropertyList.wantfor = 2;
	}
	/**
	 * High lights the rent button and clear all the values
	 *  from parameters and views
	 */
	private void highlightRent(){
		clearSearchValues(View.VISIBLE);
		sale.setBackgroundResource(R.drawable.un_selected_tab);
		forrent.setBackgroundResource(R.drawable.selected_tab);
		forRental.setBackgroundResource(R.drawable.un_selected_tab);
		propertyWant = "1";
		PropertyList.wantfor = 1;
	}
	/**
	 * This will make serch to happen only 
	 * if the property type is selected.
	 * 
	 * If the location value is set to current location then 
	 * this will launch new activity as dialog to fetch current location
	 * 
	 * And for location tracting this will check for GPS and network connections as well.
	 */
	private void performSearch(){
		if((((TextView)findViewById(R.id.LocationTv)).getText().toString()).equals("Current Location")){
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
	        	Intent intent = new Intent(getApplicationContext(), GetCurrentLocation.class);
				startActivityForResult(intent, Constants.REQUEST_UPDATE_LOCATION);
	        }else{
	        	Intent intent = new Intent(getApplicationContext(), STPAlertDialog.class);
	        	intent.putExtra("message", "Please enable GPS in your device, And search again");
	        	startActivity(intent);
	        }
		} else if(propertyType.equals("")){
			Intent intent = new Intent(getApplicationContext(), STPAlertDialog.class);
        	intent.putExtra("message", "Please choose property type.");
        	startActivity(intent);
		} else{
			searchProperty();
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advanced_search);
		try{
			keyword = (EditText) findViewById(R.id.SearchTerm);			
			Button go = (Button) findViewById(R.id.SearchBtn);
			sale = (Button) findViewById(R.id.ForSaleBtn);
			forrent = (Button) findViewById(R.id.ForRentBtn);
			forRental = (Button) findViewById(R.id.ForRentalBtn);
			saveSearch = (Button) findViewById(R.id.SaveSearchList);
			
			PropertyList.wantfor = 2;
			// Set Onclick listener to the buttons
			forRental.setOnClickListener(onClick);
			saveSearch.setOnClickListener(onClick);
			go.setOnClickListener(onClick);
			sale.setOnClickListener(onClick);
			forrent.setOnClickListener(onClick);
			((RelativeLayout)findViewById(R.id.PropertyType)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.PriceLayout)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.LandSizeLayout)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.ClassificationLayout)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.LocationLayout)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.BedroomLayout)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.BathroomLayout)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.TenureLayout)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.PsfLayout)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.TopLayout)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.ListedOnLayout)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.OptionsLayout)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.SortByLayout)).setOnClickListener(onPickerClick);
			
			((RelativeLayout)findViewById(R.id.LeaseTermLayout)).setOnClickListener(onPickerClick);
			((LinearLayout)findViewById(R.id.HdbCOVLayout)).setOnClickListener(onPickerClick);
			((LinearLayout)findViewById(R.id.HdbSchemeLayout)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.RoomTypeLayout)).setOnClickListener(onPickerClick);
			findViewById(R.id.ResetBtn).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					keyword.setText("");
					clearSearchValues(propertyWant.equals("3") ? View.GONE : View.VISIBLE);
				}
			});
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * Specifically we called this for favorites 
	 * If user favorites or un-favorite the property in detail page and comes back to list screen
	 *  then that has to be reflected in the list-view as well
	 */
	@Override
	protected void onResume() {
		super.onResume();
		postAnalytics();
	}
	private void postAnalytics(){
		// Screen Tracking
        Tracker easyTracker = EasyTracker.getInstance(this);
        easyTracker.set(Fields.SCREEN_NAME, "Property_Search::Property_Home");
        easyTracker.send(MapBuilder.createAppView().build());
        SharedFunction.sendATTagging(getApplicationContext(), "Property_Search::Property_Search_Home", 3, null);
		// Screen Tracking ends
	}
	/**
	 * This will forms the url for search property based on user selection
	 * Google analytic for advanced search 
	 * like
	 * 
	 * Screen Name :: Search Keyword :: property type :: classification
	 * 
	 * Screen Name : Search
	 * Keyword : User input
	 * Property TYpe : like condo, hdb etc...
	 * Classification :: Property sub type
	 */
	private void searchProperty(){
		try{
			String url = "";
			url = "&type=" + propertyType;
			url = url + "&for=" + propertyWant;
			url = url + "&keyword=" + keyword.getText().toString();
			url = url + minprice;
			url = url + maxprice;
			url = url + minBuiltArea;
			url = url + maxBuiltArea;
			url = url + minpsf + maxpsf;
			url = url + bed + bath + tennure + top + listedon + sortby + options;
			url = url + covMin + covMax + leaseTerm + roomType + hdbScheme;
			
	        String analyticsData ="";
	        
	        if(!keyword.getText().toString().equals("")){
	             analyticsData = analyticsData + keyword.getText().toString() + "::";
	        }
	        String wantFor = propertyWant.equals("2") ? "For Sale" : (propertyWant.equals("1") ? "For Rent" : "Room Rental");
	        analyticsData += wantFor + "::" + getViewString(R.id.PropertyTypeSpinner);
	        if(!clasi.equals("")){
	             analyticsData += "::" + getViewString(R.id.PropertySubType);
	             url = url + (propertyType.equals("2") ? "&flat-type=" : "&classification=") + clasi;
	        }
	        if(!district.equals("")){
				url = url + (propertyType.equals("2") ? "&estate=" : "&district=") + district;
			}
			if(!searchLatitude.equals("") && !searchLongitude.equals("")) {
				url = url + "&latitude=" + searchLatitude + "&longitude=" + searchLongitude;
			}
			
			Log.d("Url", url);
			url = url.replace(" ", "%20");
			url = url.replace("+", "");
			
			String screenName = "Properties_" + (propertyWant.equals("2") ? "For_Sale" : (propertyWant.equals("1") ? "For_Rent" : "Room_For_Rent"));
			screenName = screenName + "_" + ((Helvetica)findViewById(R.id.PropertyTypeSpinner)).getText().toString();
			// To show add in list
			PropertyList.propertyTypes = Integer.parseInt(propertyType);
			Intent serachIntent = new Intent(getBaseContext(), SearchPropertyList.class);
			serachIntent.putExtra("url", url);
			serachIntent.putExtra("screenName", screenName);
			startActivity(serachIntent);
			storeSearch(url);
			// Analytics part
			SharedFunction.postAnalytics(SearchTab.this, "Searches", "Listings", analyticsData );
			// GA Custom Dimentions
			List<String[]> values = new ArrayList<String[]>();
	        values.add(new String[] {Constants.ANALYTICS_SEARCHKEYWORDID, keyword.getText().toString()});
	        values.add(new String[] {Constants.ANALYTICS_PROPERTYWANTED, wantFor});
	        values.add(new String[] {Constants.ANALYTICS_PROPERTYCLASSIFICATION, getViewString(R.id.PropertySubType)});
	        values.add(new String[] {Constants.ANALYTICS_PROPERTYDISTRICT, getViewString(R.id.LocationTv)});
	        values.add(new String[] {Constants.ANALYTICS_PROPERTYTYPE, getViewString(R.id.PropertyTypeSpinner)});
	        SharedFunction.sendCustomDimention(SearchTab.this, values, "Property_Search_Result_Page");
	        // AT Internet
			postATInterNet();
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * Post the selected (filtered) values to AT Internet 
	 * analytics with custom variables
	 */
	private void postATInterNet(){
		try{
			// AT Internet tracking
			List<String> customVariables = new ArrayList<String>();
			String wantFor = propertyWant.equals("2") ? "For Sale" : (propertyWant.equals("1") ? "For Rent" : "Room Rental");
			customVariables.add(keyword.getText().toString());
			customVariables.add(wantFor);
			customVariables.add(getViewString(R.id.PropertyTypeSpinner));
			customVariables.add(getViewString(R.id.PropertySubType));
			customVariables.add(getViewString(R.id.LocationTv));
			customVariables.add(getViewString(R.id.Bedroom));
			customVariables.add(getViewString(R.id.Bathroom));
			customVariables.add(getViewString(R.id.Tenure));
			String[] psf = getViewString(R.id.Psf).split("-");
			customVariables.add(psf.length>1 ? psf[0] : "");
			customVariables.add(psf.length>1 ? psf[1] : "");
			customVariables.add(getViewString(R.id.Top));
			customVariables.add(getViewString(R.id.Options));
			customVariables.add(getViewString(R.id.SortBy));
	        SharedFunction.sendATTagging(getApplicationContext(), "Search_Result_Page", 3, customVariables);
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * @param id 	:: TextView Id
	 * @return		:: TextView String values to store for refer back option
	 */
	private String getViewString(int id){
		String val = ((Helvetica) findViewById(id)).getText().toString();
		return val.equalsIgnoreCase("Select") ? "" : val;
	}
	
	/**
	 * @param url	:: Search URL for history 
	 * Store all the user input and selection values into database for feature purpose 
	 * This will be used in view saved search
	 */
	private void storeSearch(String url){
		try{
			String wantFor = propertyWant.equals("2") ? "For Sale" : (propertyWant.equals("1") ? "For Rent" : "Room Rental");
			DatabaseHelper dataBase = new DatabaseHelper(getApplicationContext());
			dataBase.addSearch(SharedFunction.getWeekDay(),  keyword.getText().toString(), getViewString(R.id.Bedroom),
					getViewString(R.id.Bathroom), getViewString(R.id.Top), getViewString(R.id.Tenure),
					wantFor, getViewString(R.id.PropertyTypeSpinner), getViewString(R.id.PropertySubType),
					getViewString(R.id.LocationTv), getViewString(R.id.Psf), getViewString(R.id.PriceRange),
					getViewString(R.id.LandSize), getViewString(R.id.ListedOn), getViewString(R.id.SortBy), url,
					getViewString(R.id.HdbCOV), getViewString(R.id.HdbScheme), getViewString(R.id.LeaseTerm), getViewString(R.id.RoomType));
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * On back press the app will launch the home screen as fresh activity
	 * 
	 * clears all the top saved instances and launches the new activity
	 * 
	 */
	@Override
	public void onBackPressed() {
		try {
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * @param id	:: Property type id (index)
	 * 
	 * Will reset the input parameters based on the property type.
	 * 
	 * like classification (Sub type), District or estate
	 * Estate for HDB and District for all other properties
	 * 
	 * But we need to pass the district values or estate value as 0 for un-set parameters
	 */
	private void setPropertyTypeValues(int id){
		String[] propertyArray = propertyWant.equals("3") ? Constants.RENTAL_PROPERTYTYPE : Constants.PROPERTYTYPE;
		propertyType = SharedFunction.getPropertyTypeId(id+1) + "";
		clasi = "";
		minBuiltArea = "";
		maxBuiltArea = "";
		district = "";
		searchLatitude = "";
		searchLongitude = "";
		hdbScheme = "";
		covMax = "";
		covMin = "";
		((Helvetica)findViewById(R.id.PropertyTypeSpinner)).setText(propertyArray[id]);
		((Helvetica)findViewById(R.id.LandSize)).setText("Select");
		((Helvetica)findViewById(R.id.PropertySubType)).setText("Select");
		((Helvetica)findViewById(R.id.LocationTv)).setText("Select");
		if(propertyType.equals("3") || propertyType.equals("4") || propertyType.equals("6") || propertyType.equals("7")){
			((Helvetica)findViewById(R.id.Bedroom)).setText("Select");
			((Helvetica)findViewById(R.id.Bathroom)).setText("Select");
			bed = "";
			bath = "";
			((LinearLayout)findViewById(R.id.residetialParams)).setVisibility(View.GONE);
		}else{
			((LinearLayout)findViewById(R.id.residetialParams)).setVisibility(View.VISIBLE);
		}
		if(propertyType.equals("2")){
			((LinearLayout)findViewById(R.id.HdbSchemeLayout)).setVisibility(View.VISIBLE);
			((LinearLayout)findViewById(R.id.HdbCOVLayout)).setVisibility(propertyWant.equals("2") ? View.VISIBLE : View.GONE);
		} else{
			((LinearLayout)findViewById(R.id.HdbSchemeLayout)).setVisibility(View.GONE);
			((LinearLayout)findViewById(R.id.HdbCOVLayout)).setVisibility(View.GONE);
		}
	}
	/**
	 * 
	 * @param id	:: Index value for location based search
	 * 
	 * If the index is 0 then current location is enabled to search
	 * or if index is one then the district array is passed to picker for user to select
	 * otherwise this will check for the type of request like
	 * 	school, mrt, mall etc..
	 * 	based on this this will fetch array from server and displays to user for selection
	 * 
	 */
	private void requestLocationValue(int id){
		searchLatitude = "";
		searchLongitude = "";
		district = "";
		nearByPosition = -1;
		if(id == 0){	
			((TextView)findViewById(R.id.LocationTv)).setText("Current Location");
		} else if(id == 1){
			String[] districtArry = propertyType.equals("2") ? Constants.ESTATE : Constants.DISTRICTS;
			Intent intent = new Intent(getApplicationContext(), ListPicker.class);
			intent.putExtra("title", propertyType.equals("2") ?"Estate":"District");
			intent.putExtra("array", districtArry);
			startActivityForResult(intent, Constants.REQUEST_SEARCH_DISTRICT );
		} else{
			nearByPosition = id;
			if(id == 2){
				nearByUrl = UrlUtils.URL_NEARBY_AMI + "school";
			} else if(id == 3){
				nearByUrl = UrlUtils.URL_NEARBY_AMI + "mrt";
			} else if(id == 4){
				nearByUrl = UrlUtils.URL_NEARBY_AMI + "mall";
			} else if(id == 5){
				nearByUrl = UrlUtils.URL_NEARBY_AMI + "childcare";
			}
			getStringArrayFromUrl();
			LOCATIONS[1] = "By District";
		}
	}
	/***
	 * @Param requestCode : Request code to identify the calling function
	 * @param resultCode
	 *            : Response returned from the called function RESULT_OK or
	 *            RESULT_CANCEL
	 * @param data
	 *            : Is an intent extra values passed from called function
	 * 
	 * Based on the request code this will call switch case to perform 
	 * required function or values assignment.
	 * 
	 * Like 
	 * 1. Price range
	 * 2. Land or floor area
	 * 3. Property type
	 * 4. Property subtype
	 * 5. district or estate
	 * 6. location etc...
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try{
			if(resultCode==RESULT_OK){
				if(requestCode == Constants.REQUESTCODE_SETTINGS){
					getStringArrayFromUrl();
				}else{
					int id = data.getIntExtra("id",0);
					switch (requestCode) {
					case Constants.REQUEST_PROPERTYTYPE:
						setPropertyTypeValues(id);
						break;
					case Constants.REQUEST_CLASSIFICATION:
						clasi = SharedFunction.getClasificationId(propertyType+"-"+(id+1))+"";
						String[] array = SharedFunction.getClasification(Integer.parseInt(propertyType));
						((Helvetica)findViewById(R.id.PropertySubType)).setText(array[id]);
						break;
					case Constants.REQUEST_LOCATION:
						requestLocationValue(id);
						break;
					case Constants.REQUEST_PRICE:
						String[] fromarray = propertyWant.equals("2") ? Constants.SALEMINPRICE : (propertyWant.equals("1") ? Constants.RENTMINPRICE : Constants.RENTAL_MINPRICE);
						String[] toArrray = propertyWant.equals("2") ? Constants.SALEMAXPRICE : (propertyWant.equals("1") ? Constants.RENTMAXPRICE : Constants.RENTAL_MAXPRICE);
						String priceText = fromarray[data.getIntExtra("fromId", 0)] + " - "  + toArrray[data.getIntExtra("toId", 0)];
						((Helvetica)findViewById(R.id.PriceRange)).setText(priceText);
						minprice = "&min-price="+fromarray[data.getIntExtra("fromId", 0)].replaceAll("[\\,\\$A-za-z]", "");
						maxprice = "&max-price="+toArrray[data.getIntExtra("toId", 0)].replaceAll("[\\,\\$A-za-z]", "");
						break;
					case Constants.REQUEST_LANDSIZE:
						String[] floorValue = propertyType.equals("2") ? Constants.FLOORAREA : Constants.LANDAREA;
						((Helvetica)findViewById(R.id.LandSize)).setText(floorValue[data.getIntExtra("fromId", 0)] + " - " + floorValue[data.getIntExtra("toId", 0)]);
						minBuiltArea = "&min-built-in-area="+floorValue[data.getIntExtra("fromId", 0)].replaceAll("[\\,\\$A-za-z]", "");
						maxBuiltArea = "&max-built-in-area="+floorValue[data.getIntExtra("toId", 0)].replaceAll("[\\,\\$A-za-z]", "");
						break;
					case Constants.REQUEST_UPDATE_LOCATION:
						searchLatitude = data.getStringExtra("latitude");
						searchLongitude = data.getStringExtra("longitude");
						searchProperty();
						break;
					case Constants.REQUEST_BEDROOM:
						bed = id ==0 ? "" : "&bedrooms=" + (id-1);
						((Helvetica)findViewById(R.id.Bedroom)).setText(Constants.BEDROOMS[id]);
						break;
					case Constants.REQUEST_BATHROOM:
						bath = id ==0 ? "" : "&bathrooms=" + id;
						((Helvetica)findViewById(R.id.Bathroom)).setText(Constants.BATHROOMS[id]);
						break;
					case Constants.REQUEST_TENURE:
						tennure = "&tenure="+(id+1);
						String[] tenurearray = (propertyType.equals("1") || propertyType.equals("2") ||propertyType.equals("5")) ? Constants.TENURERENT : Constants.TENURESALE;
						((Helvetica)findViewById(R.id.Tenure)).setText(tenurearray[id]);
						break;
					case Constants.REQUEST_PSF:
						String psfText = Constants.PSFMIN[data.getIntExtra("fromId", 0)] + " - "  + Constants.PSFMAX[data.getIntExtra("toId", 0)];
						((Helvetica)findViewById(R.id.Psf)).setText(psfText);
						minpsf = "&min-psf="+Constants.PSFMIN[data.getIntExtra("fromId", 0)].replaceAll("[\\,\\$A-za-z]", "");
						maxpsf = "&max-psf="+Constants.PSFMAX[data.getIntExtra("toId", 0)].replaceAll("[\\,\\$A-za-z]", "");
						break;
					case Constants.REQUEST_TOP:
						top = "&top="+(id+1);
						((Helvetica)findViewById(R.id.Top)).setText(Constants.TOP[id]);
						break;
					case Constants.REQUEST_LISTEDON:
						listedon = "&listedon="+(id+1);
						((Helvetica)findViewById(R.id.ListedOn)).setText(Constants.LISTEDON[id]);
						break;
					case Constants.REQUEST_SORT:
						sortby = "&sortby="+SharedFunction.getSortBy(id);
						((Helvetica)findViewById(R.id.SortBy)).setText(Constants.SORTBYARRAY[id]);
						break;
					case Constants.REQUEST_OPTIONS:
						options = data.getStringExtra("value");
						String optionStr = data.getStringExtra("options");
						((Helvetica)findViewById(R.id.Options)).setText(optionStr);
						break;
					case Constants.REQUEST_SEARCH_DISTRICT:
						district = (id+1)+"";
						String[] districtArryNew = propertyType.equals("2") ? Constants.ESTATE : Constants.DISTRICTS;
						((Helvetica)findViewById(R.id.LocationTv)).setText(districtArryNew[id]);
						break;
					case Constants.REQUEST_SEARCH_OTHERS:
						((Helvetica)findViewById(R.id.LocationTv)).setText(searchedTmpList[id]);
						searchLatitude = locationDetails.get(id).getLat();
						searchLongitude = locationDetails.get(id).getLng();
						break;
					case Constants.REQUEST_LEASETERM:
						leaseTerm = "&lease=" + (id+1);
						((Helvetica)findViewById(R.id.LeaseTerm)).setText(Constants.LEASETERM[id]);
						break;
					case Constants.REQUEST_ROOMTYPE:
						roomType = "&room-type=" + (id+1);
						((Helvetica)findViewById(R.id.RoomType)).setText(Constants.ROOMTYPE[id]);
						break;
					case Constants.REQUEST_HDBSCHEME:
						hdbScheme = "&hdbscheme=" + (id == 0?1:4);
						((Helvetica)findViewById(R.id.HdbScheme)).setText(Constants.HDBSCHEME[id]);
						break;
					case Constants.REQUEST_HDBCOV:
						String covString = Constants.COVMIN[data.getIntExtra("fromId", 0)] + " - " + Constants.COVMAX[data.getIntExtra("toId", 0)];
						((Helvetica)findViewById(R.id.HdbCOV)).setText(covString);
						covMin = "&min-cov="+Constants.COVMIN[data.getIntExtra("fromId", 0)].replaceAll("[\\,\\$A-za-z]", "");
						covMax = "&max-cov="+Constants.COVMAX[data.getIntExtra("toId", 0)].replaceAll("[\\,\\$A-za-z]", "");
						break;
					default:
						break;
					}
				}
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * 
	 *	Location handler for location tracting
	 */
	private class LocationHelper {
		private String lat,lng;
	
		public String getLat() {
			return lat;
		}
	
		public void setLat(String lat) {
			this.lat = lat;
		}
	
		public String getLng() {
			return lng;
		}
	
		public void setLng(String lng) {
			this.lng = lng;
		}

	}
	/**
	 * Get the Near By Values from server to user selection
	 */
	private void getStringArrayFromUrl(){
		final ProgressDialog processdialog = ProgressDialog.show(SearchTab.this, "", "Loading...", true);
		processdialog.show();
		if(ConnectionCheck.checkOnline(SearchTab.this)){
			
			ConnectionManager test = new ConnectionManager();
			try{
				test.connectionHandler(SearchTab.this, null, nearByUrl, ConnectionType.CONNECTIONTYPE_GET, null,
					new AsyncHttpResponseHandler() {
					private String responseMsg;
					@Override
					public void onSuccess(String response) {
						responseMsg = response;
					}
					@Override
					public void onFinish(){
						loadAminities(responseMsg);
						processdialog.dismiss();
					}
	
					@Override
					public void onFailure(Throwable error) {
						processdialog.dismiss();
				    	updateSettings(Constants.LOWINTERNETSTR);
					}
				});
			}catch(Exception e){
				Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
				processdialog.dismiss();
			}
		}else{
			processdialog.dismiss();
			updateSettings(Constants.NETWORKSTR);
		}
				
	}
	private void loadAminities(String responseMsg){
		String value1="";
		try{
			JSONObject jsonObj = new JSONObject(responseMsg).getJSONObject("result");
			Intent intent = new Intent(getApplicationContext(), ListPicker.class);
			int totalAminities = jsonObj.getInt("total_amenities");
			switch (nearByPosition) {
			case 2:
				value1="school";
				intent.putExtra("title", "Near School");
				break;
			case 3:
				value1="mrt";
				intent.putExtra("title","Near MRT");
				break;
			case 4:
				value1="mall";
				intent.putExtra("title","Near Shopping Mall");
				break;
			case 5:
				value1="childcare";
				intent.putExtra("title","Near Child Care");
				break;
			default:
				break;
			}
			searchedTmpList = null;
			locationDetails.clear();
			if(totalAminities > 0){
				JSONObject subObj = jsonObj.getJSONObject("amenities");
				JSONArray cast = subObj.getJSONArray(value1);
				List<String> returnedArray = new ArrayList<String>();
				for (int i=0; i<cast.length(); i++) {
					LocationHelper l  = new LocationHelper();
				    JSONObject actor = cast.getJSONObject(i);
				    String name = actor.getString("name");
				    l.setLat(actor.getString("lat"));
				    l.setLng(actor.getString("lng"));
				    locationDetails.add(l);
				    returnedArray.add(name); 
				}
				
				intent.putExtra("array", returnedArray.toArray(new String[0]));
				searchedTmpList = returnedArray.toArray(new String[0]);
				startActivityForResult(intent,Constants.REQUEST_SEARCH_OTHERS );	
			}
		}catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	@Override
	public void checkUpdate() {
		//  Auto-generated method stub
	}
}
