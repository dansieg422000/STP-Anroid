package com.z.stproperty.profile;

/***************************************************************
 * Class name:
 * 
 * (Login)
 * 
 * Description:
 * 
 * (Login page for users)
 * 
 * 
 * Input variables:
 * 
 * EditText username,psw(users passwords and 
 * username(email) when they are logged in)
 * 
 * 
 * Output variables:
 * 
 * SharedPreferences mPrefs(which stored the basic information through api, 
 * like first_name, phone NO.)
 * 
 *  doLogin Button lets the user login
 *  
 *  The user credentials are sent  to the url in post method
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
 *   If the result JSONObject has a status string as scuccess
 *    then user name, password,
 *    key, device profile, mobile number, email
 *    are set in the Global Class(Application class)
 *    using the returned response object.
 *    
 *  sends a RESULT_OK result to the previous activity,
 *  along with the intent.Sends  a result value as a strinmg
 *  "loggedin" 
 *
 ****************************************************************/

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
import com.z.stproperty.dialog.STPAlertDialog;
import com.z.stproperty.shared.ConnectionCheck;
import com.z.stproperty.shared.ConnectionManager;
import com.z.stproperty.shared.ConnectionManager.ConnectionType;
import com.z.stproperty.shared.Messages;
import com.z.stproperty.shared.SharedFunction;

public class Login extends Activity {
	private EditText username, psw;
	private String response;
	private SharedPreferences mPrefs;
	private ProgressDialog dialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		try {
			mPrefs = PreferenceManager.getDefaultSharedPreferences(Login.this);
			username = (EditText) findViewById(R.id.userName);
			psw = (EditText) findViewById(R.id.Password);
			((Button) findViewById(R.id.SendBtn)).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (username.getText().toString().equals("") || psw.getText().toString().equals("")) {
						Intent intent = new Intent(getApplicationContext(), STPAlertDialog.class);
						intent.putExtra("message", "All fields are mandatory.");
						startActivity(intent);
					} else if (!username.getText().toString().equals("") && !psw.getText().toString().equals("")) {
						dialog = ProgressDialog.show(Login.this, "", "Checking. Please wait...", true);
						checkLogin();
					}
				}
			});
			((Button) findViewById(R.id.ClearBtn)).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					startActivity(getIntent());
					finish();
				}
			});
			// AT Internet tracking
		    SharedFunction.sendATTagging(getApplicationContext(), "Sign_In", 10, null);
		} catch (Exception e2) {
			Log.e("EXCEPTION", "<actual message here", e2);
		}
	}
	/**
	 * Check login with server
	 */
	private void checkLogin() {
		Map<String,String> postParameters = new HashMap<String,String>();
		postParameters.put("password", psw.getText().toString());
		postParameters.put("username", username.getText().toString());
		String sd = SharedFunction.getHashKey();
		postParameters.put("key", sd);
		try {
			if(ConnectionCheck.checkOnline(Login.this)){
				 ConnectionManager test = new ConnectionManager();
				 test.connectionHandler(Login.this, postParameters, Messages.getString("ST.property")+ "ml-login-api.php?", ConnectionType.CONNECTIONTYPE_POST, null, new AsyncHttpResponseHandler(){
					 
					 @Override
					 public void onSuccess(String responseStr) {
						 dialog.dismiss();
						 response = responseStr;
					 }

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						dialog.dismiss();
						Toast.makeText(Login.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFinish() {
						loginSuccess();
					}
				});
			 }else{
				 dialog.dismiss();
				 Toast.makeText(Login.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
			 }
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			Toast.makeText(Login.this, "Please Try Again.", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * After successful login 
	 * the user details are stored in SharedPreferences for later use
	 * if the login fail then
	 * the corressponding message is shown to the user
	 */
	private void loginSuccess(){
		try{
			JSONObject json = new JSONObject(response);
			String status = (String) json.get("status");

			if (status.equals("fail")) {
				JSONArray fbfeed = json.getJSONArray("msg");
				Intent intent = new Intent(getApplicationContext(), STPAlertDialog.class);
				intent.putExtra("message", fbfeed.getString(0));
				startActivity(intent);
			} else {
				JSONObject json1 = json.getJSONObject("deviceProfile");
				String name = (String) json1.get("first_name") + " " + json1.get("last_name").toString();
				String phone = (String) json1.get("mobile");
				mPrefs.edit().putString("userid", username.getText().toString()) .commit();
				mPrefs.edit().putString("password", psw.getText().toString()) .commit();
				mPrefs.edit().putString("name", name).commit();
				mPrefs.edit().putString("st_email", json1.getString("email")).commit();
				mPrefs.edit().putString("phone", phone) .commit();
				 if(getIntent().getStringExtra("TYPE")!=null&&(getIntent().getStringExtra("TYPE").equals("Enquiry")||getIntent().getStringExtra("TYPE").equals("Outbox"))){
                     SharedFunction.postAnalytics(Login.this, "Engagement", getIntent().getStringExtra("TYPE"), "Sign In");
                }else{
                SharedFunction.postAnalytics(Login.this, "Account", "Sign In", username.getText().toString());
                }
				Toast.makeText(Login.this, status, Toast.LENGTH_LONG).show();
				finish();
			}
			dialog.dismiss();
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
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
