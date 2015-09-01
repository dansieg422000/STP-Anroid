package com.z.stproperty.shared;

import com.google.analytics.tracking.android.Fields;

/*********************************************************************************************************
* Class	: Constants
* Type		: Activity
* Date		: 18 Set 2013
* 
* 
* Description
* 
* The commonly used constants are declared here
* 
* Note ::
*  	this class is only a utility class,
*  	you should make the class final and define a private constructor:
*  
*  	The reason is that you don't want that the default parameter-less constructor can be used anywhere in your code. 
*  	If you make the class final, 
*  	it can't be extended in subclasses what is considered to be appropriate for utility classes.
*  
*  	Since you declared only a private constructor, 
*  	other classes wouldn't be able to extend it anyway, 
*  	but no harm is done by marking it.
*********************************************************************************************************/

public final class Constants {
	
	public static final int REQUEST_NAMESEARCH = 10;
	public static final int REQUEST_DELETESEARCH = 11;

	public static final int REQUEST_PROPERTYTYPE = 1;
	public static final int REQUEST_CLASSIFICATION = 2;
	public static final int REQUEST_PRICE = 3;
	public static final int REQUEST_LANDSIZE = 4;
	public static final int REQUEST_DISTRICT = 5;
	public static final int REQUEST_BEDROOM = 6;
	public static final int REQUEST_BATHROOM = 7;
	public static final int REQUEST_TENURE = 8;
	public static final int REQUEST_PSF = 9;
	public static final int REQUEST_TOP = 10;
	public static final int REQUEST_LISTEDON = 11;
	public static final int REQUEST_OPTIONS = 12;
	public static final int REQUEST_SORT = 13;
	public static final int REQUEST_USER_ACTION = 14;
	public static final int REQUEST_ESTATE = 15;
	public static final int REQUEST_SHARE = 16;
	public static final int REQUEST_FILTER = 17;
	public static final int REQUEST_LOCATION = 18;
	public static final int REQUEST_UPDATE_LOCATION = 19;
	public static final int REQUESTCODE_SETTINGS = 20;
	public static final int REQUESTCODE_CONFIRM = 21;
	public static final int REQUEST_LEASETERM = 22;
	public static final int REQUEST_HDBSCHEME = 23;
	public static final int REQUEST_HDBCOV = 24;
	public static final int REQUEST_SEARCH_DISTRICT = 25,REQUEST_SEARCH_OTHERS = 26;
	public static final int REQUEST_ROOMTYPE = 27;
	public static final int REQUEST_ENTITLE = 28;
	public static final int REQUEST_ENMODE = 29;
	public static final int REQUEST_ENTIME = 30;
	public static final int REQUEST_ENSALUTATION = 31;
	
	public static final int ONE = 1;
	public static final int TWO = 2;
	public static final int THREE = 3;
	public static final int FOUR = 4;
	public static final int FIVE = 5;
	public static final int SIX = 6;
	public static final int SEVEN = 7;
	public static final int EIGHT = 8;
	public static final int NONE = 9;

	public static final String CONSUMERKEY = "hr4B6eAossehtlOL2ysGt7WJn";
	public static final String CONSUMERSECRET = "JgtgGwZo5E6SPOyIbxr74RSp7KQVl9qdHNtrk91OfLMneK4XqK";

	public static final String APP_ID = "116751581822315";
	
	public static final String NETWORKSTR = "network";
	public static final String LOWINTERNETSTR = "lowinternet";
	
