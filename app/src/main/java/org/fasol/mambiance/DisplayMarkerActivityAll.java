package org.fasol.mambiance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.fasol.mambiance.db.PossedeNote;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.fasol.mambiance.MainActivity.datasource;

/**
 * Created by Paola on 18/12/2017.
 */

public class DisplayMarkerActivityAll extends AppCompatActivity {

    private static final String TAG = MapMarkerActivityAll.class.getSimpleName();

    // Champs à remplir
    private TextView site_name;
    private TextView description;
    private TextView date;
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
    private ImageView photo;

    // Objets liés au marqueur
    private ArrayList<PossedeNote> l_note;
    private org.fasol.mambiance.db.Image image;
    private org.fasol.mambiance.db.Lieu lieu;
    private org.fasol.mambiance.db.Adresse adresse_marqueur;
    private ArrayList<org.fasol.mambiance.db.Mot> l_mot;
    private org.fasol.mambiance.db.RoseAmbiance roseAmbiance;
    private org.fasol.mambiance.db.Marqueur marqueur;

    // Layout de la rose des ambiances
    private FrameLayout layout_rose;



    @Override
    /* Method onCreate
    Méthode appelée lors de la création de l'activité (displaymarker)
    * */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_marker_activity); //indique quel fichier layout utiliser

        // Ajout de l'élément graphique Rose
        // Prend en paramètre l'identifiant de la vue layout rose et renvoie la vue
        layout_rose = (FrameLayout)findViewById(R.id.frame_layout_rose_display);

        layout_rose.addView(new RoseSurfaceView(this,(SeekBar)findViewById(R.id.cursor_olfactory)
                ,(SeekBar)findViewById(R.id.cursor_thermal),(SeekBar)findViewById(R.id.cursor_visual)
                ,(SeekBar)findViewById(R.id.cursor_acoustical)));

        // Accès au différents objects
        site_name=(TextView) findViewById(R.id.site_name_display);
        description = (TextView) findViewById(R.id.description_display);
        date=(TextView)findViewById(R.id.date_display);

        caract1=(TextView)findViewById(R.id.caract1_display);
        caract2=(TextView)findViewById(R.id.caract2_display);
        caract3=(TextView)findViewById(R.id.caract3_display);

        cursor1=(SeekBar) findViewById(R.id.cursor1_display);
        cursor2=(SeekBar) findViewById(R.id.cursor2_display);
        cursor3=(SeekBar) findViewById(R.id.cursor3_display);

        cursor_acoustical=(SeekBar) findViewById(R.id.cursor_acoustical);
        cursor_thermal=(SeekBar) findViewById(R.id.cursor_thermal);
        cursor_olfactory=(SeekBar) findViewById(R.id.cursor_olfactory);
        cursor_visual=(SeekBar) findViewById(R.id.cursor_visual);

        photo=(ImageView)findViewById(R.id.photo_display);

        // Bloque les curseurs de la rose des ambiances
        cursor_acoustical.setEnabled(false);
        cursor_thermal.setEnabled(false);
        cursor_olfactory.setEnabled(false);
        cursor_visual.setEnabled(false);

        // Récupère l'id du marqueur sélectionné
        Bundle bundle = getIntent().getExtras();
        long marqueur_id = bundle.getLong("marqueur_id_select");


        // Récupère les objets liés au marqueur

        //Requête serveur (GET)
        OkHttpClient client = new OkHttpClient();
        final ArrayList<String> markerList = new ArrayList<>();

        //Récupération des données du marqueur sous format JSON
        //Création de la requête GET
        Request request = new Request.Builder()
                .url("http://95.85.32.82/mambiance/v1/marqueur/"+marqueur_id)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "12e8a85c14756db4cde7b6919c303377")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "88fe12b3-17fe-0a71-0b06-0c00945e56ad")
                .build();

        //Envoi requête
        client.newCall(request).enqueue(new Callback() {
            /**
             * Called when the request could not be executed due to cancellation, a connectivity problem or
             * timeout. Because networks can fail during an exchange, it is possible that the remote server
             * accepted the request before the failure.
             *
             * @param call
             * @param e
             */
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Problème serveur, l'envoi a échoué", Toast.LENGTH_LONG).show();
            }

            /**
             * Called when the HTTP response was successfully returned by the remote server. The callback may
             * proceed to read the response body with {@link Response#body}. The response is still live until
             * its response body is {@linkplain ResponseBody closed}. The recipient of the callback may
             * consume the response body on another thread.
             * <p>
             * <p>Note that transport-layer success (receiving a HTTP response code, headers and body) does
             * not necessarily indicate application-layer success: {@code response} may still indicate an
             * unhappy HTTP response code like 404 or 500.
             *  @param call
             * @param response
             */

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //le retour est effectué dans un thread différent, sinon l'application crashe
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    //Toast.makeText(mMapView.getContext(), response.toString(), Toast.LENGTH_LONG).show();
                    if (response != null) {
                        try {
                            //Parsing JSON
                            String s = response.body().string();
                            JSONObject jsonObj = new JSONObject(s);
                            // Getting JSON Array node

                                JSONObject c = jsonObj;
                                String descr = c.getString("marqueur_comment");
                                String nom = c.getString("adresse_nom");
                                String rose_a = c.getString("rose_a");
                                String rose_o = c.getString("rose_o");
                                String rose_t = c.getString("rose_t");
                                String rose_v = c.getString("rose_v");

                                markerList.add(descr);
                                markerList.add(nom);
                                markerList.add(rose_a);
                                markerList.add(rose_o);
                                markerList.add(rose_t);
                                markerList.add(rose_v);


                            } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        /**
                         * Modify the main activity while being in the second thread
                         */
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {


                                // Affichage des données dans les champs
                                site_name.setText(markerList.get(1));
                                description.setText(markerList.get(0));
                                date.setText(DateFormat.format("dd/MM/yyyy - HH:mm:ss",marqueur.getDate_creation()));


                                for (int i=2;i<6;i++) {
                                    if (markerList.get(i)=="null"){
                                        markerList.set(i, "0");
                                    }
                                }
                                cursor_acoustical.setProgress((int) ((Float.parseFloat(markerList.get(2)) + 1.f) * 4.f));
                                cursor_olfactory.setProgress((int) ((Float.parseFloat(markerList.get(3)) + 1.f) * 4.f));
                                cursor_thermal.setProgress((int) ((Float.parseFloat(markerList.get(4)) + 1.f) * 4.f));
                                cursor_visual.setProgress((int) ((Float.parseFloat(markerList.get(5)) + 1.f) * 4.f));

                            }
                        });


                    } else {
                        Log.e(TAG, "Couldn't get json from server.");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Couldn't get json from server. Check LogCat for possible errors!",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });

                    }
                }
            }
        });


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
}


