package com.z.stproperty.directory;

/*********************************************************************************************************
 * Class	: DirectoryFragment
 * Type		: Fragment
 * Date		: 25 Jan 2013
 * 
 * General Description:
 * 
 * Will load the condo directory, commercial directory and industrial directory
 * 
 * Called by Directory class.
 * This class is responsible for the following :
 * 
 *  - Searching of Directory Details
 *  - Alphabetical sorting of Directory Details
 *  - District wise sorting f Directory details
 * 
 * Few important layouts
 *  	directories_list :: ListView that is displays the directories list.
 *  	custom_directoriesdetail :: Layout that displays the directory detail.
 *  
 *  Adapters :
 *  	DirectoryAdapter :: This adapter displays the directory details in the 
 *  						directories_list.
 *  Description :
 * 
 * onCreateView()
 * 		This sets the UI for the fragment. It also handles the operations for search edittext, 
 * 		grid button, district button and search button.
 * 		This also sets the adapter for the directories listview.
 * 
 * loadDirectories()
 * 		This will get the required contents from the URL with the provided resources 
 * 		to the JSONObject. First it will perform an Internet connection check and will
 * 		prompt the user if there is no Internet connection.If there is Internet connection,
 * 		the URL is loaded to ConnectionHandler which processes the URL and returns the response.
 * 		If the response status is fail, the loadDirectoriesList() is called. 
 * 
 * loadDirectoriesList()
 * 		Takes the JSON input and loads the contents to the listview.and will load it to the menu-items
 * 		HashMap. This also checks weather all the contents have been added to the 
 * 		list and respectively updates the loadMore flag.
 * 
 * OnScrolled :: is an interface 
 *	 	This will monitor the ListView items focus
 *	 	Once the last view gets focus then this will check for load-more flag
 *	 	If this flag is set then this will load next set of listings into the current list-view
 * 
 * hideSoftKeyboard()
 * 		If the edit box for search is not selected by user and is not in focus 
 *	    explicitly then the Soft keyboard is hidden using InputMethodManager 
 * 
 * 
 * 
 * ********************************************************************************************************/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evvolutions.android.http.AsyncHttpResponseHandler;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.z.stproperty.R;
import com.z.stproperty.fonts.HelveticaBold;
import com.z.stproperty.shared.ConnectionCheck;
import com.z.stproperty.shared.ConnectionManager;
import com.z.stproperty.shared.ConnectionManager.ConnectionType;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.UrlUtils;

public class DirectoryFragment extends Fragment {

	private static final String ARG_POSITION = "position";

