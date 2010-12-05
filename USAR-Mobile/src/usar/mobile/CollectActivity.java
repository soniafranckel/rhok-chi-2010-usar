package usar.mobile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class CollectActivity extends Activity {
	private CameraView preview;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the window title.
		//          requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Create our Preview view and set it as the content of our activity.
		//        mPreview = new CameraView(this);
		//        setContentView(mPreview);
		//          Camera camera = Camera.open();
		//           camera.startPreview();
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
				FileOutputStream outStream = null;
				try {
					// write to local sandbox file system
					// outStream =
					// CameraDemo.this.openFileOutput(String.format("%d.jpg",
					// System.currentTimeMillis()), 0);
					// Or write to sdcard
					outStream = new FileOutputStream(String.format(
							"/sdcard/%d.jpg", System.currentTimeMillis()));
					outStream.write(data);
					outStream.close();
					Log.e("CollectActivity", "onPictureTaken - wrote bytes: " + data.length);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
				}
			}
		};
		Button buttonClick = (Button) findViewById(R.id.buttonClick);
		buttonClick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				preview.camera.takePicture(shutterCallback, rawCallback,
						jpegCallback);
			}
		});

	}
}