	public static final String[] SORTBYARRAY = {"Latest First","Oldest First", "Price High to Low","Price Low to High","Title Z-A","Title A-Z","PSF High to Low","PSF Low to High"};
	/**
	 * dropdownList : Total number of districts and with numbers
	 * Total count is 28 districts
	 * Used to display directories with district wise
	 * 
	 */
	public static final String[] RESIDENTIAL_TYPE = {"Condo", "HDB", "Landed"};
	public static final String[] DISTRICTS = {"1-Cecil, Marina, Raffles Place, People's Park","2-Anson Road, Tanjong Pagar",
											    "3-Queenstown, Tiong Bahru, Alexandra","4-Telok Balangah, WTC, Mount Faber, Habourfront",
											     "5-Clementi, Pasir Panjang, West Coast, Dover","6-High Street, Beach Road",
											     "7-Golden Mile, Middle Road","8-Little India","9-Orchard Road, River Valley, Cairnhill",
											     "10-Tanglin Road, Farrer, Holland, Bukit Timah, Ardmore","11-Thomson, Watten Estate, Novena, Newton",
											     "12-Serangoon, Toa Payoh, Balestier","13-Braddell, Macpherson","14-Sims, Paya Lebar, Geylang, Eunos",
											     "15-Tanjong Rhu, Meyer, Marine Parade, Katong, Amber Road, Joo Chiat","16-Kew Drive, Upper East Coast Road, Eastwood, Bedok, Siglap, Bayshore",
											     "17-Flora, Loyang, Changi","18-Simei, Tampines, Pasir Ris","19-Hougang, Punggol, Sengkang, Serangoon",
											     "20-Mei Hwan, Thomson, Ang Mo Kio, Bishan, Braddell","21-Upper Bukit Timah, Ulu Pandan",
											     "22-Lakeside, Jurong, Boon Lay","23-Choa Chu Kang, Bukit Batok, Dairy Farm, Hillview, Bukit Panjang",
											     "24-Lim Chu Kang","25-Woodgrove, Kranji, Woodlands","26-Upper Thomson, Springleaf",
											     "27-Sembawang, Yishun","28-Seletar, Yio Chu Kang"};
	public static final String[] TOP = { "1970", "1971", "1972", "1973", "1974",
											"1975", "1976", "1977", "1978", "1979", "1980", "1981", "1982",
											"1983", "1984", "1985", "1986", "1987", "1988", "1989", "1990",
											"1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998",
											"1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006",
											"2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014",
											"2015", "2016", "2017", "2018", "2019" };
	public static final String[] BEDROOMS = { "Any", "Studio", "1 Bedroom", "2 Bedrooms",
												"3 Bedrooms", "4 Bedrooms", "5 Bedrooms", "6 Bedrooms",
												"7 Bedrooms", "8 Bedrooms", "9 Bedrooms", "10+ Bedrooms" };
	public static final String[] BATHROOMS = { "Any", "1 Bathroom", "2 Bathrooms",
												"3 Bathrooms", "4 Bathrooms", "5 Bathrooms", "6 Bathrooms",
												"7 Bathrooms", "8 Bathrooms", "9 Bathrooms", "10+ Bathrooms" };
	public static final String[] LISTEDON = { "1 Week", "2 Weeks", "3 Weeks",	"1 Month" };
	public static final String[] PSFMIN = { "S$0", "S$100", "S$200", "S$300", "S$400",
											"S$500", "S$600", "S$700", "S$800", "S$900", "S$1,000", "S$1,100",
											"S$1,200", "S$1,300", "S$1,400", "S$1,500", "S$1,750", "S$2,000",
											"S$2,500", "S$3,000", "S$3,500", "S$4,000", "S$4,500", "S$5,000+" };
	public static final String[] PSFMAX = { "S$0", "S$100", "S$200", "S$300", "S$400",
											"S$500", "S$600", "S$700", "S$800", "S$900", "S$1,000", "S$1,100",
											"S$1,200", "S$1,300", "S$1,400", "S$1,500", "S$1,750", "S$2,000",
											"S$2,500", "S$3,000", "S$3,500", "S$4,000", "S$4,500", "S$5,000+" };
	public static final String[] TENURERENT = { "Free Hold", "999 Years", "99 Years" };
	public static final String[] TENURESALE = { "30 Year Leasehold", "60 Year Leasehold", "99 Year Leasehold", "999 Year Leasehold" };
	public static final String[] CLASSIFICATION = { "Classification" };
	public static final String[] OPTIONS = { "With Photos", "With Videos", "New Launches" };
	public static final String[] PROPERTYTYPE = { "Condo", "Landed", "HDB/HUDC", "Industrial", "Land", "Office", "Retail" };
	public static final String[] RENTAL_PROPERTYTYPE = { "Condo", "Landed", "HDB/HUDC" };
	public static final String[] RENTMINPRICE = { "S$0", "S$500", "S$1,000", "S$1,500", "S$2,000", "S$2,500", "S$3,000",
													"S$3,500", "S$4,000", "S$5,000", "S$6,000", "S$7,000", "S$8,000", "S$9,000",
													"S$10,000", "S$12,000", "S$15,000", "S$20,000", "S$30,000", "S$40,000", "S$50,000" };
	public static final String[] RENTMAXPRICE = { "S$0", "S$500", "S$1,000", "S$1,500", "S$2,000", "S$2,500", "S$3,000",
													"S$3,500", "S$4,000", "S$5,000", "S$6,000", "S$7,000", "S$8,000", "S$9,000",
													"S$10,000", "S$12,000", "S$15,000", "S$20,000", "S$30,000", "S$40,000", "S$50,000" };
	public static final String[] SALEMINPRICE = { "S$0", "S$100,000", "S$200,000", "S$300,000", "S$400,000",
													"S$500,000", "S$600,000", "S$700,000", "S$800,000", "S$900,000",
													"S$1,000,000", "S$1,250,000", "S$1,500,000", "S$2,000,000",
													"S$3,000,000", "S$4,000,000", "S$5,000,000",
													"S$6,000,000", "S$8,000,000", "S$10,000,000", "S$15,000,000",
													"S$20,000,000", "S$30,000,000", "S$40,000,000", "S$50,000,000" };
	public static final String[] SALEMAXPRICE = { "S$0", "S$100,000", "S$200,000", "S$300,000", "S$400,000",
													"S$500,000", "S$600,000", "S$700,000", "S$800,000", "S$900,000",
													"S$1,000,000", "S$1,250,000", "S$1,500,000", "S$2,000,000",
													"S$3,000,000", "S$4,000,000", "S$5,000,000",
													"S$6,000,000", "S$8,000,000", "S$10,000,000", "S$15,000,000",
													"S$20,000,000", "S$30,000,000", "S$40,000,000", "S$50,000,000" };
	public static final String[] RENTAL_MINPRICE = { "S$0", "S$100", "S$200", "S$300", "S$400", "S$500", "S$600", "S$700", "S$800",
													"S$900", "S$1000", "S$1100", "S$1200", "S$1300", "S$1400", "S$1500", "S$1600",
													"S$1700", "S$1800", "S$1900", "S$2000", "S$2500", "S$3000", "S$3500", "S$4000",
													"S$4500", "S$5000" };
	public static final String[] RENTAL_MAXPRICE = { "S$0", "S$100", "S$200", "S$300", "S$400", "S$500", "S$600", "S$700", "S$800",
													"S$900", "S$1000", "S$1100", "S$1200", "S$1300", "S$1400", "S$1500", "S$1600",
													"S$1700", "S$1800", "S$1900", "S$2000", "S$2500", "S$3000", "S$3500", "S$4000",
													"S$4500", "S$5000" };
	public static final String[] CONDOCLASSIFICATION = { "Apartment", "Condo", "Walk-up", "Cluster House", "Executive Condon" };
	public static final String[] LANDEDCLASSIFICATION = { "Bungalow", "Semi-Detached", "Town House", "Detached House", "Terrace House",
															"Corner Terrace", "Good Class Bungalow", "Shophouse", "Land Only",
															"Conservation House" };
	public static final String[] HDBCLASSIFICATION = { "2 Room Flats",
														"3 Room Flats", "4 Room Flats", "5 Room Flats",
														"Executive Apartment", "Executive Maisonette", "Jumbo Flat",
														"Studio" };
	public static final String[] INDUSCLASSIFICATION = { "Light Industrial B1", "Factory/Workshop B2", "Warehouse", "Dormitory" };
	public static final String[] LANDCLASIFICATION = { "Land", "Land with building" };
	public static final String[] OFFICECLASIFICATION = { "Office", "Business/Science Park" };
	public static final String[] RETAILCLASIFICATION = { "Mall Shop", "Shop/Shophouse", "Food&Beverage", "Medical", "Other Retail" };
	public static final String[] FLOORAREA = { "0 sqft", "500 sqft", "700 sqft", "1000 sqft", "1200 sqft", "1500 sqft", "2000 sqft",
												"2500 sqft", "3000 sqft", "4000 sqft", "5000 sqft", "7500 sqft", "10,000 sqft" };

