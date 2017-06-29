package ar.edu.utn.sigmaproject.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.Duration;

import org.zkoss.image.AImage;
import org.zkoss.image.Image;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import ar.edu.utn.sigmaproject.domain.Product;

public class RenderElHelper {

	public static String getFormattedTime(Duration time) {
		if(time != null) {
			int hours = time.getHours();
			int minutes = time.getMinutes();
			while(minutes >= 60) {
				minutes -= 60;
				hours += 1;
			}
			return String.format("%d Hrs, %d Min", hours, minutes);
		}
		return "";
	}

	public static String getFormattedProcessTime(Duration time) {
		if(time != null) {
			int hours = time.getHours();
			int minutes = time.getMinutes();
			while(minutes >= 60) {
				hours = hours + 1;
				minutes = minutes - 60;
			}
			return String.format("%d hrs  %d min", hours, minutes);
		} else {
			return "0 hrs 0 min";
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ListModel listModel(List items) {
		return new ListModelList(items);
	}
	
	public static String getFormattedDate(Date date) {
		if(date != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			return dateFormat.format(date);
		}
		return "";
	}
	
	public static String getFormattedDateTime(Date date) {
		if(date != null) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			return dateFormat.format(date);
		}
		return "";
	}
	
	public static Image getProductImage(Product product) {
		Image img = null;
		try {
			img = new AImage("", product.getImageData());
		} catch (IOException exception) {}
		if(img != null) {
			return img;
		}
		return img;
	}

}
