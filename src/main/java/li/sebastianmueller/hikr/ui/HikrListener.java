package li.sebastianmueller.hikr.ui;
public interface HikrListener {

	void updateStatus(double count, double total, String message);
	
	void done();

	void updateMessage(String message);
	
	void addPayload(double payloadSize);
	
}