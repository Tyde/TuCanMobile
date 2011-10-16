package com.dalthed.tucan;


import java.net.URL;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;




public class TuCanMobileActivity extends Activity {
    /** Called when the activity is first created. */
    //private HTTPSbrowser mBrowserService;
   
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    public void onClickSendLogin (final View sfNormal) {
    	HTTPSBrowser newBrowser = new HTTPSBrowser();
    	
    	TextView answertextv = (TextView) findViewById(R.id.textView2);
    	try {
			newBrowser.execute(
					new URL[] {new URL("https://www.tucan.tu-darmstadt.de/scripts/mgrqcgi?APPNAME=CampusNet&PRGNAME=EXTERNALPAGES&ARGUMENTS=-N000000000000001")}
			);
			Toast notifyall = Toast.makeText(TucanMobile.getAppContext(), newBrowser.get(), Toast.LENGTH_SHORT);
			notifyall.show();
			answertextv.setText(newBrowser.get());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast notifyall = Toast.makeText(TucanMobile.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT);
			notifyall.show();
		}
    	
		
		
    	/*final Intent i = new Intent(this,MainMenu.class);
    	startActivity(i);*/
    	
    }
}