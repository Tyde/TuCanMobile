/**
 *	This file is part of TuCan Mobile.
 *
 *	TuCan Mobile is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	TuCan Mobile is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with TuCan Mobile.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dalthed.tucan.datamodel;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * This class represents an appointment of the schedule
 * @author Tim Kranz
 */
public class Appointment implements Serializable{
	
	private static final long serialVersionUID = 5880842251379351647L;
	
	private GregorianCalendar fromDate;
	private GregorianCalendar toDate;
	private String room = "";
	private String name;
	private boolean firstofDay = false;
	
	public Appointment(int year, int month, int day, int fromHour, int fromMinute, int toHour, int toMinute, String name, String room){
		this.fromDate = new GregorianCalendar(year, month, day, fromHour, fromMinute);
		this.toDate = new GregorianCalendar(year, month, day, toHour, toMinute);
		this.name = name;
		this.room = room;
	}
	
	public void setFirstDay(boolean isFirstDay){
		this.firstofDay = isFirstDay;
	}
	
	public boolean isFirstDay(){
		return firstofDay;
	}
	
	public GregorianCalendar getDate() {
		return fromDate;
	}
	
	public String getTimeInterval(){
		return fromDate.get(GregorianCalendar.HOUR_OF_DAY)+":"+(fromDate.get(GregorianCalendar.MINUTE) <= 9 ? "0" : "")+fromDate.get(GregorianCalendar.MINUTE)+" - "+toDate.get(GregorianCalendar.HOUR_OF_DAY)+":"+(toDate.get(GregorianCalendar.MINUTE) <= 9 ? "0" : "")+toDate.get(GregorianCalendar.MINUTE);
	}
	
	public String getRoom() {
		return room;
	}
	public String getName() {
		return name;
	}
	
	private boolean sameDay(GregorianCalendar d1, GregorianCalendar d2){
		return d1.get(GregorianCalendar.DAY_OF_YEAR) == d2.get(GregorianCalendar.DAY_OF_YEAR) && d1.get(GregorianCalendar.YEAR) == d2.get(GregorianCalendar.YEAR);
	}
	
	public String getDateDescr(){
		StringBuilder displayDate = new StringBuilder();
		
		GregorianCalendar today = (GregorianCalendar) GregorianCalendar.getInstance();
		GregorianCalendar tomorrow = (GregorianCalendar) GregorianCalendar.getInstance();
		tomorrow.add(GregorianCalendar.HOUR, 24);

		if(sameDay(today, fromDate)){
			displayDate.append("Heute");//context.getResources().getString(R.string.schedule_today)
			
		} else if (sameDay(tomorrow, fromDate)) {
			displayDate.append("Morgen");//context.getResources().getString(R.string.schedule_tomorrow)
			
		} else { // keep in mind: month is from 0 to 11.
			displayDate.append(fromDate.get(GregorianCalendar.DAY_OF_MONTH)).append(".").append((fromDate.get(GregorianCalendar.MONTH)+1));
			
		}
			
		//String weekday = fromDate.getDisplayName(GregorianCalendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()); // API >= 9
		String weekday = (String) android.text.format.DateFormat.format("EEEE", fromDate.getTimeInMillis());
		displayDate.append(" - ").append(weekday);
		return displayDate.toString();
	}
	
	
}
