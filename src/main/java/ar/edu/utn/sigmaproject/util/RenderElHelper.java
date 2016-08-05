package ar.edu.utn.sigmaproject.util;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import ar.edu.utn.sigmaproject.domain.RawMaterialType;

import javax.xml.datatype.Duration;

import java.util.List;

public class RenderElHelper {

	public static String getFormattedTime(Duration time) {
		return String.format("Dias: %d Horas: %d Minutos: %d", time.getDays(), time.getHours(), time.getMinutes());
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
