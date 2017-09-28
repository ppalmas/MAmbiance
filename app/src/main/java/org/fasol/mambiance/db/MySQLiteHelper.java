package org.fasol.mambiance.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";
    /**
     * declaration of tables
     */
    public static final String TABLE_MARQUEUR = "Marqueur";
    public static final String TABLE_LIEU = "Lieu";
    public static final String TABLE_CURSEUR = "Curseur";
    public static final String TABLE_MOT = "Mot";
    public static final String TABLE_IMAGE = "Image";
    public static final String TABLE_ROSEAMBIANCE = "Rose_ambiance";

    //creation of tables getters

    /**
     * get the name of Table Marqueur
     * @return String which is the name of table marqueur
     */
    public static String getTableMarqueur() {
        return TABLE_MARQUEUR;
    }
    /**
     * get the name of Table Lieu
     * @return String which is the name of table lieu
     */
    public static String getTableLieu() {
        return TABLE_LIEU;
    }
    /**
     * get the name of Table Curseur
     * @return String which is the name of table curseur
     */
    public static String getTableCurseur() {
        return TABLE_CURSEUR;
    }
    /**
     * get the name of Table Mot
     * @return String which is the name of table mot
     */
    public static String getTableMot() {
        return TABLE_MOT;
    }
    /**
     * get the name of Table Image
     * @return String which is the name of table image
     */
    public static String getTableImage() {
        return TABLE_IMAGE;
    }
    /**
     * get the name of Table Rose_ambiance
     * @return String which is the name of table rose_ambiance
     */
    public static String getTableRoseAmbiance() {
        return TABLE_ROSEAMBIANCE;
    }


    //names of the columns of the whole database

    public static final String COLUMN_ID = "_id";

    public static final String COLUMN_MARQUEURID = "marqueur_id";
    public static final String COLUMN_DATECREATION = "date_creation";

    public static final String COLUMN_LIEUID = "lieu_id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LIEUNOM = "lieu_nom";
    public static final String COLUMN_ADRESSE = "adresse";

    public static final String COLUMN_ROSEID = "rose_id";
    public static final String COLUMN_OLFACTORY = "o";
    public static final String COLUMN_THERMAL = "t";
    public static final String COLUMN_VISUAL = "v";
    public static final String COLUMN_ACOUSTICAL = "a";

    public static final String COLUMN_IMAGEID = "image_id";
    public static final String COLUMN_IMAGEEMP = "image_emp";

    public static final String COLUMN_MOTID = "mot_id";
    public static final String COLUMN_MOTLIBELLE = "mot_libelle";

    public static final String COLUMN_CURSEURID = "curseur_id";
    public static final String COLUMN_CURSEURLIBELLE = "curseur_libelle";
    public static final String COLUMN_CURSEURVALEUR = "curseur_valeur";


    /**
     * name of the local database
     * upgrading the version force the database to be deleted and recreated
     */
    public static final String DATABASE_NAME = "local.db";
    public static final int DATABASE_VERSION = 5;


    /**
     * query to create table lieu
     */
    private static final String DATABASE_CREATE = "create table " + TABLE_LIEU + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_LIEUNOM + " text not null, "
            + COLUMN_ADRESSE + " text not null, "
            + COLUMN_LATITUDE + " REAL, "
            + COLUMN_LONGITUDE + " REAL "
            + "); ";

    /**
     * query to create table marqueur
     */
    private static final String DATABASE_CREATE2 = "create table " + TABLE_MARQUEUR + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DATECREATION + " DATE, "
            + COLUMN_LIEUID + " INTEGER, "
            + "FOREIGN KEY( " + COLUMN_LIEUID + " )" + " REFERENCES " + TABLE_LIEU + " ( " + COLUMN_ID + " )"
            + "); ";

    /**
     * query to create table rose_ambiance
     */
    private static final String DATABASE_CREATE3 = "create table " + TABLE_ROSEAMBIANCE + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_OLFACTORY + " REAL NOT NULL, "
            + COLUMN_VISUAL + " REAL NOT NULL, "
            + COLUMN_THERMAL + " REAL NOT NULL, "
            + COLUMN_ACOUSTICAL + " REAL NOT NULL, "
            + COLUMN_MARQUEURID + " INTEGER, "
            + "FOREIGN KEY( " + COLUMN_MARQUEURID + " )" + " REFERENCES " + TABLE_MARQUEUR + " ( " + COLUMN_ID + " )"
            + "); ";
    /**
     * query to create table image
     */
    private static final String DATABASE_CREATE4 = "create table " + TABLE_IMAGE + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_IMAGEEMP + " TEXT NOT NULL UNIQUE, "
            + COLUMN_MARQUEURID + " INTEGER, "
            + "FOREIGN KEY( " + COLUMN_MARQUEURID + " )" + " REFERENCES " + TABLE_MARQUEUR + " ( " + COLUMN_ID + " )"
            + "); ";
    /**
     * query to create table mot
     */
    private static final String DATABASE_CREATE5 = "create table " + TABLE_MOT + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_MOTLIBELLE + " TEXT NOT NULL, "
            + COLUMN_MARQUEURID + " INTEGER, "
            + "FOREIGN KEY( " + COLUMN_MARQUEURID + " )" + " REFERENCES " + TABLE_MARQUEUR + " ( " + COLUMN_ID + " )"
            + "); ";
    /**
     * query to create table curseur
     */
    private static final String DATABASE_CREATE6 = "create table " + TABLE_CURSEUR + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CURSEURLIBELLE + " TEXT NOT NULL, "
            + COLUMN_CURSEURVALEUR + " INTEGER, "
            + COLUMN_MARQUEURID + " INTEGER, "
            + "FOREIGN KEY( " + COLUMN_MARQUEURID + " )" + " REFERENCES " + TABLE_MARQUEUR + " ( " + COLUMN_ID + " )"
            + "); ";

    /**
     * constructor
     *
     * @param context of our activity
     */
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        /**
         * we call each methods to create every table
         */
        database.execSQL(getDatabaseCreate());
        database.execSQL(getDatabaseCreate2());
        database.execSQL(getDatabaseCreate3());
        database.execSQL(getDatabaseCreate4());
        database.execSQL(getDatabaseCreate5());
        database.execSQL(getDatabaseCreate6());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all your data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIEU + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARQUEUR + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROSEAMBIANCE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURSEUR + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOT + "; ");
        onCreate(db);
    }

    /**
     * getter for createquery of related number
     *
     * @return the query as String
     */
    public static String getDatabaseCreate() {
        return DATABASE_CREATE;
    }

    /**
     * getter for createquery of related number
     *
     * @return the query as String
     */
    public static String getDatabaseCreate2() {
        return DATABASE_CREATE2;
    }

    /**
     * getter for createquery of related number
     *
     * @return the query as String
     */
    public static String getDatabaseCreate3() {
        return DATABASE_CREATE3;
    }

    /**
     * getter for createquery of related number
     *
     * @return the query as String
     */
    public static String getDatabaseCreate4() {
        return DATABASE_CREATE4;
    }

    /**
     * getter for createquery of related number
     *
     * @return the query as String
     */
    public static String getDatabaseCreate5() {
        return DATABASE_CREATE5;
    }

    /**
     * getter for createquery of related number
     *
     * @return the query as String
     */
    public static String getDatabaseCreate6() {
        return DATABASE_CREATE6;
    }
}

