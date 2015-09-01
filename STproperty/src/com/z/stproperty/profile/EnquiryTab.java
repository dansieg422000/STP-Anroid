package com.z.stproperty.profile;

/***************************************************************
 * Class name:
 * (tab4)
 * 
 * Description:
 * (Users can make enquiry in this UI )
 * 
 * Displays the list of enquiry sent by a user in saved search ListView itself
 * 
 * EnquiryDetailsAdapter is used to customize the enquired values display
 * 
 * custEnquirydetailsTextView is the class type used to save searched values.
 * 
 * on click of Enquiry in listView value shows the details of the enquiry.
 * 
 * Enquiries are fetched from db in a cursor and added in a array list.
 * 
 * Input variables:
 * null
 * Output variables:
 * null
 * 
 *  * *  FatherActivity
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.evvolutions.android.http.AsyncHttpResponseHandler;
import com.z.stproperty.MainActivity;
import com.z.stproperty.R;
import com.z.stproperty.database.DatabaseHelper;
import com.z.stproperty.dialog.ListPicker;
import com.z.stproperty.dialog.RangeValues;
import com.z.stproperty.dialog.STPAlertDialog;
import com.z.stproperty.dialog.ServiceEnableDialog;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.shared.ConnectionCheck;
import com.z.stproperty.shared.ConnectionManager;
import com.z.stproperty.shared.ConnectionManager.ConnectionType;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.UrlUtils;

public class EnquiryTab extends Activity {
	
	private SharedPreferences mPrefs;
	private String maxprice = "", minprice = "";
	private String[] userAction = {"Buy", "Rent", "Rent a room", "Sell" };
	private int propertyWant = 0, district = 0, estate = 0, bed = 0, propertyType = 0, classification=0;
	private ProgressDialog processdialog;
	private JSONObject enquiryJSON;
	/**
	 * Common listener for view click event
	 * based the layout id this will perform or launch the new activity
	 * with some set of values as  well for user selection
	 * 
	 * like 
	 * 
	 * 1. Property type
	 * 2. Property subtype
	 * 3. district or estate
	 * 4. Bedroom and bathroom
	 * 5. Price range
	 * 6. Land size or floor area etc...
	 */
	private OnClickListener onPickerClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			performPickerAction(v);
		}
	};
	private void performPickerAction(View v){
		Intent intent = new Intent(getApplicationContext(), ListPicker.class);
		int requestCode = Constants.REQUEST_PROPERTYTYPE;
		switch (v.getId()) {
			case R.id.PropertyType:
				intent.putExtra("title", "Property Type");
				intent.putExtra("array", Constants.PROPERTYTYPE);
				break;
			case R.id.PriceLayout:
				if(propertyWant==0){
					intent = new Intent(getApplicationContext(), STPAlertDialog.class);
					intent.putExtra("message", "Please select \"Want to\"");
					requestCode = 0;
				}else{
					intent = new Intent(getApplicationContext(), RangeValues.class);
					intent.putExtra("title", "Price Range");
					intent.putExtra("fromArray", (propertyWant==1 || propertyWant==4) ? Constants.SALEMINPRICE : (propertyWant==2? Constants.RENTMINPRICE : Constants.RENTAL_MINPRICE));
					intent.putExtra("toArray", (propertyWant==1 || propertyWant==4) ? Constants.SALEMAXPRICE : (propertyWant==2 ? Constants.RENTMAXPRICE : Constants.RENTAL_MAXPRICE));
					requestCode = Constants.REQUEST_PRICE;
				}
				break;
			case R.id.classificationLayout:
				if(propertyType==0){
					intent = new Intent(getApplicationContext(), STPAlertDialog.class);
					intent.putExtra("message", "Please select property type.");
				}else{
					intent.putExtra("title", "Classification");
					String[] array = SharedFunction.getClasification(propertyType);
					intent.putExtra("array", array);
					requestCode = Constants.REQUEST_CLASSIFICATION;
				}
				break;
			case R.id.PropertyWantTo:
				intent.putExtra("title", "Want To");
				intent.putExtra("array", userAction);
				requestCode = Constants.REQUEST_USER_ACTION;
				break;
			case R.id.districtLayout:
				intent.putExtra("title", "Districts");
				intent.putExtra("array", Constants.DISTRICTS);
				requestCode = Constants.REQUEST_DISTRICT;
				break;
			case R.id.BedroomLayout:
				intent.putExtra("title", "Bed Rooms");
				intent.putExtra("array", Constants.BEDROOMS);
				requestCode = Constants.REQUEST_BEDROOM;
				break;
			case R.id.EstateLayout:
				intent.putExtra("title", "Estates");
				intent.putExtra("array", Constants.ESTATE);
				requestCode = Constants.REQUEST_ESTATE;
				break;
			default:
				break;
		}
		startActivityForResult(intent, requestCode);
	}
	/**
	 * Re-launch this activity to clear all the input and 
	 * variable values
	 */
	private void clearFields(){
		Intent intent = new Intent(getApplicationContext(),MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("2", 3);
		startActivity(intent);
		finish();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.enquiry_tab);
			if (android.os.Build.VERSION.SDK_INT > 9) {
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}
			mPrefs = PreferenceManager.getDefaultSharedPreferences(EnquiryTab.this);
			processdialog = ProgressDialog.show(this, "", "Posting...", true);
			processdialog.dismiss();
			((Helvetica)findViewById(R.id.Name)).setText(mPrefs.getString("name", ""));
			((Helvetica)findViewById(R.id.userContact)).setText(mPrefs.getString("phone", ""));
			((Helvetica)findViewById(R.id.userName)).setText(mPrefs.getString("userid", ""));
			((Helvetica)findViewById(R.id.userEmail)).setText(mPrefs.getString("st_email", ""));
			
			((Button)findViewById(R.id.ClearBtn)).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					clearFields();
				}
			});
			// post enquiry button
			((Button)findViewById(R.id.SendBtn)).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					sendEnquiry();
				}
			});
			((Helvetica)findViewById(R.id.errorTxt)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getBaseContext(), Login.class);
					i.putExtra("TYPE", "Enquiry");
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(i, 0);
				}
			});
			((RelativeLayout)findViewById(R.id.PropertyType)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.PropertyWantTo)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.PriceLayout)).setOnClickListener(onPickerClick);
			((LinearLayout)findViewById(R.id.BedroomLayout)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.districtLayout)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.EstateLayout)).setOnClickListener(onPickerClick);
			((RelativeLayout)findViewById(R.id.classificationLayout)).setOnClickListener(onPickerClick);
		}catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	
