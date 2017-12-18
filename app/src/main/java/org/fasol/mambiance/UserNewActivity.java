package org.fasol.mambiance;

import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.fasol.mambiance.MainActivity.datasource;

/**
 * Created by Paola on 20/11/2017.
 */

public class UserNewActivity extends AppCompatActivity {

    private String nom;
    private String prenom;
    private String email;
    private String mdp;
    private String pseudo;

    private EditText u_nom;
    private EditText u_prenom;
    private EditText u_email;
    private EditText u_mdp;
    private EditText u_mdp2;
    private EditText u_pseudo;

    private Button btn_save;
    //Variable finale et statique utilisée pour vérifier qu'une adresse mail est valide
    public final static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );


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
     * Méthode permettant d'enregistrer l'utilisateur dans la base de données locale de l'appareil
     * Récupère nom, prénom, pseudo, email, mot de passe, associe une clé API, un statut
     */
    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            if (isFormularyCompleted(u_pseudo, u_mdp, u_mdp2, u_email)){
                if (isMdpLength(u_mdp, u_mdp2)) {
                    if (isMdpEqual(u_mdp, u_mdp2)) {
                        if (email_valid(u_email)) {
                            nom = u_nom.getText().toString();
                            prenom = u_prenom.getText().toString();
                            email = u_email.getText().toString();
                            mdp = u_mdp.getText().toString();
                            pseudo = u_pseudo.getText().toString();
                            datasource.open();
                            datasource.createUtilisateur(nom, prenom, mdp, "254b34604b2f943b01d5e8f9df02fe27", pseudo, email, 0);
                            datasource.close();
                            //Envoi au serveur distant
                            OkHttpClient client = new OkHttpClient();
                            //Création de la requête
                            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                            RequestBody body = RequestBody.create(mediaType, "pseudo=" + pseudo + "&password=" + mdp + "&email=" +
                                    email + "&nom=" + nom + "&prenom=" + prenom);
                            Request request = new Request.Builder()
                                    .url("http://95.85.32.82/mambiance/v1/register")
                                    .post(body)
                                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                    .addHeader("Cache-Control", "no-cache")
                                    .addHeader("Postman-Token", "3fa60859-6dd0-9e78-d6ac-d64aa9a65c99")
                                    .build();

                            //Envoi de la requête
                            client.newCall(request).enqueue(new Callback() {
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(view.getContext(), "Problème serveur, l'envoi a échoué", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    //le retour est effectué dans un thread différent
                                    final String text = response.body().string();
                                    System.out.println(text);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(view.getContext(), text, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });

                            finish();
                        } else {
                            Toast.makeText(view.getContext(), "Attention, veuillez entrer un email valide.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(view.getContext(), "Attention, les mots de passe sont différents.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(view.getContext(), "Veuillez entrer un mot de passe de 6 caractères minimum.", Toast.LENGTH_LONG).show();
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
            if (((list.get(i).getText().toString().matches("")))||(list.get(i).getText().toString()==null)) {
                bool = false;
            }
            i=i+1;
        }
        return bool;
    }



    /**
     * Méthode permettant de vérifier si l'email entré est syntaxiquement valide (something@something.something)
     * @param email edittext
     * @return booléen vrai si l'email est valide
     */
    public boolean email_valid (EditText email){
        return EMAIL_ADDRESS_PATTERN.matcher(email.getText().toString()).matches();

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
        return (m.equals(m2));
    }

    /**
     * Vérifie que la longueur du mot de passe est supérieure à 5 caractères.
     * @param mdp
     * @param mdp2
     * @return
     */
    public boolean isMdpLength (EditText mdp, EditText mdp2) {
        if (mdp.length() < 6) {
            return false;
        } else {
            return true;

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
