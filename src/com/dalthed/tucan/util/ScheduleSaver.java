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

package com.dalthed.tucan.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Build;

import com.dalthed.tucan.TucanMobile;
import com.dalthed.tucan.datamodel.Appointment;
import com.dalthed.tucan.widget.WidgetProvider;

/**
 * Zwischenspeicherung von Stundenplan auf internen Speicher (z.B. fuer Widget)
 * @author Tim Kranz
 *
 */
public class ScheduleSaver {
	public final static String SCHEDULE_FILE = "schedule";
	
	private static Context context = TucanMobile.getAppContext();
	
	public static void saveSchedule(List<Appointment> appointments){

		try{
			File file = new File(context.getFilesDir(), SCHEDULE_FILE);
			if(!file.exists())
				file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(appointments);
			oos.close();
			// new data available => update widget
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				WidgetProvider.updateWidgets(context);			
		}catch(Exception e){
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Appointment> loadSchedule(){
		try{
			File file = new File(context.getFilesDir(), SCHEDULE_FILE);
			if(file.exists()){
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				Object o = ois.readObject();
				ois.close();
				if(o instanceof ArrayList<?>)
					return (ArrayList<Appointment>) o;
				
			}
		}catch(Exception e){
			
		}
		return new ArrayList<Appointment>();
	}
}
