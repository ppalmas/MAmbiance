package org.fasol.mambiance;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.fasol.mambiance.db.Lieu;
import org.fasol.mambiance.db.Marqueur;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.fasol.mambiance.MainActivity.datasource;

/**
 * Created by fasol on 18/11/16.
 */

public class EditActivity extends AppCompatActivity implements LocationListener {

    // liste des caractéristiques possibles
    public final static String[] l_caract = {"Cozy", "Palpitant", "Formel", "Accueillant", "Sécurisant", "Inspirant", "Intime", "Animé",
            "Luxueux", "Chill", "Personnel", "Romantique", "Ennuyeux", "Chaleureux", "Business", "Reposant"};

    // champs à remplir
    private EditText site_name;
    private EditText description;
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
    private Button btn_photo;
    private String photo_emp = null;

    private ImageView mPhotoView; //thumbnail photo

    // bouton pour écrire dans la BDD
    private Button save;

    // layout contenant la rose des ambiances
    private FrameLayout layout_rose;

    // locationManager pour récupérer latitude et longitude
    private LocationManager locationManager;

    // current latitude, longitude and adress
    private float lat,lng;
    private String adresse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        // ajout de la rose des ambiances
        layout_rose = (FrameLayout) findViewById(R.id.frame_layout_rose);

        layout_rose.addView(new RoseSurfaceView(this, (SeekBar) findViewById(R.id.cursor_olfactory)
                , (SeekBar) findViewById(R.id.cursor_thermal), (SeekBar) findViewById(R.id.cursor_visual)
                , (SeekBar) findViewById(R.id.cursor_acoustical)));

        // liens vers les éléments de l'interface
        site_name = (EditText) findViewById(R.id.edit_site_name);
        description = (EditText) findViewById(R.id.edit_description);

        caract1 = (TextView) findViewById(R.id.caract1);
        caract2 = (TextView) findViewById(R.id.caract2);
        caract3 = (TextView) findViewById(R.id.caract3);



        cursor1 = (SeekBar) findViewById(R.id.cursor1);
        cursor2 = (SeekBar) findViewById(R.id.cursor2);
        cursor3 = (SeekBar) findViewById(R.id.cursor3);

        cursor_acoustical = (SeekBar) findViewById(R.id.cursor_acoustical);
        cursor_thermal = (SeekBar) findViewById(R.id.cursor_thermal);
        cursor_olfactory = (SeekBar) findViewById(R.id.cursor_olfactory);
        cursor_visual = (SeekBar) findViewById(R.id.cursor_visual);

        cursor_acoustical.setEnabled(true);
        cursor_thermal.setEnabled(true);
        cursor_olfactory.setEnabled(true);
        cursor_visual.setEnabled(true);

        btn_photo = (Button) findViewById(R.id.btn_photo);
        mPhotoView = (ImageView) findViewById(R.id.photo_thumbnail);
        save = (Button) findViewById(R.id.btn_save);

        // sélection des adjectifs aléatoirement
        ArrayList<Integer> caract_pos = new ArrayList<Integer>();
        for (int i = 0; i < l_caract.length; i++) caract_pos.add(i);

        Random rd = new Random();
        int rand = rd.nextInt(caract_pos.size());
        int caract_sel = caract_pos.get(rand);
        caract1.setText(l_caract[caract_sel]);
        caract_pos.remove(rand);
        rand = rd.nextInt(caract_pos.size());
        caract_sel = caract_pos.get(rand);
        caract2.setText(l_caract[caract_sel]);
        caract_pos.remove(rand);
        rand = rd.nextInt(caract_pos.size());
        caract_sel = caract_pos.get(rand);
        caract3.setText(l_caract[caract_sel]);
        caract_pos.remove(rand);

