package org.fasol.mambiance.db;

import android.content.ContentValues;

/**
 * Created by Paola on 27/11/2017.
 * Table PossedeNote
 */

public class PossedeNote extends DataObject {

    //Atributes
    /**
     * long id of the object possedeNote
     */
    private long note_id;
    /**
     * int value of the note
     */
    private int note_value;

    /**
     * long id of the marker linked
     */
    private long marqueur_id;

    /**
     * long id of the mot linked
     */
    private long mot_id;

    /**
     * Getter de l'id de PossedeNote
     * @return
     */
    public long getNote_id() {
        return note_id;
    }

    /**
     * Setter de l'id de PossedeNote
     * @param note_id
     */
    public void setNote_id(long note_id) {
        this.note_id = note_id;
    }
    /**
     * Getter de la valeur de PossedeNote
     * @return
     */
    public int getNote_value() {
        return note_value;
    }

    /**
     * Setter de la valeur de PossedeNote
     * @return
     */
    public void setNote_value(int note_value) {
        this.note_value = note_value;
    }

    /**
     * Getter de l'id du marqueur associé
     * @return
     */
    public long getMarqueur_id() {
        return marqueur_id;
    }

    /**
     * Setter de l'id du marqueur associé
     * @return
     */
    public void setMarqueur_id(long marqueur_id) {
        this.marqueur_id = marqueur_id;
    }

    /**
     * Getter de l'id du mot associé
     * @return
     */
    public long getMot_id() {
        return mot_id;
    }

    /**
     * Setter de l'id du mot associé
     * @return
     */
    public void setMot_id(long mot_id) {
        this.mot_id = mot_id;
    }

    /**
     * Getter du Maximum des id de PossedeNote
     * @return
     */
    public static String getGETMAXELEMENTID() {
        return GETMAXELEMENTID;
    }

//Abstract methods


    @Override
    public String toString() {
        return "PossedeNote{" +
                "note_id=" + note_id +
                ", note_value=" + note_value +
                ", marqueur_id=" + marqueur_id +
                ", mot_id=" + mot_id +
                '}';
    }

    @Override
    /*
    Method to save a datasource to a local server
     */
    public void saveToLocal(LocalDataSource datasource) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_NOTEVALUE, this.note_value);
        values.put(MySQLiteHelper.COLUMN_MOTID, this.mot_id);
        values.put(MySQLiteHelper.COLUMN_MARQUEURID, this.marqueur_id);

        if(this.registredInLocal){
            String str = "_id "+"="+this.note_id;
            datasource.getDatabase().update(MySQLiteHelper.TABLE_POSSEDENOTE, values, str, null);
        }
        else{
            long row_id = datasource.getDatabase().insert(MySQLiteHelper.TABLE_POSSEDENOTE, null, values);
            this.setNote_id(row_id);
            this.setRegistredInLocal(true);
        }
    }
    /**
     * query to get the biggest Element_id from local db
     *
     */
    private static final String
            GETMAXELEMENTID =
            "SELECT "+MySQLiteHelper.TABLE_POSSEDENOTE+"."+MySQLiteHelper.COLUMN_NOTEID+" FROM "
                    + MySQLiteHelper.TABLE_POSSEDENOTE
                    +" ORDER BY "+MySQLiteHelper.TABLE_POSSEDENOTE+"."+MySQLiteHelper.COLUMN_NOTEID
                    +" DESC LIMIT 1 ;"
            ;
}
