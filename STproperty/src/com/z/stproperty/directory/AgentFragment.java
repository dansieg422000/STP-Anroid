package com.z.stproperty.directory;

/*********************************************************************************************************
 * Class	: AgentFragment
 * Type		: Fragment
 * Date		: 25 01 2014
 * 
 * General Description:
 * 
 * Called by Directory class.
 * 
 * Will load the agent directory.
 * 
 * This class is responsible for the following :
 * 
 *  - Searching of Agent Details
 *  - Sorting of Agent Details
 * 
 *  Few important layouts
 *  directories_list :: ListView that is displays the agent list.
 *  
 * 
 * Description :
 * 
 * onCreateView()
 * 		This sets the UI for the fragment. It also handles the operations for
 * 		search EditText, grid button and search button. This also sets the adapter
 * 		for the directories ListView.
 * 
 * loadAgentDirectories()
 * 		This will get the required contents from the URL with the provided resources
 * 		to the JSONObject. First it will perform an Internet connection check and will
 * 		prompt the user if there is no Internet connection. If there is Internet connection,
 * 		the URL is loaded to ConnectionHandler which processes the URL and returns the 
 * 		response. If the response status is fail, the loadagentList() is called. 
 * 
 * loadAgentList() 
 *  	This will get the agent details from the JSONObject and will load it to the menu-items
 * 		(AgentsDetails ArrayList) list. It also performs null checks before adding the contents 
 * 		to menu-items. This also checks weather all the contents have been added to the 
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
import com.z.stproperty.adapter.AgentListAdapter;
import com.z.stproperty.fonts.HelveticaBold;
import com.z.stproperty.shared.ConnectionCheck;
import com.z.stproperty.shared.ConnectionManager;
import com.z.stproperty.shared.ConnectionManager.ConnectionType;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.UrlUtils;

public class AgentFragment extends Fragment {

	private static final String ARG_POSITION = "position";

	private Activity parent;
	private ProgressDialog dialog;
	private int pageId = 1, pageLimit = 25;
	private String response="", url = "", searchText="";
	private List<HashMap<String, String>> menuItems;
	private boolean loadMore = false;
	private AgentListAdapter agentAdapter;
	private EditText editText;
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
			// nothing
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
				loadMoreDirectory(firstVisibleItem, visibleItemCount, totalItemCount);
			}
		};
		private void loadMoreDirectory(int firstVisibleItem, int visibleItemCount, int totalItemCount){
			try{
				if((firstVisibleItem + visibleItemCount) == totalItemCount  && loadMore && !menuItems.isEmpty()){
					dialog.show();
					loadMore = false;
					pageId++;
					menuItems.remove(menuItems.size()-1);
					loadAgentDirectories();
				}
			}catch(Exception e){
				Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			}
		}
	/**
	 * 
	 * @return 	agentFragment	::	The agent fragment to be displayed in the parent fragment activity.
	 * 
	 * This will instantiate the AgentFragment and will return the same instance. 
	 * 
	 */
	public static AgentFragment newInstance(int position) {
		AgentFragment f = new AgentFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}
	/**
 	 * 		
 	 *   
     * @param inflater   		 :: The inflater to be used.
     * @param ViewGroup  		 :: The view-group container to be used.
     * @param savedInstanceState :: The bundle to be used.
     * 
     * @return View 			 :: Newly created view. Return null for the default
     *         behavior.
 	 * 
 	 * This sets the UI for the fragment. It also handles the operations for search EditText, grid button and search button.
 	 * This also sets the adapter for the directories ListView.
 	 * 
	 * 
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
		menuItems = new ArrayList<HashMap<String,String>>();
		agentAdapter = new AgentListAdapter(parent, menuItems);
		noData = (HelveticaBold) view.findViewById(R.id.NoData);
		listView.setAdapter(agentAdapter);
		listView.setOnScrollListener(onScrolled);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				try {
					Intent intent = new Intent(getActivity(), Agentdetail.class);
					intent.putExtra("userid", menuItems.get(position).get("userid"));
					getActivity().startActivity(intent);
				} catch (Exception e1) {
					Log.e("Exception", "<actual message here", e1);
				}
			}
		});
		editText = (EditText) view.findViewById(R.id.SearchText);
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
		url = UrlUtils.URL_AGENTSLIST;
		loadAgentDirectories();
		return fl;
	}
	private void performSearch(){
		dialog.show();
    	menuItems.clear();
    	pageId = 1;
    	Directory.agentFilter = "";
    	searchText = editText.getText().toString().trim();
    	loadAgentDirectories();
    	hideKeyboad();
    	Tracker easyTracker = EasyTracker.getInstance(parent);
        easyTracker.set(Fields.SCREEN_NAME, "Agent_Directory_Search::Agent_Directory_Search_Result_Page");
        easyTracker.send(MapBuilder.createAppView().build());
    	SharedFunction.sendATTagging(getActivity(), "Agent_Directory_Search::Agent_Directory_Search_Result_Page", 3, null);
    	SharedFunction.postAnalytics(getActivity().getApplicationContext(), "Searches", "Agent Directory",searchText);
	}
	@Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
        	String filter = Directory.agentFilter;
			String gaScreenName = "Agents_Directory::" + "Agents_Directory_" + (filter.equals("") ? "Home" :
								(filter.equals("All")  ? "All" : "Agents_Directory_Featured"));
			int level2Id = 6;
			if(editText!=null && !editText.getText().toString().equals("")){
				gaScreenName = "Agents_Directory::Agent_Directory_Search_Result_Page";
				level2Id = 3;
			}
			Tracker easyTracker = EasyTracker.getInstance(parent);
	        easyTracker.set(Fields.SCREEN_NAME, gaScreenName);
	        easyTracker.send(MapBuilder.createAppView().build());
	        SharedFunction.sendATTagging(parent, gaScreenName, level2Id, null);
        }
    }
	/**
	 * This will get the required contents from the URL with the provided resources 
	 * to the JSONObject. First it will perform an Internet connection check and will 
	 * prompt the user if there is no Internet connection. If there is Internet 
	 * connection, the URL is loaded to ConnectionHandler which processes the URL 
	 * and returns the response.If the response status is not fail, the loadagentList() 
	 * is called. 
	 * 
	 */
	private void loadAgentDirectories(){
		try{
			String filter = Directory.agentFilter.equals("All") ? "" : Directory.agentFilter;
			String searchUrl = url + "&sortby=priority&limit="+pageLimit+"&page=" + pageId + filter;
			if(!(Directory.agentFilter.equals("All") || !filter.equals("")) && !searchText.equals("")){
				 searchUrl = searchUrl + "&name=" + searchText;
			}
			if(ConnectionCheck.checkOnline(parent)){
				 ConnectionManager test = new ConnectionManager();
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
						loadAgentList(response);
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
	/**
	 * On Pause clear the text value
	 */
	@Override
	public void onPause() {
		super.onPause();
		editText.setText("");
	}
	/**
	 * On Resume this will check for filter values 
	 * if the filter is empty then values is stored back to editbox 
	 * otherwise cleared from editbox
	 */
	@Override
	public void onResume() {
		super.onResume();
		if(Directory.agentFilter.equals("")){
			 editText.setText(searchText);
		}else{
			 searchText = "";
		}
	}
	/**
	 * 
	 * @param json :: The JSONObject containing the agent details.
	 * 
	 * loadAgentList() will get the agent details from the JSONObject and 
	 * will load it to the menu-items (AgentsDetails ArrayList) list. It also 
	 * performs null checks before adding the contents to menu-items. This also 
	 * checks weather all the contents have been added to the list and respectively 
	 * updates the loadMore flag.
	 * 
	 * 
	 */
	private void loadAgentList(String response){
		try{
			JSONObject json = new JSONObject(response);
			if (!((String) json.get("status")).equals("fail")) {
				JSONObject fbfeed = json.getJSONObject("result");
				int totalCount = Integer.parseInt(fbfeed.get("total_agents").toString());
				if(totalCount == 0){
					noData.setVisibility(View.VISIBLE);
				}else{
					noData.setVisibility(View.GONE);
					for (int i = 0; i < pageLimit && fbfeed.has("" + i); i++) {
						Map<String, String> map = new HashMap<String, String>();
						JSONObject e1 = fbfeed.getJSONObject("" + i);
						String firstname = e1.get("firstname").toString();
						String lastname = e1.get("lastname").toString();
						String name = firstname + " " + lastname;
						String jobtitle = e1.get("jobtitle").toString();
						if (jobtitle.equals("null")) {
							jobtitle = "-";
						}
						String agency = e1.get("agency").toString();
						if (agency.equals("null")) {
							agency = "-";
						}
						String mobileno = e1.get("mobileno").toString();
						if (agency.equals("null")) {
							mobileno = "-";
						}
						map.put("userid", e1.get("userid").toString());
						map.put("picture", e1.get("picture").toString());
						map.put("name", name);
						map.put("jobtitle", jobtitle);
						map.put("agency", agency + "");
						map.put("mobileno", mobileno);
						menuItems.add((HashMap<String, String>)map);
					}
					agentAdapter.notifyDataSetChanged();
					
					loadMore = pageId * 25 < totalCount;
					if(loadMore){
						menuItems.add(null);
					}
				}
				agentAdapter.notifyDataSetChanged();
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
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