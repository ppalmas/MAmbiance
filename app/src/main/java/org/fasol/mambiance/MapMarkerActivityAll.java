package org.fasol.mambiance;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        ArrayList<Long> l_marqueurId = new ArrayList<Long>();

        OkHttpClient client = new OkHttpClient();
        ArrayList<HashMap<String, String>> markersList = new ArrayList<>();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Request request = new Request.Builder()
                .url("http://95.85.32.82/mambiance/v1/marqueur/all")
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "12e8a85c14756db4cde7b6919c303377")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "cb17b186-7953-eba3-4f79-ac8e495b2550")
                .build();

        try {
            Response response = client.newCall(request).execute();
            //La réponse est au format JSON
            if (response != null) {
                try {
                    //Parsing JSON

                    JSONObject jsonObj = new JSONObject(String.valueOf(response));
                    // Getting JSON Array node
                    JSONArray markers = jsonObj.getJSONArray("markers");

                    // looping through All Contacts
                    for (int i = 0; i < markers.length(); i++) {
                        JSONObject c = markers.getJSONObject(i);

                        long id = c.getLong("adresse_id");
                        long latitude = c.getLong("adresse_latitude");
                        long longitude = c.getLong("adresse_longitude");
                        String description = c.getString("marqueur_comment");
                        String nom = c.getString("adresse_nom");
                        String adresse = c.getString("adresse_numero") + c.getString("adresse_rue") + c.getString("adresse_ville");
                        String titre;
                        if (nom.isEmpty()||nom==null){
                            titre = adresse;
                        } else {
                            titre = nom;
                        }
                        // tmp hash map for single contact
                        HashMap<String, String> markersH = new HashMap<>();

                        // adding each child node to HashMap key => value
                   //     markersH.put("id", id);
                     //   markersH.put("latitude", latitude);
                       // markersH.put("longitude", longitude);

                        // adding marker to markers list
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*datasource.open();
        Cursor c = datasource.getAllMarkerMap();
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++) {
            l_marqueurId.add(c.getLong(4));
            mMarkerOverlay.add(new OverlayItem(c.getString(0), c.getString(1), new GeoPoint(c.getFloat(5), c.getFloat(6))));

            c.moveToNext();
        }
        c.close();
        datasource.close();*/

        MarkerIconOverlay mMarkerIconOverlay = new MarkerIconOverlay(mMarkerOverlay,
                ContextCompat.getDrawable(getApplicationContext(),R.mipmap.ic_map_marker), this, l_marqueurId);
        this.mMapView.getOverlays().add(mMarkerIconOverlay);
        mMapView.invalidate();

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


