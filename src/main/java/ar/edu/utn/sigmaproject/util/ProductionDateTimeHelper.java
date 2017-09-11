package ar.edu.utn.sigmaproject.util;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.Duration;

public class ProductionDateTimeHelper {
	private static int firstHourOfDay = 9;// horario en el que se empieza a trabajar
	private static int firstMinuteOfDay = 0;
	private static int lastHourOfDay = 18;// horario en el que se termina de trabajar
	private static int lastMinuteOfDay = 0;
	
	public static Date getFinishDate(Date startDate, Duration time) {
		if(startDate == null) {
			return null;
		}
		//TODO: tener en cuenta fines de semana y feriados
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		// primero se comprueba que la fecha de inicio se encuentre dentro del horario
		if(cal.get(Calendar.HOUR_OF_DAY) < firstHourOfDay ||
				cal.get(Calendar.HOUR_OF_DAY) >= lastHourOfDay) {
			return null;
		} else {// si esta dentro del horario en horas pero no en minutos
			if(firstMinuteOfDay!=0 || lastMinuteOfDay!=0) {//si estan configurados minutos en el horario
				//solo es valido si la hora es igual a la de comienzo o igual al previo al final
				if(cal.get(Calendar.HOUR_OF_DAY) == firstHourOfDay) {
					if(cal.get(Calendar.MINUTE) < firstMinuteOfDay) {
						return null;
					}
				}
				if(cal.get(Calendar.HOUR_OF_DAY) == lastHourOfDay-1) {
					if(cal.get(Calendar.MINUTE) >= lastMinuteOfDay) {
						return null;
					}
				}
			}
		}
		if(time != null) {
			int hours = time.getHours();
			int minutes = time.getMinutes();// puede ser que los minutos sean mayor que 60, ya que se realizaron multiplicaciones sobre time
			while(minutes >= 60) {// se vuelven los minutos a menos de 60
				minutes -= 60;
				hours += 1;
			}
			while(hours > 0) {
				hours -= 1;
				cal.add(Calendar.HOUR_OF_DAY, 1);// agrega horas
				// si luego de agregar 1 hora, el tiempo supera el horario de finalizacion
				// se resta hasta el horario de finalizacion y ese tiempo se suma al dia siguiente
				if(cal.get(Calendar.HOUR_OF_DAY) >= lastHourOfDay) {// caso supere el horario de finalizacion
					if(cal.get(Calendar.MINUTE) > 0) {
						// se agregan los minutos que superan al horario de finalizacion
						minutes += cal.get(Calendar.MINUTE);
						while(minutes >= 60) {
							minutes -= 60;
							hours += 1;
						}
					}
					// agrega 1 dia y resetea el horario
					cal.add(Calendar.DAY_OF_MONTH, 1);
					cal.set(Calendar.HOUR_OF_DAY, firstHourOfDay);
					cal.set(Calendar.MINUTE, firstMinuteOfDay);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
				}
			}
			// agrega los minutos
			if(minutes > 0) {
				cal.add(Calendar.MINUTE, minutes);
				// puede ser que al agregar los minutos se supere el horario de finalizacion
				if(cal.get(Calendar.HOUR_OF_DAY) >= lastHourOfDay) {// caso supere el horario de finalizacion
					cal.add(Calendar.DAY_OF_MONTH, 1);// agrega 1 dia
					cal.set(Calendar.HOUR_OF_DAY, firstHourOfDay);
					cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));// los minutos que superaron el horario de finalizacion se agregan al horario de inicio
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);

				}
			}
			return cal.getTime();
		}
		return startDate;// si el tiempo es null se devuelve la misma fecha de inicio
	}

	public static int getFirstHourOfDay() {
		return firstHourOfDay;
	}

	public static void setFirstHourOfDay(int firstHourOfDay) {
		ProductionDateTimeHelper.firstHourOfDay = firstHourOfDay;
	}

	public static int getFirstMinuteOfDay() {
		return firstMinuteOfDay;
	}

	public static void setFirstMinuteOfDay(int firstMinuteOfDay) {
		ProductionDateTimeHelper.firstMinuteOfDay = firstMinuteOfDay;
	}

	public static int getLastHourOfDay() {
		return lastHourOfDay;
	}

	public static void setLastHourOfDay(int lastHourOfDay) {
		ProductionDateTimeHelper.lastHourOfDay = lastHourOfDay;
	}

	public static int getLastMinuteOfDay() {
		return lastMinuteOfDay;
	}

	public static void setLastMinuteOfDay(int lastMinuteOfDay) {
		ProductionDateTimeHelper.lastMinuteOfDay = lastMinuteOfDay;
	}
}
