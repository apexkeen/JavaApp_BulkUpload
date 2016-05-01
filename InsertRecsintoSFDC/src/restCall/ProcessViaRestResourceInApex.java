package restCall;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONObject;
//import org.json.JSONArray;
import org.json.simple.parser.*;
import java.io.IOException;
import java.io.FileReader;

public class ProcessViaRestResourceInApex {

	ConnectionInsert conIns ;
	String[] urlToken;
	private static String REST_ENDPOINT = "/services/data/" ;
    private static String API_VERSION = "v34.0" ;
    private static String baseUri;
    private static String accessToken;
    
    HttpClient httpclient;
   // private static Header oauthHeader;
    public void Initialize(){
		conIns  = new RestConnectionInsert();
	}
	
		
	public void run() throws Exception{
		Initialize();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("Staring Authentication :"+dateFormat.format(date));
		String conDetail = conIns.getConnection();
		
		urlToken = conDetail.split(";");
		accessToken = urlToken[1];
		
		
		baseUri = urlToken[0] + REST_ENDPOINT + API_VERSION ;
		System.out.println("Creating Base URL to connect to SFDC=" + urlToken[0]);
	
		CreateRecordsViaRestResource();
	}
	
	public void CreateRecordsViaRestResource() throws HttpException, IOException, ParseException{
		
		System.out.println("\n_______________ ACCOUNT INSERT PROCESS BEGIN VIA TREE RESOURCE FILE _______________");
		 
	      // String uri = baseUri + "/sobjects/Account/";
	        String uri = baseUri  + "/composite/tree/Account/";
	       try {
			       	System.out.println("\n_______________ FIRST-READING JSON File _______________");
			        JSONParser parser = new JSONParser();
			       	Object obj = parser.parse(new FileReader("C:/Users/shekhar01/Documents/All Salesforce/CURL/NewAccRecords.JSON"));
			       	
	       	
	       	
	       	httpclient = new HttpClient();
	       	System.out.println("\n URL IS=" + uri);
	        PostMethod mypost = new PostMethod(uri);
	       
	        String jsonText = obj.toString();
	        System.out.println("\n_JSON TEXT=" + jsonText);
	        mypost.setRequestHeader("Authorization", "OAuth " + accessToken); 
	        mypost.setRequestEntity(new StringRequestEntity(jsonText, "application/json", "UTF-8"));
	        int response=httpclient.executeMethod(mypost);
	       
	        if (response != HttpStatus.SC_OK) {
		          System.out.println("HTTP Not OK Response" + response);
		          System.out.println(mypost.getResponseBodyAsString());
	        }
	        else{
	        	 System.out.println("HTTP OK Response " + response);
	        }
	       	
	       }
	       
	       catch(ParseException ex){
	    	   System.out.println("Issue in parsing JSON FILE ");
	      		ex.printStackTrace();
	       }
	        catch (NullPointerException npe) {
	   		npe.printStackTrace();
	   	}
		
	}
	
}
