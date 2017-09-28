package org.fasol.mambiance;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.fasol.mambiance.db.Mot;

import java.util.ArrayList;
import java.util.Iterator;

import static org.fasol.mambiance.MainActivity.datasource;

/**
 * Created by fasol on 18/11/16.
 */


public class DisplayMarkerActivity extends AppCompatActivity {

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
    private ArrayList<org.fasol.mambiance.db.Curseur> l_curseur;
    private org.fasol.mambiance.db.Image image;
    private org.fasol.mambiance.db.Lieu lieu;
    private ArrayList<org.fasol.mambiance.db.Mot> l_mot;
    private org.fasol.mambiance.db.RoseAmbiance roseAmbiance;
    private org.fasol.mambiance.db.Marqueur marqueur;

    // Layout de la rose des ambiances
    private FrameLayout layout_rose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_marker_activity);

        // Ajout Rose
        layout_rose = (FrameLayout)findViewById(R.id.frame_layout_rose_display);

        layout_rose.addView(new RoseSurfaceView(this,(SeekBar)findViewById(R.id.cursor_olfactory)
                ,(SeekBar)findViewById(R.id.cursor_thermal),(SeekBar)findViewById(R.id.cursor_visual)
                ,(SeekBar)findViewById(R.id.cursor_acoustical)));

        // Accès au différents objects
        site_name=(TextView) findViewById(R.id.site_name_display);
        description=(TextView) findViewById(R.id.description_display);
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
        datasource.open();
        marqueur=datasource.getMarqueurWithId(marqueur_id);
        l_curseur=datasource.getCurseurWithMarqueurId(marqueur_id);
        image=datasource.getImageWithMarqueurId(marqueur_id);
        lieu=datasource.getLieuWithId(marqueur.getLieu_id());
        roseAmbiance=datasource.getRoseAmbianceWithMarqueurId(marqueur_id);
        l_mot=datasource.getMotWithMarqueurId(marqueur_id);
        datasource.close();

        // Affichage des données dans les champs
        site_name.setText(lieu.getLieu_nom());

        String description_text="";
        Iterator<Mot> it = l_mot.iterator();
        description_text+=it.next().getMot_libelle();
        while(it.hasNext()){
            description_text+=", "+it.next().getMot_libelle();
        }
        description.setText(description_text);

        date.setText(DateFormat.format("dd/MM/yyyy - HH:mm:ss",marqueur.getDate_creation()));

        caract1.setText(l_curseur.get(0).getCurseur_libelle());
        caract2.setText(l_curseur.get(1).getCurseur_libelle());
        caract3.setText(l_curseur.get(2).getCurseur_libelle());
        cursor1.setProgress(l_curseur.get(0).getCurseur_valeur());
        cursor2.setProgress(l_curseur.get(1).getCurseur_valeur());
        cursor3.setProgress(l_curseur.get(2).getCurseur_valeur());

        cursor_acoustical.setProgress((int)((roseAmbiance.getA()+1.f)*4.f));
        cursor_olfactory.setProgress((int)((roseAmbiance.getO()+1.f)*4.f));
        cursor_thermal.setProgress((int)((roseAmbiance.getT()+1.f)*4.f));
        cursor_visual.setProgress((int)((roseAmbiance.getV()+1.f)*4.f));

        //affichage de la photo récupérée à partir de son emplacement
        photo.setImageDrawable(Drawable.createFromPath(image.getImage_emp()));

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
