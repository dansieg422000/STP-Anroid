package com.z.stproperty;

/***************************************************************
 * Class name: HomeActivity
 * 
 * Description:
 * 
 * (Home page)
 * 
 * Input variables:
 * 
 *	null
 * Output variables:
 * 
 * 	null
 * 
 * FatherActivity
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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.z.stproperty.application.GlobalClass;
import com.z.stproperty.article.Category;
import com.z.stproperty.dialog.ConfirmDialog;
import com.z.stproperty.dialog.ExitDialog;
import com.z.stproperty.dialog.VersionUpdate;
import com.z.stproperty.directory.Directory;
import com.z.stproperty.nearby.PropertynearbyHome;
import com.z.stproperty.profile.Login;
import com.z.stproperty.profile.Register;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.SharedFunction;

public class HomeActivity extends Activity {
	private Button signout;
	private Button signin, register;
	private ImageButton commercial, forSale, forRent, roomRental;
	private SharedPreferences mPrefs;
	private int propertyWantFor = 2;
	/**
	 * onClick	:: OnClickListener
	 * 
	 * Is common on-click listener for all the buttons and images in home screen
	 * This is grouped into single listener to make easier to alter the code and reduce the 
	 * line of code.
	 * 
	 * This will check the View ID to match with the predefined View-ID to identify
	 * which view is clicked 
	 * 
	 * Based on the view id this will launch new activity for example
	 * R.ID.NewArticles :: Category activity to show articles category 
	 */
	private OnClickListener onClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			performClickAction(v);
		}
	};
	private void performClickAction(View v){
		Intent intent = new Intent(getBaseContext(), Category.class);
		switch (v.getId()) {
		case R.id.NewArticles:
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, 0);
			break;
		case R.id.PropertySearch:
			intent = new Intent(getBaseContext(), MainActivity.class);
			intent.putExtra("2", 1);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
			break;
		case R.id.PropertyNearBy:
			intent = new Intent(getBaseContext(), PropertynearbyHome.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, 0);
			break;
		case R.id.Directories:
			intent = new Intent(getBaseContext(), Directory.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, 0);
			break;
		case R.id.Register:
			intent = new Intent(getBaseContext(), Register.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, 0);
			break;
		case R.id.SignIn:
			intent = new Intent(getBaseContext(), Login.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, 0);
			break;
		case R.id.SignOut:
			intent  =new Intent(getApplicationContext(), ConfirmDialog.class);
			intent.putExtra("acitivty", "signout");
			startActivityForResult(intent, Constants.REQUESTCODE_CONFIRM);
			break;
		case R.id.Commercial:
			if (propertyWantFor != 3) {
				listProperties("Business Space", 3);
			}
			break;
		case R.id.Landed:
			listProperties("Landed", 5);
			break;
		case R.id.Hdb:
			listProperties("HDB/HUDC", 2);
			break;
		case R.id.Condo:
			listProperties("Condo", 1);
			break;
		default:
			break;
		}
	}
	/**
	 * THis is an activity method to create view 
	 * 
	 * The home screen view is assigned to this tab
	 * and for few views the listeners are added 
	 * 
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.homepage);
			commercial = (ImageButton) findViewById(R.id.Commercial);
			forSale = (ImageButton) findViewById(R.id.ForSale);
			forRent = (ImageButton) findViewById(R.id.ForRent);
			roomRental = (ImageButton) findViewById(R.id.RoomRental);
	
			forSale.setImageDrawable(getResources().getDrawable(R.drawable.forsale));
			forRent.setImageDrawable(getResources().getDrawable(R.drawable.forrentgrey));
			roomRental.setImageDrawable(getResources().getDrawable(R.drawable.r_rent_grey));
			commercial.setBackgroundDrawable(getResources().getDrawable(R.drawable.commercialbutton));
			forSale.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					forSale.setImageDrawable(getResources().getDrawable(R.drawable.forsale));
					forRent.setImageDrawable(getResources().getDrawable(R.drawable.forrentgrey));
					roomRental.setImageDrawable(getResources().getDrawable(R.drawable.r_rent_grey));
					commercial.setBackgroundDrawable(getResources().getDrawable(R.drawable.commercialbutton));
					propertyWantFor = 2;
				}
			});
			roomRental.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					roomRental.setImageDrawable(getResources().getDrawable(R.drawable.r_rent_green));
					forSale.setImageDrawable(getResources().getDrawable(R.drawable.forsalegrey));
					forRent.setImageDrawable(getResources().getDrawable(R.drawable.forrentgrey));
					propertyWantFor = 3;
					if (propertyWantFor == 3) {
						commercial.setBackgroundDrawable(getResources().getDrawable(R.drawable.business_spage));
					}
				}
			});
			forRent.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					forSale.setImageDrawable(getResources().getDrawable(R.drawable.forsalegrey));
					forRent.setImageDrawable(getResources().getDrawable(R.drawable.forrent));
					roomRental.setImageDrawable(getResources().getDrawable(R.drawable.r_rent_grey));
					commercial.setBackgroundDrawable(getResources().getDrawable(R.drawable.commercialbutton));
					propertyWantFor = 1;
				}
			});
			// Latest News (article)
			((ImageButton) findViewById(R.id.NewArticles)).setOnClickListener(onClick);
			// search option
			((ImageButton) findViewById(R.id.PropertySearch)).setOnClickListener(onClick);
			//property Near-by
			((ImageButton) findViewById(R.id.PropertyNearBy)).setOnClickListener(onClick);
			//Directory Click
			((ImageButton) findViewById(R.id.Directories)).setOnClickListener(onClick);
			/**
			 * Property Listing for condo, hdb, landed and business space
			 */
			((ImageButton) findViewById(R.id.Condo)).setOnClickListener(onClick);
			((ImageButton) findViewById(R.id.Hdb)).setOnClickListener(onClick);
			((ImageButton) findViewById(R.id.Landed)).setOnClickListener(onClick);
			commercial.setOnClickListener(onClick);
			mPrefs = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
			signin = (Button) findViewById(R.id.SignIn);
			register = (Button) findViewById(R.id.Register);
			signout = (Button) findViewById(R.id.SignOut);
			signout.setOnClickListener(onClick);
			signin.setOnClickListener(onClick);
			register.setOnClickListener(onClick);
			if (mPrefs.contains("userid")) {
				signin.setVisibility(View.GONE);
				register.setVisibility(View.GONE);
			}else{
				signout.setVisibility(View.GONE);
			}
			GlobalClass global = (GlobalClass) getApplication();
			if(!global.isUpdatedVersion()){
				startActivity(new Intent(getApplicationContext(), VersionUpdate.class));
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * There is an option to user can log-in from home screen and come back
	 * When ever this screen resume focus this will check user login details 
	 * from shared preferences 
	 * If user logged-in then the sign-out option is enabed and the remaing option 
	 * are disabled
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (mPrefs != null && mPrefs.contains("userid")) {
			signin.setVisibility(View.GONE);
			register.setVisibility(View.GONE);
			signout.setVisibility(View.VISIBLE);
		}else{
			signin.setVisibility(View.VISIBLE);
			register.setVisibility(View.VISIBLE);
			signout.setVisibility(View.GONE);
		}
		SharedFunction.sendATTagging(getApplicationContext(), "Home", 2, null);
		SharedFunction.sendGA(getApplicationContext(), "Home");
	}

	/**
	 * @param type :: Property Type (condo, hdb, etc..)
	 * @param typeID :: Type ID
	 * 
	 * Starts activity to show all the properties related the requested type
	 */
	private void listProperties(String type, int typeID){
		Intent i = new Intent(getBaseContext(), PropertyList.class);
		i.putExtra("type", type);
		i.putExtra("type1", typeID);
		i.putExtra("wantfor", propertyWantFor);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(i, 0);
	}
	/**
	 * On back press the app will launch the home screen as fresh activity
	 * 
	 * clears all the top saved instances and launches the new activity
	 * 
	 */
	@Override
	public void onBackPressed() {
		try {
			Intent intent = new Intent(getApplicationContext(), ExitDialog.class);
			startActivityForResult(intent, 0);
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
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
	 *            Exit dialog return values handled
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try{
			if (resultCode == RESULT_OK) {
				if(requestCode == Constants.REQUESTCODE_CONFIRM){
					SharedFunction.postAnalytics(HomeActivity.this, "Account", "Sign Out", mPrefs.getString("userid",""));
					mPrefs.edit().remove("userid").commit();
					signout.setVisibility(View.GONE);
					register.setVisibility(View.VISIBLE);
					signin.setVisibility(View.VISIBLE);
					Intent i = new Intent(getBaseContext(), MainActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
					finish();
				}else if (data.getExtras().getBoolean("exit", false)) {
					HomeActivity.this.finish();
					moveTaskToBack(true);
				}
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
}
