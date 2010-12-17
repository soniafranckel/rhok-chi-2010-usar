package usar.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MapsActivity extends MapActivity {
	private MapView mapView;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapstab);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        
        Button button = (Button) findViewById(R.id.saveMap);
        button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				saveMap();
			}
		});
    }
    private void saveMap() {
    	//TODO(sonnyf)
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
