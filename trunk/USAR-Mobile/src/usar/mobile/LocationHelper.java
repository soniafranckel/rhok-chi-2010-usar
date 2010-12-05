package usar.mobile;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

public class LocationHelper implements LocationListener {

    static final String tag = "LocationHelper"; // for Log
    private final LocationManager lm;

    /** 
     * This class implements LocationListener, which listens for both
     * changes in the location of the device and changes in the status
     * of the GPS system.
     */
    public LocationHelper(LocationManager locationManager) {
        /* The location manager is the most vital part it allows access
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

    @Override
    public void onLocationChanged(Location location) {
        Log.v(tag, "Location Changed");
    }

    @Override
    public void onProviderDisabled(String provider) {
        /* This is called if/when the GPS is disabled in settings */
        Log.v(tag, "Disabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        /* This is called if/when the GPS is enabled in settings */
        Log.v(tag, "Enabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        /* This is called when the GPS status alters */
        switch (status) {
        case LocationProvider.OUT_OF_SERVICE:
            Log.v(tag, "Status Changed: Out of Service");
            break;
        case LocationProvider.TEMPORARILY_UNAVAILABLE:
            Log.v(tag, "Status Changed: Temporarily Unavailable");
            break;
        case LocationProvider.AVAILABLE:
            Log.v(tag, "Status Changed: Available");
            break;
        }
    }
}
