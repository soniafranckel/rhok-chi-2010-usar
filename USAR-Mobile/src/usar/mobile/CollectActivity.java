package usar.mobile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CollectActivity extends Activity {
	private CameraView preview;
	private Button accept;
	private Button release;
	private Button takePhoto;
	private LinearLayout approvePanel;
	private byte[] lastData;
	
    private LocationHelper lh;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.collecttab);

		preview = new CameraView(this);
		((FrameLayout) findViewById(R.id.preview)).addView(preview);

		final ShutterCallback shutterCallback = new ShutterCallback() {
			public void onShutter() {
			}
		};

		/** Handles data for raw picture */
		final PictureCallback rawCallback = new PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
			}
		};

		/** Handles data for jpeg picture */
		final PictureCallback jpegCallback = new PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				lastData = data;
				switchToAccept();
			}
		};
		takePhoto = (Button) findViewById(R.id.takePhotoButton);
		takePhoto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				preview.camera.takePicture(shutterCallback, rawCallback,
						jpegCallback);
			}
		});
		approvePanel = (LinearLayout) findViewById(R.id.approvePanel);
		createAcceptPanel();
		approvePanel.setVisibility(View.INVISIBLE);

        /* the location manager is the most vital part it allows access
         * to location and GPS status services */
        lh = new LocationHelper((LocationManager) getSystemService(LOCATION_SERVICE));
	}    
	
	@Override
    protected void onResume() {
        /*
         * onResume is is always called after onStart, even if the app hasn't been
         * paused
         *
         * add location listener and request updates every 1000ms or 10m
         */
        lh.requestLocationUpdates();
        super.onResume();
    }
	
    @Override
    protected void onPause() {
        /* GPS, as it turns out, consumes battery like crazy */
        lh.stopLocationUpdates();
        super.onResume();
    }
	
	private void switchToAccept() {
		takePhoto.setVisibility(View.INVISIBLE);
		approvePanel.setVisibility(View.VISIBLE);
	}
	private void switchToTakePhoto() {
		takePhoto.setVisibility(View.VISIBLE);
		approvePanel.setVisibility(View.INVISIBLE);
		preview.camera.startPreview();
	}
	
	private void createAcceptPanel() {
		accept = (Button) findViewById(R.id.acceptPhotoButton);
		accept.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		        // Location can be null
			    // See LocationHelper for how to emulate a location
			    Location location = lh.getLastKnownLocation();
				save(lastData, location);
				loadViewPlace(location);
			}
		});
		release = (Button) findViewById(R.id.releasePhotoButton);
		release.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switchToTakePhoto();
			}
		});
	}
	
	private void save(byte[] data, Location location) {
	    DataTransferUtil.uploadData(location);
	}
	
	public void loadViewPlace(Location location) {
		setContentView(R.layout.viewplace);
		
		LinearLayout imgPanel = (LinearLayout) findViewById(R.id.imgPanel);
		//ImageView picview = (ImageView) findViewById(R.id.picview);
		// Add click handler
		//TODO: Iterate through all images and add them
		ImageView img = new ImageView(this);
		Bitmap bmp=BitmapFactory.decodeByteArray(lastData, 0, lastData.length);
		img.setImageBitmap(Bitmap.createScaledBitmap(bmp, 100, 100, false));		
		imgPanel.addView(img);
		TextView gps = (TextView) findViewById(R.id.gps);
		if (location != null) {
		  String longLat = location.getLongitude() + "," + location.getLatitude();
		  gps.setText(longLat);
		}
	}
}
