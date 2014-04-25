package li.sebastianmueller.hikr.extractors;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import li.sebastianmueller.hikr.util.Util;

public class ImageExtractor {

	private static final String FILES_URL = "http://f.hikr.org/files/";
	private static final String PDF = ".pdf";
	private static final String HTML = ".html";
	private static final String PHOTO = "photo";
	private static final String PDF_LOGO_URL = "http://s.hikr.org/images/pdflogo.png";
	private static final String IMAGES_FOLDER = "bilder/";
	private static final String GALLERY_TAG = "new_gallery";
	private static final String SRC_TAG = "src";
	private static final String IMAGE_TITLE_ATTRIBUTE = "title";
	private static final String IMAGE_TAG = "img";
	private static final String LINEBREAK = "\n";
	
	public static List<ImageDTO> getImageLinks(Document doc, String path, String postID) throws MalformedURLException, IOException {
		List<ImageDTO> imageIDs = new ArrayList<ImageDTO>();
		Elements images = doc.getElementById(GALLERY_TAG).select(IMAGE_TAG);
		Elements hrefs = doc.getElementById(GALLERY_TAG).select("a");
		
		int imageCounter = 1;
		StringBuilder imageTitles = new StringBuilder();
		for (Element image : images) {
			String url = image.absUrl(SRC_TAG);
			if (url.equals(PDF_LOGO_URL)) {
				Element href = hrefs.get(imageCounter - 1);
				String newURL = href.toString();
				newURL = newURL.substring(newURL.indexOf(PHOTO) + PHOTO.length(), newURL.indexOf(HTML));
				url = FILES_URL + newURL + PDF;
			} else {
				String title = image.attr(IMAGE_TITLE_ATTRIBUTE);
				imageTitles.append(imageCounter + ": " + title + LINEBREAK);
				url = url.substring(0, url.length() - 5) + url.substring(url.length() - 4, url.length());
			}
			imageIDs.add(new ImageDTO(getImageID(url), url));
			imageCounter++;
		}
		imageTitles.deleteCharAt(imageTitles.lastIndexOf(LINEBREAK));
		return imageIDs;
	}
	
	public static void extract(Document doc, String path, String postID) throws MalformedURLException, IOException {
		List<ImageDTO> images = getImageLinks(doc, path, postID);
		for (int imageCounter = 0; imageCounter < images.size(); imageCounter++) {
			String url = images.get(imageCounter).getUrl();
			System.out.println("Download image: " + url);
			File localFile = new File(path + IMAGES_FOLDER + postID + "/" + imageCounter + "." + FilenameUtils.getExtension(url));
			FileUtils.copyURLToFile(new URL(url), localFile);
			ExtractHTML.addPayload(Util.getFileSizeInKB(localFile));
			ExtractHTML.updateMessage(url);
		}
	}
	
	private static String getImageID(String imageURL) {
		return imageURL.substring(imageURL.indexOf("/files/") + "/files/".length(), imageURL.lastIndexOf("."));
	}

}
