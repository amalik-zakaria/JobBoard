package com.example.jobboard.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jobboard.models.Application;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "jobboard.db";
    private static final int DATABASE_VERSION = 1;

    // Table applications
    public static final String TABLE_APPLICATIONS      = "applications";
    public static final String COL_ID                  = "id";
    public static final String COL_JOB_ID              = "job_id";
    public static final String COL_JOB_TITLE           = "job_title";
    public static final String COL_COMPANY             = "company";
    public static final String COL_LOCATION            = "location";
    public static final String COL_DATE_APPLIED        = "date_applied";
    public static final String COL_STATUS              = "status";

    private static final String CREATE_TABLE_APPLICATIONS =
            "CREATE TABLE " + TABLE_APPLICATIONS + " ("
            + COL_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_JOB_ID       + " INTEGER, "
            + COL_JOB_TITLE    + " TEXT NOT NULL, "
            + COL_COMPANY      + " TEXT, "
            + COL_LOCATION     + " TEXT, "
            + COL_DATE_APPLIED + " TEXT, "
            + COL_STATUS       + " TEXT DEFAULT 'En attente'"
            + ");";

    // Singleton
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_APPLICATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLICATIONS);
        onCreate(db);
    }

    // ─── INSERT ────────────────────────────────────────────────────────────────

    /**
     * Insère une candidature dans la base de données.
     * @return l'ID de la ligne insérée, ou -1 si déjà postulé.
     */
    public long insertApplication(int jobId, String jobTitle, String company, String location) {
        if (isAlreadyApplied(jobId)) return -1;

        String today = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date());

        ContentValues values = new ContentValues();
        values.put(COL_JOB_ID,       jobId);
        values.put(COL_JOB_TITLE,    jobTitle);
        values.put(COL_COMPANY,      company);
        values.put(COL_LOCATION,     location);
        values.put(COL_DATE_APPLIED, today);
        values.put(COL_STATUS,       "En attente");

        SQLiteDatabase db = getWritableDatabase();
        long rowId = db.insert(TABLE_APPLICATIONS, null, values);
        db.close();
        return rowId;
    }

    // ─── READ ──────────────────────────────────────────────────────────────────

    /**
     * Retourne toutes les candidatures triées par date décroissante.
     */
    public List<Application> getAllApplications() {
        List<Application> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_APPLICATIONS,
                null,
                null, null, null, null,
                COL_ID + " DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Application app = new Application(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_JOB_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_JOB_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_COMPANY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_APPLIED)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS))
                );
                list.add(app);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    // ─── HELPERS ───────────────────────────────────────────────────────────────

    /**
     * Vérifie si l'offre a déjà été postulée.
     */
    public boolean isAlreadyApplied(int jobId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_APPLICATIONS,
                new String[]{COL_ID},
                COL_JOB_ID + " = ?",
                new String[]{String.valueOf(jobId)},
                null, null, null
        );
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    /**
     * Retourne le nombre total de candidatures.
     */
    public int getApplicationCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPLICATIONS, null);
        int count = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }
}

