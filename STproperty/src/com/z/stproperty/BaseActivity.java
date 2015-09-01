package com.z.stproperty;

/**
 * 
 * @author Evvolutions
 * 
 * Every class can extend this activity. It will show the AD when user back to APP
	* 
	* Input variables:
	* int d(Value will indicate whether user come back to APP or not)
	* 
	* Output variables:
	*  null
 * It acts like base activity for all it subclasses
 * 
 * Few methods are common for all the activities 
 * So making those common methods into oneplace here and calling then from required places
 *  *
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;

import com.evvolutions.android.http.AsyncHttpResponseHandler;
import com.z.stproperty.adapter.PropertyListAdapter;
import com.z.stproperty.dialog.ServiceEnableDialog;
import com.z.stproperty.fonts.HelveticaBold;
import com.z.stproperty.shared.ConnectionCheck;
import com.z.stproperty.shared.ConnectionManager;
import com.z.stproperty.shared.ConnectionManager.ConnectionType;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.SharedFunction;

public abstract class BaseActivity extends Activity {
	protected PropertyListAdapter propertyListAdapter;
	protected ListView propertyList;
	protected HelveticaBold headerText;
	protected ProgressDialog dialog;
	protected int page = 1, total=0;
	protected boolean loadMore = false, sortClick = false, addListAdd = false;
	protected String url = "", tabtitle = "", response ="";
	protected List<HashMap<String, String>> menuItems;
	protected String gaScreenName = "", gaSearchScreenName = "";
	/**
	 * onItemClick	:: OnItemSelectedListener
	 * 
	 * Is common on-click listener for all the buttons and images in home screen
	 * This is grouped into single listener to make easier to alter the code and reduce the 
	 * line of code.
	 * 
	 * This is for sorting properties
	 * based on user selection this will load the properties in order
	 */
	protected OnItemSelectedListener onItemClick = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			onViewSelected(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// nothing
		}
	};
	/**
	 * onItemClick	:: OnItemSelectedListener
	 * 
	 * Is common on-click listener for all the buttons and images in home screen
	 * This is grouped into single listener to make easier to alter the code and reduce the 
	 * line of code.
	 * 
	 * This is for sorting properties
	 * based on user selection this will load the properties in order
	 */
	private void onViewSelected(int position){
		try{
			if(sortClick){
				page = 1;
				loadMore = false;
				dialog.show();
				menuItems.clear();
				propertyListAdapter.notifyDataSetChanged();
				page = 1;
				url = url + "&sortby=" + SharedFunction.getSortBy(position);
				String[] urlArray = url.split("&sortby=");
				if(urlArray.length>1){
					url = urlArray[0] + urlArray[1].replace(urlArray[1].split("&")[0], "") 
								+ "&sortby=" + SharedFunction.getSortBy(position);
				}else{
					url = url + "&sortby=" + SharedFunction.getSortBy(position);
				}
				checkUpdate();
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * OnScrolled :: is an interface 
	 * This will monitor the ListView items focus
	 * Once the last view gets focus then this will check for load-more flag
	 * If this flag is set then this will load next set of properties into the current list-view
	 * and the map pins are redrawn 
	 */
	protected OnScrollListener onScrolled = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// auto generated method
		}
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			try{
				if((firstVisibleItem + visibleItemCount) == totalItemCount && loadMore && !menuItems.isEmpty()){
					dialog.show();
					loadMore = false;
					page = page + 1;
					url = url.replace("&page=" + (page - 1), "&page=" + page);
					checkUpdate();
				}
			}catch(Exception e){
				Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			}
		}
	};
	/**
     * Will set R.layout.propertylist this as layout for the corresponding screen
     * and initialize few variables that depends on the view
     * OnClickListener for Map will check total property count to open the map
     */
	protected void setContentLayout(){
		setContentView(R.layout.propertylist);
		headerText = (HelveticaBold) findViewById(R.id.PropertyCount);
		menuItems = new ArrayList<HashMap<String, String>>();
		dialog = ProgressDialog.show(BaseActivity.this, "", "Loading. Please wait...", true);
		
		Button mapBtn = (Button) findViewById(R.id.propertiesOnMap);
		mapBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (total != 0) {
					Intent mapIntent = new Intent(getBaseContext(), PropertyListOnMap.class);
					mapIntent.putExtra("menuItems", (ArrayList<HashMap<String, String>>) menuItems);
					mapIntent.putExtra("tabtitle", tabtitle);
					mapIntent.putExtra("gaScreenName", gaScreenName);
					mapIntent.putExtra("gaSearchScreenName", gaSearchScreenName);
					mapIntent.putExtra("count", headerText.getText().toString());
					mapIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivityForResult(mapIntent, 0);
				}
			}
		});

		HelveticaBold titleText = (HelveticaBold) findViewById(R.id.headerTitle);
		titleText.setText(tabtitle);
		propertyList = (ListView) findViewById(R.id.propertyListView);
		propertyList.setOnScrollListener(onScrolled);
		/**
		 * 1. Will loop and display all Properties as listview 2. onclick of
		 * listview the user taken into detail screen of corresponding property
		 * 3. On-click of map icon the user taken to map view of the property
		 * location
		 * 
		 */
		propertyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				try {
					if(menuItems.get(position) != null){
						Intent i = new Intent(getBaseContext(), PropertyDetail.class);
						i.putExtra("propertyDetail", menuItems.get(position));
						i.putExtra("tabtitle", tabtitle);
						i.putExtra("gaScreenName", gaScreenName);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivityForResult(i, 0);
					}
				} catch (Exception e) {
					Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
				}
			}
		});
	}
	
	/**
	 * This is common method for all the subclasses activities
	 * to convert the properties from server response to HASHMAP
	 * this been made common to avoid confusion or miss management on code 
	 * 	changes from activity to activity and also to reduce the code duplications
	 * 
	 * loadMore :: true | False 
	 * 		True  : Only there is more properties to load needed from server
	 * 		false : No properties to load
	 */
	protected void loadListValuesToArray(){
		try{
			JSONObject json = new JSONObject(response);
			if (!json.getString("status").equals("fail")) {
				JSONObject resultJsonArray = json.getJSONObject("result");
				total = resultJsonArray.getInt("total_properties");
				for (int i = 0; resultJsonArray.has(i+""); i++) {
					menuItems.add((HashMap<String, String>)SharedFunction.getProperty(resultJsonArray.getJSONObject(i+""), 0));
					if((i+1) % 4 == 0 && addListAdd){
						menuItems.add(null);
					}
				}
				loadMore = page * 25 < total;
				if(propertyListAdapter == null){
					propertyListAdapter = new PropertyListAdapter(BaseActivity.this, menuItems, PropertyList.propertyTypes);
					propertyList.setAdapter(propertyListAdapter);
				}else{
					propertyListAdapter.notifyDataSetChanged();
				}
				String pagerStr = (page * 25 <= total ? page * 25 : total) + " of " + total + " properties";
				headerText.setText(pagerStr);
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
		dialog.dismiss();
	}
	/**
	 * This is common method for all the subclasses activities
	 * to load the properties from server
	 * this been made common to avoid confusion or miss management on code 
	 * 	changes from activity to activity and also to reduce the code duplications
	 * 
	 * Will tries to get the properties details from server
	 * and there may be chances to failure as well
	 * so this will load as asynchronous task to do fast and not to intercept the main thread 
	 */
	protected void getServerValues(){
		try {
			response = "";
			if(ConnectionCheck.checkOnline(BaseActivity.this)){
				 ConnectionManager test = new ConnectionManager();
				 test.connectionHandler(BaseActivity.this, null,  url , ConnectionType.CONNECTIONTYPE_GET, null, new AsyncHttpResponseHandler(){
					 
					 @Override
					 public void onSuccess(String responseStr) {
						 dialog.dismiss();
						 response = responseStr;
					 }

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						dialog.dismiss();
						updateSettings(Constants.LOWINTERNETSTR);
					}

					@Override
					public void onFinish() {
						loadListValuesToArray();
					}
				});
			 }else{
				 dialog.dismiss();
				 updateSettings(Constants.NETWORKSTR);
			 }
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * Abstract method to have definition on sub-classes
	 * to load the data and to do different operations 
	 */
	public abstract void checkUpdate();
	 
    /**
     * 
     * @param type :: Type network failure 	
     * 					1. No internet connection
     * 					2. Low internet connection
     * Will pop-up an dialog box to check their network status.
     */
    protected void updateSettings(String type){
    	Intent intent = new Intent(getApplicationContext(),ServiceEnableDialog.class);
    	intent.putExtra(type, true);
    	startActivityForResult(intent, Constants.REQUESTCODE_SETTINGS);
    }
    
    /**
	  * On restart of these activities it will check for back options and reload
	  * Onrestart the advertisement will get called
	  * 
	  * On resume from background process to visible
	  * this advertisement will displayed to user
	  * 
	  
	 protected void onRestart(){
       super.onRestart();
       if(appResume==1)
       {
      	 Intent i = new Intent(getBaseContext(), Advertisement.class);
           i.putExtra("back", "back");
           startActivityForResult(i, 0);
       }
       appResume=0;
    } 
	  * isApplicationBroughtToBackground 
	  * 
	  * this will check for whether app in running as background process or not
	  * If so then it will restart for advertisement activity
	  * 
	 
   private void isApplicationBroughtToBackground() {
	    ActivityManager am = (ActivityManager) FatherActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningTaskInfo> tasks = am.getRunningTasks(1);
	    if (!tasks.isEmpty()) {
	        ComponentName topActivity = tasks.get(0).topActivity;
	        String saname=FatherActivity.this.getPackageName();
	        String saname1= topActivity.getPackageName();
	        if (!saname.equals(saname1)) {
	            appResume=1;
	        }
	    }
	}*/
}
