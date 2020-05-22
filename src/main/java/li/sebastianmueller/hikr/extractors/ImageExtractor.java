package li.sebastianmueller.hikr.extractors;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
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

	private static final String FILES_URL = "https://f.hikr.org/files/";
	private static final String PDF = ".pdf";
	private static final String HTML = ".html";
	private static final String PHOTO = "photo";
	private static final String PDF_LOGO_URL = "https://s.hikr.org/images/pdflogo.png";
	private static final String IMAGES_FOLDER = "bilder/";
	private static final String GALLERY_TAG = "new_gallery";
	private static final String SRC_TAG = "src";
	private static final String IMAGE_TAG = "img";

	public static List<ImageDTO> getImageLinks(Document doc) {
		List<ImageDTO> imageIDs = new ArrayList<>();
		Element galleryElement = doc.getElementById(GALLERY_TAG);
		Elements images = galleryElement == null ? new Elements() : galleryElement.select(IMAGE_TAG);
		Element linkElement = doc.getElementById(GALLERY_TAG);
		Elements hrefs = linkElement == null ? new Elements() : linkElement.select("a");

		if (!images.isEmpty()) {
			int imageCounter = 1;
			for (Element image : images) {
				String url = image.absUrl(SRC_TAG);
				if (url.equals(PDF_LOGO_URL)) {
					Element href = hrefs.get(imageCounter - 1);
					String newURL = href.toString();
					newURL = newURL.substring(newURL.indexOf(PHOTO) + PHOTO.length(), newURL.indexOf(HTML));
					url = FILES_URL + newURL + PDF;
				} else {
					url = url.substring(0, url.length() - 5) + url.substring(url.length() - 4);
				}
				imageIDs.add(new ImageDTO(getImageID(url), url));
				imageCounter++;
			}
		}
		return imageIDs;
	}
	
	public static void extract(Document doc, String path, String postID) throws IOException {
		List<ImageDTO> images = getImageLinks(doc);
		for (int imageCounter = 0; imageCounter < images.size(); imageCounter++) {
			String url = images.get(imageCounter).getUrl();
			if (!exists(url)) {
				url = url.substring(0, url.lastIndexOf(".")) + "l" + url.substring(url.lastIndexOf("."));
			}
			System.out.println("Download image: " + url);
			File localFile = new File(path + IMAGES_FOLDER + postID + "/" + imageCounter + "." + FilenameUtils.getExtension(url));
			if (!localFile.exists()) {
				FileUtils.copyURLToFile(new URL(url), localFile);
				ExtractHTML.addPayload(Util.getFileSizeInKB(localFile));
				ExtractHTML.updateMessage(url);
			}
		}
	}

	public static boolean exists(String url){
		try {
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static String getImageID(String imageURL) {
		return imageURL.substring(imageURL.indexOf("/files/") + "/files/".length(), imageURL.lastIndexOf("."));
	}

}
