package org.fasol.mambiance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;

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
 * Created by Paola on 13/12/2017.
 */

public class MapMarkerActivityAll extends AppCompatActivity {


    private static final String TAG = MapMarkerActivityAll.class.getSimpleName();
    ;
    /**
     * Vue pour la carte
     */
    private MapView mMapView;

    private IMapController mapController;

    private MyLocationNewOverlay mLocationOverlay;

    private ArrayList<OverlayItem> mMarkerOverlay;

    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);

        org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.setCachePath(this.getFilesDir().getAbsolutePath());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        // Map
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapView.setMaxZoomLevel(19);
        mMapView.getController().setCenter(new GeoPoint(47.2156,-1.5554));

        mapController = mMapView.getController();
        mapController.setZoom(10);

        //The following code is to get the location of the user
        mLocationOverlay = new MyLocationNewOverlay(mMapView);
        this.mMapView.getOverlays().add(this.mLocationOverlay);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();

        //Affichage marqueurs enregistré dans la BDD
        mMarkerOverlay=new ArrayList<OverlayItem>();
        final ArrayList<Long> l_marqueurId = new ArrayList<Long>();

        //Requête serveur (GET)
        OkHttpClient client = new OkHttpClient();
        final ArrayList<HashMap<String, String>> markersList = new ArrayList<>();

        //Récupération des données sous format JSON
        //Création de la requête GET
        Request request = new Request.Builder()
                .url("http://95.85.32.82/mambiance/v1/marqueur/all")
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "12e8a85c14756db4cde7b6919c303377")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "cb17b186-7953-eba3-4f79-ac8e495b2550")
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
             *
             * @param call
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
                            JSONObject jsonObj = new JSONObject(response.body().string());
                            // Getting JSON Array node
                            JSONArray markers = jsonObj.getJSONArray("marqueurs");

                            // looping through All Contacts
                            for (int i = 0; i < markers.length(); i++) {
                                JSONObject c = markers.getJSONObject(i);

                                long id = c.getLong("marqueur_id");
                                String lat = c.getString("localisation_latitude");
                                String longi = c.getString("localisation_longitude");
                                float latitude = Float.parseFloat(lat);
                                float longitude = Float.parseFloat(longi);
                                String description = c.getString("marqueur_comment");
                                String nom = c.getString("adresse_nom");
                                String adresse = c.getString("adresse_numero") + c.getString("adresse_rue") + c.getString("adresse_ville");
                                String titre;
                                if (nom.isEmpty()||nom==null){
                                    titre = adresse;
                                } else {
                                    titre = nom;
                                }

                                HashMap<String, String> markersH = new HashMap<>();

                                markersList.add(markersH);
                                l_marqueurId.add(id);
                                mMarkerOverlay.add(new OverlayItem(String.valueOf(id), titre, description, new GeoPoint(latitude, longitude)));
                            }
                        } catch (final JSONException e) {
                            Log.e(TAG, "Json parsing error: " + e.getMessage());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Json parsing error: " + e.getMessage(),
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            });

                        }
                        //Affichage des marqueurs récupérés
                        MarkerIconOverlay mMarkerIconOverlay = new MarkerIconOverlay(mMarkerOverlay,
                                ContextCompat.getDrawable(getApplicationContext(),R.mipmap.ic_map_marker), MapMarkerActivityAll.this, l_marqueurId, 1);
                        MapMarkerActivityAll.this.mMapView.getOverlays().add(mMarkerIconOverlay);
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

        MapMarkerActivityAll.this.mMapView.invalidate();

        // Rotation gestures
        /*mRotationGestureOverlay = new RotationGestureOverlay(this, mMapView);
        mRotationGestureOverlay.setEnabled(true);
        mMapView.setMultiTouchControls(true);
        mMapView.getOverlays().add(this.mRotationGestureOverlay);*/
    }

    @Override
    public void onResume() {
        mLocationOverlay.enableMyLocation();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mLocationOverlay.disableMyLocation();
        super.onPause();
    }

    /**
     * Method to inflate the xml menu file
     * @param menu the menu
     * @return true if everything went good
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //On sérialise le fichier menu.xml pour l'afficher dans la barre de menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);

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

        Intent intent;

        switch (item.getItemId()) {
            case R.id.home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.allmarkers:
                intent = new Intent(this, MapMarkerActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}


