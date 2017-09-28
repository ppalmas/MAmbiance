package org.fasol.mambiance.db;

import android.content.ContentValues;

public class RoseAmbiance extends DataObject {


    //Atributes
    /**
     * long id of the object rose_ambiance
     */
    private long rose_ambiance_id;
    /**
     * float olfactory level of the marker
     */
    private float o;
    /**
     * float visual/lighting level of the marker
     */
    private float v;
    /**
     * float thermal level of the marker
     */
    private float t;
    /**
     * float acoustical level of the marker
     */
    private float a;
    /**
     * long id of the marker linked
     */
    private long marqueur_id;


    //Getters
    /**
     * getter for the RoseAmbiance_id
     * @return long rose_ambiance_id
     */
    public long getRoseAmbiance_id() { return rose_ambiance_id; }
    /**
     * getter for the olfactory level
     * @return float o
     */
    public float getO() { return o; }
    /**
     * getter for the visual/lighting level
     * @return float v
     */
    public float getV() { return v; }
    /**
     * getter for the acoustical level
     * @return float a
     */
    public float getA() { return a; }
    /**
     * getter for the thermal level
     * @return float t
     */
    public float getT() { return t; }
    /**
     * getter for the marqueur_id
     * @return long marqueur_id
     */
    public long getMarqueur_id() {
        return marqueur_id;
    }


    //Setters
    /**
     * setter for the RoseAmbiance_id
     * @param  rose_ambiance_id
     */
    public void setRoseAmbiance_id(long rose_ambiance_id) {
        this.rose_ambiance_id = rose_ambiance_id;
    }
    /**
     * setter for the olfactory level
     * @param  o
     */
    public void setO(float o) {
        this.o = o;
    }
    /**
     * setter for the acoustical level
     * @param  a
     */
    public void setA(float a) {
        this.a = a;
    }
    /**
     * setter for the visual/lighting level
     * @param  v
     */
    public void setV(float v) {
        this.v = v;
    }
    /**
     * setter for the thermal level
     * @param  t
     */
    public void setT(float t) {
        this.t = t;
    }
    /**
     * setter for the marqueur_id
     * @param  marqueur_id
     */
    public void setMarqueur_id(long marqueur_id) {
        this.marqueur_id = marqueur_id;
    }


    //Abstract methods

    @Override
    public String toString() {
        return "RoseAmbiance [rose_ambiance_id=" + rose_ambiance_id + ", o=" + o + ", v=" + v
                + ", t=" + t + ", a=" + a + ", marqueur_id=" + marqueur_id + "]";
    }


    @Override
    public void saveToLocal(LocalDataSource datasource) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_OLFACTORY, this.o);
        values.put(MySQLiteHelper.COLUMN_ACOUSTICAL, this.a);
        values.put(MySQLiteHelper.COLUMN_VISUAL, this.v);
        values.put(MySQLiteHelper.COLUMN_THERMAL, this.t);
        values.put(MySQLiteHelper.COLUMN_MARQUEURID, this.marqueur_id);

        if(this.registredInLocal){
            String str = "_id "+"="+this.rose_ambiance_id;
            datasource.getDatabase().update(MySQLiteHelper.TABLE_ROSEAMBIANCE, values, str, null);
        }
        else{
            long row_id = datasource.getDatabase().insert(MySQLiteHelper.TABLE_ROSEAMBIANCE, null, values);
            this.setRoseAmbiance_id(row_id);
            this.setRegistredInLocal(true);
        }
    }
    /**
     * query to get the biggest Element_id from local db
     *
     */
    private static final String
            GETMAXELEMENTID =
            "SELECT "+MySQLiteHelper.TABLE_ROSEAMBIANCE+"."+MySQLiteHelper.COLUMN_ROSEID+" FROM "
                    + MySQLiteHelper.TABLE_ROSEAMBIANCE
                    +" ORDER BY "+MySQLiteHelper.TABLE_ROSEAMBIANCE+"."+MySQLiteHelper.COLUMN_ROSEID
                    +" DESC LIMIT 1 ;"
            ;

}