package li.sebastianmueller.hikr.ui;
public interface HikrListener {

	public void updateStatus(double count, double total, String message);
	
	public void done();

	public void updateMessage(String message);
	
	public void addPayload(double payloadSize);
	
}