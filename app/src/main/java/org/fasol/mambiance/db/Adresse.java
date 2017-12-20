package org.fasol.mambiance.db;

import android.content.ContentValues;

/**
 * Created by Paola on 12/11/2017.
 * Classe de l'objet Adresse
 */

public class Adresse extends DataObject {
    //Atributes
    /**
     * long id of the object lieu
     */
    private long adresse_id;
    /**
     * float latitude and longitude of the place
     */
    private float latitude, longitude;
    /**
     * String name of the place
     */
    private String adresse_nom;
    /**
     * String numero the address of the place
     */
    private String numero;

    /**
     * String rue the address of the place
     */
    private String rue;

    /**
     * String ville the address of the place
     */
    private String ville;

    /**
     * String pays the address of the place
     */
    private String pays;

    /**
     * String codepostal the address of the place
     */
    private String codepostal;

    /**
     * String complement the address of the place
     */
    private String complement;

    /**
     * String geométrie of the place
     */
    private String geom;

    /**
     * Getter Adresse_id
     * @return id
     */
    public long getAdresse_id() {
        return adresse_id;
    }

    /**
     * Setter Adresse id
     * @param adresse_id
     */
    public void setAdresse_id(long adresse_id) {
        this.adresse_id = adresse_id;
    }

    /**
     * Getter Latitude
     * @return latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * Setter Latitude
     * @return latitude
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     * Getter Longitude
     * @return longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * Setter Longitude
     * @return longitude
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getNom() {
        return adresse_nom;
    }

    public void setNom(String adresse_nom) {
        this.adresse_nom = adresse_nom;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getCodepostal() {
        return codepostal;
    }

    public void setCodepostal(String codepostal) {
        this.codepostal = codepostal;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }

    @Override
    public String toString() {
        return "Adresse{" +
                "adresse_id=" + adresse_id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", nom='" + adresse_nom + '\'' +
                ", numero='" + numero + '\'' +
                ", rue='" + rue + '\'' +
                ", ville='" + ville + '\'' +
                ", pays='" + pays + '\'' +
                ", codepostal='" + codepostal + '\'' +
                ", complement='" + complement + '\'' +
                ", geom='" + geom + '\'' +
                '}';
    }

    @Override
    public void saveToLocal(LocalDataSource datasource) {
        ContentValues values = new ContentValues();


        values.put(MySQLiteHelper.COLUMN_NOM, this.adresse_nom);
        values.put(MySQLiteHelper.COLUMN_NUMERO, this.numero);
        values.put(MySQLiteHelper.COLUMN_RUE, this.rue);
        values.put(MySQLiteHelper.COLUMN_VILLE, this.ville);
        values.put(MySQLiteHelper.COLUMN_CODEPOSTAL, this.codepostal);
        values.put(MySQLiteHelper.COLUMN_PAYS, this.pays);
        values.put(MySQLiteHelper.COLUMN_COMPLEMENT, this.complement);
        values.put(MySQLiteHelper.COLUMN_LATITUDE, this.latitude);
        values.put(MySQLiteHelper.COLUMN_LONGITUDE, this.longitude);
        values.put(MySQLiteHelper.COLUMN_GEOM, this.geom);

        if(this.registredInLocal){
            String str = "_id "+"="+this.adresse_id;
            datasource.getDatabase().update(MySQLiteHelper.TABLE_ADRESSE, values, str, null);
        }
        else{
            long row_id = datasource.getDatabase().insert(MySQLiteHelper.TABLE_ADRESSE, null, values);
            this.setAdresse_id(row_id);
            this.setRegistredInLocal(true);
        }
    }
    /**
     * query to get the biggest Element_id from local db
     *
     */
    private static final String
            GETMAXELEMENTID =
            "SELECT "+MySQLiteHelper.TABLE_ADRESSE+"."+MySQLiteHelper.COLUMN_ADRESSEID+" FROM "
                    + MySQLiteHelper.TABLE_ADRESSE
                    +" ORDER BY "+MySQLiteHelper.TABLE_ADRESSE+"."+MySQLiteHelper.COLUMN_ADRESSEID
                    +" DESC LIMIT 1 ;"
            ;
}
