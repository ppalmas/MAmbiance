package org.fasol.mambiance;

import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Paola on 20/11/2017.
 */

public class UserNewActivity extends AppCompatActivity {

    private String nom;
    private String prenom;
    private String email;
    private String mdp;
    private String hashMdp;
    private String pseudo;

    private EditText u_nom;
    private EditText u_prenom;
    private EditText u_email;
    private EditText u_mdp;
    private EditText u_pseudo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
    }

    /**
     * Méthode appelée au clic de l'utilisateur sur "valider" le formulaire
     * d'inscription de l'utilisateur
     * Effectue une vérification des informations entrées
     * Géolocalise l'utilisateur et propose une adresse correspondante
     * Enregistre les informations dans la base de données
     */
    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {

        }
    };

}
