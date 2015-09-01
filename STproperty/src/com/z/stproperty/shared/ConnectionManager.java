package com.z.stproperty.shared;

import java.util.Map;

import org.apache.http.HttpEntity;

import android.content.Context;

import com.evvolutions.android.http.AsyncHttpClient;
import com.evvolutions.android.http.AsyncHttpResponseHandler;
import com.evvolutions.android.http.RequestParams;
 /***
  * 
  * @author Evvolutions
  *
  *This Class handles all the message passing from the app to the url and vice versa
  *
  * An asynchronous callback-based Http client for Android 
  * built on top of Apache’s HttpClient libraries is used. 
  * 
  * All requests are made outside of your app’s main UI thread,
  * but any callback logic will be executed on the same thread
  *  as the callback was created using Android’s Handler message passing.
  *
  */
public class ConnectionManager {
	public enum ConnectionType {
		CONNECTIONTYPE_POST, CONNECTIONTYPE_SOAP, CONNECTIONTYPE_GET
	}
		
	private RequestParams connectionVariables;
	private AsyncHttpClient client = new AsyncHttpClient();	
	/**
	public ConnectionManager(){}
	
	public ConnectionManager(Context context){
		this.connectionContext = context;	
	}
	*/
	/**
	 * 
	 * @param context	:: Base Application Context
	 * @param connectionVariables	:: Parameters Map
	 * @param serverURL	:: URL to POST or GET data
	 * @param connectionType :: POST or GET 
	 * @param entity	:: HTTP entity
	 * @param asyncHttpResponseHandler :: Response handler
	 * @throws Exception :: Throws an exception on bad URL
	 * 						Socket exception etc..
	 * 
	 *  Here SOAP type is commented because here we are not using this
	 *  type of network connection
	 */
	public void connectionHandler(Context context,Map<String,String> connectionVariables,String serverURL,ConnectionType connectionType1,HttpEntity entity,AsyncHttpResponseHandler asyncHttpResponseHandler1) throws Exception{					
		if(connectionVariables!=null){
			this.connectionVariables = new RequestParams(connectionVariables);
		}
		ConnectionType connectionType = connectionType1;
		AsyncHttpResponseHandler asyncHttpResponseHandler = asyncHttpResponseHandler1;
		if(connectionType == ConnectionType.CONNECTIONTYPE_POST&&entity==null){
			post(serverURL,this.connectionVariables,asyncHttpResponseHandler);
		}else if(connectionType == ConnectionType.CONNECTIONTYPE_POST){
			post(context,serverURL,entity,"text/xml",asyncHttpResponseHandler);
		} else if(connectionType == ConnectionType.CONNECTIONTYPE_GET){
			get(serverURL,this.connectionVariables,asyncHttpResponseHandler);
		}
		/**else if(this.connectionType == ConnectionType.CONNECTIONTYPE_SOAP){
			soap(connectionVariables.get("params").toString(),this.asyncHttpResponseHandler);					
		}*/
	}
	/**
	private void soap(String request,AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {
		client.post(this.connectionContext, UrlFactory.WS_URL, new StringEntity(request, HTTP.UTF_8), "text/xml",responseHandler); 
	}*/
	/**
	 * 
	 * @param url :: URL to connect
	 * @param params :: GET Parameter values
	 * @param responseHandler :: response handler
	 * 							onSuccess(), onFailur() and onFinish()
	 */
	private void get(String url, RequestParams params,AsyncHttpResponseHandler responseHandler) {
		client.get(url, params, responseHandler);
	}
	/**
	 * 
	 * @param url :: URL to connect
	 * @param params :: POST Parameter values
	 * @param responseHandler :: response handler
	 * 							onSuccess(), onFailur() and onFinish()
	 */
	private void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(url, params, responseHandler);
	}
	
	/**
	 * 
	 * @param context :: Base Activity Context
	 * @param url :: URL to connect
	 * @param entity :: HTTP entity
	 * @param contentType :: connection type POST or GET
	 * @param responseHandler :: Response Handler
	 *							 onSuccess(), onFailur() and onFinish()
	 */
	private  void post(Context context,String url,HttpEntity entity,String contentType,AsyncHttpResponseHandler responseHandler){
		client.post(context, url, entity, contentType, responseHandler);
	}
}