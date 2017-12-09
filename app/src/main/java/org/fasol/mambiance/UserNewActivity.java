package org.fasol.mambiance;

import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.nio.channels.DatagramChannel;
import java.util.ArrayList;

import static org.fasol.mambiance.MainActivity.datasource;

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
    private EditText u_mdp2;
    private EditText u_pseudo;

    private Button btn_save;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        u_nom = (EditText) findViewById(R.id.a_nom);
        u_prenom = (EditText) findViewById(R.id.a_prenom);
        u_email = (EditText) findViewById(R.id.a_mail);
        u_mdp = (EditText) findViewById(R.id.a_mdp);
        u_mdp2 = (EditText) findViewById(R.id.a_mdp2);
        u_pseudo = (EditText) findViewById(R.id.a_pseudo);

        btn_save = (Button) findViewById(R.id.button_save);

        btn_save.setOnClickListener(saveListener);
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
            if (isFormularyCompleted(u_pseudo, u_mdp, u_mdp2, u_email)){
                if (isMdpEqual(u_mdp, u_mdp2)){
                    nom = u_nom.getText().toString();
                    prenom = u_prenom.getText().toString();
                    email = u_email.getText().toString();
                    mdp = u_mdp.getText().toString();
                    pseudo = u_pseudo.getText().toString();
                    datasource.open();
                    datasource.createUtilisateur(nom, prenom, mdp, "254b34604b2f943b01d5e8f9df02fe27", pseudo, email, 0);
                    datasource.close();
                    finish();
                } else {
                    Toast.makeText(view.getContext(),"Attention, les mots de passe sont différents.",Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(view.getContext(),"Attention, veuillez remplir toutes les cases obligatoires (*).",Toast.LENGTH_LONG).show();
            }



        }
    };

    /**
     * Méthode vérifiant si l'utilisateur a complété des champs du formulaire
     * @return bool : booléen qui vaut true si des champs du formulaire sont remplis, false sinon
     */
    public boolean isFormularyCompleted(EditText pseudo, EditText mdp, EditText mdp2, EditText email){
        boolean bool = true;
        ArrayList<EditText> list = new ArrayList<>();

        list.add(pseudo);
        list.add(mdp);
        list.add(mdp2);
        list.add(email);
        int n = list.size();
        int i = 0;

        while ((i<n)&&(bool)){
            if (!(list.get(i).getText().toString().matches(""))) {
                bool = false;
            }
            i=i+1;
        }
        return !bool;
    }

    /**
     * Méthode permettant de vérifier si les deux mots entrés en paramètres sont égaux
     * @param mdp
     * @param mdp2
     * @return vrai si les mots sont identiques
     */
    public boolean isMdpEqual(EditText mdp, EditText mdp2){

        ArrayList<EditText> list = new ArrayList<>();

        String m = mdp.getText().toString();
        String m2 = mdp2.getText().toString();
        boolean b = m==m2;
        boolean b2 = m.equals(m2);
        return (m.equals(m2));
    }
}
