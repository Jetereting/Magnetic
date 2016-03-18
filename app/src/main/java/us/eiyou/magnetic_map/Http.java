package us.eiyou.magnetic_map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Http{
	public static String Result(String url,String params){
		URL u = null;
    	HttpURLConnection con = null;

    	try {
	    	u = new URL(url);	
	    	con = (HttpURLConnection)u.openConnection();	
	    	con.setRequestMethod("POST");	
	    	con.setDoOutput(true);	
	    	con.setDoInput(true);
	    	con.setUseCaches(false);
	    	con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");	
	    	OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");	
	    	osw.flush();
	    	osw.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
	    	if (con != null) {	
	    	}
    	}
    	
    	StringBuffer buffer = new StringBuffer();
    	try {

	    	BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
	    	String temp;
	    	while ((temp = br.readLine()) != null) {
	    		buffer.append(temp);
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}    	 
    	String response=buffer.toString();  	
    	return response;
	}
}
