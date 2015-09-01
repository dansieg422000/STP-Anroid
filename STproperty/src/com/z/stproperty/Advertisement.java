package com.z.stproperty;

/***
 * 
 * @author Evvolutions
 * 
 * Description:
 * 
 * Advertisement page, after app start, it will automatically count down(5s).
 * User can skip, or click on Advertisement image. After 5s it will jump to home screen.
 * 
 * Input variables:
 * 	(List out each input variable and its type as shown below. Include range of values or expected inputs where necessary.)
 * 
 *  String compaignstring="file:///android_asset/splashopen.html"(path of the loacal html file)
 *  WebView  webview;(a webview to Load the local html file)
 *  CountDownTimer c;(to count down to skip, for 5s)
 * Output variables:
 * 	(List out each output variable and its type. Include range of values or expected outputs where necessary.)
 * 	null
 * 
 * The advertisement screen will come after splash screen
 * This will show in portrait alone not in landscape
 * 
 * On-Click on the advertisement view the WebView will load the stp site
 * 
 * The advertisement screen will come again if the user goes off (screen lock) and resume again
 *
 * First time this will launch the MainActivity
 * After that this wont launch any activity.
 * It just finish this activity and the last activity will get resumed
 * 
 *  This advertisement activity got call from BroadCastReceivers 
 */

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.evvolutions.android.http.AsyncHttpResponseHandler;
import com.z.stproperty.application.GlobalClass;
import com.z.stproperty.fonts.HelveticaBold;
import com.z.stproperty.shared.ConnectionCheck;
import com.z.stproperty.shared.ConnectionManager;
import com.z.stproperty.shared.ConnectionManager.ConnectionType;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.UrlUtils;

public class Advertisement extends Activity {

	private String compaignstring = "file:///android_asset/splashopen.html", url = "";
	private CountDownTimer countDownTimer;
	private HelveticaBold mTextField;
	private Button skipBtn;
	private WebView webview;
	private ProgressDialog processdialog;
	/**
	 * This will check the internet connection first 
	 * the the connection is not available then the home screen is shown 
	 * otherwise the add is loaded into web-view
	 */
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			 processdialog = ProgressDialog.show(Advertisement.this, "", "Loading...", true);
			 processdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);	  		
			 processdialog.getWindow().setGravity(Gravity.CENTER);
			 processdialog.show();
			 if(ConnectionCheck.checkOnline(Advertisement.this)){
				 ConnectionManager test = new ConnectionManager();
				 test.connectionHandler(Advertisement.this, null, UrlUtils.URL_ADD + 
							SharedFunction.getHashKey() + "&ver=1.0.0", ConnectionType.CONNECTIONTYPE_GET, null, 
						 new AsyncHttpResponseHandler(){
					 @Override
					 public void onSuccess(String response) {
						 addDisplay(response);
					 }

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// Auto-generated method stub
						super.onFailure(arg0, arg1);
						showHomeScreen();
					}
				});
				checkUpdatedVersion(test);
			 }else{
				 showHomeScreen();
				 processdialog.dismiss();
				 Toast.makeText(Advertisement.this, "Please check internet connection", Toast.LENGTH_SHORT).show();
			 }
		} catch (Exception e) {
			finish();
			processdialog.dismiss();
			String dsss = e.toString().substring(9, 20);
			if (dsss.equals("UnknownHost")) {
				Toast.makeText(Advertisement.this, "Please check internet connection", Toast.LENGTH_SHORT).show();
			}
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * @param response :: Server response to show or skip advertisement
	 * 
	 * IF the response status is fail then the home screen is shown with add
	 * otherwise the add is loaded into web-view
	 */
	private void addDisplay(String response){
		try {
			 processdialog.dismiss();
			 JSONObject json = new JSONObject(response);
			 if(((String) json.get("status")).equals("fail")){
				 showHomeScreen();
			 }else{
				 loadAdvertizement();
			 }
		} catch (JSONException e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * Will Clears all the Stack from cache memory to start the home screen fresh
	 * The default Home tab is enabled to show the home screen in tab-activity
	 * and the current activity is finished from the view
	 */
	private void showHomeScreen(){
		Intent intent = new Intent(getBaseContext(), MainActivity.class);
		intent.putExtra("front", "front");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}
	/**
	 * loadAdvertizement() :: Method
	 * 
	 * This will check the advertisement display from server 
	 * And if that returns true to display the add this will load the add in web-view
	 * If user clicks on the add again then STP web-site is loaded into the web-view
	 * and there is an option is enabled to open this web-site in browser as well
	 * 
	 * And the skip button is enabled on successful add load
	 * And the timer is started for 5 seconds 
	 * after the predefined 5 seconds the add will get closed and home screen is loaded automatically
	 * 
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void loadAdvertizement(){
		setContentView(R.layout.advertisement);

		skipBtn = (Button) findViewById(R.id.button2);
		mTextField = (HelveticaBold) findViewById(R.id.textView1);
		skipBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SharedFunction.postAnalytics(Advertisement.this, "Advertisement", "Skip", mTextField.getText().toString());
				countDownTimer.cancel();
				showHomeScreen();
			}
		});
		webview = (WebView) findViewById(R.id.genericwebview);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setUseWideViewPort(true);
	    
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String myUrl) {
				if (!myUrl.equals(compaignstring)) {
					countDownTimer.cancel();
					mTextField.setText("");
					((HelveticaBold)findViewById(R.id.OpenInBrowser)).setVisibility(View.VISIBLE);
					skipBtn.setText("Close");
					webview.loadUrl(myUrl);
					url = myUrl;
					return true;
				} else {
					webview.loadUrl(myUrl);
					return true;
				}
			}
			/***
			 * @Param view : WebView
			 * @Param URL : URL to load in WebView
			 * this will check the URL if it equals to file:///android_asset/splashopen.html then the count-down will start to count
			 * else the count-down stop and the WebView is loaded into view
			 * On-Press of back button the user can go to MainActivity or exit from application
			 */
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				processdialog.dismiss();
				if (url.equals("file:///android_asset/splashopen.html")) {
					countDownTimer = new CountDownTimer(5500, 1000) {
						public void onTick(long millisUntilFinished) {
							int i = (int) (millisUntilFinished / 1000);
							mTextField.setText("" + i);
						}
						public void onFinish() {
							mTextField.setText("done!");
							showHomeScreen();
						}
					};
					countDownTimer.start();
					skipBtn.setVisibility(View.VISIBLE);
				}
			}
		});
		webview.loadUrl(compaignstring);
		((HelveticaBold)findViewById(R.id.OpenInBrowser)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
			    i.setData(Uri.parse(url));
			    startActivity(i);
			}
		});
	}
	/**
	 * Override the default back to avoid back option
	 */
	@Override
	public void onBackPressed() {
		// Auto-generated method stub
	}
	private void checkUpdatedVersion(ConnectionManager conn){
		try{
			conn.connectionHandler(getApplicationContext(), null, UrlUtils.URL_VERSION+ 
					SharedFunction.getHashKey() + "&ver=" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName, ConnectionType.CONNECTIONTYPE_GET, null, 
				 new AsyncHttpResponseHandler(){
				 @Override
				 public void onSuccess(String response) {
					 updateVersionDetail(response);
				 }
			});
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	private void updateVersionDetail(String response){
		try{
			if(response != null){
				JSONObject jsonObj = new JSONObject(response);
				if(jsonObj.has("update") && jsonObj.getString("update").equals("true")){
					GlobalClass global = (GlobalClass) getApplication();
					global.setVersionUpdate(false);
				}
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
}
