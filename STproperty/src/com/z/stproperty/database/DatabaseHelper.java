package com.z.stproperty.database;

/*********************************************************************************************************
 * Class	: DatabaseHelper
 * Type		: SQLiteOpenHelper
 * Date		: 19 Set 2013
 * 
 * Description:
 * 
 * SQLITE database
 * 
 * Values are stored in db and got back with sql queries
 * 
 * We are storing an values in local database to show the datas
 * when the user request to see his activities
 * 
 * Like 
 * 1. storing Search
 * 2. Favorites
 * 3. Enquiry
 * 
 *  When ever the user goes for favorites list the favorite list is populated
 *  like this all kind of local values are displayed
 *  
 *  This is done with device specific not with user specific
 * 
 * ********************************************************************************************************/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.z.stproperty.bean.ExpandAdapterBean;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static String dbName = "STPropertyMobile";
	private SQLiteDatabase database;
    private static int dbVersion = 2;
    private String tblHistory = "history", tblSearch="search", tblFavorites="favorits", tblEnquiry = "enquiry";
    private String type = "type",propertyFor = "for",bedRooms = "bedRooms",bathRooms = "bathRooms", 
	    		latitude = "latitude", longitude = "longitude", searchName="searchName", keyWord = "keyword",
	    		top = "top", tenure = "tenure", district = "district", listedOn = "listedon", estate = "estate",
	    		sortBy = "sortBy", searchUrl = "searchurl", enquiryTitle = "title", projectName = "name",
	    		remark = "remark", propertyPriority = "priority", hdbScheme = "scheme", hdbCov = "cov",
	    		roomType="roomtype", leaseTerm = "leaseterm";
    private String propertyId = "propertyId", propertyName = "name", datePosted = "postDate", viewDate = "viewDate",
    				classification = "classification", curDate = "search_date",
    		thumbnail = "thumbnail", psf = "psf", price = "price", priceOption = "priceOptions", builtinArea = "builtinArea", propertyTitle = "title";
    public DatabaseHelper(Context context) {
        super(context, dbName, null, dbVersion);
    }
