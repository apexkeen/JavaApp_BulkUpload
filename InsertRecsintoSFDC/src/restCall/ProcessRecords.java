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
import org.json.JSONArray;
//import org.json.simple.parser.*;

//import org.json.JSONTokener;
import org.json.JSONException;

//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.BufferedReader;
import java.io.IOException;

import java.io.FileReader;
//import java.io.IOException;
 
import com.opencsv.CSVReader;

public class ProcessRecords {
	
	ConnectionInsert conIns ;
	String[] urlToken;
	private static String REST_ENDPOINT = "/services/data" ;
    private static String API_VERSION = "/v32.0" ;
    private static String baseUri;
    private static String accessToken;
    
//    /https://na1.salesforce.com/services/data/v34.0/composite/tree/Account/
    
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
		
		//Calling Function to Create Account Records
		CreateRecords();
		
	}
	
	//Function that actually insert record in SFDC
	public void CreateRecords() throws HttpException, IOException{
		
		  
		System.out.println("\n_______________ Account INSERT PROCESS BEGIN _______________");
		 
       String uri = baseUri + "/sobjects/Account/";
      // String uri = baseUri + "/composite/tree/Account/";
        CSVReader reader = null;
        try {
        	System.out.println("\n_______________ FIRST-READING CSV File _______________");
        	
            
		                //Get the CSVReader instance with specifying the delimiter to be used
		                reader = new CSVReader(new FileReader("D:/Myaccounts.csv"),',');
		                		                
		                String [] nextLine;
		                
		             
		             
		                //Read one line at a time
		                int counter = 0;
		                JSONArray ArrayOf2kCsvRecords = new JSONArray();
		                while ((nextLine = reader.readNext()) != null)
		                {
		                	counter++;
		                	
		                	JSONObject Newaccount = new JSONObject();
		                	int Position = 0;
		                	System.out.println("\n_______________ Reading Line by Line_______________");
		                	for(String token : nextLine)
		                    {
		                        //Print all tokens
		                        System.out.println(token);
		                        
		                        if(Position ==0)	
		                        Newaccount.put("Name",token);
		                        else if (Position ==1)
		                        Newaccount.put("Type", token);
		                        else
		                        Newaccount.put("Industry", token);
		                        
		                        Position++;
		                   }
		                	// Putting current record in JSON array
		                	//Newaccount.put("array",ArrayOf2kCsvRecords);
		                	ArrayOf2kCsvRecords.put(Newaccount);
		                	
		                	
		                		if (counter > 0)
		                		{
						        	//create the JSON object containing the new Account details.
		                			System.out.println("\n_______________Posting to SFDC in chunks of 2 records______________");
			        
			        	
							        httpclient = new HttpClient();
							        PostMethod mypost = new PostMethod(uri);
							        
							        
							        System.out.println("\n uri=" + uri);
							        
							        String jsonText = ArrayOf2kCsvRecords.toString();
							        String newStr = jsonText.substring(1, jsonText.length()-1);
							        System.out.println("\n JSON TEXT=" + newStr);
							        mypost.setRequestHeader("Authorization", "OAuth " + accessToken); 
							        mypost.setRequestEntity(new StringRequestEntity(newStr, "application/json", "UTF-8"));
							        int response=httpclient.executeMethod(mypost);
							       
							        if (response != HttpStatus.SC_OK) {
								          System.out.println("HTTP Not OK Response" + response);
								          System.out.println(mypost.getResponseBodyAsString());
							        }
							        else{
							        	 System.out.println("HTTP OK Response " + response);
							        }
							        counter = 0;
		                		} // end of If(Counter =2000) 
		                }// end of While loop, File Reading
         
		        } catch (JSONException e) {
		    		System.out.println("Issue creating JSON or processing results");
		    		e.printStackTrace();
		    	}  catch (NullPointerException npe) {
		    		npe.printStackTrace();
		    	}
        
        finally {
		        	try {
		                reader.close();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
        		}
        
		}
	

}
