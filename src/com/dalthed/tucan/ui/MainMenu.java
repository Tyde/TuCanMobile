package com.dalthed.tucan.ui;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dalthed.tucan.R;

public class MainMenu extends ListActivity  {
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        
        showMenuElements();
    }
    private void showMenuElements() {
    	
    	final ArrayAdapter<String> ElementAdapter = 
    		new ArrayAdapter<String>(this,
    				android.R.layout.simple_list_item_1
    				, getResources().getStringArray(R.array.mainmenu_options));
    	setListAdapter(ElementAdapter);    	
    	
    }
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		final Toast hinweis = Toast.makeText(this, "Hallo, du hast "+position+" geklickt.", Toast.LENGTH_LONG);
		hinweis.show();
		switch (position) {
		case 0:
			Intent StartVVIntent = new Intent(this, VV.class);
			startActivity(StartVVIntent);
			//Vorlesungsverzeichnis
			break;
		case 1:
			//Stundenplan
			break;
		case 2:
			//Veranstaltungen
			break;
		case 3: 
			//Prüfungen
			break;
		
		}
	}
    
}
