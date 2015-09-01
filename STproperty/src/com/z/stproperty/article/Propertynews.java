package com.z.stproperty.article;

/**
 * 
 * Description:
 * (Show The Properties news with different categories)

 * Input variables:
 * 	ArrayList<HashMap<String, String>> menuItems(Need to get property news information)
 *  
 * Output variables:
 *  null
 * 
 * ListView contains Share button and photo in left
 * Right View Name, Author, Date and designation are shown as verticle view
 * 
 * OnClick of share icon, user can share this article to his friends with
 * 
 * FaceBook
 * Twitter
 * Email
 * 
 * Incase of no network/Low network connection the exceptions are thrown and handled as no properties availabel
 * 
 * OnBack press this will takes you to home screen
 * Full authorize method.
 * 
 * To share article on facebook/twitter the user has to login to the application
 *
 * Starts either an Activity or a dialog which prompts the user to log in to
 * Facebook and grant the requested permissions to the this application.
 *
 * This method will, when possible, use Facebook's single sign-on for
 * Android to obtain an access token. This involves proxying a call through
 * the Facebook for Android stand-alone application, which will handle the
 * authentication flow, and return an OAuth access token for making API
 * calls.
 *
 * Because this process will not be available for all users, if single
 * sign-on is not possible, this method will automatically fall back to the
 * OAuth 2.0 User-Agent flow. In this flow, the user credentials are handled
 * by Facebook in an embedded WebView, not by the client application. As
 * such, the dialog makes a network request and renders HTML content rather
 * than a native UI. The access token is retrieved from a redirect to a
 * special URL that the WebView handles.
 *
 * Note that User credentials could be handled natively using the OAuth 2.0
 * Username and Password Flow, but this is not supported by this SDK.
 *
 * See http://developers.facebook.com/docs/authentication/ and
 * http://wiki.oauth.net/OAuth-2 for more details.
 *
 * Note that this method is asynchronous and the callback will be invoked in
 * the original calling thread (not in a background thread).
 *
 * Also note that requests may be made to the API without calling authorize
 * first, in which case only public information is returned.
 *
 * IMPORTANT: Note that single sign-on authentication will not function
 * correctly if you do not include a call to the authorizeCallback() method
 * in your onActivityResult() function! Please see below for more
 * information. single sign-on may be disabled by passing FORCE_DIALOG_AUTH
 * as the activityCode parameter in your call to authorize().
 * 
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
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evvolutions.android.http.AsyncHttpResponseHandler;
import com.z.stproperty.R;
import com.z.stproperty.adapter.ArticleListAdapter;
import com.z.stproperty.shared.ConnectionCheck;
import com.z.stproperty.shared.ConnectionManager;
import com.z.stproperty.shared.ConnectionManager.ConnectionType;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.UrlUtils;

public class Propertynews extends Activity {
	private List<HashMap<String, String>> menuItems;
	private ArticleListAdapter articleListAdpter;
	private ListView list;

	private boolean loadMore = false;
	private int page = 1, totalActricle = 0;
	private TextView countDisplay;
	private String response="";
	private String categoryId;
	private ProgressDialog dialog;
	
	/**
	 * OnScrolled :: is an interface 
	 * This will monitor the ListView items focus
	 * Once the last view gets focus then this will check for load-more flag
	 * If this flag is set then this will load next set of listings into the current list-view
	 */
	private OnScrollListener onScrolled = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// Auto-generated method stub
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if((firstVisibleItem + visibleItemCount) == totalItemCount && loadMore && !menuItems.isEmpty()){
				dialog.show();
				loadMore = false;
				page++;
				menuItems.remove(menuItems.size()-1);
				checkUpdate();
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.propertynews);
		countDisplay = (TextView) findViewById(R.id.articleCount);
		Bundle extras = getIntent().getExtras();
		if (getIntent().hasExtra("id")) {
			categoryId = extras.getString("id");
		}
		
		list = (ListView) findViewById(R.id.ArticleList);
		menuItems = new ArrayList<HashMap<String, String>>();
		dialog = ProgressDialog.show(Propertynews.this,"", "Loading. Please wait...", true);
		
		checkUpdate();
		/**
		 * OnItemClick of ListView the following details are passed to the
		 * article details screen
		 * 
		 * 1. content (HTML content) 2. photo1 (Photos for gallery view) 3. date
		 * (Date of Post) 4. author1 (Author of this article) 5. des1
		 * (Description) 6. source 7. title (title of article) 8. link (Article
		 * Link Used in sharing with social networks)
		 * 
		 */
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				try {
					Intent articleDetail = new Intent(getBaseContext(),Propertynewsdetail.class);
					articleDetail.putExtra("articleblurb", menuItems.get(position).get("articleblurb"));
					articleDetail.putExtra("content", menuItems.get(position).get("content"));
					articleDetail.putExtra("date", menuItems.get(position).get("date"));
					articleDetail.putExtra("author", menuItems.get(position).get("author"));
					articleDetail.putExtra("photo", menuItems.get(position).get("photo"));
					articleDetail.putExtra("source", menuItems.get(position).get("source"));
					articleDetail.putExtra("title", menuItems.get(position).get("title"));
					articleDetail.putExtra("link", menuItems.get(position).get("link"));
					articleDetail.putExtra("category_name", getIntent().getStringExtra("category_name"));
					startActivity(articleDetail);
				} catch (Exception e1) {
					Log.e(this.getClass().getSimpleName(), e1.getLocalizedMessage(), e1);
				}
			}
		});
		list.setOnScrollListener(onScrolled);
	}
	/**
	 * Loads the property details from server with asynchronous
	 * without disturbing the main thread
	 */
	private void checkUpdate() {
		try {
			if(ConnectionCheck.checkOnline(Propertynews.this)){
				ConnectionManager test = new ConnectionManager();
				String url = UrlUtils.URL_ARTICLELIST+"&hash="+ SharedFunction.getHashKey() + "&pg=" + page + "&limit=25" + "&cat=" + categoryId;
				test.connectionHandler(Propertynews.this, null, url, ConnectionType.CONNECTIONTYPE_GET, null, 
						 new AsyncHttpResponseHandler(){
					 @Override
					 public void onSuccess(String responseStr) {
						 response = responseStr;
					 }

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						dialog.dismiss();
						Toast.makeText(Propertynews.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFinish() {
						loadArticles();
					}
				});
			 }else{
				 dialog.dismiss();
				 Toast.makeText(Propertynews.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
			 }
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * convert the articles from server response to HASHMAP
	 * this been made common to avoid confusion or miss management on code 
	 * 	changes from activity to activity and also to reduce the code duplications
	 * 
	 * loadMore :: true | False 
	 * 		True  : Only there is more properties to load needed from server
	 * 		false : No properties to load
	 */
	private void loadArticles(){
		try{
			JSONObject json = new JSONObject(response);
			String result1 = (String) json.get("status");
			totalActricle = Integer.parseInt((String) json.get("count"));

			if (!result1.equals("fail")) {
				JSONArray articlePost = json.getJSONArray("posts");
				for (int i = 0; i < articlePost.length(); i++) {
					Map<String, String> map = new HashMap<String, String>();
					JSONObject listItem = articlePost.getJSONObject(i).getJSONObject("post");
					map.put("title", (String) listItem.get("title"));
					map.put("author", listItem.getString("author"));
					map.put("date", (String) listItem.get("publishdate"));
					map.put("photo", (String) listItem.get("thumbnail_photo"));
					map.put("articleblurb", listItem.getString("articleblurb"));
					map.put("photo", (String) listItem.get("photo"));
					map.put("source", (listItem.isNull("source") || listItem.getString("source").equals("null")) ? "" : listItem.getString("source"));
					map.put("content", listItem.getString("content"));
					map.put("link", listItem.getString("permalink"));
					menuItems.add((HashMap<String, String>)map);
				}
				loadMore = page * 25 < totalActricle;
				if(loadMore){
					menuItems.add(null);
				}
				String pagerStr = (page * 25<= totalActricle ? page * 25 : totalActricle) + " of " + totalActricle + " articles";
				countDisplay.setText(pagerStr);
				if(articleListAdpter==null){
					articleListAdpter = new ArticleListAdapter(Propertynews.this, menuItems);
					list.setAdapter(articleListAdpter);
				}else{
					articleListAdpter.notifyDataSetChanged();
				}
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
		dialog.dismiss();
	}
	
	@Override
	protected void onResume() {
		// Auto-generated method stub
		super.onResume();
		// Screen Tracking
        String gaScreenName = getIntent().getStringExtra("category_name").replace(" ", "_");
        gaScreenName = gaScreenName + "::" + gaScreenName + "_Listing";
        SharedFunction.sendGA(getApplicationContext(), gaScreenName);
        // AT Internet tracking
        SharedFunction.sendATTagging(getApplicationContext(), gaScreenName, 4, null);
		// Screen Tracking ends
	}
}
