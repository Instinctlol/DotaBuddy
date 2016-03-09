package jwwu.com.dotabuddy.database;


import android.content.ContentValues;
import android.provider.BaseColumns;

public final class DotaDBContract {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION                    = 1;
    public static final String DATABASE_NAME                    = "DotaTimers.db";
    private static final String TEXT_TYPE_NOTNULL_CONFLICT_FAIL = " TEXT NOT NULL ON CONFLICT FAIL";
    private static final String TEXT_TYPE                       = " TEXT";
    private static final String INT_TYPE_NOTNULL_CONFLICT_FAIL  = " INTEGER NOT NULL ON CONFLICT FAIL";
    private static final String INT_TYPE                        = " INTEGER";
    private static final String BOOL_TYPE_NOTNULL_CONFLICT_FAIL = " INTEGER NOT NULL ON CONFLICT FAIL";
    public static final String COMMA_SEP                        = ",";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DotaDBContract() {}



    public static abstract class DotaTimerEntry implements BaseColumns {
        public static final String TABLE_NAME = "dotatimer";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_EXEC_TIME = "exectime";
        public static final String COLUMN_NAME_OFFSET = "offset";
        public static final String COLUMN_NAME_REPEAT = "repeat";
        public static final String COLUMN_NAME_EXACT = "exact";
        public static final String COLUMN_NAME_ICONTHUMBID = "iconthumbid";

        //Content: ID + TITLE + EXECTIME + OFFSET + REPEAT + EXACT + ICONTHUMBID
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_TITLE + TEXT_TYPE_NOTNULL_CONFLICT_FAIL + COMMA_SEP +
                COLUMN_NAME_EXEC_TIME + INT_TYPE_NOTNULL_CONFLICT_FAIL + COMMA_SEP +
                COLUMN_NAME_OFFSET + INT_TYPE_NOTNULL_CONFLICT_FAIL + COMMA_SEP +
                COLUMN_NAME_REPEAT + BOOL_TYPE_NOTNULL_CONFLICT_FAIL + COMMA_SEP +
                COLUMN_NAME_EXACT + BOOL_TYPE_NOTNULL_CONFLICT_FAIL + COMMA_SEP +
                COLUMN_NAME_ICONTHUMBID + INT_TYPE + COMMA_SEP +
                " UNIQUE(" + COLUMN_NAME_TITLE + COMMA_SEP + " " + COLUMN_NAME_ICONTHUMBID + ")" +
                " ON CONFLICT REPLACE" +
                " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final ContentValues putAllValues(String title,
                                                         int exectime, int offset, boolean repeat,
                                                         boolean exact, int iconthumbid) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_NAME_TITLE, title);
            cv.put(COLUMN_NAME_EXEC_TIME, exectime);
            cv.put(COLUMN_NAME_OFFSET, offset);
            cv.put(COLUMN_NAME_REPEAT, repeat ? 1 : 0);
            cv.put(COLUMN_NAME_EXACT, exact ? 1 : 0);
            cv.put(COLUMN_NAME_ICONTHUMBID, iconthumbid);
            return cv;
        }

    }

    public static abstract class DotaHeroesDatabase implements BaseColumns {
        public static final String TABLE_NAME = "heroes";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_STATS = "stats";
        public static final String COLUMN_NAME_BALANCECHANGELOG = "balancechangelog";
        public static final String COLUMN_NAME_PICTURE = "picture";

        //Content: ID + NAME + ABILITIES_ARRAY + PICTURE
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_NAME + TEXT_TYPE_NOTNULL_CONFLICT_FAIL + COMMA_SEP +
                COLUMN_NAME_STATS + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_BALANCECHANGELOG + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_PICTURE + TEXT_TYPE + COMMA_SEP +
                " UNIQUE(" + COLUMN_NAME_NAME + ")" + "ON CONFLICT REPLACE" +
                ")";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static abstract class DotaAbilitiesDatabase implements BaseColumns {
        public static final String TABLE_NAME = "abilities";
        public static final String COLUMN_NAME_HERO_ID = "hero_id";     //FOREIGN KEY
        public static final String COLUMN_NAME_NAME = "name";           //UNIQUE
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_DESCRIPTION = "desc";
        public static final String COLUMN_NAME_LORE = "lore";
        public static final String COLUMN_NAME_ABILITY = "ability";
        public static final String COLUMN_NAME_AFFECTS = "affects";
        public static final String COLUMN_NAME_BKBBLOCK = "bkbblock";
        public static final String COLUMN_NAME_BKBTEXT = "bkbtext";
        public static final String COLUMN_NAME_LINKENBLOCK = "linkenblock";
        public static final String COLUMN_NAME_LINKENKTEXT = "linkentext";
        public static final String COLUMN_NAME_PURGEABLE = "purgeable";
        public static final String COLUMN_NAME_PURGETEXT = "purgetext";
        public static final String COLUMN_NAME_ILLUSIONUSE = "illusionuse";
        public static final String COLUMN_NAME_ILLUSIONTEXT = "illusiontext";
        public static final String COLUMN_NAME_BREAKABLE = "breakable";
        public static final String COLUMN_NAME_BREAKTEXT = "breaktext";
        public static final String COLUMN_NAME_UAM = "uam";
        public static final String COLUMN_NAME_CASTPOINT = "castpoint";
        public static final String COLUMN_NAME_CASTBACKSWING = "castbackswing";
        public static final String COLUMN_NAME_TRAITSANDVALUESLIST = "traitsandvalueslist";
        public static final String COLUMN_NAME_MANA = "mana";
        public static final String COLUMN_NAME_COOLDOWN = "cooldown";
        public static final String COLUMN_NAME_AGHANIMSUPGRADE = "aghanimsupgrage";
        public static final String COLUMN_NAME_NOTESLIST = "noteslist";

        public static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_HERO_ID + INT_TYPE_NOTNULL_CONFLICT_FAIL + COMMA_SEP +    //HERO ID CAN NOT BE NULL
                COLUMN_NAME_NAME + TEXT_TYPE_NOTNULL_CONFLICT_FAIL + COMMA_SEP +     //ABILITY NAME CAN NOT BE NULL
                COLUMN_NAME_IMAGE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_LORE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_ABILITY + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_AFFECTS + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_BKBBLOCK + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_BKBTEXT + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_LINKENBLOCK + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_LINKENKTEXT + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_PURGEABLE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_PURGETEXT + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_ILLUSIONUSE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_ILLUSIONTEXT + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_BREAKABLE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_BREAKTEXT + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_UAM + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_CASTPOINT + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_CASTBACKSWING + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_TRAITSANDVALUESLIST + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_MANA + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_COOLDOWN + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_AGHANIMSUPGRADE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_NOTESLIST + TEXT_TYPE + COMMA_SEP +
                "UNIQUE("+COLUMN_NAME_NAME+") ON CONFLICT REPLACE" + COMMA_SEP +
                "FOREIGN KEY("+COLUMN_NAME_HERO_ID+") REFERENCES "+ DotaHeroesDatabase.TABLE_NAME +"("+DotaHeroesDatabase._ID+")" +
                ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
