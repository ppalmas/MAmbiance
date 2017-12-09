package org.fasol.mambiance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import org.fasol.mambiance.db.Utilisateur;

import static org.fasol.mambiance.MainActivity.datasource;

/**
 * Created by Paola on 04/12/2017.
 */

public class UserActivity extends AppCompatActivity{

    private EditText nom_user;
    private EditText prenom_user;
    private EditText pseudo;
    private EditText mail;
    private EditText datecreation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);

        nom_user = (EditText) findViewById(R.id.a_nom);
        prenom_user = (EditText) findViewById(R.id.a_prenom);
        pseudo = (EditText) findViewById(R.id.a_pseudo);
        mail = (EditText) findViewById(R.id.a_mail);
        datecreation = (EditText) findViewById(R.id.a_datecreation);

        //Récupération dans la base de données des informations de l'utilisateur
        datasource.open();
        long id = datasource.getUser0();
        //Si id vaut -2, alors aucun utilisateur n'est enregistré
        if (id>0){
            Utilisateur u = datasource.getUserWithId(id);
            // Ajout des informations de l'utilisateur
            nom_user.setText(u.getNom());
            prenom_user.setText(u.getPrenom());
            pseudo.setText(u.getPseudo());
            mail.setText(u.getEmail());
//            datecreation.setText(u.getDate_cree().toString());
        }



    }
}
