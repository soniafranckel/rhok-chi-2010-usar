package usar.mobile;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
	private static final String[] COLUMNS = {"latitude", "longitude", /*"photo",*/ "alertlevel"};

	private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private SQLiteDatabase read;
    
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
        read = mDbHelper.getReadableDatabase();
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
	 
	 public Cursor getRescueImageEntries() {
		String selection = null; // where clause
		String[] selectionArgs = null;  // You may include ?s in selection replaced by the values from selectionArgs
	    String groupBy = "longitude, latitude"; // A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself). Passing null will cause the rows to not be grouped.
	    String having = null; //A filter declare which row groups to include in the cursor, if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself). Passing null will cause all row groups to be included, and is required when row grouping is not being used.
		String orderBy = null;//	How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
		return read.query(RESCUE_TABLE_NAME, COLUMNS, selection, selectionArgs, groupBy, having, orderBy);
	 }
}

