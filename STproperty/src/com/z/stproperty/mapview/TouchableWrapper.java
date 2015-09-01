package com.z.stproperty.mapview;

/*********************************************************************************************************
 * Class	: TouchableWrapper
 * Type		: FrameLayout
 * Date		: 20 - 09 - 2013
 * 
 * Description:
 * 
 *  As we are showing the marker window on map as customized 
 *  	we can't move the view along with marker (pin)
 *  so when the user clicks or touches outside the marker window 
 *  Marker window should be closed
 *  
 *  Method :: dispatchTouchEvent()
 *  	This will deduct the touch event on the map
 *  	And based on the screen this will remove the custom makerWindow from parent View
 *  
 *  This we are doing on three different places like 
 *  
 *  property Listing (propertyMapList)
 *  Saved search list
 *  favorites list *  
 *
 * ********************************************************************************************************/

import com.z.stproperty.PropertyDetailFragment;
import com.z.stproperty.directory.DirectoryDetailFragment;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout {
  private Context context;
  public TouchableWrapper(Context context) {
    super(context);
    this.context = context;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
    	  if(context.getClass().getSimpleName().equalsIgnoreCase("DirectoryDetail")){
    		  DirectoryDetailFragment.mapZoomControls(true);
    	  }else{
    		  PropertyDetailFragment.mapZoomControls(true);
    	  }
          break;
      case MotionEvent.ACTION_UP:
    	  if(context.getClass().getSimpleName().equalsIgnoreCase("DirectoryDetail")){
    		  DirectoryDetailFragment.mapZoomControls(true);
    	  }else{
    		  PropertyDetailFragment.mapZoomControls(false);
    	  }
    	  break;
      default:
			break;
    }
    return super.dispatchTouchEvent(event);
  }
}
