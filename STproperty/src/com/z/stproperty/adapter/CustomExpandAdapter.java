package com.z.stproperty.adapter;

/***************************************************************
 * Class name:
 * (ExpAdapter)
 * 
 * Description:
 * (ExpandableListAdapter for nearby Amenities)
 * 
 * 
 * Input variables:
 * Context myContext(context of Amenities class)
 * String[][] arrGroupelements(Group name of list( "NEAREST MRT STATIONS", "NEAREST SCHOOLS", "NEAREST SHOPPING MALLS", "NEAREST CHILDCARE CENTRES"))
 * String[][] arrChildelements(Name of Amenities, Distance to amenities)
 * Output variables:
 * null
 *  
 *  Expandable list adapter is used to view the parent and child 
 * categories in same activity (Screen)
 * This makes user more comfortable to switch between categories
 * 
 * If the one parent view is expanded (Children are visible)
 * then the other previously opened views are get contracted
 * 
 ****************************************************************/

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.z.stproperty.R;
import com.z.stproperty.bean.ExpandAdapterBean;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.search.SearchPropertyList;
import com.z.stproperty.shared.SharedFunction;

public class CustomExpandAdapter extends BaseExpandableListAdapter {
	/**
	 * myContext :: Is used in inflating views ExpAdapter :: constructor is used
	 * to get the base-activity application context
	 * 
	 */
	private Activity activity;
	private LayoutInflater inflater = null;
	private int previousGroup = -1;
	private List<ExpandAdapterBean> menuItems;
	private boolean isEnquiry = false;
	public CustomExpandAdapter(Activity baseActivity, List<ExpandAdapterBean> listValues, boolean isEnquiry) {
		activity = baseActivity;
		menuItems = listValues;
		inflater = activity.getLayoutInflater();
		this.isEnquiry = isEnquiry;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	/**
	 * @return childId is returned back
	 * @param childPosition
	 *            :: child position
	 * @param groupPosition
	 *            :: group Position
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	/**
	 * Child View
	 * 
	 * @param isLastChild
	 *            :: The child view is last or not
	 * @param groupPosition
	 *            :: Parent Group position
	 * @param convertView
	 *            :: Current View
	 * @param parent
	 *            :: Parent View
	 * @param childPosition
	 *            :: Child position in parent view
	 * 
	 *            convertView is null on first time if convertView is null then
	 *            we will inflate the view with action inflater
	 * 
	 *            convertView Not null The values are assigned And the
	 *            convertView is returned back
	 * 
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View curView = null;
		if (convertView == null) {
			curView = inflater.inflate(R.layout.savesearch_child, null);
		}else{
			curView = convertView;
		}

		Helvetica header = (Helvetica) curView.findViewById(R.id.header);
		Helvetica value = (Helvetica) curView.findViewById(R.id.valueText);
		header.setText(menuItems.get(groupPosition).getHeaderArray().get(childPosition));
		value.setText(menuItems.get(groupPosition).getValueArray().get(childPosition));

		return curView;
	}

	/**
	 * returns the total count of ChildrenCount (Children views) ** length of
	 * current child at arrChildelements
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		return menuItems.get(groupPosition).getHeaderArray().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	/**
	 * GroupId is returned back Its first time this will be called to collect
	 * the default parent view selected next time the current selected group id
	 * is returned
	 */
	@Override
	public int getGroupCount() {
		return menuItems.size();
	}

	/**
	 * GroupId is returned back Its first time this will be called to collect
	 * the default parent view selected next time the current selected group id
	 * is returned
	 */
	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	/**
	 * ParentView
	 * 
	 * @param isExpanded
	 *            :: The child view is expanded or not
	 * @param groupPosition
	 *            :: Parent Group position
	 * @param convertView
	 *            :: Current View
	 * @param parent
	 *            :: Parent View
	 * 
	 *            convertView is null on first time if convertView is null then
	 *            we will inflate the view with action inflater
	 * 
	 *            convertView Not null The values are assigned And the
	 *            convertView is returned back
	 * 
	 */
	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
		View curView;
		if (convertView == null) {
			curView = inflater.inflate(R.layout.savedsearchrow, null);
		}else{
			curView = convertView;
		}
		
		Helvetica tvGroupName = (Helvetica) curView.findViewById(R.id.SearchName);
		tvGroupName.setText(menuItems.get(groupPosition).getHeaderTxt());
		curView.findViewById(R.id.parentLayout).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(previousGroup != groupPosition){
					if(previousGroup!=-1){
						((ExpandableListView) parent).collapseGroup(previousGroup);
					}
					((ExpandableListView) parent).expandGroup(groupPosition);
				}else{
					((ExpandableListView) parent).collapseGroup(groupPosition);
				}
			}
		});
		if(isEnquiry){
			((Button) curView.findViewById(R.id.SearchAgain)).setVisibility(View.GONE);
		}else{
			((Button) curView.findViewById(R.id.SearchAgain)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent searchIntent = new Intent(activity, SearchPropertyList.class);
					searchIntent.putExtra("url", menuItems.get(groupPosition).getSearchUrl());
					activity.startActivity(searchIntent);
					SharedFunction.postAnalytics(activity, "Engagement", "Search History", "Re-Search");
				}
			});
		}
		return curView;
	}

	/**
	 * Return boolean false (no stableIds)
	 */
	@Override
	public boolean hasStableIds() {
		return false;
	}

	/**
	 * Setting for child view selected
	 * 
	 * Will return childPosition as selected in groupPosition
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
		previousGroup = -1;
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
		previousGroup = groupPosition;
	}

}
