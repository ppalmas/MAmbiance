package org.fasol.mambiance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;

import org.fasol.mambiance.db.Utilisateur;

import static org.fasol.mambiance.MainActivity.datasource;

/**
 * Created by Paola on 04/12/2017.
 * Classe gérant le profil d'un utilisateur (utilisateur enregistré en base locale)
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
            if (!(u.getDate_cree()==null)){
                datecreation.setText(DateFormat.format("dd/MM/yyyy - HH:mm:ss", u.getDate_cree()));
            }

        }



    }
    /**
     * Method to inflate the xml menu file
     * @param menu the menu
     * @return true if everything went good
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        //On sérialise le fichier menu.xml pour l'afficher dans la barre de menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
}
