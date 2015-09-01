package com.z.stproperty.application;


/*********************************************************************************************************
 * Class	: Globalclass
 * Type		: Application
 * Date		: 18 Set 2013
 * 
 * Description:
 * 
 * This class is created when the application installed and launched first time
 * 	for subsequent launches this class is reused
 * 
 * This is an application wise class that has all data throughout the application
 * 
 * Even if you go off (exit) and come again this values are remains
 * 
 * We are updating the variable values that are used throughout the application
 * with Setter and Getter methods
 * 
 * Base class for those who need to maintain global application state. 
 * You can provide your own implementation by specifying its name in your AndroidManifest.
 * xml's <application> tag, which will cause that class to be instantiated
 *  for you when the process for your application/package is created.
 * 
 * ********************************************************************************************************/

import com.at.ATTag;

import android.app.Application;
import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

public class GlobalClass extends Application {
	private String deviceId = "";
	private String subDomain = ".ati-host.net";
	private String siteId = "538446";
	private String subSite = "";
	private ATTag atTag = null ;
	private boolean isLatest = true;
	public String getDeviceId(){
		return this.deviceId;
	}
	/**
	 * OnCreate of the application get device id and store it in a variable
	 * 
	 * If the the device don't have sim-card then the android id is assigned
	 * 
	 * it this has sim-card with it then device UUID is assigned
	 * 
	 * atTag.setLogDomain(SUBDOMAIN);
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		try{
			TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			if (telephonyManager.getDeviceId() != null){
				deviceId = telephonyManager.getDeviceId();
			} else{
	        	deviceId = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
	        }
			atTag = ATTag.init(this, subDomain, siteId,subSite);
			atTag.setSubDomain("logw348");
			atTag.setLogDomain(subDomain);
		}catch(Exception e){
			// exceptions handling
			Log.e("GlobalClass", e.getLocalizedMessage(), e);
		}
	}
	/**
	 * @return :: At Internet Tag variable 
	 * 
	 * to check this , if this is null then we need to reinitialize the tag
	 */
	public ATTag getAtTag(){
		return this.atTag;
	}
	/**
	 * @param atTag :: AT Internet tag variable
	 */
	public void setAtTag(ATTag atTag){
		this.atTag = atTag;
	}
	public void setVersionUpdate(boolean flag){
		isLatest = flag;
	}
	public boolean isUpdatedVersion(){
		return isLatest;
	}
}

