package org.fasol.mambiance.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.fasol.mambiance.MainActivity;

public class LocalDataSource {
    private static final String TAG = "localDataSource";
    //Database fields
    /**
     * database attributes which contains the database (need to open and close it for each actions)
     */
    private SQLiteDatabase database;

    /**
     * database attributes which allow to create the database
     */
    private MySQLiteHelper dbHelper;

    //TODO Adddescription for javadoc
    private String[] allColumnsMarqueur = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_DATECREATION, MySQLiteHelper.COLUMN_LIEUID};
    private String[] allColumnsCurseur = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_CURSEURLIBELLE, MySQLiteHelper.COLUMN_CURSEURVALEUR, MySQLiteHelper.COLUMN_MARQUEURID};
    private String[] allColumnsImage = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_IMAGEEMP, MySQLiteHelper.COLUMN_MARQUEURID};
    private String[] allColumnsMot = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_MOTLIBELLE, MySQLiteHelper.COLUMN_MARQUEURID};
    private String[] allColumnsLieu = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_LIEUNOM, MySQLiteHelper.COLUMN_ADRESSE, MySQLiteHelper.COLUMN_LATITUDE, MySQLiteHelper.COLUMN_LONGITUDE};
    private String[] allColumnsRoseAmbiance = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_OLFACTORY, MySQLiteHelper.COLUMN_VISUAL, MySQLiteHelper.COLUMN_THERMAL, MySQLiteHelper.COLUMN_ACOUSTICAL, MySQLiteHelper.COLUMN_MARQUEURID};

    //getters

    /**
     * getter for the MySQLiteHelper
     *
     * @return dbHelper
     */
    public MySQLiteHelper getDbHelper() {
        return dbHelper;
    }

    /**
     * getter for the SQLiteDatabase
     *
     * @return database
     */
    public SQLiteDatabase getDatabase() {
        return database;
    }

    //constructor

    /**
     * constructor only consists in creating dbHelper
     *
     * @param context
     */
    public LocalDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    /**
     * Open database
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * close the database, need to be called to avoid any issue on the database treatment
     */
    public void close() {
        dbHelper.close();
    }

    // ----------------------------------- MARQUEUR METHODES ------------------------------------------

    /**
     * creating a new Marqueur in the database
     *
     * @param lieu_id id of the place linked to the Marqueur
     * @return Marqueur is the created Marqueur
     */
    public Marqueur createMarqueur(long lieu_id) {
        ContentValues values = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateMnt = new Date(System.currentTimeMillis());
        values.put(MySQLiteHelper.COLUMN_DATECREATION, dateFormat.format(dateMnt));
        values.put(MySQLiteHelper.COLUMN_LIEUID, lieu_id);

        long insertId = database.insert(MySQLiteHelper.TABLE_MARQUEUR, null, values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_MARQUEUR,
                allColumnsMarqueur, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Marqueur newMarqueur = cursorToMarqueur(cursor);
        cursor.close();
        return newMarqueur;
    }

    /**
     * knowing a Marqueur_id, we want to get the marqueur itself
     *
     * @param id is the id of the marqueur we are looking for
     * @return m1 is the marqueur we were looking for
     */
    public Marqueur getMarqueurWithId(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_MARQUEUR, allColumnsMarqueur, MySQLiteHelper.COLUMN_ID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        Marqueur m1 = cursorToMarqueur(c);
        c.close();
        return m1;
    }


    /**
     * knowing an id we test if this marqueur exists
     *
     * @param id is the id of the marqueur we ask
     * @return boolean says if the marqueur with this id exists or not
     */
    public boolean existMarqueurWithId(Long id) {
        boolean res;
        Cursor c = database.query(MySQLiteHelper.TABLE_MARQUEUR, allColumnsMarqueur, MySQLiteHelper.COLUMN_ID + " = \"" + id + "\"", null, null, null, null);
        if (c.getCount() > 0) {
            c.close();
            res = true;
        } else {
            c.close();
            res = false;
        }
        return res;
    }

    /**
     * deleting Marqueur in the database
     *
     * @param m1 marqueur linked to the marqueur in the database
     */
    public void deleteMarqueur(Marqueur m1) {
        long id = m1.getMarqueur_id();
        System.out.println("Marqueur deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_MARQUEUR, MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * deleting all Marqueur in the database
     */
    public void clearMarqueur() {
        System.out.println("Marqueur cleared");
        database.execSQL("DROP TABLE IF EXISTS Marqueur");
        database.execSQL(MySQLiteHelper.getDatabaseCreate2());
    }

    /**
     * return the Marqueur linked to the cursor
     *
     * @param cursor
     * @return Marqueur linked to the cursor
     */
    private Marqueur cursorToMarqueur(Cursor cursor) {
        Marqueur marqueur = new Marqueur();

        marqueur.setMarqueur_id(cursor.getLong(0));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date dateCreation = null;
        try {
            dateCreation = dateFormat.parse(cursor.getString(1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        marqueur.setDate_creation(dateCreation);

        marqueur.setLieu_id(cursor.getLong(2));
        return marqueur;
    }

    //----------------------------------- LIEU METHODES ------------------------------------------

    /**
     * creation a new Lieu in the database
     *
     * @param nom       name of the place
     * @param adresse
     * @param latitude  latitude of the place
     * @param longitude longitude of the place
     * @return Lieu is the created Lieu
     */
    public Lieu createLieu(String nom, String adresse, double latitude, double longitude) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_LIEUNOM, nom);
        values.put(MySQLiteHelper.COLUMN_ADRESSE, adresse);
        values.put(MySQLiteHelper.COLUMN_LATITUDE, latitude);
        values.put(MySQLiteHelper.COLUMN_LONGITUDE, longitude);
        long insertId = database.insert(MySQLiteHelper.TABLE_LIEU, null, values);
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_LIEU,
                allColumnsLieu,
                MySQLiteHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        Lieu newLieu = cursorToLieu(cursor);//method at the end of the class
        cursor.close();
        return newLieu;
    }

    /**
     * update a Lieu
     *
     * @return Lieu updated
     */
    public Lieu updateLieu(Lieu lieu, String nom, String adresse, double latitude, double longitude, long adresse_id) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_LIEUNOM, nom);
        values.put(MySQLiteHelper.COLUMN_ADRESSE, adresse);
        values.put(MySQLiteHelper.COLUMN_LONGITUDE, longitude);
        values.put(MySQLiteHelper.COLUMN_LATITUDE, latitude);

        database.update(MySQLiteHelper.TABLE_LIEU, values, MySQLiteHelper.COLUMN_ID + " = " + lieu.getLieu_id(), null);
        return getLieuWithId(lieu.getLieu_id());
    }

    /**
     * knowing a Lieu_id, we want to get the lieu itself
     *
     * @param id is the id of the Lieu we are looking for
     * @return l1 is the Lieu we were looking for
     */
    public Lieu getLieuWithId(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_LIEU, allColumnsLieu, MySQLiteHelper.COLUMN_ID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        Lieu l1 = cursorToLieu(c);
        c.close();
        return l1;
    }

    /**
     * knowing a Lieu_id, we want to get the lieu itself
     *
     * @param lat latitude
     * @param lng longitude
     * @return l1 is the lieu we were looking for
     */

    public Lieu getLieuWithLatLng(double lat, double lng) {
        Cursor c = database.query(MySQLiteHelper.TABLE_LIEU, allColumnsLieu, MySQLiteHelper.COLUMN_LATITUDE + " = \"" + lat + "\"" + " AND " + MySQLiteHelper.COLUMN_LONGITUDE + " = \"" + lng + "\"", null, null, null, null);
        c.moveToFirst();
        Lieu l1 = cursorToLieu(c);
        c.close();
        return l1;
    }

    /**
     * knowing an id we test if this Lieu exists
     *
     * @param id Lieu id
     * @return boolean says if the Lieu with this id exists or not
     */
    public boolean existLieuWithId(long id) {
        boolean res;
        Cursor c = database.query(MySQLiteHelper.TABLE_LIEU, allColumnsLieu, MySQLiteHelper.COLUMN_ID + " = \"" + id + "\"", null, null, null, null);
        if (c.getCount() > 0) {
            c.close();
            res = true;
        } else {
            c.close();
            res = false;
        }
        return res;
    }

    /**
     * knowing an id we test if this Lieu exists
     *
     * @param lat latitude
     * @param lng longitude
     * @return boolean says if the Lieu with this id exists or not
     */
    public boolean existLieuWithLatLng(double lat, double lng) {
        boolean res;
        Cursor c = database.query(MySQLiteHelper.TABLE_LIEU, allColumnsLieu, MySQLiteHelper.COLUMN_LATITUDE + " = \"" + lat + "\"" + " AND " + MySQLiteHelper.COLUMN_LONGITUDE + " = \"" + lng + "\"", null, null, null, null);
        if (c.getCount() > 0) {
            c.close();
            res = true;
        } else {
            c.close();
            res = false;
        }
        return res;
    }


    /**
     * deleting a Lieu
     *
     * @param l1 is the Lieu we want to delete
     */
    public void deleteLieu(Lieu l1) {
        long id = l1.getLieu_id();
        System.out.println("Place deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_LIEU, MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * deleting all Lieu
     */
    public void clearLieu() {
        System.out.println("Lieu cleared");
        database.execSQL("DROP TABLE IF EXISTS Lieu");
        database.execSQL(MySQLiteHelper.getDatabaseCreate());
    }

    /**
     * convert a cursor to a Lieu
     *
     * @param cursor
     * @return Lieu
     */
    private Lieu cursorToLieu(Cursor cursor) {
        Lieu p1 = new Lieu();
        p1.setLieu_id(cursor.getLong(0));
        p1.setLieu_nom(cursor.getString(1));
        p1.setAdresse(cursor.getString(2));
        p1.setLatitude(cursor.getFloat(3));
        p1.setLongitude(cursor.getFloat(4));
        return p1;
    }

    //----------------------------------- IMAGE METHODES ------------------------------------------

    /**
     * creation a new Image in the database
     *
     * @param marqueur_id
     * @param img_emp     emplacement de l'image
     * @return Image is the created Image
     */
    public Image createImage(long marqueur_id, String img_emp) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MARQUEURID, marqueur_id);
        values.put(MySQLiteHelper.COLUMN_IMAGEEMP, img_emp);
        long insertId = database.insert(MySQLiteHelper.TABLE_IMAGE, null, values);
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_IMAGE,
                allColumnsImage,
                MySQLiteHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        Image newImage = cursorToImage(cursor);//method at the end of the class
        cursor.close();
        return newImage;
    }

    /**
     * update a Image
     *
     * @return Image updated
     */
    public Image updateImage(Image image, long marqueur_id, String img_emp) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MARQUEURID, marqueur_id);
        values.put(MySQLiteHelper.COLUMN_IMAGEEMP, img_emp);

        database.update(MySQLiteHelper.TABLE_IMAGE, values, MySQLiteHelper.COLUMN_ID + " = " + image.getImage_id(), null);
        return getImageWithId(image.getImage_id());
    }

    /**
     * knowing a Image_id, we want to get the image itself
     *
     * @param id is the id of the Image we are looking for
     * @return i1 is the Image we were looking for
     */
    public Image getImageWithId(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_IMAGE, allColumnsImage, MySQLiteHelper.COLUMN_ID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        Image i1 = cursorToImage(c);
        c.close();
        return i1;
    }

    /**
     * knowing a Marquer_id, we want to get the image itself
     *
     * @param id is the id of the Marqueur we are looking for
     * @return i1 is the Image we were looking for
     */
    public Image getImageWithMarqueurId(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_IMAGE, allColumnsImage, MySQLiteHelper.COLUMN_MARQUEURID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        Image i1 = cursorToImage(c);
        c.close();
        return i1;
    }

    /**
     * knowing a image emplacement, we want to get the image itself
     *
     * @param img_emp image emplacement
     * @return i1 is the image we were looking for
     */

    public Image getImageWithEmp(String img_emp) {
        Cursor c = database.query(MySQLiteHelper.TABLE_IMAGE, allColumnsImage, MySQLiteHelper.COLUMN_IMAGEEMP + " = \"" + img_emp + "\"", null, null, null, null);
        c.moveToFirst();
        Image i1 = cursorToImage(c);
        c.close();
        return i1;
    }

    /**
     * knowing an image emplacement we test if this Image exists
     *
     * @param img_emp image emplacement
     * @return boolean says if the Image with this id exists or not
     */
    public boolean existImageWithEmp(String img_emp) {
        boolean res;
        Cursor c = database.query(MySQLiteHelper.TABLE_IMAGE, allColumnsImage, MySQLiteHelper.COLUMN_IMAGEEMP + " = \"" + img_emp + "\"", null, null, null, null);
        if (c.getCount() > 0) {
            c.close();
            res = true;
        } else {
            c.close();
            res = false;
        }
        return res;
    }

    /**
     * knowing an image id we test if this Image exists
     *
     * @param id image id
     * @return boolean says if the Image with this id exists or not
     */
    public boolean existImageWithId(long id) {
        boolean res;
        Cursor c = database.query(MySQLiteHelper.TABLE_IMAGE, allColumnsImage, MySQLiteHelper.COLUMN_ID + " = \"" + id + "\"", null, null, null, null);
        if (c.getCount() > 0) {
            c.close();
            res = true;
        } else {
            c.close();
            res = false;
        }
        return res;
    }


    /**
     * deleting a Image
     *
     * @param i1 is the Image we want to delete
     */
    public void deleteImage(Image i1) {
        long id = i1.getImage_id();
        System.out.println("Image deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_IMAGE, MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * deleting all Image
     */
    public void clearImage() {
        System.out.println("Image cleared");
        database.execSQL("DROP TABLE IF EXISTS Image");
        database.execSQL(MySQLiteHelper.getDatabaseCreate4());
    }

    /**
     * convert a cursor to a Image
     *
     * @param cursor
     * @return Image
     */
    private Image cursorToImage(Cursor cursor) {
        Image i1 = new Image();
        i1.setImage_id(cursor.getLong(0));
        i1.setImage_emp(cursor.getString(1));
        i1.setMarqueur_id(cursor.getLong(2));
        return i1;
    }

    //----------------------------------- CURSEUR METHODES ------------------------------------------

    /**
     * creation a new Curseur in the database
     *
     * @param curseur_lib
     * @param curseur_val
     * @param marqueur_id
     * @return Curseur is the created Curseur
     */
    public Curseur createCurseur(String curseur_lib, int curseur_val, long marqueur_id) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MARQUEURID, marqueur_id);
        values.put(MySQLiteHelper.COLUMN_CURSEURLIBELLE, curseur_lib);
        values.put(MySQLiteHelper.COLUMN_CURSEURVALEUR, curseur_val);
        long insertId = database.insert(MySQLiteHelper.TABLE_CURSEUR, null, values);
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_CURSEUR,
                allColumnsCurseur,
                MySQLiteHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        Curseur newCurseur = cursorToCurseur(cursor);//method at the end of the class
        cursor.close();
        return newCurseur;
    }

    /**
     * update a Curseur
     *
     * @return Curseur updated
     */
    public Curseur updateCurseur(Curseur curseur, String curseur_lib, int curseur_val, long marqueur_id) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MARQUEURID, marqueur_id);
        values.put(MySQLiteHelper.COLUMN_CURSEURLIBELLE, curseur_lib);
        values.put(MySQLiteHelper.COLUMN_CURSEURVALEUR, curseur_val);

        database.update(MySQLiteHelper.TABLE_CURSEUR, values, MySQLiteHelper.COLUMN_ID + " = " + curseur.getCurseur_id(), null);
        return getCurseurWithId(curseur.getCurseur_id());
    }

    /**
     * knowing a Curseur_id, we want to get the curseur itself
     *
     * @param id is the id of the Curseur we are looking for
     * @return c1 is the Curseur we were looking for
     */
    public Curseur getCurseurWithId(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_CURSEUR, allColumnsCurseur, MySQLiteHelper.COLUMN_ID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        Curseur c1 = cursorToCurseur(c);
        c.close();
        return c1;
    }

    /**
     * knowing a Marqueur_id, we want to get the mot itself
     *
     * @param id is the id of the Marqueur we are looking for
     * @return l_curseur is the list of Curseur we were looking for
     */
    public ArrayList<Curseur> getCurseurWithMarqueurId(long id) {
        ArrayList<Curseur> l_curseur = new ArrayList<Curseur>();
        Cursor c = database.query(MySQLiteHelper.TABLE_CURSEUR, allColumnsCurseur, MySQLiteHelper.COLUMN_MARQUEURID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            l_curseur.add(cursorToCurseur(c));
            c.moveToNext();
        }
        c.close();
        return l_curseur;
    }

    /**
     * knowing an curseur id we test if this Curseur exists
     *
     * @param id curseur id
     * @return boolean says if the Curseur with this id exists or not
     */
    public boolean existCurseurWithId(long id) {
        boolean res;
        Cursor c = database.query(MySQLiteHelper.TABLE_CURSEUR, allColumnsCurseur, MySQLiteHelper.COLUMN_ID + " = \"" + id + "\"", null, null, null, null);
        if (c.getCount() > 0) {
            c.close();
            res = true;
        } else {
            c.close();
            res = false;
        }
        return res;
    }


    /**
     * deleting a Curseur
     *
     * @param c1 is the Curseur we want to delete
     */
    public void deleteCurseur(Curseur c1) {
        long id = c1.getCurseur_id();
        System.out.println("Curseur deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_CURSEUR, MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * deleting all Curseur
     */
    public void clearCurseur() {
        System.out.println("Curseur cleared");
        database.execSQL("DROP TABLE IF EXISTS Curseur");
        database.execSQL(MySQLiteHelper.getDatabaseCreate6());
    }

    /**
     * convert a cursor to a Curseur
     *
     * @param cursor
     * @return Curseur
     */
    private Curseur cursorToCurseur(Cursor cursor) {
        Curseur c1 = new Curseur();
        c1.setCurseur_id(cursor.getLong(0));
        c1.setCurseur_libelle(cursor.getString(1));
        c1.setCurseur_valeur(cursor.getInt(2));
        c1.setMarqueur_id(cursor.getLong(3));
        return c1;
    }

    //----------------------------------- MOT METHODES ------------------------------------------

    /**
     * creation a new Mot in the database
     *
     * @param mot_lib
     * @param marqueur_id
     * @return Mot is the created Mot
     */
    public Mot createMot(String mot_lib, long marqueur_id) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MARQUEURID, marqueur_id);
        values.put(MySQLiteHelper.COLUMN_MOTLIBELLE, mot_lib);
        long insertId = database.insert(MySQLiteHelper.TABLE_MOT, null, values);
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_MOT,
                allColumnsMot,
                MySQLiteHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        Mot newMot = cursorToMot(cursor);//method at the end of the class
        cursor.close();
        return newMot;
    }

    /**
     * update a Mot
     *
     * @return Mot updated
     */
    public Mot updateMot(Mot mot, String mot_lib, long marqueur_id) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MARQUEURID, marqueur_id);
        values.put(MySQLiteHelper.COLUMN_MOTLIBELLE, mot_lib);

        database.update(MySQLiteHelper.TABLE_MOT, values, MySQLiteHelper.COLUMN_ID + " = " + mot.getMot_id(), null);
        return getMotWithId(mot.getMot_id());
    }

    /**
     * knowing a Mot_id, we want to get the mot itself
     *
     * @param id is the id of the Mot we are looking for
     * @return m1 is the Mot we were looking for
     */
    public Mot getMotWithId(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_MOT, allColumnsMot, MySQLiteHelper.COLUMN_ID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        Mot m1 = cursorToMot(c);
        c.close();
        return m1;
    }

    /**
     * knowing a Marqueur_id, we want to get the mot itself
     *
     * @param id is the id of the Marqueur we are looking for
     * @return m1 is the Mot we were looking for
     */
    public ArrayList<Mot> getMotWithMarqueurId(long id) {
        ArrayList<Mot> l_mot = new ArrayList<Mot>();
        Cursor c = database.query(MySQLiteHelper.TABLE_MOT, allColumnsMot, MySQLiteHelper.COLUMN_MARQUEURID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            l_mot.add(cursorToMot(c));
            c.moveToNext();
        }
        c.close();
        return l_mot;
    }

    /**
     * knowing an mot id we test if this Mot exists
     *
     * @param id mot id
     * @return boolean says if the Mot with this id exists or not
     */
    public boolean existMotWithId(long id) {
        boolean res;
        Cursor c = database.query(MySQLiteHelper.TABLE_MOT, allColumnsMot, MySQLiteHelper.COLUMN_ID + " = \"" + id + "\"", null, null, null, null);
        if (c.getCount() > 0) {
            c.close();
            res = true;
        } else {
            c.close();
            res = false;
        }
        return res;
    }


    /**
     * deleting a Mot
     *
     * @param m1 is the Mot we want to delete
     */
    public void deleteMot(Mot m1) {
        long id = m1.getMot_id();
        System.out.println("Mot deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_MOT, MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * deleting all Mot
     */
    public void clearMot() {
        System.out.println("Mot cleared");
        database.execSQL("DROP TABLE IF EXISTS Mot");
        database.execSQL(MySQLiteHelper.getDatabaseCreate5());
    }

    /**
     * convert a cursor to a Mot
     *
     * @param cursor
     * @return Mot
     */
    private Mot cursorToMot(Cursor cursor) {
        Mot m1 = new Mot();
        m1.setMot_id(cursor.getLong(0));
        m1.setMot_libelle(cursor.getString(1));
        m1.setMarqueur_id(cursor.getLong(2));
        return m1;
    }

    //----------------------------------- ROSE_AMBIANCE METHODES ------------------------------------------

    /**
     * creation a new RoseAmbiance in the database
     *
     * @param o           olfactory
     * @param v           visual/lighting
     * @param t           thermal
     * @param a           acoustical
     * @param marqueur_id
     * @return RoseAmbiance is the created RoseAmbiance
     */
    public RoseAmbiance createRoseAmbiance(float o, float v, float t, float a, long marqueur_id) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_OLFACTORY, o);
        values.put(MySQLiteHelper.COLUMN_VISUAL, v);
        values.put(MySQLiteHelper.COLUMN_THERMAL, t);
        values.put(MySQLiteHelper.COLUMN_ACOUSTICAL, a);
        values.put(MySQLiteHelper.COLUMN_MARQUEURID, marqueur_id);
        long insertId = database.insert(MySQLiteHelper.TABLE_ROSEAMBIANCE, null, values);
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_ROSEAMBIANCE,
                allColumnsRoseAmbiance,
                MySQLiteHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        RoseAmbiance newRoseAmbiance = cursorToRoseAmbiance(cursor);//method at the end of the class
        cursor.close();
        return newRoseAmbiance;
    }

    /**
     * update a RoseAmbiance
     *
     * @return RoseAmbiance updated
     */
    public RoseAmbiance updateRoseAmbiance(RoseAmbiance rose, float o, float v, float t, float a, long marqueur_id) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MARQUEURID, marqueur_id);
        values.put(MySQLiteHelper.COLUMN_OLFACTORY, o);
        values.put(MySQLiteHelper.COLUMN_VISUAL, v);
        values.put(MySQLiteHelper.COLUMN_THERMAL, t);
        values.put(MySQLiteHelper.COLUMN_ACOUSTICAL, a);

        database.update(MySQLiteHelper.TABLE_ROSEAMBIANCE, values, MySQLiteHelper.COLUMN_ID + " = " + rose.getRoseAmbiance_id(), null);
        return getRoseAmbianceWithId(rose.getRoseAmbiance_id());
    }

    /**
     * knowing a RoseAmbiance_id, we want to get the rose itself
     *
     * @param id is the id of the RoseAmbiance we are looking for
     * @return m1 is the RoseAmbiance we were looking for
     */
    public RoseAmbiance getRoseAmbianceWithId(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_ROSEAMBIANCE, allColumnsRoseAmbiance, MySQLiteHelper.COLUMN_ID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        RoseAmbiance m1 = cursorToRoseAmbiance(c);
        c.close();
        return m1;
    }

    /**
     * knowing a Marqueur_id, we want to get the rose itself
     *
     * @param id is the id of the Marqueur we are looking for
     * @return m1 is the RoseAmbiance we were looking for
     */
    public RoseAmbiance getRoseAmbianceWithMarqueurId(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_ROSEAMBIANCE, allColumnsRoseAmbiance, MySQLiteHelper.COLUMN_MARQUEURID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        RoseAmbiance m1 = cursorToRoseAmbiance(c);
        c.close();
        return m1;
    }

    /**
     * knowing an rose id we test if this RoseAmbiance exists
     *
     * @param id rose id
     * @return boolean says if the RoseAmbiance with this id exists or not
     */
    public boolean existRoseAmbianceWithId(long id) {
        boolean res;
        Cursor c = database.query(MySQLiteHelper.TABLE_ROSEAMBIANCE, allColumnsRoseAmbiance, MySQLiteHelper.COLUMN_ID + " = \"" + id + "\"", null, null, null, null);
        if (c.getCount() > 0) {
            c.close();
            res = true;
        } else {
            c.close();
            res = false;
        }
        return res;
    }


    /**
     * deleting a RoseAmbiance
     *
     * @param r1 is the RoseAmbiance we want to delete
     */
    public void deleteRoseAmbiance(RoseAmbiance r1) {
        long id = r1.getRoseAmbiance_id();
        System.out.println("RoseAmbiance deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_ROSEAMBIANCE, MySQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * deleting all RoseAmbiance
     */
    public void clearRoseAmbiance() {
        System.out.println("RoseAmbiance cleared");
        database.execSQL("DROP TABLE IF EXISTS Rose_ambiance");
        database.execSQL(MySQLiteHelper.getDatabaseCreate3());
    }

    /**
     * convert a cursor to a RoseAmbiance
     *
     * @param cursor
     * @return RoseAmbiance
     */
    private RoseAmbiance cursorToRoseAmbiance(Cursor cursor) {
        RoseAmbiance r1 = new RoseAmbiance();
        r1.setRoseAmbiance_id(cursor.getLong(0));
        r1.setO(cursor.getFloat(1));
        r1.setV(cursor.getFloat(2));
        r1.setT(cursor.getFloat(3));
        r1.setA(cursor.getFloat(4));
        r1.setMarqueur_id(cursor.getLong(5));
        return r1;
    }

    // ---------------------------------------- AUTRES METHODES ----------------------------------------------------

    private static String GETHISTORIQUE =
            "SELECT " + MySQLiteHelper.COLUMN_LIEUNOM + ", " + MySQLiteHelper.COLUMN_ADRESSE + ", " + MySQLiteHelper.COLUMN_DATECREATION + ", " + MySQLiteHelper.TABLE_MARQUEUR + "." + MySQLiteHelper.COLUMN_ID +
                    " FROM " + MySQLiteHelper.TABLE_LIEU + " INNER JOIN " + MySQLiteHelper.TABLE_MARQUEUR +
                    " ON " + MySQLiteHelper.TABLE_LIEU + "." + MySQLiteHelper.COLUMN_ID + "=" + MySQLiteHelper.TABLE_MARQUEUR + "." + MySQLiteHelper.COLUMN_LIEUID + ";";
                    // WHERE USER_ID = ? ...

    /**
     * renvoie un cursor de l'historique des marqueurs contenant le nom du lieu, l'adresse et la date de saisie
     *
     * @return cursor
     */
    public Cursor getHistoriqueCursor() {
        Cursor c = database.rawQuery(GETHISTORIQUE, null);
        return c;
    }

    private static String GETALLMARKERMAP =
            "SELECT " + MySQLiteHelper.COLUMN_LIEUNOM + ", " + MySQLiteHelper.COLUMN_ADRESSE + ", " + MySQLiteHelper.COLUMN_LATITUDE +
                    ", " + MySQLiteHelper.COLUMN_LONGITUDE + ", " + MySQLiteHelper.TABLE_MARQUEUR + "." + MySQLiteHelper.COLUMN_ID +
                    " FROM " + MySQLiteHelper.TABLE_LIEU + " INNER JOIN " + MySQLiteHelper.TABLE_MARQUEUR +
                    " ON " + MySQLiteHelper.TABLE_LIEU + "." + MySQLiteHelper.COLUMN_ID + "=" + MySQLiteHelper.TABLE_MARQUEUR + "." + MySQLiteHelper.COLUMN_LIEUID + ";";

    /**
     * renvoie un cursor de l'ensemble des marqueurs contenant le nom du lieu, l'adresse, la latitude, la longitude et l'id
     * @return cursor
     */
    public Cursor getAllMarkerMap() {
        Cursor c = database.rawQuery(GETALLMARKERMAP, null);
        return c;
    }
}