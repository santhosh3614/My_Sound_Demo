package com.megaminds.sounddemoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by santosh on 2/9/15.
 */
public class SoundMeterDb  extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="SoundMeterDb.db";
    private static final String TABLE_NAME=SoundMeter.class.getSimpleName();
    private static final int DATABASE_VERSION=1;
    private static final String KEY_ROWID = "key_row_id";
    private static final String NOISE_DATE = "date_millis";
    private static final String QUIET_MILLIS = "quiet_millis";
    private static final String GROUP_MILLIS = "group_millis";
    private static final String NOISE_MILLIS = "noise_millis";

    public SoundMeterDb(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NOISE_DATE+" INTEGER UNIQUE,"
                + QUIET_MILLIS + " INTEGER DEFAULT 0, " + GROUP_MILLIS + " INTEGER DEFAULT 0, "
                + NOISE_MILLIS + " INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    SoundLevel getSoundlevel(long date){
        SoundLevel soundLevel=null;
        Cursor cursor = getReadableDatabase().query(TABLE_NAME, null, NOISE_DATE + "=?",
                new String[]{String.valueOf(date)}, null, null, null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            long rowId=cursor.getLong(cursor.getColumnIndex(KEY_ROWID));
            date= cursor.getLong(cursor.getColumnIndex(NOISE_DATE));
            int quietMillis= cursor.getInt(cursor.getColumnIndex(QUIET_MILLIS));
            int groupMillis= cursor.getInt(cursor.getColumnIndex(GROUP_MILLIS));
            int noiseMillis= cursor.getInt(cursor.getColumnIndex(NOISE_MILLIS));
            soundLevel=new SoundLevel(rowId,date
                    ,quietMillis,groupMillis,noiseMillis);
            cursor.close();
        }
        return soundLevel;
    }

    SoundLevel insertSoundLevel(long date){
        ContentValues contentValues=new ContentValues();
        contentValues.put(NOISE_DATE,date);
        long rowId=getWritableDatabase().insert(TABLE_NAME,null,contentValues);
        return rowId==-1?null:new SoundLevel(rowId,date,0,0,0);
    }

    void updateSoundLevel(SoundLevel soundLevel){
        ContentValues contentValues=new ContentValues();
        contentValues.put(QUIET_MILLIS,soundLevel.quietLevel);
        contentValues.put(GROUP_MILLIS,soundLevel.groupLevel);
        contentValues.put(NOISE_MILLIS,soundLevel.noiseLevel);
        getWritableDatabase().update(TABLE_NAME, contentValues, KEY_ROWID+"=?",new String[]{
                String.valueOf(soundLevel.rowId)
        });
    }




}
