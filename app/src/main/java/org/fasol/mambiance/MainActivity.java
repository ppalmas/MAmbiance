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

/**
 * Classe main, activité principale lancée au démarrage de l'application
 * et comportant les tuiles
 */

public class MainActivity extends AppCompatActivity {

    private ImageButton btn_user=null, btn_history=null, btn_map=null, btn_info=null, btn_edit=null;

    public static LocalDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datasource = new LocalDataSource(this);

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
                datasource.open();
                if (datasource.getUser0()==-2){
                    Intent secondeActiv = new Intent(MainActivity.this, UserNewActivity.class);

                    startActivity(secondeActiv);
                } else {
                    Intent secondeActiv = new Intent(MainActivity.this, EditActivity.class);

                    startActivity(secondeActiv);
                }
                datasource.close();
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
        btn_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                datasource.open();
                //Si aucun utilisateur n'est enregistré dans la base locale, on lance l'activité NouvelUtilisateur
                if (datasource.getUser0()==-2){
                    Intent secondeActiv = new Intent(MainActivity.this, UserNewActivity.class);

                    startActivity(secondeActiv);
                } else {
                    Intent secondeActiv = new Intent(MainActivity.this, UserActivity.class);

                    startActivity(secondeActiv);
                }
                datasource.close();
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
