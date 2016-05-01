package restCall;

import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;
import org.json.JSONTokener;

public class RestConnectionInsert implements ConnectionInsert {

	

		@Override
		public String getConnection() throws Exception{
			
			// TODO Auto-generated method stub
			String connectionData;
			PostMethod mypost = new PostMethod(url);
	    	NameValuePair[] data = {
	    	          new NameValuePair("grant_type", password),
	    	          new NameValuePair("client_id", client_id),
	    	          new NameValuePair("client_secret", client_secret),
	    	          new NameValuePair("username", USERNAME),
	    	          new NameValuePair("password", PASSWORD)
	    	        }; 
	    	
	    	mypost.setRequestBody(data);
	    	HttpClient client = new HttpClient();
	    	int response=client.executeMethod(mypost);
	    	JSONObject jobj = null;
	        //System.out.println("b");
	        if (response != HttpStatus.SC_OK) {
	          System.out.println("HTTP " + response);
	          System.out.println(mypost.getResponseBodyAsString());
	        }else {
	        	mypost.getResponseHeaders();
	            //System.out.println("OK");
	            jobj = new JSONObject(new JSONTokener(
						new InputStreamReader(
								mypost.getResponseBodyAsStream())));
	            connectionData = jobj.getString("instance_url");
	            connectionData += ";"+jobj.getString("access_token");
	            //System.out.println("access_token " + jobj.getString("access_token"));
	            return connectionData;
	          }
	        //System.out.println("Success");
			return null;
		}

	}

	

