package li.sebastianmueller.hikr.extractors;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import li.sebastianmueller.hikr.util.Util;

public class GPSExtractor {

	private static final String GPS_FOLDER = "gps/";
	private static final String GEO_TAG = "geo_table";
	private static final String HREF = "href";
	private static final String HTTP = "http:";
	private static final String LINK_TAG = "a";
	
	public static void extract(Document doc, String path) {
		try {
			Elements ls = doc.getElementById(GEO_TAG).getElementsByTag(LINK_TAG);
			for (Element link : ls) {
				String linkHref = link.attr(HREF);
				if (linkHref.startsWith(HTTP)) {
					File localFile = new File(path + GPS_FOLDER + FilenameUtils.getBaseName(linkHref) + "." + FilenameUtils.getExtension(linkHref));
					FileUtils.copyURLToFile(new URL(linkHref), localFile);
					ExtractHTML.addPayload(Util.getFileSizeInKB(localFile));
				}
			}
		} catch (Exception e) {}
	}

}