        // ajout d'un clicklistener sur les boutons
        btn_photo.setOnClickListener(photoListener);
        save.setOnClickListener(saveListener);

        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location == null) location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        lat=(float)location.getLatitude();
        lng=(float)location.getLongitude();
        photo_emp ="";
    }

    /**
     * Méthode appelée au clic de l'utilisateur sur "valider" le formulaire de
     * saisie d'une ambiance
     * Effectue une vérification des informations entrées
     * Géolocalise l'utilisateur et propose une adresse correspondante
     * Enregistre les informations dans la base de données
     */
    private View.OnClickListener saveListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {

            if(isFormularyNameCompleted()) {
                if (isFormularyDescriptionCompleted()) {
                    if (isFormularyPhotoCompleted()) {


                        // récupère les adresses possibles de localisation
                        List<Address> l_address = null;
                        try {
                            Geocoder geocoder = new Geocoder(EditActivity.this);
                            l_address = geocoder.getFromLocation(EditActivity.this.lat, EditActivity.this.lng, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(view.getContext(), "Veuillez allumer votre GPS", Toast.LENGTH_LONG).show();
                        }
                        if (l_address != null && !l_address.isEmpty()) {


                            // affiche une fenêtre demandant de valider l'adresse calculée
                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(EditActivity.this);
                            builderSingle.setTitle("Adresse calculée");
                            builderSingle.setMessage(l_address.get(0).getAddressLine(0) + " " +
                                    l_address.get(0).getPostalCode() + " " + l_address.get(0).getLocality());
                            // bouton recalculer
                            builderSingle.setNegativeButton(
                                    "recalculer",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            // bouton valider l'adresse
                            final List<Address> finalL_address = l_address;
                            builderSingle.setPositiveButton(
                                    "valider",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            EditActivity.this.adresse = finalL_address.get(0).getAddressLine(0) + " " +
                                                    finalL_address.get(0).getPostalCode() + " " + finalL_address.get(0).getLocality();

                                            datasource.open();

                                            Lieu lieu = datasource.createLieu(site_name.getText().toString(), adresse, lat, lng);
                                            Marqueur marqueur = datasource.createMarqueur(lieu.getLieu_id());
                                            datasource.createRoseAmbiance(cursor_olfactory.getProgress() / 4.f - 1.f, cursor_visual.getProgress() / 4.f - 1.f,
                                                    cursor_thermal.getProgress() / 4.f - 1.f, cursor_acoustical.getProgress() / 4.f - 1.f, marqueur.getMarqueur_id());
                                            datasource.createImage(marqueur.getMarqueur_id(), photo_emp);
                                            String[] mots = description.getText().toString().split("[\\p{Punct}\\s]+");
                                            for (int i = 0; i < mots.length; i++) {
                                                datasource.createMot(mots[i], marqueur.getMarqueur_id());
                                            }
                                            datasource.createCurseur(caract1.getText().toString(), cursor1.getProgress(), marqueur.getMarqueur_id());
                                            datasource.createCurseur(caract2.getText().toString(), cursor2.getProgress(), marqueur.getMarqueur_id());
                                            datasource.createCurseur(caract3.getText().toString(), cursor3.getProgress(), marqueur.getMarqueur_id());

                                            datasource.close();

                                            dialog.dismiss();
                                            Toast.makeText(view.getContext(), "Enregistrement effectué !", Toast.LENGTH_LONG).show();

                                            Intent intent = new Intent(EditActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                            builderSingle.show();
                        } else {
                            Toast.makeText(view.getContext(), "Impossible de trouver les coordonnées!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(view.getContext(), "Veuillez ajouter une photo.", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(view.getContext(),"Veuillez ajouter une description de trois mots.",Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(view.getContext(),"Veuillez ajouter un titre pour l'ambiance.",Toast.LENGTH_LONG).show();
            }
        }
    };

    /**
     * Function isFormularyPhotoCompleted
     * @return boolean : yes if there is a photo
     */
    private boolean isFormularyPhotoCompleted(){
        boolean flag=(photo_emp!=null);
        return flag;
    }

    /**
     * Function isFormularyNameCompleted
     * @return boolean flag : yes if there is a title/name for the location
     */
    private boolean isFormularyNameCompleted(){
        boolean flag = (!site_name.getText().toString().matches(""));
        return flag;
    }

    /**
     * Function isFormularyDescriptionCompleted
     * @return boolean flag : yes if there is a description
     * TODO : méthode qui vérifie que la description est entrée comme il faut (séparateur virugle, 3 mots)
     */
    private boolean isFormularyDescriptionCompleted(){
        boolean flag = (!description.getText().toString().matches(""));
        return flag;
    }

    // Gestion photo
    static final int REQUEST_TAKE_PHOTO = 1;

    /**
     * Méthode appelée au clic de l'utilisateur sur le bouton "Photo"
     */
    private View.OnClickListener photoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Toast.makeText(view.getContext(),"Erreur pas de photo",Toast.LENGTH_LONG).show();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(EditActivity.this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }

        }
    };
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        photo_emp = image.getAbsolutePath();
        return image;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // si une photo a été prise, affichage du thumbnail
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Get the dimensions of the View
            int targetW = btn_photo.getWidth();
            int targetH = btn_photo.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(photo_emp, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(photo_emp, bmOptions);
            mPhotoView.setImageBitmap(bitmap);

            mPhotoView.setVisibility(android.view.View.VISIBLE);
            btn_photo.setVisibility(android.view.View.GONE);
        }
    }

    // LocationListener methods override
    @Override
    public void onLocationChanged(Location location) {
        lat=(float)location.getLatitude();
        lng=(float)location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
