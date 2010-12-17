package usar.mobile;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LocationsActivity extends MapActivity {
	private MapView mapView;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationstab);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
