package ar.edu.utn.sigmaproject.util;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

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

}
