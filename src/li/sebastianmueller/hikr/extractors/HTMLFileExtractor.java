package li.sebastianmueller.hikr.extractors;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;

import li.sebastianmueller.hikr.util.Util;

public class HTMLFileExtractor {

	private static final String IMAGE_SEPARATOR = "%2C";
	private static final String PRINT_URL = "https://www.hikr.org/print_rando.php?p_rando_font=medium&p_rando_box=on&p_rando_text=on&p_rando_comments=on&p_rando_photos=on&p_photo_size=large&post_id=";
	private static final String SELECTION_URL = "&photos_sel=%2C";
	private static final String PRINT_ACTION_URL = "&act=print&submit=Drucken";
	
	public static void extract(String userName, Document doc, String postID) throws IOException {
		List<ImageDTO> images = ImageExtractor.getImageLinks(doc, userName + "/", postID);
		
		String imagePart = "";
		for (ImageDTO image : images) {
			imagePart = imagePart + IMAGE_SEPARATOR + image.getId();
		}
		
		String url = PRINT_URL + postID + SELECTION_URL + imagePart + PRINT_ACTION_URL;
		File localFile = new File(userName + "/" + postID + ".html");
		FileUtils.copyURLToFile(new URL(url), localFile);
		ExtractHTML.addPayload(Util.getFileSizeInKB(localFile));	
	}

}