	private int curPosition;
	private Activity parent;
	private ProgressDialog dialog;
	private int pageId = 1, pageLimit = 25;
	private String response="", url = "", directoryType="", searchText = "";
	private List<HashMap<String, String>> menuItems;
	private boolean loadMore = false;
	private EditText editText;
	private DirectoryAdapter directoryAdapter;
	private String typeName;
	private HelveticaBold noData;
	/**
	 * OnScrolled :: is an interface 
	 * This will monitor the ListView items focus
	 * Once the last view gets focus then this will check for load-more flag
	 * If this flag is set then this will load next set of listings into the current list-view
	 */
	private OnScrollListener onScrolled = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// logs and work
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			loadMoreDirectory(firstVisibleItem, visibleItemCount, totalItemCount);
		}
	};
	private void loadMoreDirectory(int firstVisibleItem, int visibleItemCount, int totalItemCount){
		try{
			if((firstVisibleItem + visibleItemCount) == totalItemCount && loadMore && !menuItems.isEmpty()){
				dialog.show();
				loadMore = false;
				pageId++;
				menuItems.remove(menuItems.size()-1);
				loadDirectories();
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * 
	 * @return 	directoryFragment	::	The agent fragment to be displayed in the parent fragment activity.
	 * 
	 * This will instantiate the DirectoryFragment and will return the same instance. 
	 * 
	 */
	public static DirectoryFragment newInstance(int position) {
		DirectoryFragment f = new DirectoryFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}
	@Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
        	String gaScreenName = typeName.replace(" ", "_");
        	if(editText!=null && !editText.getText().toString().equals("")){
        		gaScreenName = gaScreenName + "::" + gaScreenName + "_Search_Result_Page";
        	}else{
        		gaScreenName = gaScreenName + getAnalatics(gaScreenName);
        	}
            postAnalytics(gaScreenName);
        }
    }
	private void postAnalytics(String gaScreenName){
		Tracker easyTracker = EasyTracker.getInstance(parent);
        easyTracker.set(Fields.SCREEN_NAME, gaScreenName);
        easyTracker.send(MapBuilder.createAppView().build());
        SharedFunction.sendATTagging(parent, gaScreenName, gaScreenName.contains("Search") ? 3 : 6, null);
	}
	
	/**
     * Perform initialization of all fragments and loaders.
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		curPosition = getArguments().getInt(ARG_POSITION);
	}
	/**
     * @param inflater   		 :: The inflater to be used.
     * @param ViewGroup  		 :: The view-group container to be used.
     * @param savedInstanceState :: The bundle to be used.
     * 
     * @return View 			 :: Newly created view. Return null for the default
     *         						behavior.
 	 * 
 	 * This sets the UI for the fragment. It also handles the operations for search EditText, grid button and search button.
 	 * This also sets the adapter for the directories ListView.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.directory_listing, null);
		ListView listView = (ListView) view.findViewById(R.id.DirectoryListView);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		parent = getActivity();
		dialog = ProgressDialog.show(parent, "", "Loading. Please wait...", true);
		FrameLayout fl = new FrameLayout(parent);
		fl.setLayoutParams(params);
		fl.addView(view);
		noData = (HelveticaBold) view.findViewById(R.id.NoData);
		menuItems = new ArrayList<HashMap<String,String>>();
		directoryAdapter = new DirectoryAdapter(parent, menuItems);
		listView.setAdapter(directoryAdapter);
		listView.setOnScrollListener(onScrolled);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				try {
					Intent intent = new Intent(getActivity(), DirectoryDetail.class);
					intent.putExtra("id", menuItems.get(position).get("id"));
					intent.putExtra("type", directoryType);
					intent.putExtra("typeName", typeName);
					getActivity().startActivity(intent);
				} catch (Exception e1) {
					Log.e("Exception", "<actual message here", e1);
				}
			}
		});
		editText = (EditText) view.findViewById(R.id.SearchText);
		editText.setHint("Search Project Name");
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                	performSearch();
                	return true;
                }
                return false;
            }
        });
		switch (curPosition) {
		case 1:
			url = UrlUtils.URL_DIRECOTRYLIST + "&type=1";
			typeName = "Condo Directory";
			directoryType = "1";
			break;
		case 2:
			url = UrlUtils.URL_DIRECOTRYLIST + "&type=4,6,7";
			directoryType = "4,6,7";
			typeName = "Commercial Directory";
			break;
		case 3:
			url = UrlUtils.URL_DIRECOTRYLIST + "&type=3";
			directoryType = "3";
			typeName = "Industrial Directory";
			break;
		default:
			break;
		}
		loadDirectories();
		return fl;
	}
	private void performSearch(){
		dialog.show();
    	menuItems.clear();
    	clearFilter();
    	searchText = editText.getText().toString().trim();
    	pageId = 1;
    	loadDirectories();
    	hideKeyboad();
    	String gaScreenName = typeName.replace(" ", "_") + "_Search::" + typeName.replace(" ", "_") + "_Search_Result_Page";
		postAnalytics(gaScreenName);
    	SharedFunction.postAnalytics(getActivity().getApplicationContext(), "Searches", typeName, searchText);
	}
	/**
	 * Based on the position this will clear the filter text
	 * 1	:: condo
	 * 2	:: Commercial
	 * 3	:: Industrial
	 */
	private void clearFilter(){
		switch (curPosition) {
		case 1:
			Directory.condoFilter = "";
			break;
		case 2:
			Directory.comFilter = "";
			break;
		case 3:
			Directory.indusFilter = "";
			break;
		default:
			break;
		}
	}
	private String getAnalatics(String analytics){
		String filter = curPosition==1 ? Directory.condoFilter :(curPosition==2?Directory.comFilter : Directory.indusFilter);
		String filterStr = "_All";
		if(filter.contains("isnew")){
			filterStr = "_New_Projects";
		}else if(filter.contains("ispopular")){
			filterStr = "_Popular";
		}else if(filter.contains("district")){
			filterStr = "_District";
		}else if(filter.equals("")){
			filterStr = "_Home";
		}
		return "::" + analytics + filterStr;
	}
	/**
	 * This will get the required contents from the URL with the provided 
	 * resources to the JSONObject. First it will perform an Internet connection 
	 * check and will prompt the user if there is no Internet connection. If there 
	 * is Internet connection, the URL is loaded to ConnectionHandler which processes
	 * the URL and returns the response. If the response status is not fail, 
	 * the loadDirectoriesList() is called. 
	 * 
	 */
	private void loadDirectories(){
		try{
			if(ConnectionCheck.checkOnline(parent)){
				 ConnectionManager test = new ConnectionManager();
				 String filterTxt = curPosition==1 ? Directory.condoFilter :(curPosition==2?Directory.comFilter : Directory.indusFilter);
				 String filter = filterTxt.equals("All") ? "" : filterTxt;
				 String searchUrl = url + "&limit="+pageLimit+"&page=" + pageId + filter;
				 if(!(filterTxt.equals("All") || !filter.equals("")) && !searchText.equals("")){
					 searchUrl = searchUrl + "&name=" + searchText;
				 }
				 searchUrl = searchUrl.replace(" ", "%20");
				 searchUrl = searchUrl.replace("+", "");
				 
				 test.connectionHandler(parent, null, searchUrl, ConnectionType.CONNECTIONTYPE_GET, null, new AsyncHttpResponseHandler(){
					 
					 @Override
					 public void onSuccess(String responseStr) {
						 response = responseStr;
					 }

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						dialog.dismiss();
						Toast.makeText(parent, "Please check your internet connection", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFinish() {
						loadDirectoriesList(response);
						dialog.dismiss();
					}
				});
			 }else{
				 dialog.dismiss();
				 Toast.makeText(parent, "Please check your internet connection", Toast.LENGTH_SHORT).show();
			 }
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		editText.setText("");
	}

	@Override
	public void onResume() {
		super.onResume();
		String filterTxt = curPosition==1 ? Directory.condoFilter :(curPosition==2?Directory.comFilter : Directory.indusFilter);
		if(filterTxt.equals("")){
			 editText.setText(searchText);
		}else{
			 searchText = "";
		}
	}
	/**
	 * Takes the JSON input and loads the contents to the listview.and will load it to the menu-items
	 * HashMap. This also checks weather all the contents have been added to the 
	 * list and respectively updates the loadMore flag.
	 * 
	 * @param json - The JSONObject containing the directory details.
	 */
	private void loadDirectoriesList(String response){
		try{
			JSONObject json = new JSONObject(response);
			if (!((String) json.get("status")).equals("fail")) {
				JSONObject fbfeed = json.getJSONObject("result");
				int totalCount = fbfeed.getInt("total_properties");
				if(totalCount == 0){
					noData.setVisibility(View.VISIBLE);
				}else{
					noData.setVisibility(View.GONE);
					for (int i = 0; i < pageLimit && fbfeed.has("" + i); i++) {
						Map<String, String> map = new HashMap<String, String>();
						JSONObject e1 = fbfeed.getJSONObject("" + i);
						map.put("id", e1.getString("id"));
						map.put("block", e1.getString("block"));
						map.put("name", e1.getString("name"));
						map.put("street", e1.getString("street"));
						menuItems.add((HashMap<String, String>)map);
					}
					directoryAdapter.notifyDataSetChanged();
					loadMore = pageId * pageLimit < totalCount;
					if(loadMore){
						menuItems.add(null);
					}
				}
				directoryAdapter.notifyDataSetChanged();
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * Class	: DirectoryAdapter
	 * Type		: Adapter
	 * Date		: 25 01 2014
	 * 
	 *  This adapter displays the directory details in the directories_list.
	 * 
	 * @author Evvolutions
	 *
	 */
	public class DirectoryAdapter extends BaseAdapter {
	    private LayoutInflater inflater=null;
	    private List<HashMap<String, String>> menuItems;
	    /**
	     * 
	     * @param a :: BaseActivity (For application context purpose)
	     * @param directories :: The array list (Needs to be displayed in list-view)
	     */
	    public DirectoryAdapter(Activity activity, List<HashMap<String,String>> directories) {
	    	menuItems=directories;
	    	inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	  }
	    /**
	     * @return :: Count of array or array-list 
	     */
	    public int getCount() {
	    	return menuItems.size();
	    }
	    /**
	     * @return :: Object at position (List-view position)
	     */
	    public Object getItem(int position) {
	        return position;
	    }
	    /**
	     * @return :: Item's position in list-view
	     */
	    public long getItemId(int position) {
	        return position;
	    }
	    /**
		  * Child View
		  * @param position :: current position
		  * @param convertView :: Current View
		  * @param parent :: Parent View
		  * 
		  * convertView is null on first time 
		  * if convertView is null then we will inflate the view with action inflater
		  * 
		  * convertView Not null 
		  * 	The values are assigned
		  * 	And the convertView is returned back
		  * 
		  *    The view shows like 
		  *    LeftView :: Image and share icon
		  *    Right View :: Name, 
		  *    				block, 
		  *    				street,  etc...
		  *    
		  */
	    public View getView(final int position, View View, ViewGroup parent) {
	    	View convertView = null;
	    	try{
		    	if(menuItems.get(position)==null){
		    		convertView = inflater.inflate(R.layout.loading, null);
		    	}else{
			    	convertView = inflater.inflate(R.layout.directorycondorows, null);
			        Map<String, String> item = new HashMap<String, String>();
			        item = menuItems.get(position);
			        String block= item.get("block");
			        TextView name=(TextView) convertView.findViewById(R.id.textView1); 
			        name.setText(item.get("name"));
			        TextView jobtitle=(TextView) convertView.findViewById(R.id.textView2); 
			        jobtitle.setText((block.equals("null") ? "" : block+" ")+item.get("street"));
		    	}
	    	}catch(Exception e){
	    		Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			}
	    	return convertView;
	    }
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    hideKeyboad();
	}
	/*** 
	 * If the edit box for search is not selected by user and 
	 *  is not in focus explicitly then the Soft keyboard 
	 *  is hidden using InputMethodManager 
	 * 
	 */
	private void hideKeyboad(){
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
	}
}