package com.z.stproperty.profile;

/***
 * 
 * 
 * The mailidbx , password , confirm password, first name, last name
 * country code , mobile number edit boxes are checked if they are empty
 *   if yes then the Registertabalerts dialog class is called
 *    and the user is displayed -
 *    "Kindly fill all the mandatory fields"
 *  
 *   
 *  else if all the fields are entered then the mail edit box value's
 *   length is checked if greater than 100
 *    if yes then the Registertabalerts dialog class is called
 *    and the user is displayed -
 *    "Email id cannot exceed 100 characters" 
 *   
 *   
 *   else password and confirm password values length is checked
 *   if any value is less than 4 or greater than 40
 *   if yes then the Registertabalerts dialog class is called
 *    and the user is displayed -
 *    "Password must be between 4 to 50 characters."
 *   
 *   
 *   else check if password edit box value equals
 *   confirm password edit box
 *   if yes then the Registertabalerts dialog class is called
 *    and the user is displayed -
 *    "Passwords do not match."
 *   
 *   
 *   else check if firstname edit box value's length is greater than 100
 *    if yes then the Registertabalerts dialog class is called
 *    and the user is displayed -
 *    "First Name cannot exceed 100 characters"  
 *   
 *   
 *   else check if lastname edit box value's length is greater than 100
 *    if yes then the Registertabalerts dialog class is called
 *    and the user is displayed -
 *    "Last Name cannot exceed 100 characters"
 *   
 *   
 *   else check if country code edit box value's length is greater than 6
 *    if yes then the Registertabalerts dialog class is called
 *    and the user is displayed -
 *    "Country code cannot be more than 2 digits"
 *   
 *   
 *   else check if mobile number edit box value's length is greater than 20
 *    if yes then the Registertabalerts dialog class is called
 *    and the user is displayed -
 *    "Mobile number cannot excced 20 digits"
 *   
 *   else all the edit box values are put in a hash map
 *   
 *  The hashmap values are sent  to the registrationUrl in post method
 *  along with the device uid and key
 *  The key value is calculated using md5()
 *  
 *  if internet is connected then connection is established
 *  else
 *   user is redirected to enable wifi or check internet settings
 *   
 *   
 *  Onsuccessful connection
 *   if returned status is SUCCESS , RESPONSE is  accepted as a JSONObject
 *   
 *  OnFailure of connection 
 *    user is redirected to enable wifi or check internet settings 
 *    
 *  OnFinish of Connection the status is checked
 *   If the result JSONObject has a status string as success
 *   then the edit box values are save din local db
 *   and the application class
 *   
 */

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.evvolutions.android.http.AsyncHttpResponseHandler;
import com.google.analytics.tracking.android.EasyTracker;
import com.z.stproperty.R;
import com.z.stproperty.application.GlobalClass;
import com.z.stproperty.dialog.STPAlertDialog;
import com.z.stproperty.shared.ConnectionCheck;
import com.z.stproperty.shared.ConnectionManager;
import com.z.stproperty.shared.ConnectionManager.ConnectionType;
import com.z.stproperty.shared.Messages;
import com.z.stproperty.shared.SharedFunction;

