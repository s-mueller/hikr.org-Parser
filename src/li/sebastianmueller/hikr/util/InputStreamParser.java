package li.sebastianmueller.hikr.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class InputStreamParser {

	private InputStreamParser() {}
	
	public static String convertStreamToString(InputStream inputStream) throws IOException {
		String string = "";
		String line = "";
		  
	    BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
	    
	    while ((line = rd.readLine()) != null) {
	    	string += line;
	    }
	    
	    return string;
	}
	
}