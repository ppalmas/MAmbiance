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
 */

public class MarkerIconOverlay extends ItemizedIconOverlay<OverlayItem> {

    private Context mContext;
    private ArrayList<Long> mL_marqueurId;

    public MarkerIconOverlay(List<OverlayItem> pList, Drawable pDefaultMarker, Context pContext, ArrayList<Long> l_marqueurId) {
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
    }

    @Override
    protected boolean onSingleTapUpHelper(final int index, final OverlayItem item, final MapView mapView) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle(item.getTitle());
        dialog.setMessage(item.getSnippet());
        dialog.setPositiveButton("Details",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent markerDisplayIntent = new Intent(mContext, DisplayMarkerActivity.class);
                        markerDisplayIntent.putExtra("marqueur_id_select", mL_marqueurId.get(index));
                        mContext.startActivity(markerDisplayIntent);
                    }
                });
        dialog.show();
        return true;
    }

}
