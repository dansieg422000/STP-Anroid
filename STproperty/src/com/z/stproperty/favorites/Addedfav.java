package com.z.stproperty.favorites;

/***************************************************************
 * Class name:
 * (Addedfav)
 * 
 * Description:
 * (Display the added favourite properties as list)
 * 
 * 
 * Input variables:
 * SharedPreferences mPrefs(all added favourite info is stored in it)
 * 
 * Output variables:
 *  ArrayList<HashMap<String, String>> menuItems(will be used when user click on each item, and it will be pass to property detail)
 *  int position(it indicated which item was clicked)
 *  
 *  Addedfav class will list all the favorites from local database
 * Local database has the favorite property id and respective values to display in listview
 * Details about the favorites are got from server at runtime
 * If the property is unavailable then we will not show the detail screen
 * otherwise the details about the favorite shown 
 * user can also delete this property from their favorites list
 * 
 * INFO tab contains all properties list (favorites)
 * 
 * MAP will locate the exact location of the property in GEO map
 *  
 ****************************************************************/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.z.stproperty.MainActivity;
import com.z.stproperty.PropertyDetail;
import com.z.stproperty.PropertyList;
import com.z.stproperty.PropertyListOnMap;
import com.z.stproperty.R;
import com.z.stproperty.adapter.FavoritesListAdapter;
import com.z.stproperty.database.DatabaseHelper;
import com.z.stproperty.dialog.ConfirmDialog;
import com.z.stproperty.fonts.HelveticaBold;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.SharedFunction;

public class Addedfav extends Activity {
	private ListView list;
	private List<HashMap<String, String>> menuItems;
	private FavoritesListAdapter favAdapter;
	private String tabtitle = "Favorites";
	private ProgressDialog dialog;
	private static HelveticaBold favCount;
	private static ImageView noFav;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorites_list);
		dialog = ProgressDialog.show(Addedfav.this, "", "Loading. Please wait...", true);
		Button mapTab = (Button) findViewById(R.id.propertiesOnMap);
		favCount = (HelveticaBold) findViewById(R.id.PropertyCount);
		favCount.setText("");
		noFav = (ImageView)findViewById(R.id.noFavorites);
		mapTab.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// on-click of map icon this function get called
				Intent mapView = new Intent(getBaseContext(), PropertyListOnMap.class);
				mapView.putExtra("menuItems", (ArrayList<HashMap<String, String>>)menuItems);
				mapView.putExtra("tabtitle", tabtitle);
				mapView.putExtra("count", favCount.getText().toString());
				mapView.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(mapView, 0);
				// Net-rating for map view of the property
			}
		});
		((Button) findViewById(R.id.ClearAll)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!menuItems.isEmpty()){
					Intent intent  =new Intent(getApplicationContext(), ConfirmDialog.class);
					intent.putExtra("acitivty", "favorites");
					startActivityForResult(intent, Constants.REQUESTCODE_CONFIRM);
				}
			}
		});
		list = (ListView) findViewById(R.id.FavoritesList);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				try {
					String type = menuItems.get(position).get("type").toString();
					PropertyList.wantfor = type.equals("For Rent") ? 1 : (type.equals("For Sale") ? 2 : 3);
					Intent i = new Intent(getBaseContext(), PropertyDetail.class);
					i.putExtra("propertyDetail", menuItems.get(position));
					i.putExtra("tabtitle", tabtitle);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				} catch (Exception e1) {
					Log.e(this.getClass().getSimpleName(), e1.getLocalizedMessage(), e1);
				}
			}
		});
		loadFavorites();
	}
	/**
	 * Loads the stored favorites into HASHMAP
	 * and displays into the list-view
	 * If no favorites found then the No Favorite Icon is displayed
	 */
	private void loadFavorites() {
		try {
			menuItems = new ArrayList<HashMap<String, String>>();
			DatabaseHelper database = new DatabaseHelper(getApplicationContext());
			List<HashMap<String, String>> propertyList = database.getFavOrHistory(false);
			if (!propertyList.isEmpty()) {
				noFav.setVisibility(View.GONE);
				int total = propertyList.size();
				for (int i = 0; i < total; i++) {
					menuItems.add((HashMap<String, String>) SharedFunction.getProperty(new JSONObject(propertyList.get(i)), 0));
				}
			}else{
				noFav.setVisibility(View.VISIBLE);
			}
			favAdapter = new FavoritesListAdapter(Addedfav.this, menuItems);
			list.setAdapter(favAdapter);
			resetFavCount(propertyList.size());
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
		dialog.dismiss();
	}
	/**
	 * On Resume this activity from details screen we need to 
	 * reload this because there is an chance for user to un-favorite 
	 * some properties from details screen.
	 * 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if(favAdapter!=null){
			loadFavorites();
		}
		// net-rating implementation
		SharedFunction.sendGA(getApplicationContext(), "Favourites");
		// AT Internet tracking
	    SharedFunction.sendATTagging(getApplicationContext(), "Favourites", 10, null);
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
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	public static void resetFavCount(int count){
		if(count == 0){
			favCount.setText("");
			noFav.setVisibility(View.VISIBLE);
		}else{
			favCount.setText(count +" Favorites");
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
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			favCount.setText("");
			DatabaseHelper database = new DatabaseHelper(getApplicationContext());
			database.clearFavorites();
			Toast.makeText(getApplicationContext(), "Favorites cleared successfully.", Toast.LENGTH_LONG).show();
			menuItems.clear();
			favAdapter.notifyDataSetChanged();
		}
	}
}
