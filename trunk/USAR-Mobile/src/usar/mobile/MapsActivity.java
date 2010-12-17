package usar.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.maps.MapActivity;

public class MapsActivity extends MapActivity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapstab);
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
