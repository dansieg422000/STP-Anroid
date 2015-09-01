package com.facebook.android;

/*********************************************************************************************************
 * Class	: Shareonfacebook
 * Type		: Activity
 * Date		: 18 Set 2013
 * 
 * Description:
 * 
 * Main Facebook object for interacting with the Facebook developer API.
 * Provides methods to log in and log out a user, make requests using the REST
 * and Graph APIs, and start user interface interactions with the API (such as
 * pop-ups promoting for credentials, permissions, stream posts, etc.)
 *
 *          
 * Full authorize method.
 *
 * Starts either an Activity or a dialog which prompts the user to log in to
 * Facebook and grant the requested permissions to the given application.
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
 * @param activity
 *            The Android activity in which we want to display the
 *            authorization dialog.
 * @param applicationId
 *            The Facebook application identifier e.g. "350685531728"
 * @param permissions
 *            A list of permissions required for this application: e.g.
 *            "read_stream", "publish_stream", "offline_access", etc. see
 *            http://developers.facebook.com/docs/authentication/permissions
 *            This parameter should not be null -- if you do not require any
 *            permissions, then pass in an empty String array.
 * @param activityCode
 *            Single sign-on requires an activity result to be called back
 *            to the client application -- if you are waiting on other
 *            activities to return data, pass a custom activity code here to
 *            avoid collisions. If you would like to force the use of legacy
 *            dialog-based authorization, pass FORCE_DIALOG_AUTH for this
 *            parameter. Otherwise just omit this parameter and Facebook
 *            will use a suitable default. See
 *            http://developer.android.com/reference/android/
 *              app/Activity.html for more information.
 * @param listener
 *            Callback interface for notifying the calling application when
 *            the authentication dialog has completed, failed, or been
 *            canceled.
 * ********************************************************************************************************/
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.Facebook.DialogListener;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.SharedFunction;

public class ShareWithfacebook{
	private static final String APP_ID = Constants.APP_ID;
	private static final String[] PERMISSIONS = new String[] {"publish_stream"};
	private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String KEY = "facebook-credentials";
	private Facebook facebook;
	private String messageToPost;
	private String imageurl;
	private String description;
	private String url;
	private Activity context;
	String facebookMessage;
	public boolean saveCredentials(Facebook facebook) {
    	Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
    	editor.putString(TOKEN, facebook.getAccessToken());
    	editor.putLong(EXPIRES, facebook.getAccessExpires());
    	return editor.commit();
	}
	public final boolean restoreCredentials(Facebook facebook) {
    	SharedPreferences sharedPreferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
    	facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
    	facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
    	return facebook.isSessionValid();
	}
	public ShareWithfacebook(Activity context, String message, String facebookImage, String facebookdesc, String url){
		this.context = context;
		facebook = new Facebook(APP_ID);
		restoreCredentials(facebook);
		facebookMessage = message;
		if (facebookMessage == null){
			facebookMessage = "Test wall post";
		}
		messageToPost = facebookMessage;
		imageurl=facebookImage;
		description=facebookdesc;
		this.url = url;
		
		if (! facebook.isSessionValid()) {
			loginAndPostToWall();
		}else {
			postToWall(messageToPost,imageurl,description,url);
		}
	}
	public final void loginAndPostToWall(){
		 facebook.authorize(context, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
	}
	public void postToWall(String message,String imageurl,String desc,String url){
		Bundle parameters = new Bundle();
                parameters.putString("caption", message);
                parameters.putString("image", imageurl);
                parameters.putString("description", desc);
                parameters.putString("link", url);
                try {
        	        facebook.request("me");
			String response = facebook.request("me/feed", parameters, "POST");
			if (response == null || response.equals("") ||
			        response.equals("false")) {
				showToast("Blank response.");
			}else {
				SharedFunction.postAnalytics(context, "Lead", "Facebook Share",  facebookMessage);
				showToast("Message posted to your facebook wall!");
			}
		} catch (Exception e) {
			showToast("Failed to post to wall!");
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/***
	 * 
		 * @author Evoolutions
		 * 
		 * DialogListener is an interface that lets one implement the below functions
		 * 
		 * 
		 * @param values
	     *            Key-value string pairs extracted from the response.
	     * public void onComplete(Bundle values) is
         * Called when a dialog completes.
         * Executed by the thread that initiated the dialog.
         * 
         * 
         *  public void onFacebookError(FacebookError e);
         *  Called when a Facebook responds to a dialog with an error.
         *  Executed by the thread that initiated the dialog.
         * 
         * 
         * 
         *  public void onError(DialogError e)
         *  Called when a dialog has an error.
         *  Executed by the thread that initiated the dialog.
         * 
         *  public void onCancel();
         * Called when a dialog is canceled by the user.
         * Executed by the thread that initiated the dialog.
         * 
         * 
         * 
	 */
	class LoginDialogListener implements DialogListener {
	    public void onComplete(Bundle values) {
	    	saveCredentials(facebook);
	    	if (messageToPost != null){
				postToWall(messageToPost,imageurl,description,url);
			}
	    }
	    public void onFacebookError(FacebookError error) {
	    	showToast("Authentication with Facebook failed!");
	    }
	    public void onError(DialogError error) {
	    	showToast("Authentication with Facebook failed!");
	    }
	    public void onCancel() {
	    	showToast("Authentication with Facebook cancelled!");
	    }
	}
	private void showToast(String message){
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
