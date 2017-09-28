package org.fasol.mambiance.db;

import android.content.ContentValues;


public class Curseur extends DataObject {


    //Atributes
    /**
     * long id of the object curseur
     */
    private long curseur_id;
    /**
     * String libelle of the cursor
     */
    private String curseur_libelle;
    /**
     * int value of the cursor
     */
    private int curseur_valeur;
    /**
     * long id of the marker linked
     */
    private long marqueur_id;


    //Getters
    /**
     * getter for the Curseur_id
     * @return long curseur_id
     */
    public long getCurseur_id() {
        return curseur_id;
    }
    /**
     * getter for the Curseur_libelle
     * @return long curseur_libelle
     */
    public String getCurseur_libelle() {
        return curseur_libelle;
    }
    /**
     * getter for the curseur_valeur
     * @return int curseur_valeur
     */
    public int getCurseur_valeur() { return curseur_valeur; }
    /**
     * getter for the marqueur_id
     * @return long marqueur_id
     */
    public long getMarqueur_id() {
        return marqueur_id;
    }


    //Setters
    /**
     * setter for the Curseur_id
     * @param  curseur_id
     */
    public void setCurseur_id(long curseur_id) {
        this.curseur_id = curseur_id;
    }
    /**
     * setter for the curseur_libelle
     * @param curseur_libelle
     */
    public void setCurseur_libelle(String curseur_libelle) { this.curseur_libelle = curseur_libelle; }
    /**
     * setter for the curseur_valeur
     * @param curseur_valeur
     */
    public void setCurseur_valeur(int curseur_valeur) { this.curseur_valeur = curseur_valeur; }
    /**
     * setter for the marqueur_id
     * @param  marqueur_id
     */
    public void setMarqueur_id(long marqueur_id) { this.marqueur_id = marqueur_id; }


    //Abstract methods

    @Override
    public String toString() {
        return "Curseur [curseur_id=" + curseur_id + ", curseur_libelle=" + curseur_libelle + ", curseur_valeur=" + curseur_valeur
                + ", marqueur_id=" + marqueur_id + "]";
    }


    @Override
    public void saveToLocal(LocalDataSource datasource) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_CURSEURLIBELLE, this.curseur_libelle);
        values.put(MySQLiteHelper.COLUMN_CURSEURVALEUR, this.curseur_valeur);
        values.put(MySQLiteHelper.COLUMN_MARQUEURID, this.marqueur_id);

        if(this.registredInLocal){
            String str = "_id "+"="+this.curseur_id;
            datasource.getDatabase().update(MySQLiteHelper.TABLE_CURSEUR, values, str, null);
        }
        else{
            long row_id = datasource.getDatabase().insert(MySQLiteHelper.TABLE_CURSEUR, null, values);
            this.setCurseur_id(row_id);
            this.setRegistredInLocal(true);
        }
    }
    /**
     * query to get the biggest Element_id from local db
     *
     */
    private static final String
            GETMAXELEMENTID =
            "SELECT "+MySQLiteHelper.TABLE_CURSEUR+"."+MySQLiteHelper.COLUMN_CURSEURID+" FROM "
                    + MySQLiteHelper.TABLE_CURSEUR
                    +" ORDER BY "+MySQLiteHelper.TABLE_CURSEUR+"."+MySQLiteHelper.COLUMN_CURSEURID
                    +" DESC LIMIT 1 ;"
            ;

}