	public static final String[] LANDAREA = { "0 sqft", "1,000 sqft", "2,500 sqft", "5,000 sqft", "7,500 sqft", "10,000 sqft"
											, "15,000 sqft", "20,000 sqft", "30,000 sqft", "40,000 sqft", "50,000 sqft"};
	
	public static final String[] ESTATE = { "1-Ang Mo Kio", "2-Bedok",
											"3-Bishan", "4-Bukit Batok", "5-Bukit Merah", "6-Bukit Panjang",
											"7-Bukit Timah", "8-Central Area", "9-Chua Chu Kang",
											"10-Clementi", "11-Geylang", "12-Hougang", "13-Jurong East",
											"14-Jurong West", "15-Kallang / Whampoa", "16-Lim Chu Kang",
											"17-Marine Parade", "18-Pasir Ris", "19-Punggol", "20-Queenstown",
											"21-Sembawang", "22-Sengkang", "23-Serangoon", "24-Tampines",
											"25-Toa Payoh", "26-Woodlands", "27-Yishun" };
	
	public static final String[] SEARCH_ROOMTYPE = { "Any", "Master", "Common" };
	public static final String[] SEARCH_LEASEPERIOD = { "Any", "1 Month", "2 Months", "3 Months", "4 Months", "5 Months", "6 Months"
														, "7 Months", "8 Months", "9 Months", "10 Months", "11 Months", "12 Months", "13 Months"
														, "14 Months", "15 Months", "16 Months", "17 Months", "18 Months", "19 Months", "20 Months"
														, "21 Months", "22 Months", "23 Months", "24 Months", "24+ Months", "Flexible"};
	
