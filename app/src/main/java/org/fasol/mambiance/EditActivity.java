package org.fasol.mambiance;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import org.fasol.mambiance.db.Adresse;
import org.fasol.mambiance.db.Lieu;
import org.fasol.mambiance.db.Marqueur;
import org.fasol.mambiance.db.Mot;

import org.fasol.mambiance.db.RoseAmbiance;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.fasol.mambiance.MainActivity.datasource;

/**
 * Created by fasol on 18/11/16.
 */

public class EditActivity extends AppCompatActivity implements LocationListener {

    // liste des caractéristiques possibles
    public final static String[] l_caract = {"Cozy", "Palpitant", "Formel", "Accueillant", "Sécurisant", "Inspirant", "Intime", "Animé",
            "Luxueux", "Chill", "Personnel", "Romantique", "Ennuyeux", "Chaleureux", "Business", "Reposant"};

    private long user_id;
    // champs à remplir
    private EditText site_name;
    private EditText description;
    private TextView caract1;
    private TextView caract2;
    private TextView caract3;
    private SeekBar cursor1;
    private SeekBar cursor2;
    private SeekBar cursor3;
    private SeekBar cursor_acoustical;
    private SeekBar cursor_thermal;
    private SeekBar cursor_olfactory;
    private SeekBar cursor_visual;
    private Button btn_photo;
    private String photo_emp = null;
    private long address_id = -2;
    public String rue;
    private String numero;
    private String ville;
    private String code_postal;
    private String pays;
    private String complement;



    private ImageView mPhotoView; //thumbnail photo

    // bouton pour écrire dans la BDD
    private Button save;

    // layout contenant la rose des ambiances
    private FrameLayout layout_rose;

    // locationManager pour récupérer latitude et longitude
    private LocationManager locationManager;

    // current latitude, longitude and adress
    private float lat,lng;

    private String adresse;

    //Id des mots affichés à l'écran
    private long id1, id2, id3;
    // Store the result when asking the user for the location
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 0;


