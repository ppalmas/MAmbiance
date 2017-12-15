package org.fasol.mambiance.db;

import java.io.PipedOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import org.fasol.mambiance.MainActivity;
import org.slf4j.helpers.Util;

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
    private String[] allColumnsMarqueur = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_DATECREATION, MySQLiteHelper.COLUMN_LIEUID,
            MySQLiteHelper.COLUMN_DESCRIPTION, MySQLiteHelper.COLUMN_USERID};
    private String[] allColumnsImage = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_IMAGEEMP, MySQLiteHelper.COLUMN_MARQUEURID};
    private String[] allColumnsMot = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_MOTLIBELLE, MySQLiteHelper.COLUMN_MOTBOOLEAN};
    private String[] allColumnsLieu = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_LATITUDE, MySQLiteHelper.COLUMN_LONGITUDE, MySQLiteHelper.COLUMN_ADRESSEID};
    private String[] allColumnsRoseAmbiance = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_OLFACTORY, MySQLiteHelper.COLUMN_VISUAL, MySQLiteHelper.COLUMN_THERMAL, MySQLiteHelper.COLUMN_ACOUSTICAL, MySQLiteHelper.COLUMN_MARQUEURID};
    private String[] allColumnsAdresse = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_NOM, MySQLiteHelper.COLUMN_NUMERO,
            MySQLiteHelper.COLUMN_RUE, MySQLiteHelper.COLUMN_VILLE, MySQLiteHelper.COLUMN_PAYS, MySQLiteHelper.COLUMN_CODEPOSTAL,
            MySQLiteHelper.COLUMN_COMPLEMENT, MySQLiteHelper.COLUMN_ADRESSE_LATITUDE, MySQLiteHelper.COLUMN_ADRESSE_LONGITUDE,
            MySQLiteHelper.COLUMN_GEOM};
    private String[] allColumnsUtilisateur = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_USERNOM, MySQLiteHelper.COLUMN_USERPRENOM,
            MySQLiteHelper.COLUMN_MDP, MySQLiteHelper.COLUMN_EMAIL, MySQLiteHelper.COLUMN_PSEUDO, MySQLiteHelper.COLUMN_STATUT,
            MySQLiteHelper.COLUMN_CLEAPI, MySQLiteHelper.COLUMN_DATECREE};
    private String[] allColumnsPossedeNote = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_NOTEVALUE,
            MySQLiteHelper.COLUMN_MARQUEURID, MySQLiteHelper.COLUMN_MOTID} ;




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
     * @param user_id
     * @return Marqueur is the created Marqueur
     */
    public Marqueur createMarqueur(long lieu_id, long user_id, String description) {
        ContentValues values = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateMnt = new Date(System.currentTimeMillis());
        values.put(MySQLiteHelper.COLUMN_DATECREATION, dateFormat.format(dateMnt));
        values.put(MySQLiteHelper.COLUMN_LIEUID, lieu_id);
        values.put(MySQLiteHelper.COLUMN_DESCRIPTION, description);
        values.put(MySQLiteHelper.COLUMN_USERID, user_id);

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
     * Méthode permettant de récupérer tous les marqueurs entrés par un utilsateur
     * @param id de l'utilisateur
     * @return
     */
    public ArrayList<Marqueur> getMarqueurWithUserId(long id){
        ArrayList<Marqueur> l = new ArrayList<>();
        Cursor c = database.query(MySQLiteHelper.TABLE_MARQUEUR, allColumnsMarqueur, MySQLiteHelper.COLUMN_USERID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            l.add(cursorToMarqueur(c));
            c.moveToNext();
        }
        c.close();
        return l;

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
        marqueur.setDescription(cursor.getString(3));
        marqueur.setUser_id(cursor.getLong(4));
        return marqueur;
    }

    //----------------------------------- LIEU METHODES ------------------------------------------

    /**
     * creation a new Lieu in the database
     *

     * @param latitude  latitude of the place
     * @param longitude longitude of the place
     * @return Lieu is the created Lieu
     */
    public Lieu createLieu2(double latitude, double longitude) {
        ContentValues values = new ContentValues();
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
     * creation a new Lieu in the database
     *
     * @param id_ad id de l'adresse
     * @param latitude  latitude of the place
     * @param longitude longitude of the place
     * @return Lieu is the created Lieu
     */
    public Lieu createLieu(double latitude, double longitude, Long id_ad) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_LATITUDE, latitude);
        values.put(MySQLiteHelper.COLUMN_LONGITUDE, longitude);
        values.put(MySQLiteHelper.COLUMN_ADRESSEID, id_ad);
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
    public Lieu updateLieu(Lieu lieu, double latitude, double longitude, long adresse_id) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_LONGITUDE, longitude);
        values.put(MySQLiteHelper.COLUMN_LATITUDE, latitude);
        values.put(MySQLiteHelper.COLUMN_ADRESSEID, adresse_id);

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
        p1.setLatitude(cursor.getFloat(1));
        p1.setLongitude(cursor.getFloat(2));
        p1.setAddress_id(cursor.getLong(3));
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



    //----------------------------------- MOT METHODES ------------------------------------------

    /**
     * creation a new Mot in the database
     *
     * @param mot_lib
     * @param actif
     * @return Mot is the created Mot
     */
    public Mot createMot(String mot_lib, boolean actif) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MOTBOOLEAN, actif);
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
     * Méthode permettant de renvoyer la liste des mots "actifs" i.e. les mots
     * dont le booléen vaut 1 (vrai) i.e. les mots affichés dans l'interface
     * @return
     */
    public ArrayList<Mot> getMotActif (){
        ArrayList<Mot> l_mot = new ArrayList<Mot>();
        Cursor c = database.query(MySQLiteHelper.TABLE_MOT, allColumnsMot, MySQLiteHelper.COLUMN_MOTBOOLEAN + " = \"" + 1 + "\"", null, null, null, null);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            l_mot.add(cursorToMot(c));
            c.moveToNext();
        }
        c.close();
        return l_mot;
    }

    /**
     * update a Mot
     *
     * @return Mot updated
     */
    public Mot updateMot(Mot mot, String mot_lib, boolean actif) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MOTBOOLEAN, actif);
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
        m1.setActif(cursor.getInt(2));
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

    // --------------------------------- METHODES pour ADRESSE ------------------------------------------
    /**
     * creating a new Adresse in the database
     *
     * @return Adresse is the created Adresse
     */
    public Adresse createAdresse(String nom, String numero, String rue, String ville, String codepostal, String pays, String complement, double latitude, double longitude) {
        ContentValues values = new ContentValues();


        values.put(MySQLiteHelper.COLUMN_NOM, nom);
        values.put(MySQLiteHelper.COLUMN_NUMERO, numero);
        values.put(MySQLiteHelper.COLUMN_RUE, rue);
        values.put(MySQLiteHelper.COLUMN_VILLE, ville);
        values.put(MySQLiteHelper.COLUMN_PAYS, pays);
        values.put(MySQLiteHelper.COLUMN_CODEPOSTAL, codepostal);
        values.put(MySQLiteHelper.COLUMN_COMPLEMENT, complement);
        values.put(MySQLiteHelper.COLUMN_ADRESSE_LATITUDE, latitude);
        values.put(MySQLiteHelper.COLUMN_ADRESSE_LONGITUDE, longitude);
        //values.put(MySQLiteHelper.COLUMN_GEOM, "POINT(" + latitude + ", " + longitude + ")");
        values.put(MySQLiteHelper.COLUMN_GEOM, longitude);

        long insertId = database.insert(MySQLiteHelper.TABLE_ADRESSE, null, values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_ADRESSE,
                allColumnsAdresse, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);

        cursor.moveToFirst();
        Adresse newAdresse = cursorToAdresse(cursor);
        cursor.close();
        return newAdresse;
    }

    /**
     * knowing a Adresse_id, we want to get the adresse itself
     *
     * @param id is the id of the Adresse we are looking for
     * @return l1 is the Adresse we were looking for
     */
    public Adresse getAdresseWithId(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_ADRESSE, allColumnsAdresse, MySQLiteHelper.COLUMN_ID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        Adresse l1 = cursorToAdresse(c);
        c.close();
        return l1;
    }

    /**
     * convert a cursor to an Adresse
     *
     * @param cursor
     * @return Adresse
     */
    private Adresse cursorToAdresse(Cursor cursor) {
        Adresse p1 = new Adresse();
        p1.setAdresse_id(cursor.getLong(0));
        p1.setNom(cursor.getString(1));
        p1.setNumero(cursor.getString(2));
        p1.setRue(cursor.getString(3));
        p1.setVille(cursor.getString(4));
        p1.setCodepostal(cursor.getString(5));
        p1.setPays(cursor.getString(6));
        p1.setComplement(cursor.getString(7));
        p1.setLatitude(cursor.getFloat(8));
        p1.setLongitude(cursor.getFloat(9));
        p1.setGeom(cursor.getString(10));
        return p1;
    }

    /**
     * Méthode permettant de chercher une adresse dans la base de données et de renvoyer son id
     * @param code_pos
     * @param numero
     * @param pays
     * @param rue
     * @param ville
     * @return id de l'adresse entrée en paramètre
     */

    public long getAdresseById(String numero, String rue, String ville, String pays, String code_pos){

        //Par défaut l'id vaut -2. Si au sortir de la méthode, l'id vaut -2, alors aucune adresse
        // ne correspond dans la base de données
        long id = -2;
        ArrayList<Adresse> l_adr = new ArrayList<Adresse>();
        Cursor c = database.rawQuery("SELECT _id FROM " + MySQLiteHelper.TABLE_ADRESSE + " WHERE ("
                + MySQLiteHelper.TABLE_ADRESSE + "." + MySQLiteHelper.COLUMN_RUE + " = '" + numero + "" + rue + "' AND " +
                MySQLiteHelper.TABLE_ADRESSE + "." + MySQLiteHelper.COLUMN_VILLE + " = '" + ville + "' AND " +
                MySQLiteHelper.TABLE_ADRESSE + "." + MySQLiteHelper.COLUMN_PAYS + " = '" + pays + "')", new String[]{});
        c.moveToFirst();
        if (c.getCount() != 0) {
            for (int i = 0; i < c.getCount(); i++) {
                l_adr.add(cursorToAdresse(c));
                c.moveToNext();
            }
            c.close();
            id = l_adr.get(0).getAdresse_id();
        } else {
            c = database.rawQuery("SELECT _id FROM " + MySQLiteHelper.TABLE_ADRESSE + " WHERE (" +
                    MySQLiteHelper.TABLE_ADRESSE + "." + MySQLiteHelper.COLUMN_NUMERO + " = '" + numero + "' AND " +
                    MySQLiteHelper.TABLE_ADRESSE + "." + MySQLiteHelper.COLUMN_RUE + " = '" + rue + "' AND " +
                    MySQLiteHelper.TABLE_ADRESSE + "." + MySQLiteHelper.COLUMN_VILLE + " = '" + ville + "' AND " +
                            MySQLiteHelper.TABLE_ADRESSE + "." + MySQLiteHelper.COLUMN_PAYS + " = '" + pays + "')", new String[]{});
            c.moveToFirst();
            if (c.getCount() != 0) {
                for (int i = 0; i < c.getCount(); i++) {
                    l_adr.add(cursorToAdresse(c));
                    c.moveToNext();
                }
                c.close();
                id = l_adr.get(0).getAdresse_id();
            }
        }

        return id;
    }

    // --------------------------------- METHODES pour UTILISATEUR ------------------------------------------
    /**
     * creating a new Utilisateur in the database
     *
     * @return User is the created Utilisateur
     */
    public Utilisateur createUtilisateur(String nom, String prenom, String mdp, String cleapi, String pseudo, String email, int statut) {
        ContentValues values = new ContentValues();


        values.put(MySQLiteHelper.COLUMN_USERNOM, nom);
        values.put(MySQLiteHelper.COLUMN_USERPRENOM, prenom);
        values.put(MySQLiteHelper.COLUMN_MDP, mdp);
        values.put(MySQLiteHelper.COLUMN_EMAIL, email);
        values.put(MySQLiteHelper.COLUMN_PSEUDO, pseudo);
        values.put(MySQLiteHelper.COLUMN_STATUT, statut);
        values.put(MySQLiteHelper.COLUMN_CLEAPI, cleapi);
//TODO date ne fonctionne pas


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateMnt = new Date(System.currentTimeMillis());
        values.put(MySQLiteHelper.COLUMN_DATECREE, dateFormat.format(dateMnt));

        long insertId = database.insert(MySQLiteHelper.TABLE_UTILISATEUR, null, values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_UTILISATEUR,
                allColumnsUtilisateur, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();

        Utilisateur newUser = cursorToUtilisateur(cursor);
        cursor.close();
        return newUser;
    }

    /**
     * convert a cursor to an Utilisateur
     *
     * @param cursor
     * @return Utilisateur
     */
    private Utilisateur cursorToUtilisateur(Cursor cursor) {
        Utilisateur p1 = new Utilisateur();
        p1.setUser_id(cursor.getLong(0));
        p1.setNom(cursor.getString(1));
        p1.setPrenom(cursor.getString(2));
        p1.setMdp(cursor.getString(3));
        p1.setEmail(cursor.getString(4));
        p1.setPseudo(cursor.getString(5));
        p1.setStatut(cursor.getInt(6));
        p1.setCleapi(cursor.getString(7));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date dateCreation = null;
        try {
            dateCreation = dateFormat.parse(cursor.getString(1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        p1.setDate_cree(dateCreation);
        return p1;
    }

    /**
     * Méthode permettant de retourner l'id du premier utilisateur présent dans la base de données, s'il existe
     * Sinon, l'id vaut 2
     * @return id d'un utilisateur
     */
    public long getUser0(){
        long id = -2;
        ArrayList<Utilisateur> l_user = new ArrayList<Utilisateur>();
        Cursor c = database.query(MySQLiteHelper.TABLE_UTILISATEUR,
                allColumnsUtilisateur, null, null,
                null, null, null);
        c.moveToFirst();
        if (c.getCount()>0){
            l_user.add(cursorToUtilisateur(c));
            id = l_user.get(0).getUser_id();
        }
        c.close();

        return id;
    }

    /**
     * Méthode permettant de retourner un Utilisateur dont l'id est en paramètre
     * @param id
     * @return
     */
    public Utilisateur getUserWithId(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_UTILISATEUR, allColumnsUtilisateur, MySQLiteHelper.COLUMN_ID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        Utilisateur U1 = cursorToUtilisateur(c);
        c.close();
        return U1;
    }

    //----------------------------------- POSSEDENOTE METHODES ------------------------------------------

    /**
     * creation a new PossedeNote in the database
     *
     * @param mot_id
     * @param note_val
     * @param marqueur_id
     * @return Curseur is the created Curseur
     */
    public PossedeNote createPossedeNote(int note_val, long marqueur_id, long mot_id) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MARQUEURID, marqueur_id);
        values.put(MySQLiteHelper.COLUMN_NOTEVALUE, note_val);
        values.put(MySQLiteHelper.COLUMN_MOTID, mot_id);
        long insertId = database.insert(MySQLiteHelper.TABLE_POSSEDENOTE, null, values);
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_POSSEDENOTE,
                allColumnsPossedeNote,
                MySQLiteHelper.COLUMN_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        PossedeNote newNote = cursorToNote(cursor);//method at the end of the class
        cursor.close();
        return newNote;
    }


    /**
     * knowing a Note_id, we want to get the PossedeNote itself
     *
     * @param id is the id of the Note we are looking for
     * @return c1 is the Note we were looking for
     */
    public PossedeNote getNoteWithId(long id) {
        Cursor c = database.query(MySQLiteHelper.TABLE_POSSEDENOTE, allColumnsPossedeNote, MySQLiteHelper.COLUMN_ID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        PossedeNote c1 = cursorToNote(c);
        c.close();
        return c1;
    }

    /**
     * knowing a Marqueur_id, we want to get the mot itself
     *
     * @param id is the id of the Marqueur we are looking for
     * @return m1 is the Mot we were looking for
     */
    public ArrayList<PossedeNote> getPossedeNoteWithMarqueurId(long id) {
        ArrayList<PossedeNote> l_mot = new ArrayList<PossedeNote>();
        Cursor c = database.query(MySQLiteHelper.TABLE_POSSEDENOTE, allColumnsPossedeNote, MySQLiteHelper.COLUMN_MARQUEURID + " = \"" + id + "\"", null, null, null, null);
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            l_mot.add(cursorToNote(c));
            c.moveToNext();
        }
        c.close();
        return l_mot;
    }





    /**
     * convert a cursor to a Curseur
     *
     * @param cursor
     * @return PossedeNote
     */
    private PossedeNote cursorToNote(Cursor cursor) {
        PossedeNote c1 = new PossedeNote();
        c1.setNote_id(cursor.getLong(0));
        c1.setNote_value(cursor.getInt(1));
        c1.setMarqueur_id(cursor.getLong(2));
        c1.setMot_id(cursor.getLong(3));
        return c1;
    }


    // ---------------------------------------- AUTRES METHODES ----------------------------------------------------

    private static String GETHISTORIQUE =
            "SELECT " + MySQLiteHelper.COLUMN_NOM + ", " + MySQLiteHelper.COLUMN_NUMERO + ", " + MySQLiteHelper.COLUMN_RUE +
                    ", " + MySQLiteHelper.COLUMN_VILLE + ", " + MySQLiteHelper.COLUMN_PAYS + ", " +
                    MySQLiteHelper.COLUMN_DATECREATION + " ," + MySQLiteHelper.COLUMN_ADRESSEID + ", " + MySQLiteHelper.TABLE_MARQUEUR + "." + MySQLiteHelper.COLUMN_ID +
                    " FROM " + MySQLiteHelper.TABLE_ADRESSE + " INNER JOIN " + MySQLiteHelper.TABLE_LIEU + " INNER JOIN " + MySQLiteHelper.TABLE_MARQUEUR +
                    " ON " + MySQLiteHelper.TABLE_ADRESSE + "." + MySQLiteHelper.COLUMN_ID + "=" + MySQLiteHelper.TABLE_LIEU + "." + MySQLiteHelper.COLUMN_ADRESSEID +
                   " AND " + MySQLiteHelper.TABLE_LIEU + "." + MySQLiteHelper.COLUMN_ID + "=" + MySQLiteHelper.TABLE_MARQUEUR + "." + MySQLiteHelper.COLUMN_LIEUID + ";";
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
            "SELECT " + MySQLiteHelper.COLUMN_NOM+ ", " + MySQLiteHelper.COLUMN_NUMERO + ", " + MySQLiteHelper.COLUMN_RUE +
                    ", " + MySQLiteHelper.COLUMN_VILLE + ", " + MySQLiteHelper.COLUMN_PAYS + ", "
                    + MySQLiteHelper.COLUMN_LATITUDE + ", " + MySQLiteHelper.COLUMN_LONGITUDE + ", " +
                    MySQLiteHelper.TABLE_MARQUEUR + "." + MySQLiteHelper.COLUMN_ID + ", " + MySQLiteHelper.COLUMN_DESCRIPTION+
                    " FROM " + MySQLiteHelper.TABLE_ADRESSE + " INNER JOIN " + MySQLiteHelper.TABLE_LIEU + " INNER JOIN " +
                    MySQLiteHelper.TABLE_MARQUEUR +
                    " ON " + MySQLiteHelper.TABLE_ADRESSE + "." + MySQLiteHelper.COLUMN_ID + "=" + MySQLiteHelper.TABLE_LIEU + "." + MySQLiteHelper.COLUMN_ADRESSEID + " AND " +
    MySQLiteHelper.TABLE_LIEU + "." + MySQLiteHelper.COLUMN_ID + "=" + MySQLiteHelper.TABLE_MARQUEUR + "." + MySQLiteHelper.COLUMN_LIEUID + ";";

    /**
     * renvoie un cursor de l'ensemble des marqueurs contenant le nom du lieu, l'adresse, la latitude, la longitude et l'id
     * @return cursor
     */
    public Cursor getAllMarkerMap() {
        Cursor c = database.rawQuery(GETALLMARKERMAP, null);
        return c;
    }
}