	public static final String[] AGENT_FILTER = {"Show All Agents","Featured Agents","By Alphabets"};
	public static final String[] CONDO_FILTER = {"Show All Condos","Popular Condos", "New Projects", "Districts", "By Alphabets"};
	public static final String[] COMMERCIAL_FILTER = {"Show All Commercials","Popular Commercials", "New Projects", "Districts", "By Alphabets"};
	public static final String[] INTUSTRIAL_FILTER = {"Show All Industrials","Popular Industrials", "New Projects", "Districts", "By Alphabets"};
	public static final String[] BY_ALPHABETS = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N",
													"O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	
	public static final String[] LEASETERM = {"1 Month", "2 Months", "3 Months", "4 Months", "5 Months", "6 Months", "7 Months", 
											"8 Months", "9 Months", "10 Months", "11 Months", "12 Months", "13 Months", "14 Months",
											"15 Months", "16 Months", "17 Months", "18 Months", "19 Months", "20 Months", "21 Months",
											"22 Months", "23 Months", "24+ Months", "Flexible"};
	public static final String[] HDBSCHEME = {"DBSS","HUDC"};
	public static final String[] COVMIN = {"S$0", "S$10,000", "S$20,000", "S$30,000", "S$40,000", "S$50,000", "S$60,000",
										"S$70,000", "S$80,000", "S$90,000", "S$100,000", "S$110,000", "S$120,000", "S$130,000",
										"S$140,000", "S$150,000", "S$160,000", "S$170,000", "S$180,000", "S$190,000", "S$200,000"};
	public static final String[] COVMAX = {"S$0", "S$10,000", "S$20,000", "S$30,000", "S$40,000", "S$50,000", "S$60,000",
										"S$70,000", "S$80,000", "S$90,000", "S$100,000", "S$110,000", "S$120,000", "S$130,000",
										"S$140,000", "S$150,000", "S$160,000", "S$170,000", "S$180,000", "S$190,000", "S$200,000"};
	public static final String[] ROOMTYPE = {"Master", "Common"};
    public static final String[] PROPERTYTYPEARRAY = new String[]{"Condo","Landed","HDB/HUDC","Industrial","Land","Office","Retail"};
	
    //Analytics
    public static final String ANALYTICS_SEARCHKEYWORDID = Fields.customDimension(1);
    public static final String ANALYTICS_PROPERTYWANTED =Fields.customDimension(2);
    public static final String ANALYTICS_PROPERTYTYPE = Fields.customDimension(3);
    public static final String ANALYTICS_PROPERTYCLASSIFICATION = Fields.customDimension(4);
    public static final String ANALYTICS_PROPERTYDISTRICT = Fields.customDimension(5);
    public static final String ANALYTICS_ENQUIRY_PROPERTYWANTED = Fields.customDimension(6);
    public static final String ANALYTICS_ENQUIRY_CLASSIFICATION = Fields.customDimension(7);
    public static final String ANALYTICS_ENQUIRY_WANTTO = Fields.customDimension(8);
    public static final String ANALYTICS_ENQUIRY_TITLE = Fields.customDimension(9);
    public static final String ANALYTICS_ENQUIRY_PROJECTNAME = Fields.customDimension(10);
    public static final String ANALYTICS_CONTACT_EMAIL = Fields.customDimension(11);
    public static final String ANALYTICS_CONTACT_PHONENUMBER = Fields.customDimension(12);
    public static final String ANALYTICS_CONTACT_NAME = Fields.customDimension(13);
    public static final String ANALYTICS_CONTACT_FEEDBACK = Fields.customDimension(14);
	
    private Constants(){
		// constant class constructor
	}
}