/**
 * Will create the database and 
 * tables when the application is installed
 * 
 */
    @Override
    public void onCreate(SQLiteDatabase database) {
    	database.execSQL("CREATE TABLE "+tblHistory+"(pId INTEGER PRIMARY KEY AUTOINCREMENT, " +
        		bedRooms+" VARCHAR(3), " +	bathRooms+" VARCHAR(3), " + propertyPriority + " TEXT, " +
				propertyId+" VARCHAR(25), " + propertyFor+" VARCHAR(25), " + propertyName+" VARCHAR(100), " 
        		+ propertyTitle + " TEXT, "+ datePosted + " TEXT, " + price + " TEXT, " + type+" VARCHAR(25), " + latitude+"  VARCHAR(50), " +
				longitude+"  VARCHAR(50), " + psf+" VARCHAR(25), " + builtinArea+" VARCHAR(25), " + priceOption+" VARCHAR(50), "+
				thumbnail+" TEXT, " + viewDate+" DATETIME  default CURRENT_TIMESTAMP, "+classification+" VARCHAR(100));");
    	database.execSQL("CREATE TABLE "+tblFavorites+"(pId INTEGER PRIMARY KEY AUTOINCREMENT, " +
        		bedRooms+" VARCHAR(3), " +	bathRooms+" VARCHAR(3), " + propertyPriority + " TEXT, " +
				propertyId+" VARCHAR(25), " + propertyFor+" VARCHAR(25), " + propertyName+" VARCHAR(100), " 
        		+ propertyTitle + " TEXT, "+ datePosted + " TEXT, " + price + " TEXT, " + type+" VARCHAR(25), " + latitude+"  VARCHAR(50), " +
				longitude+"  VARCHAR(50), " + psf+" VARCHAR(25), " + builtinArea+" VARCHAR(25), " + priceOption+" VARCHAR(50), "+
				thumbnail+" TEXT, " + viewDate+" DATETIME  default CURRENT_TIMESTAMP, "+classification+" VARCHAR(100));");
    	
    	database.execSQL("CREATE TABLE "+tblSearch+"(SearchId INTEGER PRIMARY KEY AUTOINCREMENT, " +
        		bedRooms+" VARCHAR(3), " +	bathRooms+" VARCHAR(3), " + top + " VARCHAR(25), " + tenure + " TEXT, "
				+ propertyFor+" VARCHAR(25), " + searchName + " TEXT, " + hdbCov + " TEXT, " + hdbScheme + " TEXT, " + roomType + " TEXT, "
        		+ keyWord + " TEXT, "+ price + " TEXT, " + type+" VARCHAR(25), " + district + " TEXT, " + leaseTerm + " TEXT, "
				+ psf + " VARCHAR(40), " + builtinArea + " VARCHAR(40), " + listedOn + " TEXT, " + sortBy + " TEXT, "
				+ classification+" VARCHAR(100), " + searchUrl + " TEXT, "+ curDate +" TEXT default CURRENT_DATE);");
    	
    	database.execSQL("CREATE TABLE "+tblEnquiry+"(enquiryId INTEGER PRIMARY KEY AUTOINCREMENT, " +
        		bedRooms+" VARCHAR(25), " + propertyFor+" VARCHAR(25), " + enquiryTitle + " TEXT, " + classification + " TEXT, "
        		+ projectName + " TEXT, "+ price + " TEXT, " + type + " VARCHAR(25), " + district + " TEXT, "
        		+ estate + " TEXT, " + remark + " TEXT, "+ curDate +" TEXT default CURRENT_DATE);");
    }
    /**
     * On Update of application from one version to another version 
     * The database also gets updated to latest one
     * 
     * The old tables and values are deleted and
     * new table and data's are populated
     * 
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
            int newVersion) {
        /**Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");*/
        database.execSQL("DROP TABLE IF EXISTS "+tblHistory);
        database.execSQL("DROP TABLE IF EXISTS "+tblFavorites);
        database.execSQL("DROP TABLE IF EXISTS "+tblSearch);
        database.execSQL("DROP TABLE IF EXISTS "+tblEnquiry);
        onCreate(database);
    }
    /**
     * 
     * @param propertyDetails :: Property detail JSON
     * @param historyFlag :: History or favorite
     * 
     * Stored the values in same table for history and favorite
     */
    public void addFavOrHistory(Map<String, String> propertyDetails, boolean historyFlag) {
    	try{
		    database = this.getWritableDatabase();  
			ContentValues values = new ContentValues();
		    values.put(type,propertyDetails.get("type"));
		    values.put(propertyFor, propertyDetails.get("propertyFor"));
		    values.put(propertyId, propertyDetails.get("propertyId"));
		    values.put(propertyName, propertyDetails.get("propertyName"));
		    values.put(propertyTitle, propertyDetails.get("propertyTitle"));
		    values.put(priceOption, propertyDetails.get("priceOption"));
		    values.put(propertyPriority, propertyDetails.get("property_highlights"));
		    values.put(price, propertyDetails.get("price"));
		    values.put(latitude, propertyDetails.get("latitude"));
		    values.put(longitude, propertyDetails.get("longitude"));
		    values.put(psf, propertyDetails.get("psf"));
		    values.put(bedRooms, propertyDetails.get("bedRooms"));
		    values.put(bathRooms, propertyDetails.get("bathRooms"));
		    values.put(builtinArea, propertyDetails.get("builtinArea"));
		    values.put(thumbnail, propertyDetails.get("thumbnail"));
		    values.put(datePosted, propertyDetails.get("datePosted"));
		    values.put(classification, propertyDetails.get("classification"));
		    database.insert(historyFlag? tblHistory : tblFavorites, null, values); 
    	}catch (Exception e) {
    		Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
    	database.close();
    }
    /***
     * @param sName 	:: Search Name - custom user search name used to display the details
     * @param keyword 	:: Search Keyword - used to filter the properties
     * @param bedroom 	:: No of bedrooms
     * @param bathroom	:: no of bathrooms
     * @param topVal	:: TOP 
     * @param tenureVal	:: Tenure
     * @param wantfor	:: Property Wanted for (sale/rent/rental)
     * @param propertyType 	:: Property type like Cando, land, landed etc..
     * @param clasification	:: Classification (sub type)
     * @param locationVal	:: District to search
     * @param psfval	:: PSF min and max
     * @param priceVal	:: price Min and Max
     * @param floorArea	:: Biult-in-area min and max
     * @param listed	:: Listed on value
     * @param sortby	:: Sorting order
     * @param searchurl	:: Url for later search with same options
     */
    public void addSearch(String sName, String keyword, String bedroom, String bathroom, String topVal, String tenureVal,
    		String wantfor, String propertyType, String clasification, String locationVal, String psfval, String priceVal,
    		String floorArea, String listed, String sortby, String searchurl, String cov, String scheme, String roomtype,
    		String lease){
    	try{
		    database = this.getWritableDatabase();  
			ContentValues values = new ContentValues();
			values.put(searchName, sName);
			values.put(keyWord, keyword);
			values.put(top, topVal);
			values.put(tenure, tenureVal);
			values.put(district, locationVal);
			values.put(listedOn, listed);
			values.put(sortBy, sortby);
		    values.put(type,propertyType);
		    values.put(propertyFor, wantfor);
		    values.put(price, priceVal);
		    values.put(psf, psfval);
		    values.put(bedRooms, bedroom);
		    values.put(bathRooms, bathroom);
		    values.put(builtinArea, floorArea);
		    values.put(classification, clasification);
		    values.put(searchUrl, searchurl);
		    values.put(hdbCov, cov);
		    values.put(hdbScheme, scheme);
		    values.put(roomType, roomtype);
		    values.put(leaseTerm, lease);
		    database.insert(tblSearch, null, values); 
    	}catch (Exception e) {
    		Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
    	database.close();
    }
    /**
     * 
     * @return	:: ArrayList<ExpandAdapterBean>
     * 
     * Adds all the search category into a bean and stores into the list
     * the only selected search categories are stored the remaining values are omitted from BEAN 
     */
    public List<ExpandAdapterBean> getSearchSummary(){
    	List<ExpandAdapterBean> search = new ArrayList<ExpandAdapterBean>();
    	try {
    		database = this.getWritableDatabase();
	    	Cursor cur = database.rawQuery("select * from "+tblSearch+" order by SearchId desc limit 0,25",	new String[] {});
    		cur.moveToFirst();
    		for(int i=0;i<cur.getCount();i++){
    			List<String> header = new ArrayList<String>();
    			List<String> values = new ArrayList<String>();
    			String keyword =  cur.getString(cur.getColumnIndex(keyWord));
    			if(!keyword.equals("")){
    				header.add("Keyword");
    				values.add(keyword);
    			}
    			if(!cur.getString(cur.getColumnIndex(top)).equals("")){
    				header.add("TOP");
    				values.add(cur.getString(cur.getColumnIndex(top)));
    			}
    			if(!cur.getString(cur.getColumnIndex(tenure)).equals("")){
    				header.add("Tenure");
    				values.add(cur.getString(cur.getColumnIndex(tenure)));
    			}
    			if(!cur.getString(cur.getColumnIndex(district)).equals("")){
    				header.add("Location");
    				values.add(cur.getString(cur.getColumnIndex(district)));
    			}
    			if(!cur.getString(cur.getColumnIndex(type)).equals("")){
    				header.add("Property Type");
    				values.add(cur.getString(cur.getColumnIndex(type)));
    			}
    			if(!cur.getString(cur.getColumnIndex(classification)).equals("")){
    				header.add("Property Subtype");
    				values.add(cur.getString(cur.getColumnIndex(classification)));
    			}
    			if(!cur.getString(cur.getColumnIndex(hdbScheme)).equals("")){
    				header.add("HDB Scheme");
    				values.add(cur.getString(cur.getColumnIndex(hdbScheme)));
    			}
    			if(!cur.getString(cur.getColumnIndex(price)).equals("")){
    				header.add("Price");
    				values.add(cur.getString(cur.getColumnIndex(price)));
    			}
    			if(!cur.getString(cur.getColumnIndex(hdbCov)).equals("")){
    				header.add("HDB COV");
    				values.add(cur.getString(cur.getColumnIndex(hdbCov)));
    			}
    			if(!cur.getString(cur.getColumnIndex(builtinArea)).equals("")){
    				header.add("Floor Area");
    				values.add(cur.getString(cur.getColumnIndex(builtinArea)));
    			}
    			if(!cur.getString(cur.getColumnIndex(propertyFor)).equals("")){
    				header.add("For");
    				values.add(cur.getString(cur.getColumnIndex(propertyFor)));
    			}
    			if(!cur.getString(cur.getColumnIndex(listedOn)).equals("")){
    				header.add("Listed On");
    				values.add(cur.getString(cur.getColumnIndex(listedOn)));
    			}
    			if(!cur.getString(cur.getColumnIndex(sortBy)).equals("")){
    				header.add("Sort By");
    				values.add(cur.getString(cur.getColumnIndex(sortBy)));
    			}
    			if(!cur.getString(cur.getColumnIndex(leaseTerm)).equals("")){
    				header.add("Lease Term");
    				values.add(cur.getString(cur.getColumnIndex(leaseTerm)));
    			}
    			if(!cur.getString(cur.getColumnIndex(roomType)).equals("")){
    				header.add("Room Type");
    				values.add(cur.getString(cur.getColumnIndex(roomType)));
    			}
    			if(!cur.getString(cur.getColumnIndex(psf)).equals("")){
    				header.add("PSF");
    				values.add(cur.getString(cur.getColumnIndex(psf)));
    			}
    			if(!cur.getString(cur.getColumnIndex(bedRooms)).equals("")){
    				header.add("Bedroom");
    				values.add(cur.getString(cur.getColumnIndex(bedRooms)));
    			}
    			if(!cur.getString(cur.getColumnIndex(bathRooms)).equals("")){
    				header.add("Bathroom");
    				values.add(cur.getString(cur.getColumnIndex(bathRooms)));
    			}
    			header.add("Date");
				values.add(cur.getString(cur.getColumnIndex(searchName)) +", "+cur.getString(cur.getColumnIndex(curDate)));
    			
				ExpandAdapterBean bean = new ExpandAdapterBean();
    			String title = cur.getString(cur.getColumnIndex(propertyFor)) + (keyword.equals("") ? "" : " - " + keyword);
    			bean.setHeaderTxt(title);
    			bean.setSearchUrl(cur.getString(cur.getColumnIndex(searchUrl)));
    			bean.setHeaderArray(header);
    			bean.setValueArray(values);
    			search.add(bean);
        		cur.moveToNext();
    		}
    		cur.close();
    		database.close();
    	} catch (Exception e) {
    		Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
    	}
    	return search;
    }
    /**
     * 
     * @param historyFlag	:: Flag (set for history and not for favorites)
     * @return :: List of values that are stored in table
     */
    public List<HashMap<String, String>> getFavOrHistory(boolean historyFlag){
    	List<HashMap<String, String>> history = new ArrayList<HashMap<String,String>>();
    	try {
    		database = this.getWritableDatabase();
	    	Cursor cur = database.rawQuery("select * from " + (historyFlag ? tblHistory : tblFavorites)
	    			+" group by "+propertyId+" order by "+viewDate+" desc limit 0, 30",	new String[] { });
    		cur.moveToFirst();
    		for(int i=0;i<cur.getCount();i++){
    			Map<String, String> property = new HashMap<String, String>();
    			try{
	    			property.put("property_type", cur.getString(cur.getColumnIndex(type)));
	    			property.put("property_for", cur.getString(cur.getColumnIndex(propertyFor)));
	    			property.put("id", cur.getString(cur.getColumnIndex(propertyId)));
	    			property.put("property_title", cur.getString(cur.getColumnIndex(propertyTitle)));
	    			property.put("price_option", cur.getString(cur.getColumnIndex(priceOption)));
	    			property.put("price", cur.getString(cur.getColumnIndex(price)));
	    			property.put("latitude", cur.getString(cur.getColumnIndex(latitude)));
	    			property.put("longitude", cur.getString(cur.getColumnIndex(longitude)));
	    			property.put("psf", cur.getString(cur.getColumnIndex(psf)));
	    			property.put("bedrooms", cur.getString(cur.getColumnIndex(bedRooms)));
	    			property.put("bathroom", cur.getString(cur.getColumnIndex(bathRooms)));
	    			property.put("built-in_area", cur.getString(cur.getColumnIndex(builtinArea)));
	    			property.put("thumbnail", cur.getString(cur.getColumnIndex(thumbnail)));
	    			property.put("classification", cur.getString(cur.getColumnIndex(classification)));
	    			property.put("date_posted", cur.getString(cur.getColumnIndex(datePosted)));
	    			property.put("property_highlights", cur.getString(cur.getColumnIndex(propertyPriority)));
    			}catch(Exception e){
    				Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
    			}
    			history.add((HashMap<String, String>)property);
        		cur.moveToNext();
    		}
    		cur.close();
    		database.close();
    	} catch (Exception e) {
    		Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
    	}
    	return history;
    }
    /***
     * Clears history table
     */
    public void clearHistory(){
    	try{
    		database = this.getWritableDatabase();
    		database.delete(tblHistory, null, new String[]{});
    	}catch (Exception e) {
    		Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
    	database.close();
    }
    /**\
     * Deletes all the search rows in table
     */
    public void deleteSearch(){
    	try{
    		database = this.getWritableDatabase();
    		database.delete(tblSearch, null, new String[]{});
    	}catch (Exception e) {
    		Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
    	database.close();
    }
    /**
     * 
     * @param id 
     * 		Property ID
     * 
     * @return 
     * 		True or false
     * 
     * Will check is there same property id exists in database
     * 
     * if so then it will return true
     * 
     * otherwise returns false
     * 
     */
    public boolean isFavorite(String id){
    	boolean favorite = false;
    	try{
	    	database = this.getWritableDatabase();
			Cursor cur = database.rawQuery("select * from "+tblFavorites+" where "+propertyId+"=?", new String[] {id});
			cur.moveToFirst();
			if(cur.getCount()>0){
				favorite = true;
			}
			cur.close();
		}catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
		database.close();
		return favorite;
    }
    /**
     * 
     * @param id
     * 	property ID
     * 
     * Delete (Un-Favorite) the property from favorites
     *  
     */
    public void deleteFavorite(String id){
    	try{
	    	database = this.getWritableDatabase();
			database.delete(tblFavorites, propertyId+"=?", new String[] {id});
		}catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
		database.close();
    }
    /**
     * @param title	:: Enquiry title
     * @param pName :: Project Name (keyword)
     * @param remarks	:: Remarks
     * @param bedroom :: (count )
     * @param district :: Index of district
     * @param estate :: Index of estate
     * @param clasification :: Subtype
     * @param type :: Property Type
     * @param want :: Buy, shell, rent etc...
     * @param price :: Price range
     * 
     * Store all the enquiry values into the database and return when user resguest it 
     */
    public void addEnquiry(String title, String pName, String remarks, String bedroom, String district, String estate,
    		String clasification, String type, String want, String price){
    	try{
		    database = this.getWritableDatabase();  
			ContentValues values = new ContentValues();
			values.put(this.enquiryTitle, title);
			values.put(this.projectName, pName);
			values.put(this.remark, remarks);
			values.put(this.bedRooms, bedroom);
			values.put(this.district, district);
			values.put(this.estate, estate);
			values.put(this.classification, clasification);
		    values.put(this.type,type);
		    values.put(this.propertyFor, want);
		    values.put(this.price, price);
		    database.insert(tblEnquiry, null, values); 
    	}catch (Exception e) {
    		Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
    	database.close();
    }
    /**
     * 
     * @return :: Array of outBox values
     * Only the filed values are added into the list
     */
    public List<ExpandAdapterBean> getEnquirySummary(){
    	List<ExpandAdapterBean> enquiry = new ArrayList<ExpandAdapterBean>();
    	try {
    		database = this.getWritableDatabase();
	    	Cursor cur = database.rawQuery("select * from "+tblEnquiry+" order by enquiryId desc limit 0,30",	new String[] {});
    		cur.moveToFirst();
    		for(int i=0;i<cur.getCount();i++){
    			List<String> header = new ArrayList<String>();
    			List<String> values = new ArrayList<String>();
				header.add("Property Wanted");
				values.add(cur.getString(cur.getColumnIndex(type)));
				header.add("Classification");
				values.add(cur.getString(cur.getColumnIndex(classification)));
				header.add("Want To");
				values.add(cur.getString(cur.getColumnIndex(propertyFor)));
				header.add("Budget Range");
				if(!cur.getString(cur.getColumnIndex(price)).equals("")){
					values.add(cur.getString(cur.getColumnIndex(price)));
					header.add("Bedrooms");
				}
				values.add(cur.getString(cur.getColumnIndex(bedRooms)));
				header.add("District");
				values.add(cur.getString(cur.getColumnIndex(district)));
    			if(!cur.getString(cur.getColumnIndex(estate)).equals("")){
    				header.add("Estate");
    				values.add(cur.getString(cur.getColumnIndex(estate)));
    			}
    			ExpandAdapterBean bean = new ExpandAdapterBean();
    			String title = cur.getString(cur.getColumnIndex(enquiryTitle)) +" - "+cur.getString(cur.getColumnIndex(curDate));
    			bean.setHeaderTxt(title);
    			bean.setHeaderArray(header);
    			bean.setValueArray(values);
    			enquiry.add(bean);
        		cur.moveToNext();
    		}
    		cur.close();
    		database.close();
    	} catch (Exception e) {
    		Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
    	}
    	return enquiry;
    }
    /**
     * Clears the OutBox table values
     */
    public void clearEnquiry(){
    	try{
    		database = this.getWritableDatabase();
    		database.delete(tblEnquiry, null, new String[]{});
    	}catch (Exception e) {
    		Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
    	database.close();
    }
    /**
     * Clears the favorites table values
     */
    public void clearFavorites(){
    	try{
    		database = this.getWritableDatabase();
    		database.delete(tblFavorites, null, new String[]{});
    	}catch (Exception e) {
    		Log.e(this.getClass().getSimpleName(), e.getLocalizedMessage(), e);
		}
    	database.close();
    }
}