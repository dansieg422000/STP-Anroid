package com.z.stproperty;

/***************************************************************
 * Class name:
 * (Photos)
 * 
 * Description:
 * (This class provide a gallery interface for user to view property photos)
 * 
 * 
 * Input variables:
 * String[] photos1(Stored the URL of photos which will be loaded)
 *  
 * Output variables:
 * null
 ****************************************************************/

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;

import com.z.stproperty.adapter.PhotoGallery;
import com.z.stproperty.adapter.ThumbImageAdapter;
import com.z.stproperty.shared.ImageLoader;
import com.z.stproperty.shared.SharedFunction;
import com.z.stproperty.shared.TouchImageView;

@SuppressWarnings("deprecation")
public class Photos extends Activity {

	private static String[] photos;
	private Gallery thumbView;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.photos);
			Intent i = getIntent();
			Bundle extras = i.getExtras();
			if (i.hasExtra("photos1")) {
				photos = (String[]) extras.get("photos1");
			}
			final ViewPager gallery = (ViewPager) findViewById(R.id.GalleryPager);
			gallery.setHorizontalFadingEdgeEnabled(false);
			PhotoGallery galleryAdap = new PhotoGallery();
			gallery.setAdapter(galleryAdap);
			final ThumbImageAdapter thumbAdapter = new ThumbImageAdapter(Photos.this, photos);
			gallery.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					//  Auto-generated method stub
					thumbView.performItemClick(thumbView.getChildAt(arg0), arg0, thumbView.getAdapter().getItemId(arg0));
					thumbView.requestFocusFromTouch();
					thumbView.setSelection(arg0);
					// GA Tagging
					SharedFunction.sendGA(getApplicationContext(), "Image_View");
					// AT Internet rating
					SharedFunction.sendATTagging(getApplicationContext(), "Image_View", 12, null);
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					//  Auto-generated method stub
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
					//  Auto-generated method stub
				}
			});
			ImageLoader imageLoader = new ImageLoader(Photos.this);
			for(int index=0; index<photos.length;index++){
				TouchImageView image = new TouchImageView(Photos.this);
				image.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
	        	imageLoader.displayImage(photos[index].replace("_S.", "_L."), image);
	        	galleryAdap.addView(image);
			}
			galleryAdap.notifyDataSetChanged();
			thumbView = (Gallery) findViewById(R.id.thumbImages);
			thumbView.setAdapter(thumbAdapter);
			thumbView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					thumbAdapter.setSelection(arg2);
					gallery.setCurrentItem(arg2, true);
					thumbView.requestFocusFromTouch();
					thumbView.setSelection(arg2);
				}
			});
			int selected = getIntent().getIntExtra("selected", 0);
			gallery.setSelected(true);
			gallery.setCurrentItem(selected, true);
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
	}
}