/**
 * In-order to post an enquiry 
 * the user must logged-in already otherwise
 * Dialog box will ask user to login 
 * If the user doesn't have account the they can go with option register
 * or can cancel the euquiry option
 * 
 * @return Dialog box for user with options to 
 * 
 * 1. Register 
 * 2. Login
 * 3. Cancel dialog 
 */
	private AlertDialog showUserEnquieryDialog() {
		final AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
				// set message, title, and icon
				.setTitle("Login Authentication")
				.setMessage("Please log in to continue")
				.setIcon(R.drawable.appicon)
				// whatever should be done when answering "YES"
				// set three option buttons
				.setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							Intent i = new Intent(getBaseContext(), Login.class);
							i.putExtra("TYPE", "Enquiry");
							i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivityForResult(i, 0);
						}
					})
				// setPositiveButton :: Register
				.setNeutralButton("Register", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							Intent i = new Intent(getBaseContext(), Register.class);
							i.putExtra("TYPE", "Enquiry");
							i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivityForResult(i, 0);
						}
					})
				 // setNegativeButton
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// part
						}
					}).create();

		return myQuittingDialogBox;
	}
	/**
	 * OnResume on this activity 
	 * this will check for user login status 
	 * 	if the user already logged in then the outbox will be displayed to the user
	 * 	otherwise the dialog box will open with option 
	 * 		Login, Register, Cancel to choose
	 */
	protected void onResume() {
		mPrefs = PreferenceManager.getDefaultSharedPreferences(EnquiryTab.this);
		if (!mPrefs.contains("userid")) {
			((LinearLayout)findViewById(R.id.aboutLayout)).setVisibility(View.GONE);
			((ScrollView)findViewById(R.id.enquiryScroll)).setVisibility(View.GONE);
			((Helvetica)findViewById(R.id.errorTxt)).setVisibility(View.VISIBLE);
			((Button)findViewById(R.id.ClearBtn)).setVisibility(View.GONE);
			Dialog dialog = showUserEnquieryDialog();
			dialog.show();
		}else{
			((LinearLayout)findViewById(R.id.aboutLayout)).setVisibility(View.VISIBLE);
			((ScrollView)findViewById(R.id.enquiryScroll)).setVisibility(View.VISIBLE);
			((Button)findViewById(R.id.ClearBtn)).setVisibility(View.VISIBLE);
			((Helvetica)findViewById(R.id.errorTxt)).setVisibility(View.GONE);
			 SharedFunction.postAnalytics(EnquiryTab.this, "Engagement", "Enquiry", "Logged In");
			// GA net-rating
			SharedFunction.sendGA(EnquiryTab.this, "Enquiry");
	        // AT Internet tracking
	        SharedFunction.sendATTagging(getApplicationContext(), "Enquiry", 10, null);
		}
		super.onResume();
	}
	
	/**
	 * This method will check and update the users 
	 * with mandatory fields
	 * 
	 * Depends on the property Type (condo, hdb etc...)
	 * the values that are posted to server is 
	 * different so we need to validate all fields with 
	 * property type wise
	 * 
	 * If all the fields are filled correctly then that will be converted 
	 * as xml data and posted to server
	 */
	@SuppressLint("SimpleDateFormat")
	private void sendEnquiry(){
		if (!getInput(R.id.EnquiryTitle).equals("") && !getInput(R.id.projectName).equals("")
				&& !getInput(R.id.Remarks).equals("") && !minprice.equals("") && !maxprice.equals("")
				&& classification >0 && propertyWant >0 && district > 0 &&
				((propertyType == 2 && estate > 0) || propertyType > 0 ) && 
				(((propertyType == 1 || propertyType == 2 || propertyType == 5) && bed > 0) || propertyType > 0 )) {
			try {
				processdialog.show();
				String dateTime = new SimpleDateFormat("yyyyMMdd").format(new Date()).toString();
				String postXML = "<?xml version='1.0'?> <st701><property_leads><property_type_id><![CDATA["
						+ propertyType + "]]></property_type_id>"
						+ "<want_to><![CDATA[" + propertyWant + "]]></want_to>"
						+ "<enquiry_title><![CDATA[" + getInput(R.id.EnquiryTitle) + "]]></enquiry_title>"
						+ "<property_project_name_id><![CDATA[ " + getInput(R.id.projectName) + "]]></property_project_name_id>"
						+ "<property_classification_id><![CDATA[" + classification  + "]]></property_classification_id>"
						+ "<property_district_id><![CDATA[" + district + "]]></property_district_id>"
						+ "<property_estate_id><![CDATA[" + estate + "]]></property_estate_id>"
						+ "<bedrooms><![CDATA[" + (bed-1) + "]]></bedrooms>"
						+ "<budget_min><![CDATA[" + minprice.replace("S", "") + "]]></budget_min>"
						+ "<budget_max><![CDATA[" + maxprice.replace("S", "") + "]]></budget_max>"
						+ "<username><![CDATA[" + mPrefs.getString("userid", "") + "]]></username>"
						+ "<name><![CDATA[" + mPrefs.getString("name", "") + "]]></name>"
						+ "<email><![CDATA[" + mPrefs.getString("st_email", "") + "]]></email>"
						+ "<contact_number><![CDATA[" + mPrefs.getString("phone", "") + "]]></contact_number>"
						+ "<remarks><![CDATA[" + getInput(R.id.Remarks) + "]]></remarks>"
						+ "<published_date><![CDATA[" + dateTime + "]]></published_date>"
						+ "</property_leads></st701>";
				postXML = postXML.replace(",", "");
				postXML = postXML.replace("$", "");
				Map<String,String> postValues= new HashMap<String, String>();
				postValues.put("action", "newEnquiry");
				postValues.put("xml_file", postXML);
				postValues.put("hash",SharedFunction.getHashKey());
				if(ConnectionCheck.checkOnline(getApplicationContext())){
					ConnectionManager conn = new ConnectionManager();
					conn.connectionHandler(this,postValues,UrlUtils.NEW_ENQUEIRY_URL, ConnectionType.CONNECTIONTYPE_POST,null,
							new AsyncHttpResponseHandler(){
						@Override
						public void onSuccess(String response) {
							try {
								enquiryJSON = new JSONObject(response);				  
							} catch (Exception e) {
								processdialog.dismiss();
								Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
							}
						}	
						@Override
						public void onFailure(Throwable error) {
							processdialog.dismiss();
							updateSettings(Constants.LOWINTERNETSTR);
						}
						@Override
						public void onFinish() {
							enquirySuccess();
						}
					});
				}else{
					updateSettings(Constants.NETWORKSTR);
				}
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			}
		} else {
			Intent intent = new Intent(getApplicationContext(), STPAlertDialog.class);
			intent.putExtra("message", "All fields are mandatory.");
			startActivity(intent);
		}
	}
	/**
	 * If enquiry posted successfully then the values are stored in database 
	 * to show in outbox (This is not user specific)
	 * 
	 * Then this will re-launch this activity to clear the input fields
	 * 
	 * On unsuccessful this will displays an error message to user
	 */
	private void enquirySuccess(){
		try{
			String enquiryStatus = enquiryJSON!=null ? enquiryJSON.getString("status") : "";
			if(enquiryStatus.equals("success")){
				DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
				dbHelper.addEnquiry(getInput(R.id.EnquiryTitle), getInput(R.id.projectName), getInput(R.id.Remarks),
						getTextView(R.id.Bedroom), getTextView(R.id.District), getTextView(R.id.Estate),
						getTextView(R.id.classification), getTextView(R.id.PropertyTypeSpinner),
						getTextView(R.id.WantTo), getTextView(R.id.PriceRange));

				List<String[]> values = new ArrayList<String[]>();
				String[] val1 = {Constants.ANALYTICS_ENQUIRY_PROPERTYWANTED,getTextView(R.id.PropertyTypeSpinner)};
				values.add(val1);
				String[] val2 = {Constants.ANALYTICS_ENQUIRY_CLASSIFICATION,getTextView(R.id.classification)};
				values.add(val2);
				String[] val3 = {Constants.ANALYTICS_ENQUIRY_WANTTO,getTextView(R.id.WantTo)};
				values.add(val3);
				String[] val4 = {Constants.ANALYTICS_ENQUIRY_TITLE,getInput(R.id.EnquiryTitle)};
				values.add(val4);
				String[] val5 = {Constants.ANALYTICS_ENQUIRY_PROJECTNAME,getInput(R.id.projectName)};
				values.add(val5);
				SharedFunction.sendCustomDimention(EnquiryTab.this, values, "Enquiry");
				SharedFunction.sendATTagging(getApplicationContext(), "Successful_Enquiry", 10, null);
				Toast.makeText(EnquiryTab.this, "Enquiry posted successfully.", Toast.LENGTH_LONG).show();
				clearFields();
			}else if(enquiryStatus.equals("fail")){
				Intent intent = new Intent(getApplicationContext(), STPAlertDialog.class);
				intent.putExtra("message", "Please check your input and try again.");
				startActivity(intent);
			}
			processdialog.dismiss();
		}catch (Exception e) {
			processdialog.dismiss();
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		} 
	}
	/**
	 * @param id
	 * @return
	 */
	private String getInput(int id){
		return ((EditText)findViewById(id)).getText().toString().trim();
	}
	/**
	 * @param id
	 * @return
	 */
	private String getTextView(int id){
		String text = "";
		text = ((Helvetica)findViewById(id)).getText().toString().trim();
		return text.equalsIgnoreCase("select") ? "" : text;
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
	private void setPropertyTypeValue(int id){
		propertyType = SharedFunction.getPropertyTypeId(id+1);
		((Helvetica)findViewById(R.id.PropertyTypeSpinner)).setText(Constants.PROPERTYTYPE[id]);
		if(propertyType==2){
			findViewById(R.id.hdbEstate).setVisibility(View.VISIBLE);
		}else{
			estate = 0;
			findViewById(R.id.hdbEstate).setVisibility(View.GONE);
			((Helvetica) findViewById(R.id.Estate)).setText("Select");
		}
		if(propertyType == 1 || propertyType == 2 || propertyType == 5){
			((LinearLayout)findViewById(R.id.BedroomLayout)).setVisibility(View.VISIBLE);
		}else{
			bed = 0;
			((Helvetica) findViewById(R.id.Bedroom)).setText("Select");
			((LinearLayout)findViewById(R.id.BedroomLayout)).setVisibility(View.GONE);
		}
		classification = 0;
		((Helvetica)findViewById(R.id.classification)).setText("Select");
	}
	/**
	 * @param data	:: Bundle data that contains the values of price min and max values
	 * 
	 * Will set min and max price range values to parameters
	 */
	private void setPriceValue(Intent data){
		String[] fromarray = (propertyWant==1 || propertyWant==4) ? Constants.SALEMINPRICE
				: (propertyWant == 2 ? Constants.RENTMINPRICE : Constants.RENTAL_MINPRICE);
		String[] toArrray = (propertyWant==1 || propertyWant==4) ? Constants.SALEMAXPRICE
				: (propertyWant == 2 ? Constants.RENTMAXPRICE : Constants.RENTAL_MAXPRICE);
		String priceText = fromarray[data.getIntExtra("fromId", 0)] + " - " 
						+ toArrray[data.getIntExtra("toId", 0)];
		((Helvetica)findViewById(R.id.PriceRange)).setText(priceText);
		minprice = fromarray[data.getIntExtra("fromId", 0)];
		maxprice = fromarray[data.getIntExtra("toId", 0)];
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
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try{
			if(resultCode==RESULT_OK){
				int id = data.getIntExtra("id",0);
				switch (requestCode) {
				case Constants.REQUESTCODE_SETTINGS:
					sendEnquiry();
					break;
				case Constants.REQUEST_PROPERTYTYPE:
					setPropertyTypeValue(id);
					break;
				case Constants.REQUEST_CLASSIFICATION:
					classification = SharedFunction.getClasificationId(propertyType+"-"+(id+1));
					String[] array = SharedFunction.getClasification(propertyType);
					((Helvetica)findViewById(R.id.classification)).setText(array[id]);
					break;
				case Constants.REQUEST_USER_ACTION:
					propertyWant = id+1;
					((Helvetica)findViewById(R.id.WantTo)).setText(userAction[id]);
					((Helvetica)findViewById(R.id.PriceRange)).setText("Select");
					minprice = "";
					maxprice = "";
					break;
				case Constants.REQUEST_PRICE:
					setPriceValue(data);
					break;
				case Constants.REQUEST_DISTRICT:
					district = id+1;
					((Helvetica)findViewById(R.id.District)).setText(Constants.DISTRICTS[id]);
					break;
				case Constants.REQUEST_ESTATE:
					estate = id+1;
					((Helvetica)findViewById(R.id.Estate)).setText(Constants.ESTATE[id]);
					break;
				case Constants.REQUEST_BEDROOM:
					bed = id+1;
					((Helvetica)findViewById(R.id.Bedroom)).setText(Constants.BEDROOMS[id]);
					break;
				default:
					break;
				}
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * On back press the app will launch the home screen as fresh activity
	 * 
	 * clears all the top saved instances
	 * and launches the new activity
	 * 
	 */
	@Override
	 public void onBackPressed() {
	 	 try{
	 		Intent intent = new Intent(getApplicationContext(),MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
	 	 }catch (Exception e) {
	 		Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		 }
	 }
	/**
     * 
     * @param type :: Type network failure 	
     * 					1. No internet connection
     * 					2. Low internet connection
     * Will pop-up an dialog box to check their network status..
     */
    protected void updateSettings(String type){
    	Intent intent = new Intent(getApplicationContext(),ServiceEnableDialog.class);
    	intent.putExtra(type, true);
    	startActivityForResult(intent, Constants.REQUESTCODE_SETTINGS);
    }
}