    // url to get all products list
    private static String url = "http://95.85.32.82/mambiance/v1/marqueur";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);



        datasource.open();
        //L'id du user vaut le premier id trouvé dans la base de données
        // Un seul utilisateur est enregistré en local sur l'appareil
        user_id = datasource.getUser0();

        // ajout de la rose des ambiances
        layout_rose = (FrameLayout) findViewById(R.id.frame_layout_rose);

        layout_rose.addView(new RoseSurfaceView(this, (SeekBar) findViewById(R.id.cursor_olfactory)
                , (SeekBar) findViewById(R.id.cursor_thermal), (SeekBar) findViewById(R.id.cursor_visual)
                , (SeekBar) findViewById(R.id.cursor_acoustical)));

        // liens vers les éléments de l'interface
        site_name = (EditText) findViewById(R.id.edit_site_name);
        description = (EditText) findViewById(R.id.edit_description);

        caract1 = (TextView) findViewById(R.id.caract1);
        caract2 = (TextView) findViewById(R.id.caract2);
        caract3 = (TextView) findViewById(R.id.caract3);

        cursor1 = (SeekBar) findViewById(R.id.cursor1);
        cursor2 = (SeekBar) findViewById(R.id.cursor2);
        cursor3 = (SeekBar) findViewById(R.id.cursor3);

        cursor_acoustical = (SeekBar) findViewById(R.id.cursor_acoustical);
        cursor_thermal = (SeekBar) findViewById(R.id.cursor_thermal);
        cursor_olfactory = (SeekBar) findViewById(R.id.cursor_olfactory);
        cursor_visual = (SeekBar) findViewById(R.id.cursor_visual);

        cursor_acoustical.setEnabled(true);
        cursor_thermal.setEnabled(true);
        cursor_olfactory.setEnabled(true);
        cursor_visual.setEnabled(true);

        btn_photo = (Button) findViewById(R.id.btn_photo);
        mPhotoView = (ImageView) findViewById(R.id.photo_thumbnail);
        save = (Button) findViewById(R.id.btn_save);

        datasource.open();
        ArrayList<Mot> caract_pos = datasource.getMotActif();
        datasource.close();
        //Si la liste de Mots activés dépasse 3 i.e. le nombre de mots affichables à l'écran,
        // on affiche 3 mots pris aléatoirement
        if (caract_pos.size()>3){

            Random rd = new Random();
            //Affichage du premier mot aléatoire
            // entier rand correspondant à un entier aléatoire < taille de la lise
            int rand = rd.nextInt(caract_pos.size());
            // Mot sélectionné aléatoirement
            Mot caract_sel = caract_pos.get(rand);
            id1 = caract_sel.getMot_id();
            // Affichage du libellé de ce mot
            caract1.setText(caract_sel.getMot_libelle());
            caract_pos.remove(rand);
            rand = rd.nextInt(caract_pos.size());
            caract_sel = caract_pos.get(rand);
            id2 = caract_sel.getMot_id();
            caract2.setText(caract_sel.getMot_libelle());
            caract_pos.remove(rand);
            rand = rd.nextInt(caract_pos.size());
            caract_sel = caract_pos.get(rand);
            id3 = caract_sel.getMot_id();
            caract3.setText(caract_sel.getMot_libelle());
            caract_pos.remove(rand);

        } else {
            //Sinon on affiche les 3 mots en supposant que l'admin en a défini au moins 3 actifs
            caract1.setText(caract_pos.get(0).getMot_libelle());
            caract2.setText(caract_pos.get(1).getMot_libelle());
            caract3.setText(caract_pos.get(2).getMot_libelle());
        }

        // ajout d'un clicklistener sur les boutons
        btn_photo.setOnClickListener(photoListener);
        save.setOnClickListener(saveListener);

        //Permissions
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
//TODO: gérer le problème d'arrêt lorsque l'appli arrive pas à prendre des coordonnées car par allumé etc.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(EditActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {

                } else {
                    ActivityCompat.requestPermissions(EditActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSIONS_REQUEST_COARSE_LOCATION);
                }
            } else {
                ActivityCompat.requestPermissions(EditActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_FINE_LOCATION);

            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);



        photo_emp ="";



    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                } else {
                    // permission denied
                }
                return;
            }
        }
    }

    /**
     * Méthode appelée au clic de l'utilisateur sur "valider" le formulaire de
     * saisie d'une ambiance
     * Effectue une vérification des informations entrées
     * Géolocalise l'utilisateur et propose une adresse correspondante
     * Enregistre les informations dans la base de données
     * Envoie les données vers le serveur distant (marqueur, rose, notes)
     */
    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {

            //Vérification du formulaire
            if(isFormularyNameCompleted()) {
                if (isFormularyPhotoCompleted()) {
                    if (isFormularyDescriptionCompleted()) {
                        List<Address> l_address = null;
                        //Si une connexion internet wifi ou non est détectée, on récupère la position
                        //Sinon, on demande l'adresse à l'utilisateur
                        if (isInternetOn()){
                            // récupère les adresses possibles de localisation
                            try {
                                Geocoder geocoder = new Geocoder(EditActivity.this);
                                l_address = geocoder.getFromLocation(EditActivity.this.lat, EditActivity.this.lng, 1);
                                if (l_address != null && !l_address.isEmpty()) {
                                    // affiche une fenêtre demandant de valider l'adresse calculée
                                    //Préparation du layout (fichier xml)
                                    LayoutInflater inflater = (LayoutInflater)
                                            EditActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final View custView = inflater.inflate(R.layout.popup_address, null);
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.MATCH_PARENT);
                                    //Dialog Builder
                                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(EditActivity.this);
                                    builderSingle.setView(custView);
                                    builderSingle.setTitle("Adresse calculée");
                                    builderSingle.setMessage(l_address.get(0).getAddressLine(0) + " " +
                                            l_address.get(0).getPostalCode() + " " + l_address.get(0).getLocality());

                                    // bouton recalculer
                                    builderSingle.setNegativeButton(
                                            "recalculer",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    // bouton valider l'adresse
                                    final List<Address> finalL_address = l_address;
                                    builderSingle.setPositiveButton(
                                            "valider",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    rue = ((EditText) custView.findViewById(R.id.a_rue)).getText().toString();
                                                    numero = ((EditText) custView.findViewById(R.id.a_numero)).getText().toString();
                                                    ville = ((EditText) custView.findViewById(R.id.a_ville)).getText().toString();
                                                    pays = ((EditText) custView.findViewById(R.id.a_pays)).getText().toString();
                                                    code_postal = ((EditText) custView.findViewById(R.id.a_codepostal)).getText().toString();
                                                    complement = ((EditText) custView.findViewById(R.id.a_complement)).getText().toString();

                                                    //Si le formulaire d'adresse est complété, on vérifie que tous les champs obligatoires sont remplis
                                                    //Sinon on affiche des messages d'erreurs
                                                    if (isFormularyCompleted(numero, rue, ville, pays)) {
                                                        if (isFormularyAllCompleted(numero, rue, ville, pays)) {

                                                            EditActivity.this.adresse = finalL_address.get(0).getAddressLine(0) + " " +
                                                                    finalL_address.get(0).getPostalCode() + " " + finalL_address.get(0).getLocality();

                                                            //Ouverture base de données (LocalDataSource)
                                                            datasource.open();
                                                            long id = datasource.getAdresseById(numero, rue, ville,
                                                                    pays, code_postal);

                                                            String geom = "POINT("+lat+", "+ lng + ")";
                                                            if (id>0) {
                                                                //On vérifie si l'adresse existe déjà, alors id vaut l'id de l'adresse trouvée
                                                                address_id = id;
                                                                Adresse adresse1 = datasource.getAdresseWithId(id);
                                                            } else {
                                                                //SInon, on ajoute l'adresse à la base de données
                                                                Adresse adresse1 = datasource.createAdresse(site_name.getText().toString(),
                                                                        numero, rue, ville,
                                                                        code_postal, pays, complement,
                                                                        lat, lng);
                                                                address_id = adresse1.getAdresse_id();


                                                            }
                                                            //Notes de la rose des ambiances
                                                            float rose_o = cursor_olfactory.getProgress() / 4.f - 1.f;
                                                            float rose_v = cursor_visual.getProgress() / 4.f - 1.f;
                                                            float rose_t = cursor_thermal.getProgress() / 4.f - 1.f;
                                                            float rose_a = cursor_acoustical.getProgress() / 4.f - 1.f;

                                                            //Création du lieu
                                                            Lieu lieu = datasource.createLieu(lat, lng, address_id);
                                                            long lieu_id = lieu.getLieu_id();

                                                            //Création du marqueur
                                                            Marqueur marqueur = datasource.createMarqueur(lieu_id, user_id, description.getText().toString());
                                                            long marqueur_id = marqueur.getMarqueur_id();
                                                            RoseAmbiance rose = datasource.createRoseAmbiance(rose_o, rose_v,
                                                                    rose_t, rose_a, marqueur.getMarqueur_id());
                                                            datasource.createImage(marqueur.getMarqueur_id(), photo_emp);
                                                            //Définition du marqueur au format json

                                                            //Ajout des notes dans la base de données
                                                            datasource.createPossedeNote(cursor1.getProgress(), marqueur.getMarqueur_id(), id1);
                                                            datasource.createPossedeNote(cursor2.getProgress(), marqueur.getMarqueur_id(), id2);
                                                            datasource.createPossedeNote(cursor3.getProgress(), marqueur.getMarqueur_id(), id3);

                                                            String mot1 = datasource.getMotWithId(id1).getMot_libelle();
                                                            String mot2 = datasource.getMotWithId(id2).getMot_libelle();
                                                            String mot3 = datasource.getMotWithId(id3).getMot_libelle();

                                                            datasource.close();

                                                            //Envoi au serveur distant
                                                            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                                                            RequestBody body_marqueur = RequestBody.create(mediaType, "adresse_numero=" + numero +
                                                                    "&adresse_nom=" + site_name.getText().toString() + "&adresse_rue=" +
                                                                    rue + "&adresse_ville=" + ville + "&adresse_codepostal=" + code_postal +
                                                                    "&adresse_pays=" + pays + "&adresse_complement=" + complement +
                                                                    "&adresse_latitude=" + String.valueOf(lat) + "&adresse_longitude="+
                                                                    String.valueOf(lng) + "&adresse_geom=" + geom +
                                                                    "&localisation_latitude=" + String.valueOf(lat)+ "&localisation_longitude="
                                                                    + String.valueOf(lng) + "&rose_o" + rose_o + "=&rose_t" + rose_t+
                                                                    "=&rose_v=" + rose_v + "&rose_a=" + rose_a);
                                                            //Création requête
                                                            Request request_marqueur = new Request.Builder()
                                                                    .url("http://95.85.32.82/mambiance/v1/marqueur")
                                                                    .post(body_marqueur)
                                                                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                                                    .addHeader("Authorization", "12e8a85c14756db4cde7b6919c303377")
                                                                    .addHeader("Cache-Control", "no-cache")
                                                                    .addHeader("Postman-Token", "9f68ca7f-7281-0344-2fca-9018c45d65e5")
                                                                    .build();
                                                            // On crée une note pour chaque mot et note entrée, pas de vérification si ça existe déjà
                                                            RequestBody body_note1 = RequestBody.create(mediaType,
                                                                    "marqueur_id="+ marqueur_id +"&mot_libelle="
                                                                            + mot1+"&note_value="+cursor1.getProgress());
                                                            Request request_note1 = new Request.Builder()
                                                                    .url("http://95.85.32.82/mambiance/v1/note")
                                                                    .post(body_note1)
                                                                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                                                    .addHeader("Authorization", "12e8a85c14756db4cde7b6919c303377")
                                                                    .addHeader("Cache-Control", "no-cache")
                                                                    .addHeader("Postman-Token", "9f68ca7f-7281-0344-2fca-9018c45d65e5")
                                                                    .build();
                                                            RequestBody body_note2 = RequestBody.create(mediaType,
                                                                    "marqueur_id="+ marqueur_id +"&mot_libelle="
                                                                            +mot2+"&note_value="+cursor2.getProgress());
                                                            Request request_note2 = new Request.Builder()
                                                                    .url("http://95.85.32.82/mambiance/v1/note")
                                                                    .post(body_note2)
                                                                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                                                    .addHeader("Authorization", "12e8a85c14756db4cde7b6919c303377")
                                                                    .addHeader("Cache-Control", "no-cache")
                                                                    .addHeader("Postman-Token", "9f68ca7f-7281-0344-2fca-9018c45d65e5")
                                                                    .build();
                                                            RequestBody body_note3 = RequestBody.create(mediaType,
                                                                    "marqueur_id="+ marqueur_id +"&mot_libelle="
                                                                            +mot3+"&note_value="+cursor3.getProgress());
                                                            Request request_note3 = new Request.Builder()
                                                                    .url("http://95.85.32.82/mambiance/v1/note")
                                                                    .post(body_note3)
                                                                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                                                    .addHeader("Authorization", "12e8a85c14756db4cde7b6919c303377")
                                                                    .addHeader("Cache-Control", "no-cache")
                                                                    .addHeader("Postman-Token", "9f68ca7f-7281-0344-2fca-9018c45d65e5")
                                                                    .build();



                                                            OkHttpClient okHttpClient = new OkHttpClient();
                                                            // appel au serveur de la requête pour créer un marqueur
                                                            okHttpClient.newCall(request_marqueur).enqueue(new Callback() {
                                                                /**
                                                                 * Méthode OnFailure du call
                                                                 * @param call
                                                                 * @param e
                                                                 */
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
                                                            okHttpClient.newCall(request_note1).enqueue(new Callback() {
                                                                /**
                                                                 * Méthode OnFailure du call
                                                                 * @param call
                                                                 * @param e
                                                                 */
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
                                                            okHttpClient.newCall(request_note2).enqueue(new Callback() {
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
                                                            okHttpClient.newCall(request_note3).enqueue(new Callback() {
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


                                                            dialog.dismiss();

                                                            Toast.makeText(view.getContext(), "Enregistrement de l'adresse entrée effectué !", Toast.LENGTH_LONG).show();

                                                            finish(); //pour finir l'activité, l'enlever, et revenir à l'activité d'avant
                                                        } else {
                                                            Toast.makeText(view.getContext(), "Veuillez remplir tous les champs * ou aucun pour valider l'adresse calculée.", Toast.LENGTH_LONG).show();
                                                        }
                                                    } else {

                                                        EditActivity.this.adresse = finalL_address.get(0).getAddressLine(0) + " " +
                                                                finalL_address.get(0).getPostalCode() + " " + finalL_address.get(0).getLocality();
                                                        //Récupération de l'adresse
                                                        String[] rue_split = finalL_address.get(0).getAddressLine(0).split(",");
                                                        rue = rue_split[0];
                                                        ville = finalL_address.get(0).getLocality();
                                                        code_postal = finalL_address.get(0).getPostalCode();
                                                        pays = finalL_address.get(0).getCountryName();
                                                        numero = finalL_address.get(0).getFeatureName();
                                                        //ouverture base de données
                                                        datasource.open();
                                                        //On vérifie si l'adresse existe déjà
                                                        long id = datasource.getAdresseById("", rue, ville,
                                                                code_postal, pays);
                                                        if (id>0) {
                                                            //alors id vaut l'id de l'adresse trouvée
                                                            address_id = id;
                                                        } else {
                                                            //Sinon, on ajoute l'adresse calculée à la base de données
                                                            Adresse adresse = datasource.createAdresse(site_name.getText().toString(),
                                                                    numero, rue, ville, code_postal, pays, "",
                                                                    lat, lng);
                                                            address_id = adresse.getAdresse_id();
                                                        }
                                                        //Création du lieu
                                                        Lieu lieu = datasource.createLieu(lat, lng, address_id);
                                                        long lieu_id = lieu.getLieu_id();
                                                        float rose_o = cursor_olfactory.getProgress() / 4.f - 1.f;
                                                        float rose_v = cursor_visual.getProgress() / 4.f - 1.f;
                                                        float rose_t = cursor_thermal.getProgress() / 4.f - 1.f;
                                                        float rose_a = cursor_acoustical.getProgress() / 4.f - 1.f;
                                                        //Création du marqueur
                                                        Marqueur marqueur = datasource.createMarqueur(lieu_id, user_id, description.getText().toString());
                                                        long marqueur_id = marqueur.getMarqueur_id();
                                                        datasource.createRoseAmbiance(rose_o, rose_v,
                                                                rose_t, rose_a, marqueur.getMarqueur_id());
                                                        datasource.createImage(marqueur.getMarqueur_id(), photo_emp);
                                                        String mot1 = datasource.getMotWithId(id1).getMot_libelle();
                                                        String mot2 = datasource.getMotWithId(id2).getMot_libelle();
                                                        String mot3 = datasource.getMotWithId(id3).getMot_libelle();
                                                        //Ajout des notes dans la base de données
                                                        datasource.createPossedeNote(cursor1.getProgress(), marqueur.getMarqueur_id(), id1);
                                                        datasource.createPossedeNote(cursor2.getProgress(), marqueur.getMarqueur_id(), id2);
                                                        datasource.createPossedeNote(cursor3.getProgress(), marqueur.getMarqueur_id(), id3);
                                                        datasource.close();

                                                        String geom = "POINT("+lat+", "+ lng + ")";
                                                        //Envoi au serveur distant
                                                        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                                                        RequestBody body_marqueur = RequestBody.create(mediaType, "adresse_numero=" + numero +
                                                                "&adresse_nom=" + site_name.getText().toString() + "&adresse_rue=" +
                                                                rue + "&adresse_ville=" + ville + "&adresse_codepostal=" + code_postal +
                                                                "&adresse_pays=" + pays + "&adresse_complement=" + complement +
                                                                "&adresse_latitude=" + String.valueOf(lat) + "&adresse_longitude="+
                                                                String.valueOf(lng) + "&adresse_geom=" + geom +
                                                                "&localisation_latitude=" + String.valueOf(lat)+ "&localisation_longitude="
                                                                + String.valueOf(lng) + "&rose_o" + rose_o + "=&rose_t" + rose_t+
                                                                "=&rose_v=" + rose_v + "&rose_a=" + rose_a);
                                                        Request request_marqueur = new Request.Builder()
                                                                .url("http://95.85.32.82/mambiance/v1/marqueur")
                                                                .post(body_marqueur)
                                                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                                                .addHeader("Authorization", "12e8a85c14756db4cde7b6919c303377")
                                                                .addHeader("Cache-Control", "no-cache")
                                                                .addHeader("Postman-Token", "9f68ca7f-7281-0344-2fca-9018c45d65e5")
                                                                .build();
                                                        // On crée une note pour chaque mot et note entrée, pas de vérification si ça existe déjà
                                                        RequestBody body_note1 = RequestBody.create(mediaType,
                                                                "marqueur_id="+ marqueur_id +"&mot_libelle="
                                                                        + mot1+"&note_value="+cursor1.getProgress());
                                                        Request request_note1 = new Request.Builder()
                                                                .url("http://95.85.32.82/mambiance/v1/note")
                                                                .post(body_note1)
                                                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                                                .addHeader("Authorization", "12e8a85c14756db4cde7b6919c303377")
                                                                .addHeader("Cache-Control", "no-cache")
                                                                .addHeader("Postman-Token", "9f68ca7f-7281-0344-2fca-9018c45d65e5")
                                                                .build();
                                                        RequestBody body_note2 = RequestBody.create(mediaType,
                                                                "marqueur_id="+ marqueur_id +"&mot_libelle="
                                                                        +mot2+"&note_value="+cursor2.getProgress());
                                                        Request request_note2 = new Request.Builder()
                                                                .url("http://95.85.32.82/mambiance/v1/note")
                                                                .post(body_note2)
                                                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                                                .addHeader("Authorization", "12e8a85c14756db4cde7b6919c303377")
                                                                .addHeader("Cache-Control", "no-cache")
                                                                .addHeader("Postman-Token", "9f68ca7f-7281-0344-2fca-9018c45d65e5")
                                                                .build();
                                                        RequestBody body_note3 = RequestBody.create(mediaType,
                                                                "marqueur_id="+ marqueur_id +"&mot_libelle="
                                                                        +mot3+"&note_value="+cursor3.getProgress());
                                                        Request request_note3 = new Request.Builder()
                                                                .url("http://95.85.32.82/mambiance/v1/note")
                                                                .post(body_note3)
                                                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                                                .addHeader("Authorization", "12e8a85c14756db4cde7b6919c303377")
                                                                .addHeader("Cache-Control", "no-cache")
                                                                .addHeader("Postman-Token", "9f68ca7f-7281-0344-2fca-9018c45d65e5")
                                                                .build();

                                                        OkHttpClient okHttpClient = new OkHttpClient();
                                                        // appel au serveur de la requête pour créer un marqueur
                                                        okHttpClient.newCall(request_marqueur).enqueue(new Callback() {
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
                                                        okHttpClient.newCall(request_note1).enqueue(new Callback() {
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
                                                        okHttpClient.newCall(request_note2).enqueue(new Callback() {
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
                                                        okHttpClient.newCall(request_note3).enqueue(new Callback() {
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

                                                        dialog.dismiss();

                                                        Toast.makeText(view.getContext(), "Enregistrement de l'adresse calculée effectué !", Toast.LENGTH_LONG).show();

                                                        finish(); //pour finir l'activité, l'enlever, et revenir à l'activité d'avant

                                                    }

                                                }
                                            });
                                    builderSingle.show();




                                } else {
                                    Toast.makeText(view.getContext(), "Impossible de trouver les coordonnées! Allumez votre GPS ?", Toast.LENGTH_LONG).show();
                                }


                            } catch (IOException e) {
                                e.printStackTrace();

                            }catch (NullPointerException e){
                                e.printStackTrace();
                                Toast.makeText(view.getContext(), "Veuillez allumer votre GPS", Toast.LENGTH_LONG).show();
                            }



                        } else {

                            Toast.makeText(view.getContext(), "Veuillez connecter votre appareil à internet.", Toast.LENGTH_LONG).show();
                        }



                    }else {
                        Toast.makeText(view.getContext(),"Veuillez ajouter une description.",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(view.getContext(), "Veuillez ajouter une photo.", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(view.getContext(),"Veuillez ajouter un titre pour l'ambiance.",Toast.LENGTH_LONG).show();
            }
        }
    };

    /**
     * Function isFormularyPhotoCompleted
     * @return boolean : yes if there is a photo
     */
    private boolean isFormularyPhotoCompleted(){
        boolean flag=(photo_emp!="");
        return flag;
    }

    /**
     * Function isFormularyNameCompleted
     * @return boolean flag : yes if there is a title/name for the location
     */
    private boolean isFormularyNameCompleted(){
        boolean flag = (!site_name.getText().toString().matches(""));
        return flag;
    }

    /**
     * Function isFormularyDescriptionCompleted
     * @return boolean flag : yes if there is a description
     */
    private boolean isFormularyDescriptionCompleted(){
        boolean flag = (!description.getText().toString().matches(""));
        return flag;
    }

    // Gestion photo
    static final int REQUEST_TAKE_PHOTO = 1;

    /**
     * Méthode appelée au clic de l'utilisateur sur le bouton "Photo"
     */
    private View.OnClickListener photoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Toast.makeText(view.getContext(),"Erreur pas de photo",Toast.LENGTH_LONG).show();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(EditActivity.this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }

        }
    };
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        photo_emp = image.getAbsolutePath();
        return image;
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

    /**
     * Method to handle the clicks on the items of the toolbar
     *
     * @param item the item
     * @return true if everything went good
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent ;

        switch (item.getItemId()) {
            case R.id.home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        datasource.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // si une photo a été prise, affichage du thumbnail
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Get the dimensions of the View
            int targetW = btn_photo.getWidth();
            int targetH = btn_photo.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(photo_emp, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(photo_emp, bmOptions);
            mPhotoView.setImageBitmap(bitmap);

            mPhotoView.setVisibility(android.view.View.VISIBLE);
            btn_photo.setVisibility(android.view.View.GONE);
        }
    }

    // LocationListener methods override
    @Override
    public void onLocationChanged(Location location) {
        lat=(float)location.getLatitude();
        lng=(float)location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("Latitude","status");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("Latitude","disable");
    }

    /**
     * Method to check if internet is available (and no portal !)
     * @return internet booléen, vaut vrai s'il y a une connexion, faux sinon
     */
    public final boolean isInternetOn() {
        ConnectivityManager con = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean mobile = false;
        try {
            mobile = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        } catch (NullPointerException e) {
            mobile = false;
        }
        boolean internet = wifi | mobile;
        return internet;
    }

    /**
     * Méthode vérifiant si l'utilisateur a complété des champs du formulaire
     * @return bool : booléen qui vaut true si des champs du formulaire sont remplis, false sinon
     */
    public boolean isFormularyCompleted(String numero, String rue, String ville, String pays){
        boolean bool = true;
        ArrayList<String> list = new ArrayList<>();

        list.add(numero);
        list.add(rue);
        list.add(ville);
        list.add(pays);
        int n = list.size();
        int i = 0;

        while ((i<n)&&(bool)){
            if (!(list.get(i).matches(""))) {
                bool = false;
            }
            i=i+1;
        }
        return !bool;
    }

    /**
     * Méthode permettant de vérifier si tous les champs du formulaire d'adresse ont été remplis
     * @return true si le formulaire est entièrement rempli, false sinon
     */
    public boolean isFormularyAllCompleted(String numero, String rue, String ville, String pays){

        boolean bool = true;
        ArrayList<String> list = new ArrayList<>();
        list.add(numero);
        list.add(rue);
        list.add(ville);
        list.add(pays);
        int n = list.size();
        int i = 0;
        while ((i<n)&&(bool)){
            if (list.get(i).matches("")) {
                bool = false;
            }
            i = i +1;
        }
        return bool;
    }

}

