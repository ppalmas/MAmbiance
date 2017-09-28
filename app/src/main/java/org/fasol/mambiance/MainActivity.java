package org.fasol.mambiance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import org.fasol.mambiance.db.LocalDataSource;

public class MainActivity extends AppCompatActivity {

    private ImageButton btn_user=null, btn_history=null, btn_map=null, btn_info=null, btn_edit=null;

    public static LocalDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datasource = new LocalDataSource(this);
        datasource.open();

        // drop tables et test ajout dans la BDD
        /*datasource.clearLieu();
        datasource.clearMarqueur();
        datasource.clearCurseur();
        datasource.clearImage();
        datasource.clearMot();
        datasource.clearRoseAmbiance();

        /*Lieu lieu = datasource.createLieu("Parc à touristes","3 rue du trottoir 95000 PARIS",48.856667f,2.350833f);
        Marqueur marqueur = datasource.createMarqueur(lieu.getLieu_id());
        RoseAmbiance rose = datasource.createRoseAmbiance(.5f,-1.f,.25f,.0f,marqueur.getMarqueur_id());
        Image image = datasource.createImage(marqueur.getMarqueur_id(), "drawable://parc_photo");
        Mot mot = datasource.createMot("pouet",marqueur.getMarqueur_id());
        Curseur c1 = datasource.createCurseur("Cozy", 2, marqueur.getMarqueur_id());
        Curseur c2 = datasource.createCurseur("Palpitant", 5, marqueur.getMarqueur_id());
        Curseur c3 = datasource.createCurseur("Formel", 8, marqueur.getMarqueur_id());*/

        datasource.close();

        btn_user=(ImageButton)findViewById(R.id.btn_user);
        btn_edit=(ImageButton)findViewById(R.id.btn_edit);
        btn_history=(ImageButton)findViewById(R.id.btn_history);
        btn_map=(ImageButton)findViewById(R.id.btn_map);
        btn_info=(ImageButton)findViewById(R.id.btn_info);

        // Lien avec l'activité historique
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondeActiv = new Intent(MainActivity.this, HistoryActivity.class);

                startActivity(secondeActiv);
            }
        });

        // Lien avec l'activité saisie des marqueurs
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondeActiv = new Intent(MainActivity.this, EditActivity.class);

                startActivity(secondeActiv);
            }
        });

        // Lien avec l'activité carte
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondeActiv = new Intent(MainActivity.this, MapMarkerActivity.class);

                startActivity(secondeActiv);
            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent secondeActiv = new Intent(MainActivity.this, InfoActivity.class);

                startActivity(secondeActiv);
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
