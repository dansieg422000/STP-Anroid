package com.z.stproperty.shared;

import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.at.ATParams;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.z.stproperty.R;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.fonts.HelveticaBold;

public class SharedFunction {
	private static final List<String> HIGHTLIGHT = new ArrayList<String>(){
		private static final long serialVersionUID = 8391303353560802702L;
		{
			add("priority");
			add("corporate");
			add("platinum");
			add("premium");
			add("featuredagent");
		}
	};
	private SharedFunction(){
		// private constructor to hide implicit public one
	}
	/***
	 * MD5 values for st701 plus string Current date
	 * Getting current date and time to calculate md5 string to pass (Server)
	 * This function will get the string values and converts this to hex values and return back
	 * If it encounter any exception then null value goes back
	 * 
	 * @return MD5 values is returned to calling class
	 */
	@SuppressLint("SimpleDateFormat")
	public static final String getHashKey(){
		String s = new SimpleDateFormat("ddMMyyyy").format(new Date())
				.toString();
		s = "st701" + s;
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(s.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
			Log.e("getHashKey", e.getLocalizedMessage(), e);
		}
		return s;
	}
	/***
	 * MD5 values for st701 plus string Current date
	 * Getting current date and time to calculate md5 string to pass (Server)
	 * This function will get the string values and converts this to hex values and return back
	 * If it encounter any exception then null value goes back
	 * 
	 * @return MD5 values is returned to calling class
	 */
	@SuppressLint("SimpleDateFormat")
	public static final String getHashKeyWithId(String id){
		String s = new SimpleDateFormat("ddMMyyyy").format(new Date())
				.toString();
		s = "st701" + s + id;
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(s.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
			Log.e("getHashKey", e.getLocalizedMessage(), e);
		}
		return s;
	}
	 
	 /**
	  * 
	  * @param value
	  * 	Price value
	  * @return
	  * 	Price string with formated
	  * 
	  * Throughout the application we are showing the price in 
	  * 000,000,000.00 format 
	  * 
	  * getPriceWithComma() will give us the correct formated price string to display
	  * 
	  */
	 public static String getPriceWithComma(String value){
		 NumberFormat formatter = new DecimalFormat("###,###,###.##");
		 return formatter.format(Double.parseDouble(value));
	 }
	 
