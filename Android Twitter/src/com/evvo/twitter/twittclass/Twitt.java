package com.evvo.twitter.twittclass;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.evvo.twitter.R;
import com.evvo.twitter.twittclass.TwitterApp.TwDialogListener;

public class Twitt {

	private TwitterApp mTwitter;
	private Activity activity;
	private String twitt_msg;

	public Twitt(Activity act, String consumer_key, String consumer_secret) {
		this.activity = act;
		mTwitter = new TwitterApp(activity, consumer_key, consumer_secret);
	}

	public void shareToTwitter(String msg) {
		this.twitt_msg = msg;
		mTwitter.setListener(mTwLoginDialogListener);

		if (mTwitter.hasAccessToken()) {
			showTwittDialog();
		} else {
			mTwitter.authorize();
		}
	}

	private void showTwittDialog() {

		final Dialog dialog = new Dialog(activity);
		dialog.setContentView(R.layout.twitt_dialog);
		dialog.setTitle("Twitter");

		Button btnPost = (Button) dialog.findViewById(R.id.btnpost);
		final EditText et = (EditText) dialog.findViewById(R.id.twittext);
		et.setText(twitt_msg);
		et.setSelection(et.getText().length());
		btnPost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				twitt_msg = et.getText().toString().trim();

				if (twitt_msg.length() == 0) {
					showToast("Twitt is empty!!!");
					return;
				} /*else if (twitt_msg.length() > 140) {
					showToast("Twitt is more than 140 characters not allowed!!!");
					return;
				}*/
				dialog.dismiss();
				new PostTwittTask().execute(twitt_msg);

			}

		});

		dialog.show();

	}

	private TwDialogListener mTwLoginDialogListener = new TwDialogListener() {

		public void onError(String value) {
			showToast("Login Failed");
			mTwitter.resetAccessToken();
		}

		public void onComplete(String value) {
			showTwittDialog();
		}
	};

	void showToast(final String msg) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();

			}
		});

	}

	class PostTwittTask extends AsyncTask<String, Void, String> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Posting Twitt...");
			pDialog.setCancelable(false);
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... twitt) {
			try {
				mTwitter.updateStatus(twitt[0]);
				return "success";

			} catch (Exception e) {
				if (e.getMessage().toString().contains("duplicate")) {
					return "Posting Failed because of Duplicate message...";
				}
				Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
				return "Posting Failed!!!";
			}

		}

		@Override
		protected void onPostExecute(String result) {
			pDialog.dismiss();

			if (null != result && result.equals("success")) {
				showToast("Posted Successfully");

			} else {
				showToast(result);
			}

			super.onPostExecute(result);
		}
	}
}
