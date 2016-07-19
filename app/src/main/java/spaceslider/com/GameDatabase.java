package spaceslider.com;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by haemish on 2016/07/15.
 */
public class GameDatabase extends SQLiteOpenHelper
{
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    //Queries
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + HighScore.TABLE_NAME_HIGH_SCORES + " (" +
                    HighScore._ID + " INTEGER PRIMARY KEY," +
                    HighScore.COLUMN_NAME_PLAYER + TEXT_TYPE + COMMA_SEP +
                    HighScore.COLUMN_NAME_SCORE + TEXT_TYPE + " )";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + HighScore.TABLE_NAME_HIGH_SCORES;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "GameDatabase.db";

    public GameDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static abstract class HighScore implements BaseColumns
    {
        public static final String TABLE_NAME_HIGH_SCORES   = "localhighscores";
        public static final String COLUMN_NAME_PLAYER       = "playername";
        public static final String COLUMN_NAME_SCORE        = "playerscore";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addHighScore(String player,int score)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HighScore.COLUMN_NAME_PLAYER,player);
        values.put(HighScore.COLUMN_NAME_SCORE,Integer.toString(score));
        db.insert(HighScore.TABLE_NAME_HIGH_SCORES,null,values);
        db.close();
    }

    public String getHighScore()
    {
        int counter = 0;
        String high_score_string = "";
        SQLiteDatabase db=this.getReadableDatabase();
        String selectQuery = "SELECT "+HighScore.COLUMN_NAME_PLAYER+","+HighScore.COLUMN_NAME_SCORE+" FROM " + HighScore.TABLE_NAME_HIGH_SCORES + " ORDER BY "+HighScore.COLUMN_NAME_SCORE +" DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst())
        {
            do
            {
                high_score_string += cursor.getString(0)+"\t"+cursor.getString(1)+"\n\r";
                counter++;
            } while ((cursor.moveToNext())&&(counter<5));
        }
        db.close();
        return high_score_string;
    }
}
