package org.fasol.mambiance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.fasol.mambiance.db.MySQLiteHelper;

import static org.fasol.mambiance.MainActivity.datasource;

/**
 * Created by fasol on 18/11/16.
 */

public class HistoryActivity extends AppCompatActivity {

    ListView v_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);

        v_list=(ListView)findViewById(R.id.listview_history);

        datasource.open();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_item_history, datasource.getHistoriqueCursor(),
                new String[]{MySQLiteHelper.COLUMN_LIEUNOM, MySQLiteHelper.COLUMN_ADRESSE, MySQLiteHelper.COLUMN_DATECREATION, MySQLiteHelper.COLUMN_ID},
                new int[]{R.id.site_name,R.id.site_adress,R.id.date,R.id.marqueur_selection_id});


        // affichage
        v_list.setAdapter(adapter);
        // réaction au clic
        v_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v_item, int pos, long id) {

                Intent displayActiv = new Intent(HistoryActivity.this, DisplayMarkerActivity.class);

                LinearLayout l_item = (LinearLayout)v_item;
                TextView marqueur_id = (TextView)l_item.getChildAt(2);

                displayActiv.putExtra("marqueur_id_select", Long.parseLong((String)marqueur_id.getText()));
                startActivity(displayActiv);
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
