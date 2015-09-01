package com.z.stproperty.adapter;

/**
 * 
 * Customized adapter for photos gallery ImageLoader :: Is another class
 * running in background to load the images into views 1. This will check
 * the url path in cache if it is available then the image is loaded from
 * cache 2. Otherwise it will download the image in background and updates
 * the view 2.1 After updating, images are cached (feature use) 3. The
 * default image like loading is displayed in view (before loading image)
 * 
 */

import java.util.Arrays;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.z.stproperty.R;
import com.z.stproperty.shared.ImageLoader;

public class ThumbImageAdapter extends BaseAdapter {
	private ImageLoader imageLoader;
	private String[] photos1;
	private int selectedThumb = 0;
	public ThumbImageAdapter(Context c, String[] photos) {
		photos1 = Arrays.copyOf(photos, photos.length);
		imageLoader = new ImageLoader(c.getApplicationContext());
	}

	/**
	 * @return :: Count of array or array-list
	 */
	public int getCount() {
		return photos1.length;
	}

	/**
	 * @return :: Object at position (List-view position)
	 */
	public Object getItem(int position) {
		return position;
	}

	/**
	 * @return :: Item's position in list-view
	 */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Child View
	 * 
	 * @param position
	 *            :: current position
	 * @param convertView
	 *            :: Current View
	 * @param parent
	 *            :: Parent View
	 * 
	 *            convertView is null on first time if convertView is null
	 *            then we will inflate the view with action inflater
	 * 
	 *            convertView Not null The values are assigned And the
	 *            convertView is returned back
	 * 
	 */
	static class ViewHolder {
		 private ImageView thumbImage;
	 }
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View curView;
		if (convertView == null) {
			curView = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumb_image, null);
			holder = new ViewHolder();
			holder.thumbImage = (ImageView)curView.findViewById(R.id.image);
			curView.setTag(holder);
		} else {
			curView = convertView;
			holder = (ViewHolder) convertView.getTag();
		}
		String forphoto = photos1[position];
		imageLoader.displayImage(forphoto, holder.thumbImage);
		if(selectedThumb == position){
			curView.setBackgroundColor(Color.WHITE);
		}else{
			curView.setBackgroundColor(Color.BLACK);
		}
		return curView;
	}
	public void setSelection(int position){
		selectedThumb = position;
		notifyDataSetChanged();
	}
}
