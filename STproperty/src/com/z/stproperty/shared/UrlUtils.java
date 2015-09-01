package com.z.stproperty.shared;

import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

/**
 * 
 * @author :: Evvolutions
 * 
 * Utils class contains all net-rating urls
 * url string last param value is empty that will contains the device id
 */
public class UrlUtils {
	
	private static String BASEURL  = "http://api.stproperty.sg/stproperty/api/middle-layer.php";
	public static final String URL_ADD = BASEURL + "?action=showAd&hash=";
	public static final String URL_VERSION = BASEURL + "?action=AndroidVer&hash=";
	public static final String URL_LISTING = BASEURL + "?action=listing";
	public static final String URL_AD_DETAILS = BASEURL + "?action=adDetails&hash=";
	public static final String URL_SEARCH = BASEURL + "?action=search";
	public static final String URL_NEARBY = BASEURL + "?action=propertiesNearby";
	public static final String URL_ARTICLELIST = BASEURL + "?action=articles";
	public static final String URL_AGENTSLIST = BASEURL + "?action=agents";
	public static final String URL_DIRECOTRYLIST = BASEURL + "?action=directories";
	public static final String URL_DIRECOTRYDETAIL = BASEURL + "?action=directoryDetails";
	public static final String URL_ARTICLECAT = BASEURL + "?action=articleCategory";
	public static final String NEW_ENQUEIRY_URL = BASEURL + "?action=newEnquiry";
	public static final String URL_FEEDBACK = "http://api.stproperty.sg/stproperty/ml-feedback-api.php";
	public static final String URL_NEARBY_AMI = BASEURL + "?action=amenities&category=";
	public static final String URL_DIRECTORY_AGENTDETAIL = BASEURL + "?action=agentDetailsWebview";
	public static final String URL_AGENT_DIR_COUNT = BASEURL + "?action=agentDetails&hash=";
	public static final String ABOUT = "http://www.stproperty.sg/";
	
	private UrlUtils(){
		// private constructor to hide implicit public one
	}
	/**
     * 
     * @param is : Stream of bytes reed from server
     * @param os : bye array
     * 
     * This function is used to convert from stream to bytes 
     * the bye values are converted to bitmap in superclass 
     */
	public static void copyStream(InputStream is, OutputStream os) {
        final int bufferSize=1024;
        try {
            byte[] bytes=new byte[bufferSize];
            for(;;) {
              int count=is.read(bytes, 0, bufferSize);
              if(count==-1){
                  break;
              }
              os.write(bytes, 0, count);
            }
        } catch(Exception e){
        	Log.e("CopyStream", e.getLocalizedMessage(), e);
        }
    }
}