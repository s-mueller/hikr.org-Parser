package li.sebastianmueller.hikr.extractors;

public class ImageDTO {

	private String id;
	private String url;
	
	public ImageDTO(String id, String url) {
		this.id = id;
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getId() {
		return id;
	}
	
}