package org.fasol.mambiance.db;

import android.content.ContentValues;

public class Mot extends DataObject {


    //Atributes
    /**
     * long id of the object mot
     */
    private long mot_id;
    /**
     * String text of the word
     */
    private String mot_libelle;
    /**
     * long id of the marker linked
     */
    private long marqueur_id;


    //Getters
    /**
     * getter for the Mot_id
     * @return long mot_id
     */
    public long getMot_id() {
        return mot_id;
    }
    /**
     * getter for the mot_libelle
     * @return String mot_libelle
     */
    public String getMot_libelle() { return mot_libelle; }
    /**
     * getter for the marqueur_id
     * @return long marqueur_id
     */
    public long getMarqueur_id() {
        return marqueur_id;
    }


    //Setters
    /**
     * setter for the Mot_id
     * @param  mot_id
     */
    public void setMot_id(long mot_id) {
        this.mot_id = mot_id;
    }
    /**
     * setter for the mot_libelle
     * @param mot_libelle
     */
    public void setMot_libelle(String mot_libelle) { this.mot_libelle = mot_libelle; }
    /**
     * setter for the marqueur_id
     * @param  marqueur_id
     */
    public void setMarqueur_id(long marqueur_id) { this.marqueur_id = marqueur_id; }


    //Abstract methods

    @Override
    public String toString() {
        return "Mot [mot_id=" + mot_id + ", mot_libelle=" + mot_libelle + ", marqueur_id=" + marqueur_id + "]";
    }


    @Override
    public void saveToLocal(LocalDataSource datasource) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_MOTLIBELLE, this.mot_libelle);
        values.put(MySQLiteHelper.COLUMN_MARQUEURID, this.marqueur_id);

        if(this.registredInLocal){
            String str = "_id "+"="+this.mot_id;
            datasource.getDatabase().update(MySQLiteHelper.TABLE_MOT, values, str, null);
        }
        else{
            long row_id = datasource.getDatabase().insert(MySQLiteHelper.TABLE_MOT, null, values);
            this.setMot_id(row_id);
            this.setRegistredInLocal(true);
        }
    }
    /**
     * query to get the biggest Element_id from local db
     *
     */
    private static final String
            GETMAXELEMENTID =
            "SELECT "+MySQLiteHelper.TABLE_MOT+"."+MySQLiteHelper.COLUMN_MOTID+" FROM "
                    + MySQLiteHelper.TABLE_MOT
                    +" ORDER BY "+MySQLiteHelper.TABLE_MOT+"."+MySQLiteHelper.COLUMN_MOTID
                    +" DESC LIMIT 1 ;"
            ;

}