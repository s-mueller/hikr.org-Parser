package li.sebastianmueller.hikr.extractors;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import li.sebastianmueller.hikr.ui.HikrListener;
import li.sebastianmueller.hikr.util.Util;

public class ExtractHTML {

	private static final String HIKR_URL = "https://www.hikr.org/user/";
	private static final String TOUR_URL = "/tour/";
	private static final String SKIP_URL = "?skip=";
	
	public static List<HikrListener> listeners = new ArrayList<>();
	
	public static void addListener(HikrListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public static void parse(String userName, int startPosition) throws IOException {
		List<String> links = getAllLinksForHikrUser(userName);
		
		double count = 1;
		
		for (String link : links) {
			for (HikrListener listener : listeners) {
				listener.updateStatus(count, links.size(), "Download: " + link);
			}
			
			if (count < startPosition) {
				System.out.println("Skip tour #" + count);
				count++;
			} else {
				System.out.println((int) count + " of " + links.size() + " (" + Util.round(count / links.size() * 100, 2) + "%): " + link);
				
				Document doc = Jsoup.connect(link).get();
				String postID = extractPostID(link);
				
				HTMLFileExtractor.extract(userName, doc, postID);
				ImageExtractor.extract(doc, userName + "/", postID);
				GPSExtractor.extract(doc, userName + "/");
				
				count++;
			}
		}
		for (HikrListener listener : listeners) {
			listener.done();
		}
	}

	private static String extractPostID(String link) {
		return link.substring(link.indexOf("post") + "post".length(), link.lastIndexOf("."));
	}
	
	private static List<String> getAllLinksForHikrUser(String userName) throws IOException {
		List<String> links = new ArrayList<String>();
		int tourCounter = 0;
		boolean endReached = false;
		
		while(!endReached) {
			String url = HIKR_URL + userName + TOUR_URL + SKIP_URL + tourCounter;
			List<String> newLinks = LinkExtractor.extract(url);
			links.addAll(newLinks);
			tourCounter += 20;
			if (newLinks.size() == 0) {
				endReached = true;
			}
		}
		return links;
	}

	public static void addPayload(double payload) {
		for (HikrListener listener : listeners) {
			listener.addPayload(payload);
		}
	}

	public static void updateMessage(String message) {
		for (HikrListener listener : ExtractHTML.listeners) {
			listener.updateMessage("Download: " + message);
		}
	}
	
}