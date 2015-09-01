package com.z.stproperty.shared;

/***************************************************************
* Class name:
* (Messages)
* 
* Description:
* (Store static strings into message.properties)
* 
* 
* Input variables:
* null
* 
* Output variables:
* null
****************************************************************/

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import android.util.Log;

public class Messages {
  private static final String         BUNDLE_NAME     = "com.z.stproperty.messages";

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(Messages.BUNDLE_NAME);

  private Messages() {
    // empty private constructor
  }

  public static String getString(String key) {
    try {
    	return Messages.RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
    	Log.e("Messages", e.getLocalizedMessage(), e);
    	return '!' + key + '!';
    }
  }
}
