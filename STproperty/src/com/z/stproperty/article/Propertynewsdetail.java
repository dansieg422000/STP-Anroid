package com.z.stproperty.article;

/**
 * @author EVVOLUTIONS
 * 
 * Description:
 * (Show The Properties news details)
 * 
 * 
 * Input variables:
 * ArrayList<HashMap<String, String>> menuItems(Need to get latitude and longitude from it)
 *
 *  
 * Output variables:
 *  null
 *  
 * The details screen contains following things 
 * 
 * 1. Title
 * 2. Short Description
 * 3. Author
 * 4. Date
 * 5. Source
 * 6. Article content
 * 
 * In the top right corner of the screen share button is available
 * User can share this article with his friends through
 * 
 * FaceBook
 * Twitter
 * Email (Any email configured with mobile)
 * 
 * For shareing this with facebook and twitter
 * the user has to give permission to app
 * The app will post data on his wall on behalf of them
 *
 */

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.evvo.twitter.twittclass.Twitt;
import com.facebook.android.ShareWithfacebook;
import com.google.analytics.tracking.android.EasyTracker;
import com.z.stproperty.R;
import com.z.stproperty.dialog.ShareContent;
import com.z.stproperty.fonts.Helvetica;
import com.z.stproperty.fonts.HelveticaBold;
import com.z.stproperty.shared.Constants;
import com.z.stproperty.shared.ImageLoader;
import com.z.stproperty.shared.SharedFunction;

public class Propertynewsdetail extends Activity {
	private String articleTitle = "", articleDes = "", articlePhoto = "", webLink = "";
	/**
	 * OnCreate to set article details view layout
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article_detail);
		try {
			Bundle extras = getIntent().getExtras();
			articleTitle = extras.getString("title");
			articleDes = extras.getString("articleblurb");
			articlePhoto = extras.getString("photo");
			webLink = extras.getString("link");
			((Helvetica) findViewById(R.id.articlePlurb)).setText(articleDes);
			((HelveticaBold) findViewById(R.id.articleTitle)).setText(articleTitle);
			((Helvetica) findViewById(R.id.articlePublishDate)).setText(": " + extras.getString("date"));
			((Helvetica) findViewById(R.id.articleAuthor)).setText(": " + extras.getString("author"));
			((Helvetica) findViewById(R.id.articleSource)).setText(": " + extras.getString("source"));
			
			Helvetica articleContent = (Helvetica) findViewById(R.id.articleContent);
			articleContent.setText(Html.fromHtml(extras.getString("content")));
			articleContent.setMovementMethod(LinkMovementMethod.getInstance());
			ImageView photo1 = (ImageView) findViewById(R.id.articlePhoto);
			ImageLoader imageLoader = new ImageLoader(getApplicationContext());
			imageLoader.displayImage(articlePhoto, photo1);
			// net-rating 
			((Button) findViewById(R.id.ShareIcon)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), ShareContent.class);
                    startActivityForResult(intent, Constants.REQUEST_SHARE);
				}
			});
			// AT Internet tracking With Custom variables
			List<String> customVariables = new ArrayList<String>();
			customVariables.add(articleTitle);
			customVariables.add(extras.getString("source"));
			customVariables.add(extras.getString("author"));
			customVariables.add(extras.getString("date"));
			String screenName = getIntent().getStringExtra("category_name").replace(" ", "_");
			screenName = screenName + "::" + screenName + "_Article";
			SharedFunction.sendATTagging(getApplicationContext(), screenName, 4, customVariables);
	        SharedFunction.sendGA(getApplicationContext(), screenName);
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * 
	 * @param shareTxt
	 *            :: share text (article details)
	 * 
	 *            This will ask user authentication and login details to share
	 *            the property
	 */
	private void twitterAuthorize(){
		try{
			Twitt twitt = new Twitt(this, Constants.CONSUMERKEY, Constants.CONSUMERSECRET);
			SharedFunction.postAnalytics(Propertynewsdetail.this, "Lead", "Twitter Share",  articleTitle);
			twitt.shareToTwitter(articleTitle+"\n\n"+webLink);
		} catch(Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			Toast.makeText(getApplicationContext(), "no response,please try again", Toast.LENGTH_LONG).show();
		}
	}
	/**
	 * 
	 *  title
	 *            :: article title
	 *  url
	 *            :: URl to see the deatils
	 *  price
	 *            :: price
	 *  email
	 *            :: contact email id
	 * 
	 *            This will open the email client installed in device If there
	 *            is no email client installed then this will says an error
	 *            message
	 * 
	 */
	private void shareWithEmail(){
		try{
		   	String path = Images.Media.insertImage(getContentResolver(), SharedFunction.loadBitmap(articlePhoto),"title", null);
    	   	Uri screenshotUri = Uri.parse(path);
    	   	String shareContent = articleTitle+"\n\n"+
    	   			articleDes+"\n\n"+
    	   			"Click on the following link view more detail about the property news\n\n"+webLink+"\n\n";
    	   	Intent intent = new Intent(Intent.ACTION_SEND);
    	   	intent.setType("image/png");
    	   	intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
    		intent.putExtra(Intent.EXTRA_SUBJECT, articleTitle);
    		intent.putExtra(Intent.EXTRA_TEXT, ""+shareContent);
    		intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
    		SharedFunction.postAnalytics(Propertynewsdetail.this, "Lead", "Email Share",  articleTitle);
    		startActivity(Intent.createChooser(intent, ""));
		} catch(Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
			Toast.makeText(getApplicationContext(), "no response,please try again", Toast.LENGTH_LONG).show();
		}
	}
	/***
	 * @Param requestCode : Request code to identify the calling function
	 * @param resultCode
	 *            : Response returned from the called function RESULT_OK or
	 *            RESULT_CANCEL
	 * @param data
	 *            : Is an intent extra values passed from called function
	 * 
	 *            face == 1 then need to share the this property with fb
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try{
			if(resultCode == RESULT_OK && requestCode == Constants.REQUEST_SHARE){
				int id = data.getIntExtra("id", 0);
				switch (id) {
				case 0:
					new ShareWithfacebook(Propertynewsdetail.this, articleTitle, articlePhoto, articleDes, webLink);
					SharedFunction.postAnalytics(Propertynewsdetail.this, "Lead", "Facebook Share",  articleTitle);
					break;
				case 1:
					twitterAuthorize();
					break;
				case 2:
					shareWithEmail();
					break;
				default:
					break;
				}
			}
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
	/**
	 * Used to start the session for this activity 
	 * in Google Analytics Screen capture
	*/
	 @Override
	 public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance(this).activityStart(this);
	 }
	 /**
	 * Used to End the session for this activity 
	 * in Google Analytics Screen capture
	 */
	 @Override
	 public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this);
	 }
}