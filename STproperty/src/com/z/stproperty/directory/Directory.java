package com.z.stproperty.directory;

/*********************************************************************************************************
 * Class	: Directory
 * Type		: FragmentActivity
 * Date		: 25 01 2014
 * 
 * Description:
 * 
 * Will show directories
 * 
 * 1. Agent Directory
 * 2. condo directory
 * 3. commercial directory
 * 4. industrial directory
 * 
 * Agent :: On Click of directory list the details are shown into dialog
 * 
 * For all other activity
 * 
 * 1. The dialog box contains the gallery and details 
 * 		(count of for sale and for rent properties)
 * 2. Map Will contains the location of property
 * 3. Additional information
 * 4. Near by aminities 
 * 
 * all are shown in tab-views
 * 
 * ********************************************************************************************************/

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.z.stproperty.R;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.PagerSlidingTabStrip;
import com.z.stproperty.shared.SharedFunction;

public class Directory  extends FragmentActivity{
	private ViewPager pager;
	static String agentFilter = "", condoFilter = "", comFilter = "", indusFilter = "";
	private MyPagerAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      try {
		      setContentView(R.layout.directory_listpager);
		      agentFilter = "";
		      condoFilter = "";
		      comFilter = "";
		      indusFilter = "";
		      if (android.os.Build.VERSION.SDK_INT > 9) {
					StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
							.permitAll().build();
					StrictMode.setThreadPolicy(policy);
		      }
		      PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		      pager = (ViewPager) findViewById(R.id.pager);
		      adapter = new MyPagerAdapter(getSupportFragmentManager());
		      pager.setOffscreenPageLimit(4);
		      pager.setAdapter(adapter);

		      tabs.setViewPager(pager);
		      ((Button)findViewById(R.id.FilterDirectory)).setOnClickListener(new OnClickListener() {
				
		    	  @Override
		    	  public void onClick(View v) {
					openFilter();
		    	  }
		      });
				String gaScreenName = "Agents_Directory::" + "Agents_Directory_Home";
				Tracker easyTracker = EasyTracker.getInstance(getApplicationContext());
		        easyTracker.set(Fields.SCREEN_NAME, gaScreenName);
		        easyTracker.send(MapBuilder.createAppView().build());
		        SharedFunction.sendATTagging(getApplicationContext(), gaScreenName, 6, null);
		  } catch (Exception e2) {
			  Log.e("Exception", "<actual message here", e2);
		  }
	 }
	 /**
	  * Opens new activity for user to choose the filter 
	  * Based on the selection the fragment is refreshed
	  */
	 private void openFilter(){
		 Intent intent = new Intent(getApplicationContext(), DirectoryFilter.class);
		 intent.putExtra("position", pager.getCurrentItem());
		 startActivityForResult(intent, Constants.REQUEST_FILTER);
	 }
	 /**
	  * 
	  * Class		: MyPagerAdapter
	  * Type		: FragmentPagerAdapter
	  * Date		: 25 Jan 2013
	  * 
	  * This is the PagerAdapter to load the directory contents. It calls
	  * the new instances of AgentFragment and DirectoryFragments in it's
	  * getItem method.
	  * 
	  * 
	  * @author Evvolutions
	  *
	  */
	 public class MyPagerAdapter extends FragmentPagerAdapter {

		private String[] tabTitles = { "Agent", "Condo", "Commercial", "Industrial"};

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tabTitles[position];
		}

		@Override
		public int getCount() {
			return tabTitles.length;
		}
		public int getItemPosition(Object object) {
		   return POSITION_NONE;
		}
		@Override
		public Fragment getItem(int position) {
			if(position == 0){
				return AgentFragment.newInstance(position);
			}else{
				return DirectoryFragment.newInstance(position);
			}
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
	 */
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if(arg1 == RESULT_OK && arg0 == Constants.REQUEST_FILTER){
			String filterText = arg2.getStringExtra("filterParam");
			switch (pager.getCurrentItem()) {
			 case 0:
				 agentFilter = filterText;
				 postAnalytics("Agents_Directory::" +  
						 (filterText.equals("All")  ? "Agents_Directory_All" : "Agents_Directory_Featured"));
				 break;
			 case 1:
				 condoFilter = filterText;
				 postAnalytics("Condo_Directory::Condo_Directory" + getAnalatics(filterText));
				 break;
			 case 2:
				 comFilter = filterText;
				 postAnalytics("Commercial_Directory::Commercial_Directory"+getAnalatics(filterText));
				 break;
			 case 3:
				 indusFilter = filterText;
				 postAnalytics("Industrial_Directory::Industrial_Directory"+getAnalatics(filterText));
				 break;
			 default:
				 break;
			}
			View selectedView = pager.getChildAt(pager.getCurrentItem());
			((EditText)selectedView.findViewById(R.id.SearchText)).setText("");
			adapter.notifyDataSetChanged();
		}
	}
	private String getAnalatics(String analytics){
		String filterStr = "_All";
		if(analytics.contains("isnew")){
			filterStr = "_New_Projects";
		}else if(analytics.contains("ispopular")){
			filterStr = "_Popular";
		}else if(analytics.contains("district")){
			filterStr = "_District";
		}
		return filterStr;
	}
	private void postAnalytics(String gaScreenName){
		Tracker easyTracker = EasyTracker.getInstance(Directory.this);
        easyTracker.set(Fields.SCREEN_NAME, gaScreenName);
        easyTracker.send(MapBuilder.createAppView().build());
        SharedFunction.sendATTagging(Directory.this, gaScreenName, 6, null);
	}
}
