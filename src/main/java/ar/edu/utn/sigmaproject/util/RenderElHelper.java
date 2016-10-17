package ar.edu.utn.sigmaproject.util;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import ar.edu.utn.sigmaproject.domain.RawMaterialType;

import javax.xml.datatype.Duration;

import java.util.List;

public class RenderElHelper {

	public static String getFormattedTime(Duration time) {
		int hours = time.getHours();
		int minutes = time.getMinutes();
		while(minutes >= 60) {
			minutes -= 60;
			hours += 1;
		}
		return String.format("%d Horas, %d Minutos", hours, minutes);
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

	public static String getFormattedMeasure(RawMaterialType rawMaterialType) {
		String lenght = "(L) " + rawMaterialType.getLength().doubleValue() + " " + rawMaterialType.getLengthMeasureUnit().getShortName();
		String depth = "(E) " + rawMaterialType.getDepth().doubleValue() + " " + rawMaterialType.getDepthMeasureUnit().getShortName();
		String width = "(A) " + rawMaterialType.getWidth().doubleValue() + " " + rawMaterialType.getWidthMeasureUnit().getShortName();
		return lenght + " x " + depth + " x " + width;
	}

}
