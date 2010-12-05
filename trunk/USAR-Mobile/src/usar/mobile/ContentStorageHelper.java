package usar.mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ContentStorageHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "data";
	private static final String RESCUE_TABLE_NAME = "rescueT";
	private static final String TABLE2 = "create table rescueT "
		+ "(_id integer primary key autoincrement, "
		+ "longitude text not null, "
		+ "latitude text not null, "
		+ "photo text not null, "
		+ "alertlevel text not null);";

	private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private final Context mCtx;
	
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(TABLE2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Rescue Data", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS rescueT");
            onCreate(db);
        }
    }
    
	public ContentStorageHelper(Context context) {
		this.mCtx = context;
	}

	public ContentStorageHelper open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
	
	public void close() {
        mDbHelper.close();
    }
	
	 public long createRescueImageEntry(byte[] imagedata, String Latval, String Longval, String alertlev) {
	 	ContentValues initialValues = new ContentValues();
	 	initialValues.put("longitude", Longval);
	 	initialValues.put("latitude", Latval);
	 	initialValues.put("photo","placeholder");
	 	initialValues.put("alertlevel",alertlev);

	    return mDb.insert(RESCUE_TABLE_NAME, null, initialValues);
	 }
}

