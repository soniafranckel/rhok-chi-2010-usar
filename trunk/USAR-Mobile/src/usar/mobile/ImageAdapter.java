package usar.mobile;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ImageButton addImg;

	private ArrayList<byte[]> images;

    public ImageAdapter(Context c, ArrayList<byte[]> images, ImageButton addImg) {
       context = c;
       this.addImg = addImg;
     //   TypedArray a = obtainStyledAttributes(R.styleable.HelloGallery);
     //   mGalleryItemBackground = a.getResourceId(
      //          R.styleable.HelloGallery_android_galleryItemBackground, 0);
     //   a.recycle();
        this.images = images;
    }

    public int getCount() {
        return images.size() + 1;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	if (position >= images.size()) {
    		return addImg;
    	} else {
    		ImageView img = new ImageView(context);
    		byte[] data = images.get(position);
    		Bitmap bmp=BitmapFactory.decodeByteArray(data, 0, data.length);
    		img.setImageBitmap(Bitmap.createScaledBitmap(bmp, 100, 100, false));
    		img.setScaleType(ImageView.ScaleType.FIT_XY);

    		return img; 
        }
    }
}