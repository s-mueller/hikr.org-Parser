package li.sebastianmueller.hikr.util;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Util {

	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static double getFileSizeInKB(File file) {
		long fileSizeInBytes = file.length();
		return fileSizeInBytes / 1024.0;
	}
	
}