package usar.mobile;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TextView;

public class RhokMobile extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, CollectActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("Collect").setIndicator("Collect").setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, LocationsActivity.class);
        spec = tabHost.newTabSpec("locations").setIndicator("Locations").setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, MapsActivity.class);
        spec = tabHost.newTabSpec("maps").setIndicator("Maps").setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }
}