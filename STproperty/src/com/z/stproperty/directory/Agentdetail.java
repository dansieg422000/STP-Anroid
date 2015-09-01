package com.z.stproperty.directory;

/*********************************************************************************************************
 * Class	: AgentDetail
 * Type		: Activity
 * Date		: 26 01 2014
 * 
 * General Description:
 * 
 * Called by AgentListAdapter.
 * 
 * Will load the agent details in webview.
 * 
 ***********************************************************************************************************/

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.evvolutions.android.http.AsyncHttpResponseHandler;
import com.z.stproperty.BaseActivity;
import com.z.stproperty.R;
import com.z.stproperty.shared.ConnectionCheck;
import com.z.stproperty.shared.ConnectionManager;
import com.z.stproperty.shared.ConnectionManager.ConnectionType;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.UrlUtils;

public class Agentdetail extends BaseActivity {
	private String response = "";
	private ProgressDialog processdialog;
	
	private OnClickListener onClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// Auto-generated method stub
			String url = UrlUtils.URL_SEARCH +"&agent=1&type=1,2,3,4,5,6,7" + "&limit=25&userid=" + getIntent().getStringExtra("userid");
			url = url + "&for=" + (v.getId() == R.id.ForSale ? 2 : (v.getId() == R.id.RoomRental ? 3 : 1));
			Intent intent = new Intent(Agentdetail.this, Agentsalelist.class);
			intent.putExtra("url", url);
			startActivity(intent);
		}
	};
	/**
	 * @param savedInstanceState
	 *            :: If the activity is being re-initialized after previously
	 *            being shut down then this Bundle contains the data it most
	 *            recently supplied in onSaveInstanceState(android.os.Bundle)
	 * 
	 * Called when the activity is starting. This is where most
	 * initialization should go: calling setContentView(int) to
	 * inflate the activity's UI, using findViewById(int) to
	 * programmatically interact with widgets in the UI.
	 * 
	 * Here a ProgressDialog is displayed while the agent detail is 
	 * getting loaded and the agent URL is loaded in a webView.
	 * 
	 */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.agentdetail);
			checkUpdate();
			displayAgentDetails();
			// Screen Tracking
	        SharedFunction.sendGA(getApplicationContext(), "Agents_Directory::Agents_Directory_Ad_Detail");
	        // AT Internet tracking
	        SharedFunction.sendATTagging(getApplicationContext(), "Agents_Directory::Agents_Directory_Ad_Detail", 6, null);
			// Screen Tracking ends
		} catch (Exception e2) {
			Log.e("Exception", "<actual message here", e2);
		}
	}
	/**
	 * Agent details are shown in web-view
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void displayAgentDetails(){
		try {
			if(ConnectionCheck.checkOnline(Agentdetail.this)){
				processdialog = ProgressDialog.show(Agentdetail.this, "", "Loading. Please wait...", true);
				WebView agentDetail = (WebView) findViewById(R.id.agentDetail);
				agentDetail.getSettings().setJavaScriptEnabled(true);
				agentDetail.getSettings().setBuiltInZoomControls(true);
				/**agentDetail.setWebViewClient(new WebViewClient() {
			           @Override
			           public boolean shouldOverrideUrlLoading(WebView view, String myUrl) {
			        	   return true;
			           }
			 		   @Override
			 		   public void onPageFinished(WebView view, String url) {
			 			   super.onPageFinished(view, url);
				 		   processdialog.dismiss();
			 		   }
			 	});*/
				String url = UrlUtils.URL_DIRECTORY_AGENTDETAIL + "&hash=" + SharedFunction.getHashKey() + "&userid=" + getIntent().getStringExtra("userid");
				agentDetail.loadUrl(url);
			 }else{
				 processdialog.dismiss();
				 Toast.makeText(Agentdetail.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
				 finish();
			 }
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			finish();
		}
	}
	@Override
	public void checkUpdate(){
		try{
			String url = UrlUtils.URL_AGENT_DIR_COUNT + SharedFunction.getHashKeyWithId(getIntent().getStringExtra("userid")) + "&userid=" + getIntent().getStringExtra("userid");
			if(ConnectionCheck.checkOnline(Agentdetail.this)){
				 ConnectionManager test = new ConnectionManager();
				 test.connectionHandler(Agentdetail.this, null, url, ConnectionType.CONNECTIONTYPE_GET, null, new AsyncHttpResponseHandler(){
					 @Override
					 public void onSuccess(String responseStr) {
						 response = responseStr;
					 }
					 @Override
					 public void onFailure(Throwable arg0, String arg1) {
						 processdialog.dismiss();
						 updateSettings(Constants.LOWINTERNETSTR);
					 }
					 @Override
					 public void onFinish() {
						 processdialog.dismiss();
						 displayDetails();
					 }
				});
			 }else{
				 processdialog.dismiss();
				 updateSettings(Constants.NETWORKSTR);
			 }
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			finish();
		}
	}
	private void displayDetails(){
		try{
			JSONObject jsonObj = new JSONObject(response);
			jsonObj = jsonObj.getJSONObject("result");
			String saleCount = jsonObj.has("forsalecount") ?  jsonObj.getString("forsalecount").replace("null", "0"): "0";
			String rentCount = jsonObj.has("forrentcount") ?  jsonObj.getString("forrentcount").replace("null", "0"): "0";
			String rentalCount = jsonObj.has("forroomrentalcount") ?  jsonObj.getString("forroomrentalcount").replace("null", "0"): "0";
			Button forSale = (Button) findViewById(R.id.ForSale);
			if(saleCount.equals("0")){
				forSale.setBackgroundResource(R.drawable.green_btn_disables);
				forSale.getBackground().setAlpha(128);
			}else{
				forSale.setOnClickListener(onClick);
				forSale.setText(forSale.getText() + " (" +saleCount + ")");
			}
			Button forRent = (Button) findViewById(R.id.ForRent);
			if(rentCount.equals("0")){
				forRent.setBackgroundResource(R.drawable.green_btn_disables);
				forRent.getBackground().setAlpha(128);
			}else{
				forRent.setOnClickListener(onClick);
				forRent.setText(forRent.getText() + " (" +rentCount + ")");
			}
			Button forRental = (Button) findViewById(R.id.RoomRental);
			if(rentalCount.equals("0")){
				forRental.setBackgroundResource(R.drawable.green_btn_disables);
				forRental.getBackground().setAlpha(128);
			}else{
				forRental.setOnClickListener(onClick);
				forRental.setText(forRental.getText() + " (" +rentalCount + ")");
			}
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			finish();
		}
	}
}
