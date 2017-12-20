package org.fasol.mambiance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fasol on 02/12/16.
 * Classe permettant de gérer l'icône s'affichant sur la carte pour indiquer un lieu
 */

public class MarkerIconOverlay extends ItemizedIconOverlay<OverlayItem> {

    private Context mContext;
    private ArrayList<Long> mL_marqueurId;
    private int bool;//vaut 0 s'il s'agit de l'historique de l'appareil, 1 si c'est celui du serveur distant

    public MarkerIconOverlay(List<OverlayItem> pList, Drawable pDefaultMarker, Context pContext, ArrayList<Long> l_marqueurId, int b) {
        super(pList, pDefaultMarker, new OnItemGestureListener<OverlayItem>() {
            @Override public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                return false;
            }
            @Override public boolean onItemLongPress(final int index, final OverlayItem item) {
                return false;
            }
        } , pContext);
        mContext=pContext;
        mL_marqueurId=l_marqueurId;
        bool = b;
    }

    /**
     * Méthode permettant de rediriger l'utilisateur vers une popup d'informations du marqueur lorsqu'il clique sur l'icône
     * Si ensuite il clique sur "Détails", un nouvel écran apparaît (DisplayMarkerActivity)
     * @param index
     * @param item
     * @param mapView
     * @return
     */
    @Override
    protected boolean onSingleTapUpHelper(final int index, final OverlayItem item, final MapView mapView) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(item.getTitle());
        dialog.setMessage(item.getSnippet());
        dialog.setPositiveButton("Details",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Si le Booléen vaut 0, le marqueur est enregistré en local
                        if (bool==0){
                            Intent markerDisplayIntent = new Intent(mContext, DisplayMarkerActivity.class);
                            markerDisplayIntent.putExtra("marqueur_id_select", mL_marqueurId.get(index));
                            mContext.startActivity(markerDisplayIntent);
                            //Si le booléen vaut 1, l'utilisateur consulte la carte des marqueurs du serveur
                            //Il faut donc faire une requête serveur pour consulter les informations du marqueur
                        } else {
                            Intent markerDisplayIntent = new Intent(mContext, DisplayMarkerActivityAll.class);
                            markerDisplayIntent.putExtra("marqueur_id_select", mL_marqueurId.get(index));
                            mContext.startActivity(markerDisplayIntent);
                        }

                    }
                });
        dialog.show();
        return true;
    }

}
