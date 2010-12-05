package usar.mobile;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class LocationHelper implements LocationListener {

	/* this class implements LocationListener, which listens for both
	 * changes in the location of the device and changes in the status
	 * of the GPS system.
	 * */

	static final String tag = "LocationHelper"; // for Log
	private final LocationManager lm;

	public LocationHelper(LocationManager locationManager) {
		/* the location manager is the most vital part it allows access
		 * to location and GPS status services */
		this.lm = locationManager;
	}

	public void requestLocationUpdates() {
		/* Add location listener and request updates every 1000ms or 10m */
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, this);
	}

	public void stopLocationUpdates() {
		/* GPS, as it turns out, consumes battery like crazy */
		lm.removeUpdates(this);
	}

	public Location getLastKnownLocation() {
		return lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	public void onLocationChanged(Location location) {
		Log.v(tag, "Location Changed");		
	}

	public void onProviderDisabled(String provider) {
		/* this is called if/when the GPS is disabled in settings */
		Log.v(tag, "Disabled");

		/* bring up the GPS settings */
		Intent intent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		// callback to startActivity(intent); in activity

	}

	public void onProviderEnabled(String provider) {
		Log.v(tag, "Enabled");
		// this needs to be an activity Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		/* This is called when the GPS status alters */
		switch (status) {
		case LocationProvider.OUT_OF_SERVICE:
			Log.v(tag, "Status Changed: Out of Service");
			// this needs to be an activity Toast.makeText(this, "Status Changed: Out of Service",
			//        Toast.LENGTH_SHORT).show();
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			Log.v(tag, "Status Changed: Temporarily Unavailable");
			// this needs to be an activity Toast.makeText(this, "Status Changed: Temporarily Unavailable",
			//          Toast.LENGTH_SHORT).show();
			break;
		case LocationProvider.AVAILABLE:
			Log.v(tag, "Status Changed: Available");
			// this needs to be an activity Toast.makeText(this, "Status Changed: Available",
			//        Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
