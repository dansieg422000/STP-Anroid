package com.z.stproperty.article;

/**
 * @author EVVOLUTIONS
 * 
 * Class name:
 * (Category)
 * 
 * Description:
 * 	Show the Category of property news
 * 
 * Input variables:
 * 	ArrayList<HashMap<String, String>> menuItems(Stored the information property news categories from sever)
 * Output variables:
 * 	String id(The id of category will be passed to property news(property news url need this value))
 * 
 * 
 * ListView contains name and count in one row
 * Second row Short Description about the category
 * 
 * In-case of no network/Low network connection the exceptions are thrown and handled as no properties available
 * 
 * OnBack press this will takes you to home screen
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.evvolutions.android.http.AsyncHttpResponseHandler;
import com.z.stproperty.R;
import com.z.stproperty.adapter.ArticleCategoryAdapter;
import com.z.stproperty.shared.ConnectionCheck;
import com.z.stproperty.shared.ConnectionManager;
import com.z.stproperty.shared.ConnectionManager.ConnectionType;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.UrlUtils;

public class Category extends Activity {
	private String response="";
	private ProgressDialog processdialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		processdialog = ProgressDialog.show(Category.this, "", "Loading...", true);
		processdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);	  		
		processdialog.getWindow().setGravity(Gravity.CENTER);
		processdialog.show();
		loadCategory();
	}
	/**
	 * Will check internet connection to load the data (Article Categories) from server
	 * In-case of low Internet connection (socket exception)
			 * or In-case f no network connection (UnknownHost)
	 */
	private void loadCategory(){
		try {
			if(ConnectionCheck.checkOnline(Category.this)){
				 ConnectionManager test = new ConnectionManager();
				 test.connectionHandler(Category.this, null, UrlUtils.URL_ARTICLECAT+"&hash=" + SharedFunction.getHashKey(),
						 ConnectionType.CONNECTIONTYPE_GET, null,  new AsyncHttpResponseHandler(){
					 @Override
					 public void onSuccess(String responseStr) {
						 processdialog.dismiss();
						 response = responseStr;
					 }

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						processdialog.dismiss();
						Toast.makeText(Category.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFinish() {
						loadCategoryValues();
						processdialog.dismiss();
					}
				});
			 }else{
				 processdialog.dismiss();
				 Toast.makeText(Category.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
			 }
		} catch (Exception e) {
			/**
			 * In-case of low Internet connection (socket exception)
			 * or In-case f no network connection (UnknownHost)
			 */
			String dsss = e.toString().substring(9, 20);
			if (dsss.equals("UnknownHost")) {
				Toast.makeText(Category.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
			}
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	private void loadCategoryValues(){
		try{
			final List<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
			JSONObject json = new JSONObject(response);
			JSONArray categoryJson = json.getJSONArray("categories");
			for (int i = 0; i < categoryJson.length(); i++) {
				Map<String, String> map = new HashMap<String, String>();
				JSONObject categoryObj = categoryJson.getJSONObject(i);

				map.put("name", categoryObj.get("category_name").toString());
				map.put("id", categoryObj.get("id").toString());
				map.put("category_blurb", categoryObj.get("category_blurb").toString());
				map.put("article_category_count", categoryObj.get("article_category_count").toString());

				menuItems.add((HashMap<String, String>)map);
			}
			ArticleCategoryAdapter articleAdapter = new ArticleCategoryAdapter(Category.this, menuItems);
			ListView list = (ListView) findViewById(R.id.CategoryList);
			list.setAdapter(articleAdapter);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {

					Intent intent = new Intent(getBaseContext(), Propertynews.class);
					intent.putExtra("id", menuItems.get(position).get("id"));
					intent.putExtra("category_name", menuItems.get(position).get("name"));
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(intent, 0);
				}
			});
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
}
