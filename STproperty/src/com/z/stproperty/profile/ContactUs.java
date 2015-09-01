package com.z.stproperty.profile;

/***************************************************************
 * Class name:
 * (ContactUs)
 * 
 * Description:
 * (Use for contact us page. list the basic information of STProperty. User can also send email to STProperty through this page)
 * 
 * 
 * Input variables:
 * SharedPreferences mPrefs(can obtain logged in user's information, like username, phone NO.)
 * webveiw w(load static html text for STProperty information);
 * 
 * Output variables:
 *  ArrayList<NameValuePair> postParameters(Values send to server)
 *  
 *   User can post their feedback about the property or 
 * 	about general
 * In-order to post this they need to give all mandatory fields
 * And proper details
 * The comments should be particular.
 * 
 * All the fields are has validation 
 * After successful validation the form posted to server
 * And the success message is displayed to user
 * 
 * If there is any error is posting the data then proper message about the failure is displayed
 ****************************************************************/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.evvolutions.android.http.AsyncHttpResponseHandler;
import com.google.analytics.tracking.android.EasyTracker;
import com.z.stproperty.R;
import com.z.stproperty.dialog.STPAlertDialog;
import com.z.stproperty.shared.ConnectionManager;
import com.z.stproperty.shared.ConnectionManager.ConnectionType;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.UrlUtils;

public class ContactUs extends Activity {
	private ProgressDialog processdialog;
	private JSONObject responseJson;
	/**
	 * Common listener for view click event
	 * based the layout id this will perform or launch the new activity
	 * with some set of values for user selection
	 */
	private OnClickListener onClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			performViewClick(v);
		}
	};
	private void performViewClick(View v){
		switch (v.getId()) {
		case R.id.ClearBtn:
			startActivity(getIntent());
			finish();
			break;
		case R.id.VisitSTP:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(UrlUtils.ABOUT));
			SharedFunction.postAnalytics(ContactUs.this, "Engagement", "Visit STProperty", "Contact us");
			startActivity(browserIntent);
			break;
		case R.id.SendBtn:
			postContact();
			break;
		case R.id.AboutBtn:
			Intent intent = new Intent(getApplicationContext(), AboutSTP.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.contactus);
			((Button) findViewById(R.id.VisitSTP)).setOnClickListener(onClick);
			((Button) findViewById(R.id.ClearBtn)).setOnClickListener(onClick);
			((Button) findViewById(R.id.SendBtn)).setOnClickListener(onClick);
			((Button) findViewById(R.id.AboutBtn)).setOnClickListener(onClick);
			processdialog = ProgressDialog.show(this, "", "Sending...", true);
			processdialog.dismiss();
			SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(ContactUs.this);
			((EditText)findViewById(R.id.Name)).setText(mPrefs.getString("name", ""));
			((EditText)findViewById(R.id.Mobile)).setText(mPrefs.getString("phone", ""));
			((EditText)findViewById(R.id.Email)).setText(mPrefs.getString("st_email", ""));
			// AT Internet tracking
		    SharedFunction.sendATTagging(getApplicationContext(), "Contact Us", 10, null);
			this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		} catch (Exception e2) {
			Log.e("EXCEPTION", "<actual message here", e2);
		}
	}
	/**
	 * @param id :: Input id (textview or EditText)
	 * @return :: View string values
	 * 
	 * Will get the view string and trims it and pass it to the calling function
	 */
	private String getInput(int id){
		return ((EditText)findViewById(id)).getText().toString().trim();
	}
	/**
	 * Will check the user input values for posting
	 * This will check in the following order and the corresponding error message is shown to the user
	 * 
	 * 1. If any of the input field is empty
	 * 2. Check for email format
	 * 3. Mobile number (length to 8 digits)
	 * 
	 * If all the above cases are passed then the values are posted to server
	 * and the success message is shown
	 * 
	 */
	private void postContact(){
		try{
			if(getInput(R.id.Email).equals("") || getInput(R.id.Mobile).equals("") || getInput(R.id.Name).equals("")
					|| getInput(R.id.Remarks).equals("")){
				Intent intent = new Intent(getApplicationContext(), STPAlertDialog.class);
				intent.putExtra("message", "All fields are mandatory.");
				startActivity(intent);
			}else if(!isValidEmail(getInput(R.id.Email))){
				Intent intent = new Intent(getApplicationContext(), STPAlertDialog.class);
				intent.putExtra("message", "Check your email-id.");
				startActivity(intent);
			}else if(getInput(R.id.Mobile).length()<8){
				Intent intent = new Intent(getApplicationContext(), STPAlertDialog.class);
				intent.putExtra("message", "Mobile number should be atleast 8 digits.");
				startActivity(intent);
			}else{
				processdialog.show();
				Map<String, String> postValues = new HashMap<String, String>();
				postValues.put("key", SharedFunction.getHashKey());
				postValues.put("name", getInput(R.id.Name));
				postValues.put("email", getInput(R.id.Email));
				postValues.put("contact", getInput(R.id.Mobile));
				postValues.put("comment", getInput(R.id.Remarks));
				ConnectionManager conn = new ConnectionManager();
				conn.connectionHandler(this,postValues,UrlUtils.URL_FEEDBACK, ConnectionType.CONNECTIONTYPE_POST,null,
						new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(String response) {
						try {
							responseJson = new JSONObject(response);
						} catch (Exception e) {
							Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
							processdialog.dismiss();
						}
					}	
					@Override
					public void onFailure(Throwable error) {
						processdialog.dismiss();
					}
					@Override
					public void onFinish() {
						feedPostSuccess();
						processdialog.dismiss();
					}
				});
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	private void feedPostSuccess(){
		try{
			String enquiryStatus = responseJson!=null ? responseJson.getString("status") : "";
			if(enquiryStatus.equals("success")){
				   List<String[]> values = new ArrayList<String[]>();
			       String[] val1 = {Constants.ANALYTICS_CONTACT_EMAIL,getInput(R.id.Email)};
			       values.add(val1);
			       String[] val2 = {Constants.ANALYTICS_CONTACT_PHONENUMBER,getInput(R.id.Mobile)};
			       values.add(val2);
			       String[] val3 = {Constants.ANALYTICS_CONTACT_NAME,getInput(R.id.Name)};
			       values.add(val3);
			       String[] val4 = {Constants.ANALYTICS_CONTACT_FEEDBACK,getInput(R.id.Remarks)};
			       values.add(val4);
			       SharedFunction.sendCustomDimention(ContactUs.this, values, "Contact_Us");
				   SharedFunction.postAnalytics(ContactUs.this, "Engagement", "Feedback", "Successful");
				   // AT Internet tracking
				   SharedFunction.sendATTagging(getApplicationContext(), "Feedback", 10, null);
				   Toast.makeText(getApplicationContext(), responseJson.getJSONArray("msg").get(0).toString(), Toast.LENGTH_LONG).show();
			}else{
				Intent intent = new Intent(getApplicationContext(), STPAlertDialog.class);
				intent.putExtra("message", "Please check your input and try again.");
				SharedFunction.postAnalytics(ContactUs.this, "Engagement", "Feedback", "Unsuccessful");
				startActivity(intent);
			}
		}catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * 
	 * @param num
	 *            :: Double number string
	 * @return :: Valid double not not
	 * 
	 *         If number format exception arise then true is return false for
	 *         all other cases
	 * 
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
	 * 
	 * @param target
	 *            :: Email String
	 * @return :: True or False
	 * 
	 *         If string id null then false is return Otherwise The validation
	 *         result is return If valid Email then true else false
	 */
	public static final boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
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
