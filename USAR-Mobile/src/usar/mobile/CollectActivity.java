package usar.mobile;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class CollectActivity extends Activity {
	private CameraView preview;
	private Button accept;
	private Button release;
	private Button takePhoto;
	private LinearLayout approvePanel;
	private byte[] lastData;
	private ArrayList<byte[]> images;
	private ContentStorageHelper mDbHelper;
    private LocationHelper lh;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mDbHelper = new ContentStorageHelper(this);
        mDbHelper.open();
		images = new ArrayList<byte[]>();
		loadPictureTaker();
	}
	private void loadPictureTaker() { 
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
				images.add(lastData);
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
	
	private void save(byte[] data, Location location, String dangerLevel) {
	    DataTransferUtil.uploadData(location);
	    String latitude, longitude;
	    if (location != null) {
	    	latitude = Double.toString(location.getLatitude());
	    	longitude = Double.toString(location.getLongitude());
	    } else {
	    	latitude = "0";
	    	longitude = "0";
	    }
		
		mDbHelper.createRescueImageEntry(data, latitude, longitude, dangerLevel);
	}
	
	public void loadViewPlace(final Location location) {
		setContentView(R.layout.viewplace);
		
		ImageButton picview = new ImageButton(this);
		picview.setImageResource(R.drawable.addimg);
		picview.setMaxHeight(100);
		picview.setMaxWidth(100);
		picview.setScaleType(ScaleType.FIT_XY);
		picview.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				loadPictureTaker();
			}
		});
		
		Gallery imgPanel = (Gallery) findViewById(R.id.imgPanel);
		imgPanel.setAdapter(new ImageAdapter(this, images, picview));
	
		// Danger level
		final Spinner dangerLevel = (Spinner) findViewById(R.id.dangerLevel);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.dangerLevels, android.R.layout.simple_spinner_item);
			/*@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewInflate inflater=CollectActivity.this.getViewInflate();
				View row=inflater.inflate(R.layout.row, null, null);
				TextView label=(TextView)row.findViewById(R.id.label);

				label.setText(items[position]);

				if (items[position].length()>4) {
				ImageView icon=(ImageView)row.findViewById(R.id.icon);

				icon.setImageResource(R.drawable.delete);
			}*/
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    dangerLevel.setAdapter(adapter);
	    
		TextView gps = (TextView) findViewById(R.id.gps);
		if (location != null) {
		  String longLat = location.getLongitude() + "," + location.getLatitude();
		  gps.setText(longLat);
		} else {
			gps.setText("Unknown");
		}
		
		Button submit = (Button) findViewById(R.id.submit);
		Button delete = (Button) findViewById(R.id.delete);
		submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				save(location, (String) dangerLevel.getSelectedItem());
				clear();
			}
		});
		delete.setOnClickListener(new OnClickListener() {	
			public void onClick(View v) {
				new AlertDialog.Builder(CollectActivity.this)
		        .setTitle("Delete location images")
		        .setMessage("Are you sure you want to delete these images?")
		        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						clear();
					}
		        }).setNegativeButton("No", null)
		        .show();
			}
		});
		
	}
	
	private void save(Location location, String dangerLevel) {
		for (byte[] image : images) {
			save(image, location, dangerLevel);
		}
	}
	
	private void clear() {
		images = new ArrayList<byte[]>();
		CollectActivity.this.loadPictureTaker();
	}
}