	 /**
		 * 
	 * @param index 
	 * 		Index value of an array
	 * @return
	 * 		String value for the index
	 * 
	 * In Application we are showing the names in different (Full name)
	 * But when we passing the parameter values
	 * that time we need to pass the correct one to server
	 * 
	 * Based on the index (selected by user) the corresponding sort by value will be sent back
	 * 
	 * Default value is PRIORITY
	 * 
	 */
	public static String getSortBy(int index){
		switch (index) {
		case 0:
			return "date-desc";
		case 1:
			return "date-asc";
		case 2:
			return "price-desc";
		case 3:
			return "price-asc";
		case 4:
			return "title-desc";
		case 5:
			return "title-asc";
		case 6:
			return "psf-desc";
		case 7:
			return "psf-asc";
		default:
			return "priority";
		}
    }
	/**
	  * @param url : Image location
	  * @return : Will decodes the bitmap images stream from server and sends bitmap as return value
	  */
	public static Bitmap loadBitmap(String url) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
		} catch (Exception e) {
			Log.e("loadBitmap", e.getLocalizedMessage(), e);
		}
		return bitmap;
	}
	/**
	 * @param propertyType	:: property type to differently highlight the pin in map
	 * @return
	 */
	public static int loadMapPin(String propertyType){
		int drawableResource = R.drawable.business;
		try{
			if(propertyType.equalsIgnoreCase("condo")){
				drawableResource = R.drawable.condo; 
			}else if(propertyType.equalsIgnoreCase("HDB") || propertyType.equalsIgnoreCase("HUDC") 
					|| propertyType.equalsIgnoreCase("HDB/HUDC")){
				drawableResource = R.drawable.hdb;
			}else if(propertyType.equalsIgnoreCase("landed")){
				drawableResource = R.drawable.landed;
			}else if(propertyType.equalsIgnoreCase("Industrial")){
				drawableResource = R.drawable.industrial;
			}else if(propertyType.equalsIgnoreCase("land")){
				drawableResource = R.drawable.land;
			}else if(propertyType.equalsIgnoreCase("Office")){
				drawableResource = R.drawable.office;
			}else if(propertyType.equalsIgnoreCase("Retail")){
				drawableResource = R.drawable.business;
			}
		}catch(Exception e){
			Log.e("loadMapPin", e.getLocalizedMessage(), e);
		}
		return drawableResource;
	}
	/**
	 * 
	 * @param propertyJson :: JSON value returned from server or from database
	 * @param propertyTypes :: property type to show o hide bedroom and bathroom 
	 * @return :: MashMap value that contains the whole data passed to this function.
	 * 
	 * This function been called from different activities to show properties and in-order 
	 * to reduce code complex and improve the code quality we made this as separate function to parse json to map values
	 */
	public static Map<String, String> getProperty(JSONObject propertyJson, int propertyTypes){
		Map<String, String> map = new HashMap<String, String>();
		try{
			int floorarea = 0;
			String datepsoted;
			if (propertyJson.get("built-in_area").toString().equals("null")) {
				floorarea = 0;
			} else{
				floorarea = propertyJson.getInt("built-in_area");
			}
			
			String price = propertyJson.getString("price").equals("null") ? "" : propertyJson.getString("price");
			datepsoted = propertyJson.get("date_posted").toString();
			String psf = propertyJson.getString("psf");
			psf = psf.equals("null") ||  psf.equals("-") || psf.equals("0") ? "-" : (psf.contains("SGD") ? psf : ("SGD " + psf));
			String bedroom = "", shower = "";
			if (propertyTypes == 3 || propertyTypes == 4) {
				bedroom = "-";
				shower = "-";
			} else {
				String bedStr = propertyJson.getString("bedrooms");
				bedroom = bedStr.equals("null") || bedStr.equals("0") ? "-" : bedStr;
				String bathStr = propertyJson.get("bathroom").toString();
				shower = bathStr.equals("null") || bathStr.equals("0") ? "-" : bathStr;
			}
			map.put("title", propertyJson.getString("property_title"));
			map.put("classification", propertyJson.getString("classification"));
			map.put("property_type", propertyJson.getString("property_type"));
			map.put("floorarea", floorarea + "");
			map.put("price", price);
			map.put("bedroom", bedroom);
			map.put("photo", (String) propertyJson.getString("thumbnail"));
			map.put("shower", shower);
			map.put("type", propertyJson.getString("property_for"));
			map.put("longitude", propertyJson.getString("longitude"));
			map.put("latitude", propertyJson.getString("latitude"));
			map.put("pID", propertyJson.getString("id"));
			map.put("dateposted", datepsoted);
			map.put("psf", psf);
			String priceOption = propertyJson.getString("price_option");
			map.put("price_option", priceOption.equals("") || priceOption.equals("null") ? "Price on Ask" : priceOption);
			JSONArray propertyHighLight = propertyJson.optJSONArray("property_highlights");
			if(propertyHighLight == null){
				map.put("property_highlights", propertyJson.getString("property_highlights"));
			}else{
				map.put("property_highlights", propertyHighLight.length()>0 ? getPrpertyHighlight(propertyHighLight) : "");
			}
		}catch(Exception e){
			Log.e("getProperty", e.getLocalizedMessage(), e);
		}
		return map;
	}
	private static String getPrpertyHighlight(JSONArray array){
		try{
			for(int index=0; index<array.length(); index++){
				if(HIGHTLIGHT.contains(array.getString(index))){
					return array.getString(index);
				}
			}
		}catch(Exception e){
			Log.e("getPrpertyHighlight", e.getLocalizedMessage(), e);
		}
		return "";
	}
	
	/**
	  * 
	  * @param key 
	  * 	Key value to get classification id
	  * @return
	  * 	classification id
	  * 
	  * key :: property listing type id - sub-type index
	  * 
	  * based these combination the classification id is returned.
	  * 
	  * Note : Integer.parseInt("1")
	  * 	We can assign the integer number directly
	  * 	But when we are validating in sonar this gives a magic number error
	  * 	in-order to avoid this error we are parsing the string equal value of number 
	  */
	 public static int getClasificationId(String key){
		 Map<String, Integer> clasificationId = new HashMap<String, Integer>();
		 clasificationId.put("2-1",Integer.parseInt("1"));
		 clasificationId.put("2-2",Integer.parseInt("2"));
		 clasificationId.put("2-3",Integer.parseInt("3"));
		 clasificationId.put("2-4",Integer.parseInt("4"));
		 clasificationId.put("2-5",Integer.parseInt("5"));
		 clasificationId.put("2-6",Integer.parseInt("34"));
		 clasificationId.put("2-7",Integer.parseInt("35"));
		 clasificationId.put("2-8",Integer.parseInt("36"));
		 clasificationId.put("1-1",Integer.parseInt("6"));
		 clasificationId.put("1-2",Integer.parseInt("7"));
		 clasificationId.put("1-3",Integer.parseInt("8"));
		 clasificationId.put("1-4",Integer.parseInt("9"));
		 clasificationId.put("1-5",Integer.parseInt("10"));
		 clasificationId.put("5-1",Integer.parseInt("11"));
		 clasificationId.put("5-2",Integer.parseInt("12"));
		 clasificationId.put("5-3",Integer.parseInt("13"));
		 clasificationId.put("5-4",Integer.parseInt("14"));
		 clasificationId.put("5-5",Integer.parseInt("15"));
		 clasificationId.put("5-6",Integer.parseInt("16"));
		 clasificationId.put("5-7",Integer.parseInt("17"));
		 clasificationId.put("5-8",Integer.parseInt("18"));
		 clasificationId.put("5-9",Integer.parseInt("19"));
		 clasificationId.put("5-10",Integer.parseInt("20"));
		 clasificationId.put("6-1",Integer.parseInt("21"));
		 clasificationId.put("6-2",Integer.parseInt("22"));
		 clasificationId.put("7-1",Integer.parseInt("23"));
		 clasificationId.put("7-2",Integer.parseInt("24"));
		 clasificationId.put("7-3",Integer.parseInt("25"));
		 clasificationId.put("7-4",Integer.parseInt("26"));
		 clasificationId.put("7-5",Integer.parseInt("27"));
		 clasificationId.put("3-1",Integer.parseInt("28"));
		 clasificationId.put("3-2",Integer.parseInt("29"));
		 clasificationId.put("3-3",Integer.parseInt("30"));
		 clasificationId.put("3-4",Integer.parseInt("31"));
		 clasificationId.put("4-1",Integer.parseInt("32"));
		 clasificationId.put("4-2",Integer.parseInt("33"));
		 return clasificationId.get(key);
	 }
	 /**
	  * 
	  * @param conext
	  * 	Base application context
	  * 
	  * @param id
	  * 	Parent type id
	  * 
	  * @return
	  * 	Clarification array (sub-types)
	  * 
	  * Each and every property type has its own sub-types 
	  * 
	  * Based on the parent type this function 
	  * will return the sub-types array as classification
	  * 
	  */
	 public static String[] getClasification(int id){
		 String[] array=null;
		 switch (id) {
			 case 0:
				 array = new String[]{};
				 break;
			case Constants.ONE:
				array = Constants.CONDOCLASSIFICATION;
				break;
			case Constants.SEVEN:
				array = Constants.RETAILCLASIFICATION;
				break;
			case Constants.TWO:
				array = Constants.HDBCLASSIFICATION;
				break;
			case Constants.THREE:
				array = Constants.INDUSCLASSIFICATION;
				break;
			case Constants.FOUR:
				array = Constants.LANDCLASIFICATION;
				break;
			case Constants.FIVE:
				array = Constants.LANDEDCLASSIFICATION;
				break;
			case Constants.SIX:
				array = Constants.OFFICECLASIFICATION;
				break;
			default:
				break;
			}
		 return array;
	 }
	 /**
	 * 
	 * @param id :: property type array index
	 * @return
	 * 		Property Type Id
	 * 
	 * Based on the array index the property ID is 
	 * returned back
	 * 
	 */
	public static int getPropertyTypeId(int id){
		return (id == 0 || id == Constants.ONE || id == Constants.SIX || id == Constants.SEVEN) ? id : (id == Constants.TWO ? Constants.FIVE : (id-Constants.ONE));
	}
	/**
	 * @return	:: Current Week Day String 
	 */
	public static String getWeekDay(){
		SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.US);
		Date curDate = new Date();
		return dayFormat.format(curDate);
	}
	/**
	 * 
	 * @param priority	 :: priority type of property
	 * @return :: resource id to display on the row
	 * 
	 * All properties has its own priority and features, 
	 * in-order to highlight that we made this function
	 * and this is been globally called in this application
	 */
	public static int getPriorityImage(String priority){
		if(priority.equalsIgnoreCase("priority")){
			return R.drawable.priority;
		}else if(priority.equalsIgnoreCase("corporate")){
			return R.drawable.corporate;
		}else if(priority.equalsIgnoreCase("featured") || priority.equalsIgnoreCase("featuredagent")){
			return R.drawable.featured;
		}else if(priority.equalsIgnoreCase("platinum")){
			return R.drawable.platinum;
		}else if(priority.equalsIgnoreCase("premium")){
			return R.drawable.premium;
		}else{
			return 0;
		}
	}
	/**
	 * @param context	:: BaseApplicationContext
	 * @param category	:: Category
	 * @param action	:: Action
	 * @param label		:: Label to display
	 */
	 public static void postAnalytics(Context context, String category,String action, String label ){
         try{
              EasyTracker easyTracker = EasyTracker.getInstance(context);
              easyTracker.send(MapBuilder
                         .createEvent(category, action, label, null) 
                         .build());
         } catch(Exception e){
              Log.e(context.getClass().getSimpleName(), e.getMessage(), e);
         }
         
    }
	 /**
	  * 
	  * This posts the custom variables details to google analytics.
	  * Here, the first array value(values[0]) is the id and second 
	  * is the value.
	  * @param context
	  * @param screenName
	  */
	public static void sendCustomDimention(Context context,List<String[]> values, String screenName){
		try{
			 // Sending a screenview in v3 using MapBuilder.
			 Tracker tracker = GoogleAnalytics.getInstance(context).getTracker(context.getResources().getString(R.string.ga_trackingId));
			 tracker.set(Fields.SCREEN_NAME, screenName);
			 for(String[] val:values){
	       	  	if(!val[1].equals("")){
	       	  		tracker.send(MapBuilder.createAppView()
	     			   .set(val[0], val[1]).build());
	       	  	}
	         }
		} catch(Exception e){
	        Log.e(context.getClass().getSimpleName(), e.getMessage(), e);
		}
	 }
	 /**
	  * 
	  * @param context
	  * @param inflater
	  * @param view
	  * @param googleMap
	  * @param detailsJson
	  * @param inflat
	   * 		:: Inflater
	   * @param googleMap
	   * 		:: Google map to draw map pins
	   * @param mapScrollView
	   * 		:: Map scroll view
	  * @return
	  */
	  public static View addMapPins(Context context, LayoutInflater inflater, View view, GoogleMap googleMap,
				JSONObject detailsJson, ScrollView mapScrollView){
			try{
				boolean multiplePin = false;
				String latitude="",longitude="", title = "";
				int mapIcon = R.drawable.mappin;
				if(detailsJson.has("latitude")){
					latitude = detailsJson.getString("latitude");
					longitude = detailsJson.getString("longitude");
					title = detailsJson.getString("name");
				}else{
					latitude = detailsJson.getString("map_latitude");
					longitude = detailsJson.getString("map_longitude");
					title = detailsJson.getString("property_title");
					mapIcon = SharedFunction.loadMapPin(detailsJson.getString("property_type"));
				}
				
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				Marker marker = null;
				Bitmap icon = BitmapFactory.decodeResource(context.getResources(), mapIcon);
				marker = googleMap.addMarker(new MarkerOptions()
						.title(title)
						.position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
						.icon(BitmapDescriptorFactory.fromBitmap(icon)));
				builder.include(marker.getPosition());
				LinearLayout nearByAminities = (LinearLayout) view.findViewById(R.id.NearByAminites);
				boolean hasNoData = false;
				if(!detailsJson.getString("nearby_amenities").equals("[]")){
					JSONObject arrChildelements = detailsJson.getJSONObject("nearby_amenities");
					multiplePin = true;
					if(arrChildelements.has("mall")){
						JSONArray nearByArr = arrChildelements.getJSONArray("mall");
						List<Marker> markerArray = new ArrayList<Marker>();
						
						for(int index=0;index<nearByArr.length();index++){
							JSONObject nearByMall = nearByArr.getJSONObject(index);
							icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.mall);
							marker = googleMap.addMarker(new MarkerOptions().title(nearByMall.getString("name") + " (" + nearByMall.get("distance").toString() + " km)")
											.position(new LatLng(Double.parseDouble(nearByMall.getString("lat")),Double.parseDouble(nearByMall.getString("lng"))))
											.icon(BitmapDescriptorFactory.fromBitmap(icon)));
							builder.include(marker.getPosition());
							markerArray.add(marker);
						}
						addnearByView(nearByAminities, nearByArr, "NEAREST SHOPPING MALLS", R.drawable.mall, markerArray,
								inflater, googleMap, mapScrollView);
					}
					if(arrChildelements.has("childcare")){
						JSONArray nearByArr = arrChildelements.getJSONArray("childcare");
						List<Marker> markerArray = new ArrayList<Marker>();
						
						for(int index=0;index<nearByArr.length();index++){
							JSONObject nearByMall = nearByArr.getJSONObject(index);
							icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.daycare);
							marker = googleMap.addMarker(new MarkerOptions().title(nearByMall.getString("name") + " (" + nearByMall.get("distance").toString() + " km)")
											.position(new LatLng(Double.parseDouble(nearByMall.getString("lat")),Double.parseDouble(nearByMall.getString("lng"))))
											.icon(BitmapDescriptorFactory.fromBitmap(icon)));
							builder.include(marker.getPosition());
							markerArray.add(marker);
						}
						addnearByView(nearByAminities, nearByArr, "NEAREST CHILDCARE CENTRES", R.drawable.daycare, markerArray,
								inflater, googleMap, mapScrollView);
					}
					if(arrChildelements.has("school")){
						JSONArray nearByArr = arrChildelements.getJSONArray("school");
						List<Marker> markerArray = new ArrayList<Marker>();
						
						for(int index=0;index<nearByArr.length();index++){
							JSONObject nearByMall = nearByArr.getJSONObject(index);
							icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.school);
							marker = googleMap.addMarker(new MarkerOptions().title(nearByMall.getString("name") + " (" + nearByMall.get("distance").toString() + " km)")
											.position(new LatLng(Double.parseDouble(nearByMall.getString("lat")),Double.parseDouble(nearByMall.getString("lng"))))
											.icon(BitmapDescriptorFactory.fromBitmap(icon)));
							builder.include(marker.getPosition());
							markerArray.add(marker);
						}
						addnearByView(nearByAminities, nearByArr, "NEAREST SCHOOLS", R.drawable.school, markerArray,
								inflater, googleMap, mapScrollView);
					}
					if(arrChildelements.has("mrt")){
						JSONArray nearByArr = arrChildelements.getJSONArray("mrt");
						List<Marker> markerArray = new ArrayList<Marker>();
						
						for(int index=0;index<nearByArr.length();index++){
							JSONObject nearByMall = nearByArr.getJSONObject(index);
							icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.train);
							marker = googleMap.addMarker(new MarkerOptions().title(nearByMall.getString("name") + " (" + nearByMall.get("distance").toString() + " km)")
											.position(new LatLng(Double.parseDouble(nearByMall.getString("lat")),Double.parseDouble(nearByMall.getString("lng"))))
											.icon(BitmapDescriptorFactory.fromBitmap(icon)));
							builder.include(marker.getPosition());
							markerArray.add(marker);
						}
						addnearByView(nearByAminities, nearByArr, "NEAREST MRT STATIONS", R.drawable.train, markerArray,
								inflater, googleMap, mapScrollView);
					}
				} else {
					builder = new LatLngBounds.Builder();
					builder.include(new LatLng(1.366700, 103.800000));
					hasNoData = true;
					googleMap.clear();
					CameraPosition cameraPosition = new CameraPosition.Builder().target(
		                    new LatLng(1.366700, 103.800000)).zoom(10)
		                    .tilt(12).build();
					// Creates a CameraPosition from the builder
					googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					//googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(1.300000, 103.800000)));
					Helvetica noData = new Helvetica(context);
					noData.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
					noData.setBackgroundColor(Color.WHITE);
					noData.setTextColor(Color.BLACK);
					noData.setText("No Data");
					nearByAminities.addView(noData, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
				}
				if(!multiplePin && !hasNoData){
					builder.include(new LatLng(marker.getPosition().latitude+0.093,marker.getPosition().longitude+0.093));
				}
				if(!hasNoData){
				LatLngBounds bounds = builder.build();
				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 300, 300, 0);
				googleMap.animateCamera(cu);
				}
				
			}catch(Exception e){
				Toast.makeText(context, "Property map details not found.", Toast.LENGTH_LONG).show();
				Log.e(context.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			}
			return view;
		}
	  /**
	   * 
	   * @param nearByAminities
	   * 			:: Json Values.
	   * @param nearByArr
	   * 			:: The layout where the values need to be entered.
	   * @param head
	   *           :: The header text
	   * @param imageId
	   * 		:: The image-id of the pin to be placed in the list.
	   * @param markerArray
	   * 		:: Marker array 
	   * @param inflat
	   * 		:: Inflater
	   * @param googleMap
	   * 		:: Google map to draw map pins
	   * @param mapScrollView
	   * 		:: Map scroll view
	   */
		private static void addnearByView(LinearLayout nearByAminities, JSONArray nearByArr, String head, int imageId, List<Marker> markerArray,
				LayoutInflater inflat, final GoogleMap googleMap, final ScrollView mapScrollView){
			try{
				View group  = inflat.inflate(R.layout.amenitiesgroup, null);
				((Helvetica) group.findViewById(R.id.ParentText)).setText(head);
				int childIndex = nearByAminities.getChildCount();
				nearByAminities.addView(group, childIndex);
				childIndex = childIndex + 1;
				for(int index=0;index<nearByArr.length();index++, childIndex++){
					View child  = inflat.inflate(R.layout.amenitieschild, null);
					JSONObject nearByMall = nearByArr.getJSONObject(index);
					((HelveticaBold) child.findViewById(R.id.ChildText)).setText(nearByMall.getString("name"));
					((HelveticaBold) child.findViewById(R.id.KMDistance)).setText("(" + nearByMall.get("distance").toString() + " km)");
					nearByAminities.addView(child, childIndex);
					((ImageView)child.findViewById(R.id.NearByImage)).setImageResource(imageId);
					final Marker marker = markerArray.get(index);
					child.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude)));
							marker.showInfoWindow();
							mapScrollView.scrollTo(0, 0);
						}
					});
				}
			}catch(Exception e){
				Log.e("Near By Map Pins", e.getLocalizedMessage(), e);
			}
		}
		/**
		 * 
		 * @param pageName 	:: AT Internet Screen Name
		 * @param level2	:: Level 2 ID
		 * @param customVariable	:: Custom variables
		 * 
		 * Log.d("sendATTagging", pageName + ", " + level2);
		 */
		public static void sendATTagging(Context context, String pageName, int level2, List<String> customVariable){
			try{
//				Log.d("pageName", pageName + "_"+ level2 + "-" + (customVariable!=null ? customVariable.toString() : ""));
				SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
				ATParams atp = new ATParams();
				atp.setPage(pageName);
				if(level2 > 0){
					atp.setLevel2(Integer.toString(level2));
				}
				if(customVariable != null){
					// add custom variable to the post values
					for(int index=0; index<customVariable.size();index++){
						atp.setCustomForm(Integer.toString(index+1), customVariable.get(index));
					}
				}
				atp.setCustomCritera("1", mPrefs.contains("userid") ? "1" : "2");
				atp.xt_sendTag();
			}catch(Exception e){
				Log.e("sendATTagging", e.getLocalizedMessage(), e);
			}
		}
		public static void sendGA(Context context, String gaScreenName){
			try{
				Tracker easyTracker = EasyTracker.getInstance(context);
		        easyTracker.set(Fields.SCREEN_NAME, gaScreenName);
		        easyTracker.send(MapBuilder.createAppView().build());
			}catch(Exception e){
				Log.e("sendGA", e.getLocalizedMessage(), e);
			}
		}
}
