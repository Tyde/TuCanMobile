package com.dalthed.tucan.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dalthed.tucan.R;
import com.dalthed.tucan.ui.RegisterExams;
import com.dalthed.tucan.ui.SimpleWebListActivity;

/**
 * {@link ListAdapter}, welcher zum darstellen von Pr�fungsanmeldungen sinnvoll
 * ist. Der Adapter nutzt registerexam_row_date.xml.<br>
 * <br>
 * Der Adapter ist f�hig, eine �berschrift darzustellen oder Content:<br>
 * <br>
 * Schema �berschrift:
 * <table>
 * <tr>
 * <td colspan=4>***************************************************</td>
 * </tr>
 * <tr>
 * <td>*</td>
 * <td>�BERSCHRIFT</td>
 * <td align=right></td>
 * <td>*</td>
 * </tr>
 * <tr>
 * <td colspan=4>***************************************************</td>
 * </tr>
 * </table>
 * <br>
 * <br>
 * Schema Content:
 * <table>
 * <tr>
 * <td colspan=4>***************************************************</td>
 * </tr>
 * <tr>
 * <td>*</td>
 * <td>Datum</td>
 * <td align=right>Status</td>
 * <td>*</td>
 * </tr>
 * <tr>
 * <td>*</td>
 * <td>Name der Pr�fung</td>
 * <td align=right></td>
 * <td>*</td>
 * </tr>
 * 
 * <tr>
 * <td colspan=4>***************************************************</td>
 * </tr>
 * </table>
 * 
 * @author Daniel Thiem
 * 
 */
public class RegisterExamAdapter extends ArrayAdapter<String> {
	ArrayList<Boolean> eventisModule;
	ArrayList<String> eventName;
	ArrayList<Integer> examSelection;
	private Context context;

	/**
	 * {@link ListAdapter}, welcher zum darstellen von Pr�fungsanmeldungen
	 * sinnvoll ist. Der Adapter nutzt registerexam_row_date.xml.<br>
	 * <br>
	 * Der Adapter ist f�hig, eine �berschrift darzustellen oder Content:<br>
	 * <br>
	 * Schema �berschrift (eventisModule == true):
	 * <table>
	 * <tr>
	 * <td colspan=4>***************************************************</td>
	 * </tr>
	 * <tr>
	 * <td>*</td>
	 * <td>eventName</td>
	 * <td align=right></td>
	 * <td>*</td>
	 * </tr>
	 * <tr>
	 * <td colspan=4>***************************************************</td>
	 * </tr>
	 * </table>
	 * <br>
	 * <br>
	 * Schema Content (eventisModule == false):
	 * <table>
	 * <tr>
	 * <td colspan=4>***************************************************</td>
	 * </tr>
	 * <tr>
	 * <td>*</td>
	 * <td>examDate</td>
	 * <td align=right>examSelection</td>
	 * <td>*</td>
	 * </tr>
	 * <tr>
	 * <td>*</td>
	 * <td>eventName</td>
	 * <td align=right></td>
	 * <td>*</td>
	 * </tr>
	 * 
	 * <tr>
	 * <td colspan=4>***************************************************</td>
	 * </tr>
	 * </table>
	 * 
	 * @param context
	 *            {@link Activity} Context
	 * @param eventisModule
	 *            siehe Tabelle
	 * @param eventName
	 *            siehe Tabelle
	 * @param examDate
	 *            siehe Tabelle
	 * @param examSelection
	 *            siehe Tabelle
	 * @author Daniel Thiem
	 */
	public RegisterExamAdapter(Context context, ArrayList<Boolean> eventisModule,
			ArrayList<String> eventName, ArrayList<String> examDate,
			ArrayList<Integer> examSelection) {

		super(context, R.layout.registerexam_row, R.id.registerexam_row_date, examDate);
		this.context = context;
		this.eventisModule = eventisModule;
		this.eventName = eventName;
		this.examSelection = examSelection;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);
		LinearLayout isModuleLayout = (LinearLayout) row
				.findViewById(R.id.registerexam_row_ismodule);
		LinearLayout isEventLayout = (LinearLayout) row.findViewById(R.id.registerexam_row_isevent);

		Log.i(SimpleWebListActivity.LOG_TAG, "Calling " + position);

		// Unterscheiden, ob �berschrift oder Content angezeigt wird
		if (eventisModule.get(position)) {
			// �berschrift Frame anzeigen
			isModuleLayout.setVisibility(View.VISIBLE);
			isEventLayout.setVisibility(View.GONE);
			TextView ModuleNameView = (TextView) row
					.findViewById(R.id.registerexam_row_module_name);

			ModuleNameView.setText(eventName.get(position));
		} else {
			// Content Frame anzeigen
			isModuleLayout.setVisibility(View.GONE);
			isEventLayout.setVisibility(View.VISIBLE);
			TextView eventNameView = (TextView) row.findViewById(R.id.registerexam_row_examName);
			TextView eventSelectionView = (TextView) row
					.findViewById(R.id.registerexam_row_selection);
			eventNameView.setText(eventName.get(position));
			String selectionstring = "";

			// Auswahlstatus in Text umwandeln und Farben w�hlen
			String[] selectionstrings = context.getResources().getStringArray(
					R.array.register_status);
			selectionstring = selectionstrings[position];
			switch (examSelection.get(position)) {
			case 0:
				eventSelectionView.setTextColor(Color.BLACK);

				break;
			case 1:
				eventSelectionView.setTextColor(Color.BLACK);

				break;
			case 2:
				eventSelectionView.setTextColor(context.getResources()
						.getColor(R.color.tucan_green));

				break;
			case 3:
				eventSelectionView.setTextColor(context.getResources().getColor(
						R.color.register_deregister_red));

				break;
			default:
				break;

			}

			eventSelectionView.setText(selectionstring);
		}

		return row;
	}

}