public class Register extends Activity {
	private EditText username, pwd, lastname, firstname, mobile, confirm;
	private String response = "";
	private ProgressDialog processdialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		try {
			username = (EditText) findViewById(R.id.Email);
			pwd = (EditText) findViewById(R.id.Password);
			firstname = (EditText) findViewById(R.id.FirstName);
			lastname = (EditText) findViewById(R.id.LastName);
			mobile = (EditText) findViewById(R.id.Mobile);
			confirm = (EditText) findViewById(R.id.ConfirmPassword);
	
			// Click Clear
			((Button) findViewById(R.id.ClearBtn)).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					startActivity(getIntent());
					finish();
				}
			});
	
			// Click submit
			((Button) findViewById(R.id.SendBtn)).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					postRegister();
				}
			});
			// AT Internet tracking
	        SharedFunction.sendATTagging(getApplicationContext(), "Register", 11, null);
		} catch (Exception e2) {
			Log.e("EXCEPTION", "<actual message here", e2);
		}
	}
	/**
	 * Validates the user form before submission
	 */
	private void postRegister(){
		try{
			if (username.getText().toString().equals("")
					|| pwd.getText().toString().equals("")
					|| lastname.getText().toString().equals("")
					|| firstname.getText().toString().equals("")
					|| mobile.getText().toString().equals("")) {
				showErrorMsg("All are mandatory fields.");
			} else if (!username.getText().toString().equals("")
					&& !pwd.getText().toString().equals("")
					&& !lastname.getText().toString().equals("")
					&& !firstname.getText().toString().equals("")
					&& !mobile.getText().toString().equals("")) {
				if (pwd.getText().toString().equals(confirm.getText().toString())) {
					int d = mobile.length();
					if (checkchar(mobile.getText().toString())) {
						showErrorMsg("Symbols and characters can not put in number.");
					} else if (d < 8) {
						showErrorMsg("Mobile number should be 8 digits.");
					} else {
						processdialog = ProgressDialog.show(this, "", "Requesting...", true);
						Map<String,String> postParameters = new HashMap<String,String>();
						String udid = ((GlobalClass) getApplication()).getDeviceId();
						postParameters.put("deviceuid", udid);
						postParameters.put("key",SharedFunction.getHashKey());
						postParameters.put("password", pwd.getText().toString());
						postParameters.put("first_name", firstname.getText().toString());
						postParameters.put("last_name", lastname.getText().toString());
						postParameters.put("mobile_country_code", "65");
						postParameters.put("mobile", mobile.getText().toString());
						postParameters.put("username", username.getText().toString());
						postParameters.put("email", username.getText().toString());
						postRegisterValues(postParameters);
					}
				} else {
					showErrorMsg("Passwords are not the same.");
				}
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * @param postParameters :: Map values to post register 
	 */
	private void postRegisterValues(Map<String, String> postParameters){
		try {
			if(ConnectionCheck.checkOnline(this)){
				 ConnectionManager test = new ConnectionManager();
				 test.connectionHandler(this, postParameters, Messages.getString("ST.property")+ "ml-registration-api.php?", ConnectionType.CONNECTIONTYPE_POST, null, new AsyncHttpResponseHandler(){
					 
					 @Override
					 public void onSuccess(String responseStr) {
						 response = responseStr;
					 }

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						processdialog.dismiss();
						Toast.makeText(Register.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFinish() {
						registerSuccess();
					}
				});
			 }else{
				 processdialog.dismiss();
				 Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
			 }
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			processdialog.dismiss();
			Toast.makeText(this, "Please Try Again.", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * On successful registration the user details are stored in shared preferences
	 */
	private void registerSuccess(){
		try{
			JSONObject json = new JSONObject(response);
			String result1 = (String) json.get("status");
			if (result1.equals("fail")) {
				JSONArray fbfeed = json.getJSONArray("msg");
				showErrorMsg(fbfeed.getString(0));
			} else {
				String name = firstname.getText() + " " + lastname.getText();
				String phone = mobile.getText().toString();
				SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(Register.this);
				mPrefs.edit().putString("userid", username.getText().toString()).commit();
				mPrefs.edit().putString("password", pwd.getText().toString()).commit();
				mPrefs.edit().putString("name", name).commit();
				mPrefs.edit().putString("st_email", username.getText().toString());
				mPrefs.edit().putString("phone", phone) .commit();
				if(getIntent().hasExtra("TYPE")){
                    SharedFunction.postAnalytics(Register.this, "Engagement", getIntent().getStringExtra("TYPE"), "Register");
				}else{
					SharedFunction.postAnalytics(Register.this, "Account", "Register", username.getText().toString());
				}
				showErrorMsg("You have successfully registered.");
				// Goole Analytics
				SharedFunction.sendGA(Register.this, "Successful_Registration");
				// AT Internet
				SharedFunction.sendATTagging(getApplicationContext(), "Successful_Registration", 11, null);
				finish();
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			showErrorMsg("Please try again.");
		}
		processdialog.dismiss();
	}
	/**
	 * @param message	:: Error message
	 */
	private void showErrorMsg(String message){
		Intent intent = new Intent(getApplicationContext(), STPAlertDialog.class);
		intent.putExtra("message", message);
		startActivity(intent);
	}
	/**
	 * 
	 * @param num
	 *            :: contact number
	 * @return :: boolean validate result on number
	 */
	public boolean checkchar(String num) {
		try {
			Double.parseDouble(num);
		} catch (NumberFormatException nfe) {
			Log.e("EXCEPTION", "<actual message here", nfe);
			return true;
		}
		return false;
	}
	/**
	 * Used to start the session for this activity 
	 * in Google Analytics Screen capture
	*/
	 @Override
	 public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance(this).activityStart(this);
	 }
	 /**
	 * Used to End the session for this activity 
	 * in Google Analytics Screen capture
	 */
	 @Override
	 public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this);
	 }
}
