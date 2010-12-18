package usar.mobile;

import java.util.List;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class LocationsActivity extends MapActivity {
	private MapView mapView;
	private List<Overlay> mapOverlays;
	private Drawable drawable;
	private ContentStorageHelper mDbHelper;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationstab);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        
        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        load();
    }

    private void load() {
    	mDbHelper = new ContentStorageHelper(this);
    	mDbHelper.open();
    	Cursor cursor = mDbHelper.getRescueImageEntries();
    	if (cursor.moveToFirst()) {
    		addPoint(cursor.getInt(0), cursor.getInt(1), "photo taken", "alert level: " + cursor.getString(2));
    		while(cursor.moveToNext()) {
    			addPoint(cursor.getInt(0), cursor.getInt(1), "photo taken", "alert level: " + cursor.getString(2));
    		}
    	}
    }

	private void addPoint(int latitude, int longitude, String title, String snippet) {
    	VisitedLocationsOverlay itemizedoverlay = new VisitedLocationsOverlay(drawable, this);
        
    	GeoPoint point = new GeoPoint(latitude,longitude);
        OverlayItem overlayitem = new OverlayItem(point, title, snippet);
        itemizedoverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedoverlay);
    }
    

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
