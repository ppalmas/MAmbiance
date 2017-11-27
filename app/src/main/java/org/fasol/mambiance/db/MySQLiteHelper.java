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
    public static final String TABLE_POSSEDENOTE = "PossedeNote";
    public static final String TABLE_UTILISATEUR = "Utilisateur";
    public static final String TABLE_ADRESSE = "Adresse";
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

    /**
     * get the name of Table_possedeNote
     * @return String which is the name of table possede_Note
     */
    public static String getTablePossedenote() { return TABLE_POSSEDENOTE;}

    /**
     * get the name of Table_utilisateur
     * @return String which is the name of table rose_ambiance
     */
    public String getTableUtilisateur() {return TABLE_UTILISATEUR;}

    /**
     * get the name of Table_Adresse
     * @return String which is the name of table adresse
     */
    public String getTableAdresse() {return TABLE_ADRESSE;}



    //names of the columns of the whole database

    public static final String COLUMN_ID = "_id";

    //Table Marqueur
    public static final String COLUMN_MARQUEURID = "marqueur_id";
    public static final String COLUMN_DATECREATION = "date_creation";

    //Table Localisation/Lieu
    public static final String COLUMN_LIEUID = "lieu_id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    //Table Adresse
    public static final String COLUMN_CODEPOSTAL = "code_postal";
    public static final String COLUMN_COMPLEMENT = "complement";
    public static final String COLUMN_GEOM = "geom";
    public static final String COLUMN_ADRESSEID = "adresse_id";
    public static final String COLUMN_ADRESSE_LATITUDE = "adresse_latitude";
    public static final String COLUMN_ADRESSE_LONGITUDE = "adresse_longitude";
    public static final String COLUMN_NOM = "adresse_nom";
    public static final String COLUMN_NUMERO = "numero";
    public static final String COLUMN_PAYS = "pays";
    public static final String COLUMN_RUE = "rue";
    public static final String COLUMN_VILLE = "ville";

    //Table Rose
    public static final String COLUMN_ROSEID = "rose_id";
    public static final String COLUMN_OLFACTORY = "o";
    public static final String COLUMN_THERMAL = "t";
    public static final String COLUMN_VISUAL = "v";
    public static final String COLUMN_ACOUSTICAL = "a";

    //Table Photo/Image
    public static final String COLUMN_IMAGEID = "image_id";
    public static final String COLUMN_IMAGEEMP = "image_emp";

    //Table Mot
    public static final String COLUMN_MOTID = "mot_id";
    public static final String COLUMN_MOTLIBELLE = "mot_libelle";
    //public static final String COLUMN_BOOLEAN= "mot_boolen"; //Colonne pour savoir si le mot est choisi pour apparaître ou non

    //Table possedeNote
    public static final String COLUMN_NOTEID = "note_id";
    public static final String COLUMN_VALUE = "note_value";

    //Table Curseur TODO a enlever
    public static final String COLUMN_CURSEURID = "curseur_id";
    public static final String COLUMN_CURSEURLIBELLE = "curseur_libelle";
    public static final String COLUMN_CURSEURVALEUR = "curseur_valeur";

    //Table Utilisateur
    public static final String COLUMN_CLEAPI = "user_cleapi";
    public static final String COLUMN_DATECREE = "user_date";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_USERID = "user_id";
    public static final String COLUMN_MDP = "mot_de_passe";
    public static final String COLUMN_USERNOM = "user_nom";
    public static final String COLUMN_USERPRENOM = "user_prenom";
    public static final String COLUMN_PSEUDO = "pseudo";
    public static final String COLUMN_STATUT = "statut";

    /**
     * name of the local database
     * upgrading the version force the database to be deleted and recreated
     */
    public static final String DATABASE_NAME = "local.db";
    public static final int DATABASE_VERSION = 12;


    /**
     * query to create table lieu
     */
    private static final String DATABASE_CREATE = "create table " + TABLE_LIEU + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_LATITUDE + " REAL, "
            + COLUMN_LONGITUDE + " REAL, "
            + COLUMN_ADRESSEID + " INTEGER, "
            + "FOREIGN KEY( " + COLUMN_ADRESSEID + " )" + " REFERENCES " + TABLE_ADRESSE + " ( " + COLUMN_ID + " )"
            + "); ";

    /**
     * query to create table utilisateur
     */
    private static final String DATABASE_CREATE9 = "create table " + TABLE_UTILISATEUR + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USERNOM + " text not null, "
            + COLUMN_USERPRENOM + " text not null, "
            + COLUMN_MDP + " text not null, "
            + COLUMN_EMAIL + " text not null, "
            + COLUMN_PSEUDO + " text not null, "
            + COLUMN_STATUT + " INTEGER, "
            + COLUMN_CLEAPI + " text not null, "
            + COLUMN_DATECREE + " TIMESTAMP " //TODO VERIFIER
            + "); ";

    /**
     * query to create table Adresse
     */
    private static final String DATABASE_CREATE7 = "create table " + TABLE_ADRESSE + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NOM + " TEXT, "
            + COLUMN_NUMERO + " TEXT, "
            + COLUMN_RUE + " TEXT, "
            + COLUMN_VILLE + " TEXT, "
            + COLUMN_PAYS + " TEXT, "
            + COLUMN_CODEPOSTAL + " TEXT, "
            + COLUMN_COMPLEMENT + " TEXT, "
            + COLUMN_ADRESSE_LATITUDE + " REAL, "
            + COLUMN_ADRESSE_LONGITUDE + " REAL, "
            + COLUMN_GEOM + " TEXT "
            + "); ";

    /**
     * query to create table marqueur
     */
    private static final String DATABASE_CREATE2 = "create table " + TABLE_MARQUEUR + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DATECREATION + " DATE, "
            + COLUMN_LIEUID + " INTEGER, "
            //+ COLUMN_USERID + " INTEGER, "

            //+ "FOREIGN KEY( " + COLUMN_USERID + " )" + " REFERENCES " + TABLE_UTILISATEUR + " ( " + COLUMN_ID + " )"
            + "FOREIGN KEY( " + COLUMN_LIEUID + " )" + " REFERENCES " + TABLE_LIEU + " ( " + COLUMN_ID + " )"
            //TODO rajouter ligne du dessus utilisateur
            + "); ";

    /**
     * TODO query to create table possedeNote
     */
   /** private static final String DATABASE_CREATE8 = "create table" + TABLE_POSSEDENOTE + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_VALUE + " INTEGER, "
            + COLUMN_MARQUEURID + " INTEGER, "
            + COLUMN_MOTID + " INTEGER, "
            + "FOREIGN KEY( " + COLUMN_MARQUEURID + " )" + " REFERENCES " + TABLE_MARQUEUR + " ( " + COLUMN_ID + " ), "
            + "FOREIGN KEY( " + COLUMN_MOTID + " )" + " REFERENCES " + TABLE_MOT + " ( " + COLUMN_ID + " )"
            + "); ";
*/
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
            + COLUMN_MARQUEURID + " INTEGER, " //TODO à enlever
           // + COLUMN_MOTBOOLEAN + " BOOLEAN, "
            + "FOREIGN KEY( " + COLUMN_MARQUEURID + " )" + " REFERENCES " + TABLE_MARQUEUR + " ( " + COLUMN_ID + " )"
            + "); ";

    /**
     * query to create table curseur
     */
    //TODO à enlever
    private static final String DATABASE_CREATE6 = "create table " + TABLE_CURSEUR + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CURSEURLIBELLE + " TEXT NOT NULL, "
            + COLUMN_CURSEURVALEUR + " INTEGER, "
            + COLUMN_MARQUEURID + " INTEGER, "
            //+ COLUMN_USERID + "INTEGER, "
            + "FOREIGN KEY( " + COLUMN_MARQUEURID + " )" + " REFERENCES " + TABLE_MARQUEUR + " ( " + COLUMN_ID + " )"
            //+ "FOREIGN KEY( " + COLUMN_USERID + " )" + " REFERENCES " + TABLE_UTILISATEUR + " ( " + COLUMN_ID + " )"
            //TODO ADD
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
        database.execSQL(getDatabaseCreate9());
        database.execSQL(getDatabaseCreate());
        database.execSQL(getDatabaseCreate2());
        database.execSQL(getDatabaseCreate3());
        database.execSQL(getDatabaseCreate4());
        database.execSQL(getDatabaseCreate5());
        database.execSQL(getDatabaseCreate6());
        database.execSQL(getDatabaseCreate7());
       // database.execSQL(getDatabaseCreate8());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all your data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIEU + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADRESSE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARQUEUR + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROSEAMBIANCE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURSEUR + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOT + "; ");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSSEDENOTE + "; ");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UTILISATEUR + "; ");
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

    /** getter for createquery of related number
     * @return the query as String
     */
    public static String getDatabaseCreate7() {
        return DATABASE_CREATE7;
    }

   /** public static String getDatabaseCreate8() {
        return DATABASE_CREATE8;
    }*/ //TODO

    /**

     * getter for createquery of related number
     *
     * @return the query as String
     */
    public static String getDatabaseCreate9() {
        return DATABASE_CREATE9;
    }

}

