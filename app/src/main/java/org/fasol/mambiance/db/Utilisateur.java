package org.fasol.mambiance.db;

import android.content.ContentValues;

import java.util.Date;

/**
 * Created by Paola on 25/11/2017.
 * Classe pour gérer l'objet Utilisateur
 */

public class Utilisateur extends DataObject {
//Atributes
    /**
     * id de l'utilisateur
     */
    private long user_id;
    /**
     * nom de l'utilisateur (facultatif à entrer)
     */
    private String nom;
    /**
     * prénom de l'utilisateur (facultatif à entrer)
     */
    private String prenom;
    /**
     * Mot de passe de l'utilisateur (pas enregistré en local actuellement)
     */
    private String mdp;
    /**
     * Clé API créée dans la base de données distante
     */
    private String cleapi;
    /**
     * Email de l'utilisateur (obligatoire)
     */
    private String email;
    /**
     * Date de création du compte utilisateur
     */
    private Date date_cree;
    /**
     * Pseudo de l'utilisateur
     */
    private String pseudo;
    /**
     * Statut de l'utilisateur (pour gérer la connexion avec base de données distante)
     */
    private int statut;

    /**
     * Getter user id
     * @return id
     */
    public long getUser_id() {
        return user_id;
    }

    /**
     * Setter user id
     * @param user_id
     */
    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    /**
     * Getter Nom de l'utilisateur
     * @return
     */
    public String getNom() {
        return nom;
    }

    /**
     * Setter nom
     * @param nom
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getCleapi() {
        return cleapi;
    }

    public void setCleapi(String cleapi) {
        this.cleapi = cleapi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDate_cree() {
        return date_cree;
    }

    public void setDate_cree(Date date_cree) {
        this.date_cree = date_cree;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public int getStatut() {
        return statut;
    }

    public void setStatut(int statut) {
        this.statut = statut;
    }

    public static String getGETMAXELEMENTID() {
        return GETMAXELEMENTID;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "user_id=" + user_id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", mdp='" + mdp + '\'' +
                ", cleapi='" + cleapi + '\'' +
                ", email='" + email + '\'' +
                ", date_cree=" + date_cree +
                ", pseudo='" + pseudo + '\'' +
                ", statut=" + statut +
                '}';
    }

    @Override
    public void saveToLocal(LocalDataSource datasource) {
        ContentValues values = new ContentValues();


        values.put(MySQLiteHelper.COLUMN_USERNOM, this.nom);
        values.put(MySQLiteHelper.COLUMN_USERPRENOM, this.prenom);
        values.put(MySQLiteHelper.COLUMN_MDP, this.mdp);
        values.put(MySQLiteHelper.COLUMN_EMAIL, this.email);
        values.put(MySQLiteHelper.COLUMN_PSEUDO, this.pseudo);
        values.put(MySQLiteHelper.COLUMN_STATUT, this.statut);
        values.put(MySQLiteHelper.COLUMN_CLEAPI, this.cleapi);
        values.put(MySQLiteHelper.COLUMN_DATECREE, this.date_cree.toString());

        if(this.registredInLocal){
            String str = "_id "+"="+this.user_id;
            datasource.getDatabase().update(MySQLiteHelper.TABLE_UTILISATEUR, values, str, null);
        }
        else{
            long row_id = datasource.getDatabase().insert(MySQLiteHelper.TABLE_UTILISATEUR, null, values);
            this.setUser_id(row_id);
            this.setRegistredInLocal(true);
        }
    }
    /**
     * query to get the biggest Element_id from local db
     *
     */
    private static final String
            GETMAXELEMENTID =
            "SELECT "+MySQLiteHelper.TABLE_UTILISATEUR+"."+MySQLiteHelper.COLUMN_USERID+" FROM "
                    + MySQLiteHelper.TABLE_UTILISATEUR
                    +" ORDER BY "+MySQLiteHelper.TABLE_UTILISATEUR+"."+MySQLiteHelper.COLUMN_USERID
                    +" DESC LIMIT 1 ;"
            ;

}
