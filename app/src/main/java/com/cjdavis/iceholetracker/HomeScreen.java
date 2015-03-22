package com.cjdavis.iceholetracker;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class HomeScreen extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // TODO: Rewrite App so everything doesn't happen in one activity
    public static final String TAG = HomeScreen.class.getSimpleName();

    /*
    * Define a request code to send to Google Play services
    * This code is returned in Activity.onActivityResult
    */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static String FOLDER_NAME = "IceHoleTracker";
    private final static String FILE_NAME = "saved-depths.csv";

    private GoogleApiClient mGoogleApiClient;
    private File directory;
    private File records;
    private EditText edtHoleDepth;
    private EditText edtNotes;


    private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        edtHoleDepth = (EditText)findViewById(R.id.edtHoleDepth);
        edtNotes = (EditText)findViewById(R.id.edtNotes);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if(!checkFile()) {
            Toast.makeText(getApplicationContext(), "Error with file storage. Check log for more details",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

        if(!checkFile()) {
            Toast.makeText(getApplicationContext(), "Error with file storage. Check log for more details",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    public void GetGPSCoordinates(View v) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            Toast.makeText(getApplicationContext(), "Waiting for location", Toast.LENGTH_LONG)
                    .show();
        } else {
            // TODO: compare location.getTime(), and if it's too old (we'll determine that later), request a location update)
            try {
                // TODO: Find a better method of saving the file
                FileWriter writer = new FileWriter(records, true);
                writer.write(String.format("%1$s,%2$s,%3$s,%4$s,%5$s,%6$s,\"%7$s\"\n",
                        sdfDate.format(System.currentTimeMillis()),
                        edtHoleDepth.getText(),
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getAltitude(),
                        location.getAccuracy(),
                        edtNotes.getText()));
                writer.close();
                Toast.makeText(getApplicationContext(), "Saved to file!", Toast.LENGTH_LONG)
                        .show();
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
            }

            edtHoleDepth.setText("");
            edtNotes.setText("");
        }
    }

    public void SendGPSCoordinates(View v) {
        // TODO: Call Intent for Email app and attach file to new email
        Toast.makeText(getApplicationContext(), "Not implemented yet!",
                Toast.LENGTH_LONG).show();
    }

    public boolean isStorageReadable() {
        String storageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(storageState) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState)) {
            return true;
        }

        return false;
    }

    public boolean isStorageWritable() {
        String storageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(storageState)) {
            return true;
        }

        return false;
    }

    private boolean checkFile() {
        // TODO: Find out the proper way to create this file in public store
        if (isStorageReadable() && isStorageWritable()) {
            directory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), FOLDER_NAME
            );
            if (!directory.exists() && !directory.mkdirs()) {
                Log.e(TAG,"Unable to create directory for file storage");
                return false;
            } else {
                records = new File(directory.getPath(), FILE_NAME);
                if (!records.exists()) {
                    try {
                        records.createNewFile();
                        FileWriter writer = new FileWriter(records);
                        writer.write("timestamp,depth,latitude,longitude,altitude,accuracy,notes\n");
                        writer.close();
                    } catch (IOException ex) {
                        Log.e(TAG, ex.getMessage());
                       return false;
                    }
                }
            }
        } else {
            Log.e(TAG, "File system is not writable");
            return false;
        }

        return true;
    }
}
