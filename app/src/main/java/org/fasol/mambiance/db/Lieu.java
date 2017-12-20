package org.fasol.mambiance.db;

import android.content.ContentValues;

/**
 * Classe de l'objet Lieu
 */

public class Lieu extends DataObject {


    //Atributes
    /**
     * long id of the object lieu
     */
    private long lieu_id;
    /**
     * long id of the address
     */
    private long address_id;
    /**
     * float latitude and longitude of the place
     */
    private float latitude, longitude;


    //Getters
    /**
     * getter for the Lieu_id
     * @return long lieu_id
     */
    public long getLieu_id() {
        return lieu_id;
    }
    /**
     * getter for the latitude
     * @return float latitude
     */
    public float getLatitude() {
        return latitude;
    }
    /**
     * getter for the longitude
     * @return float longitude
     */
    public float getLongitude() {
        return longitude;
    }



    //Setters
    /**
     * setter for the Lieu_id
     * @param  lieu_id
     */
    public void setLieu_id(long lieu_id) {
        this.lieu_id = lieu_id;
    }

    /**
     * setter for the latitude
     * @param  latitude
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
    /**
     * setter for the longitude
     * @param  longitude
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }


    public long getAddress_id() {
        return address_id;
    }

    public void setAddress_id(long address_id) {
        this.address_id = address_id;
    }
//Abstract methods


    @Override
    public String toString() {
        return "Lieu{" +
                "lieu_id=" + lieu_id +
                ", address_id=" + address_id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public void saveToLocal(LocalDataSource datasource) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_LATITUDE, this.latitude);
        values.put(MySQLiteHelper.COLUMN_LONGITUDE, this.longitude);
        values.put(MySQLiteHelper.COLUMN_ADRESSEID, this.address_id);

        if(this.registredInLocal){
            String str = "_id "+"="+this.lieu_id;
            datasource.getDatabase().update(MySQLiteHelper.TABLE_LIEU, values, str, null);
        }
        else{
            long row_id = datasource.getDatabase().insert(MySQLiteHelper.TABLE_LIEU, null, values);
            this.setLieu_id(row_id);
            this.setRegistredInLocal(true);
        }
    }
    /**
     * query to get the biggest Element_id from local db
     *
     */
    private static final String
            GETMAXELEMENTID =
            "SELECT "+MySQLiteHelper.TABLE_LIEU+"."+MySQLiteHelper.COLUMN_LIEUID+" FROM "
                    + MySQLiteHelper.TABLE_LIEU
                    +" ORDER BY "+MySQLiteHelper.TABLE_LIEU+"."+MySQLiteHelper.COLUMN_LIEUID
                    +" DESC LIMIT 1 ;"
            ;

}