package usar.mobile;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class CollectActivity extends Activity {
	private CameraView preview;
	private Button accept;
	private Button release;
	private Button takePhoto;
	private LinearLayout approvePanel;
	private byte[] lastData;

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
				save(lastData);
			}
		});
		release = (Button) findViewById(R.id.releasePhotoButton);
		release.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				lastData = null;
				switchToTakePhoto();
			}
		});
	}
	
	private void save(byte[] data) {
		//TODO
	}
}
