package com.z.stproperty.shared;

/***
 * 
 * @author Evvolutions
 * 
 * This class checks if  the internet connection is connected.
 * 
 * It is done by using the ConnectivityManager Class and NetworkInfo.
 * 
 * 
 *	ConnectivityManager  Class  answers queries about the state of network connectivity. 
 *
 * 	It also notifies applications when network connectivity changes. 
 * 	Get an instance of this class by calling
 *     Context.getSystemService(Context.CONNECTIVITY_SERVICE).
 *     
 *     
 *     
 *	NetworkInfo Class Describes the status of a network interface.
 *
 *	Use getActiveNetworkInfo()  using ConnectivityManager,
 *		to get an instance 
 *		that represents the current network connection.
 * 
 *
 *	Check if network info instance is not null and isConnectedOrConnecting()
 *
 *	(where is isConnectedOrConnecting
 *		Indicates whether network connectivity exists or is in the process of being established.)
 *   	return true
 *   else
 *   	return false
 *   
 *   Note ::
 *  	this class is only a utility class, you should make the class final and define a private constructor:
 *  
 *  	The reason is that you don't want that the default parameter-less constructor can be used anywhere in your code. 
 *  	If you make the class final, it can't be extended in subclasses what is considered to be appropriate for utility classes.
 *  	Since you declared only a private constructor, other classes wouldn't be able to extend it anyway, 
 *  	but no harm is done by marking it.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public final class ConnectionCheck {
	private ConnectionCheck(){
		// constant class constructor 
	}
	public static boolean checkOnline(Context ctx){
		try{
		    ConnectivityManager cm =(ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		        return true;
		    }
		    return false;
		}catch (Exception e) {
			Log.e("ConnectionCheck", e.getLocalizedMessage(), e);
			return false;
		}
	}
}
