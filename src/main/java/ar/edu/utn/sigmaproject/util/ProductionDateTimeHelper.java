/*
 * The MIT License
 *
 * Copyright (C) 2017 SigmaProject.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
		if(cal.get(Calendar.HOUR_OF_DAY) < firstHourOfDay || cal.get(Calendar.HOUR_OF_DAY) >= lastHourOfDay) {
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
			int seconds = time.getSeconds();
			while(seconds >= 60) {
				seconds -= 60;
				minutes += 1;
			}
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
			// agrega los segundos
			if(seconds > 0) {
				cal.add(Calendar.SECOND, seconds);
				// no se considera  cambiar el dia si los segundo superan el horario de finalizacion
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

	public static boolean isOutsideWorkingHours(Date startDate) {
		// devuelve true si la fecha se encuentra fuera de las horas de trabajo
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		// primero se comprueba que el startDate se encuentre dentro del horario
		if(startCalendar.get(Calendar.HOUR_OF_DAY) < firstHourOfDay ||
				startCalendar.get(Calendar.HOUR_OF_DAY) >= lastHourOfDay) {
			return true;
		} else {// si esta dentro del horario en horas pero no en minutos
			if(firstMinuteOfDay!=0 || lastMinuteOfDay!=0) {//si estan configurados minutos en el horario
				//solo es valido si la hora es igual a la de comienzo o igual al previo al final
				if(startCalendar.get(Calendar.HOUR_OF_DAY) == firstHourOfDay) {
					if(startCalendar.get(Calendar.MINUTE) < firstMinuteOfDay) {
						return true;
					}
				}
				if(startCalendar.get(Calendar.HOUR_OF_DAY) == lastHourOfDay-1) {
					if(startCalendar.get(Calendar.MINUTE) >= lastMinuteOfDay) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static String getFormattedFirst() {
		if(firstMinuteOfDay == 0) {
			return firstHourOfDay + "";
		}
		return firstHourOfDay + " : " + firstMinuteOfDay;
	}

	public static String getFormattedLast() {
		if(lastMinuteOfDay == 0) {
			return lastHourOfDay + "";
		}
		return lastHourOfDay + " : " + lastMinuteOfDay;
	}

	public static Date getFirstHourOfDay(Date dateFinish) {
		// devuelve el date en la misma fecha pero con la hora de inicio del dia de trabajo
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateFinish);
		cal.set(Calendar.HOUR_OF_DAY, firstHourOfDay);
		cal.set(Calendar.MINUTE, firstMinuteOfDay);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getLastHourOfDay(Date dateStart) {
		// devuelve el date en la misma fecha pero con la hora de fin del dia del trabajo
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateStart);
		cal.set(Calendar.HOUR_OF_DAY, lastHourOfDay);
		cal.set(Calendar.MINUTE, lastMinuteOfDay);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	public static Date addDays(int number, Date dateStart) {
		// devuelve el date sumado la cantidad de dias en number
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateStart);
		cal.add(Calendar.DAY_OF_MONTH, number);
		return cal.getTime();
	}
}