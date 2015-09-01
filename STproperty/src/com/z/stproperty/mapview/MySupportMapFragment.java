package com.z.stproperty.mapview;

import com.google.android.gms.maps.SupportMapFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/***
 * 
 * @author Evvolutions
 * 
 * A Map component in an app. 
 * This fragment is the simplest way to place a map in an application. 
 * It's a wrapper around a view of a map to automatically handle the necessary life cycle needs. 
 * Being a fragment, this component can be added to an activity's layout file simply with the XML below.
 * 
 * 
 * 
 * A GoogleMap can only be acquired using getMap() when the underlying maps system is loaded
 *  and the underlying view in the fragment exists. 
 *  
 *  This class automatically initializes the maps system and the view; 
 *  however you cannot be guaranteed when it will be ready because this depends on the 
 *  availability of the Google Play services APK.
 *  
 *  
 * If a GoogleMap is not available, getMap() will return null.
 *  
 *   
 * A view can be removed when the SupportMapFragment's onDestroyView() method is called 
 * and the useViewLifecycleInFragment(boolean) option is set. 
 * 
 * 
 * When this happens the SupportMapFragment is no longer valid until the view is recreated again later 
 * when SupportMapFragment's onCreateView(LayoutInflater, ViewGroup, Bundle) method is called.
 *
 *
 * Any objects obtained from the GoogleMap is associated with the view. 
 * 
 * It's important to not hold on to objects (e.g. Marker) beyond the view's life. 
 * Otherwise it will cause a memory leak as the view cannot be released.
 * 
 * 
 * To use this class, you must include the Android support library in your build path.
 *
 */
public class MySupportMapFragment extends SupportMapFragment {
	private View mOriginalContentView;
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
    mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);    
    TouchableWrapper mTouchView = new TouchableWrapper(getActivity());
    mTouchView.addView(mOriginalContentView);
    return mTouchView;
  }
  @Override
  public View getView() {
    return mOriginalContentView;
  }
}