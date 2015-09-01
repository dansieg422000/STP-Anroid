package com.z.stproperty.bean;

/*******************************************************************************************
 * Class	: ExpandAdapterBean
 * Type		: BEAN
 * Date		: 19 02 2014
 * 
 * General Description:
 * 
 * The set and get methods are used to set and get the values from this bean
 * 
 * This is a common bean method for saved search and outbox
 * 
 * where as the outbox doesn't contain the url to search again as 
 * like saved search
 *******************************************************************************************/

import java.util.ArrayList;
import java.util.List;

public class ExpandAdapterBean {
	private String headerTxt="";
	private String searchUrl = "";
	private List<String> headerArray = new ArrayList<String>();
	private List<String> valueArray = new ArrayList<String>();
	
	public void setHeaderTxt(String headerTxt) {
		this.headerTxt = headerTxt;
	}
	public String getHeaderTxt() {
		return headerTxt;
	}
	public void setHeaderArray(List<String> headerArray) {
		this.headerArray = headerArray;
	}
	public List<String> getHeaderArray() {
		return headerArray;
	}
	public void setValueArray(List<String> valueArray) {
		this.valueArray = valueArray;
	}
	public List<String> getValueArray() {
		return valueArray;
	}
	public void setSearchUrl(String searchUrl){
		this.searchUrl = searchUrl;
	}
	public String getSearchUrl(){
		return this.searchUrl;
	}
}
