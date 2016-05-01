package restCall;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import org.json.JSONArray;
import org.json.simple.parser.*;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.FileReader;


public class ProcessViaApexRest {
	
	ConnectionInsert conIns ;
	String[] urlToken;
	private static String REST_ENDPOINT = "/services/apexrest" ;
    //private static String API_VERSION = "v34.0" ; // not needed in case of apex REst 
    private static String baseUri;
    private static String accessToken;
    
    HttpClient httpclient;
   // private static Header oauthHeader;
    
    public void Initialize(){
		conIns  = new RestConnectionInsert();
	}
    
public void runNew() throws Exception{
		
		

		Initialize();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println("Staring Authentication :"+dateFormat.format(date));
		String conDetail = conIns.getConnection();
		
		urlToken = conDetail.split(";");
		accessToken = urlToken[1];
		
		
		baseUri = urlToken[0] + REST_ENDPOINT  ;
		System.out.println("Creating Base URL to connect to SFDC=" + urlToken[0]);
	
		CreateRecordsViaBulkApex();
	
	}
	
		
	
	
	public void CreateRecordsViaBulkApex() throws HttpException, IOException, ParseException{
		
		System.out.println("\n_______________ ACCOUNT INSERT PROCESS BEGIN VIA FILE _______________");
		 
	      // String uri = baseUri + "/sobjects/Account/";
	        String uri = baseUri  + "/BulkContactInsert";
	        //String uri = baseUri + "/sobjects/Account/";
	        // String uri = baseUri + "/composite/tree/Account/";
	          CSVReader reader = null;
	          try {
	          	System.out.println("\n_______________ FIRST-READING CSV File _______________");
	          	
	              
	  		                //Get the CSVReader instance with specifying the delimiter to be used
	  		                reader = new CSVReader(new FileReader("D:/Dataloader1/ContactlistWithoutHeaders.csv"),',');
	  		                		                
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
	  		                        Newaccount.put("FirstName",token);
	  		                        else if (Position ==1)
	  		                        Newaccount.put("LastName", token);
	  		                        else if (Position ==2)
	  		                        Newaccount.put("Phone", token);
	  		                        else if (Position ==3)
		  		                    Newaccount.put("MobilePhone", token);
	  		                        else if (Position ==4)
		  		                    Newaccount.put("Fax", token);
	  		                        else if (Position ==5)
			  		                 Newaccount.put("Email", token);
	  		                        
	  		                        Position++;
	  		                   }
	  		                	// Putting current record in JSON array
	  		                	//Newaccount.put("array",ArrayOf2kCsvRecords);
	  		                	ArrayOf2kCsvRecords.put(Newaccount);
	  		                	
	  		                	
	  		                		if (counter == 199)
	  		                		{
	  						        	//create the JSON object containing the new Account details.
	  		                			System.out.println("\n_______________Posting to SFDC in chunks of 200 records______________");
	  			        
	  			        	
	  							        httpclient = new HttpClient();
	  							        PostMethod mypost = new PostMethod(uri);
	  							        
	  							        System.out.println("\n uri=" + uri);
	  							        String jsonText = ArrayOf2kCsvRecords.toString();
	  							        System.out.println("\n JSON TEXT=" + jsonText);
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
	  							        counter = 0;
	  							      ArrayOf2kCsvRecords = new JSONArray();
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
