package ar.edu.utn.sigmaproject.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.Duration;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import ar.edu.utn.sigmaproject.domain.ProcessState;
import ar.edu.utn.sigmaproject.domain.ProductionOrderDetail;

public class ProductionOrderReportDataSource implements JRDataSource {

	private List<ProductionOrderDetail> productionOrderDetailList = new ArrayList<ProductionOrderDetail>();
	private int index = -1;

	public ProductionOrderReportDataSource(List<ProductionOrderDetail> productionOrderDetailList) {
		// agrega todos menos los detalles cancelados
		for(ProductionOrderDetail each : productionOrderDetailList) {
			if(each.getState() != ProcessState.Cancelado) {
				this.productionOrderDetailList.add(each);
			}
		}
	}

	public boolean next() throws JRException {
		index++;
		return (index < productionOrderDetailList.size());
	}

	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;
		String fieldName = field.getName();
		if ("process_name".equals(fieldName)) {
			value = productionOrderDetailList.get(index).getProcess().getType().getName();
		} else if ("machine_name".equals(fieldName)) {
			if(productionOrderDetailList.get(index).getProcess().getType().getMachineType() != null) {
				if(productionOrderDetailList.get(index).getMachine() != null) {
					value = productionOrderDetailList.get(index).getMachine().getName();
				} else {
					value = "No seleccionado";
				}
			} else {
				value = "No requiere";
			}
		} else if ("worker_name".equals(fieldName)) {
			if(productionOrderDetailList.get(index).getWorker() != null) {
				value = productionOrderDetailList.get(index).getWorker().getName();
			} else {
				value = "No seleccionado";
			}
		} else if ("date_start".equals(fieldName)) {
			Date date = productionOrderDetailList.get(index).getDateStart();
			if(date != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				value = dateFormat.format(date);
			} else {
				value = "No seleccionado";
			}
		} else if ("date_finish".equals(fieldName)) {
			Date date = productionOrderDetailList.get(index).getDateFinish();
			if(date != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				value = dateFormat.format(date);
			} else {
				value = "No seleccionado";
			}
		} else if ("duration_total".equals(fieldName)) {
			Duration time = productionOrderDetailList.get(index).getTimeTotal();
			if(time != null) {
				int hours = time.getHours();
				int minutes = time.getMinutes();
				int seconds = time.getSeconds();
				while(seconds >= 60) {
					seconds -= 60;
					minutes += 1;
				}
				while(minutes >= 60) {
					hours = hours + 1;
					minutes = minutes - 60;
				}
				value = String.format("%d hrs  %d min  %d seg", hours, minutes);
			} else {
				value = "0 hrs 0 min 0 seg";
			}
		} else if ("piece_name".equals(fieldName)) {
			value = productionOrderDetailList.get(index).getProcess().getPiece().getName();
		} else if ("piece_quantity".equals(fieldName)) {
			value = productionOrderDetailList.get(index).getQuantityPiece();
		}
		return value;
	}
}
