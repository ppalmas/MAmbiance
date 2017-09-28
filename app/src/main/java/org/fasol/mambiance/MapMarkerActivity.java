package org.fasol.mambiance;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import static org.fasol.mambiance.MainActivity.datasource;

/**
 * Created by fasol on 18/11/16.
 */

public class MapMarkerActivity extends AppCompatActivity{

    /**
     * Vue pour la carte
     */
    private MapView mMapView;

    private IMapController mapController;

    private MyLocationNewOverlay mLocationOverlay;

    private ArrayList<OverlayItem> mMarkerOverlay;

    // private RotationGestureOverlay mRotationGestureOverlay;


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

        datasource.open();
        Cursor c = datasource.getAllMarkerMap();
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++) {
            l_marqueurId.add(c.getLong(4));
            mMarkerOverlay.add(new OverlayItem(c.getString(0), c.getString(1), new GeoPoint(c.getFloat(2), c.getFloat(3))));
            c.moveToNext();
        }
        c.close();
        datasource.close();

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

        Intent intent;

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

}
