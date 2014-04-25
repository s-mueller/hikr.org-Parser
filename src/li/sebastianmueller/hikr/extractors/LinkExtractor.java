package li.sebastianmueller.hikr.extractors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import li.sebastianmueller.hikr.util.InputStreamParser;

public class LinkExtractor {

	private static final String HTML_A_HREF_TAG_PATTERN = "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	private static final String POST_URL = "http://www.hikr.org/tour/post";
	
	public static List<String> extract(String url) throws ClientProtocolException, IOException {
		List<String> links = new ArrayList<String>();
		HttpGet httpGetID = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		
		HttpResponse response = client.execute(httpGetID);
		String responseString = InputStreamParser.convertStreamToString(response.getEntity().getContent());
		Pattern pattern = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
		Matcher matcher = pattern.matcher(responseString);
			
		while (matcher.find()) {
			String href = matcher.group(1);
			if (href.startsWith("'" + POST_URL)) {
				href = href.substring(1, href.length() - 1);
				if (!links.contains(href)) {
					links.add(href);
				}
			}
		}
		
		httpGetID.abort();
		return links;
	}

}