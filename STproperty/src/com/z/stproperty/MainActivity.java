package com.z.stproperty;

/***************************************************************
* Class name:
* (MainActivity)
* 
* Description:
* Main tabs(5 tabs which are) 
* 1. homepage,
* 2. search,
* 3. favorite,
* 4. enquiry and
* 5. more
* 
* 
* Input variables:
* 5 activities which are homepage,search,favorite,enquiry, and more
* 
* Output variables:
* null
* 
****************************************************************/

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TextView;

import com.z.stproperty.favorites.Addedfav;
import com.z.stproperty.profile.EnquiryTab;
import com.z.stproperty.profile.MoreTab;
import com.z.stproperty.search.SearchTab;
import com.z.stproperty.shared.Messages;
 

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	private int tabIndex=0;
    /** 
     * Called when the activity is first created. 
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
       // Resource object to get Drawables
      Resources res = getResources(); 
      // The activity TabHost
      TabHost tabHost = getTabHost();
      // Resusable TabSpec for each tab
      TabHost.TabSpec spec;
      // Reusable Intent for each tab
      Intent intent;
	  Bundle extras=getIntent().getExtras();

		if(getIntent().hasExtra("2")) {
			tabIndex = extras.getInt("2");
		}
      intent = new Intent().setClass(this, HomeActivity.class);
      //$NON-NLS-1$ //$NON-NLS-2$
      spec = tabHost.newTabSpec(Messages.getString("HomeTab.home")).setIndicator(Messages.getString("HomeTab.home"),res.getDrawable(R.drawable.home)).setContent(intent);
      tabHost.addTab(spec);
      //$NON-NLS-1$ //$NON-NLS-2$
      intent = new Intent().setClass(this, SearchTab.class);
      spec = tabHost.newTabSpec(Messages.getString("HomeTab.search")).setIndicator(Messages.getString("HomeTab.search"),res.getDrawable(R.drawable.search)).setContent(intent);
      tabHost.addTab(spec);
      // Do the same for the other tabs
      //$NON-NLS-1$ //$NON-NLS-2$
      intent = new Intent().setClass(this, Addedfav.class);
      spec = tabHost.newTabSpec(Messages.getString("HomeTab.favourites")).setIndicator(Messages.getString("HomeTab.favourites"),res.getDrawable(R.drawable.favourites)).setContent(intent);
      tabHost.addTab(spec);
      //$NON-NLS-1$ //$NON-NLS-2$
      intent = new Intent().setClass(this, EnquiryTab.class);
      spec = tabHost.newTabSpec(Messages.getString("HomeTab.enquiry")).setIndicator(Messages.getString("HomeTab.enquiry"),
                        res.getDrawable(R.drawable.enquiry)).setContent(intent);
      tabHost.addTab(spec);
      //$NON-NLS-1$ //$NON-NLS-2$
      intent = new Intent().setClass(this, MoreTab.class);
      spec = tabHost.newTabSpec(Messages.getString("HomeTab.more")).setIndicator(Messages.getString("HomeTab.more"),
                        res.getDrawable(R.drawable.more)).setContent(intent);
      tabHost.addTab(spec);
     
      tabHost.setCurrentTab(tabIndex);
      for(int i=0;i<tabHost.getTabWidget().getChildCount();i++) {
    	  //Unselected Tabs
          TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); 
          tv.setTextColor(Color.parseColor("#ffffff"));
      }
    }
    /**
	 *  Override the back press to avoid default back
	 */
	@Override
	public void onBackPressed() {
		// Override the back press to avoid default back
	}
	@Override
	protected void onChildTitleChanged (Activity childActivity, CharSequence title) {
		super.onChildTitleChanged(childActivity, title);
		setTitle(title);
	}
}