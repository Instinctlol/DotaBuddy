package jwwu.com.dotabuddy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DotaDBSQLiteHelper extends SQLiteOpenHelper {

    public DotaDBSQLiteHelper(Context context) {
        super(context, DotaDBContract.DATABASE_NAME, null, DotaDBContract.DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DotaDBContract.DotaTimerEntry.CREATE_TABLE);
        db.execSQL(DotaDBContract.DotaHeroesDatabase.CREATE_TABLE);
        db.execSQL(DotaDBContract.DotaAbilitiesDatabase.CREATE_TABLE);
    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DotaDBContract.DotaTimerEntry.DELETE_TABLE);
        db.execSQL(DotaDBContract.DotaAbilitiesDatabase.DELETE_TABLE);  //drop this before heroes because foreign keys
        db.execSQL(DotaDBContract.DotaHeroesDatabase.DELETE_TABLE);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
        //API-LEVEL 16: db.setForeignKeyConstraintsEnabled(true);
    }
}
