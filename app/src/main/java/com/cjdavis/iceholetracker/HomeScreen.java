package com.cjdavis.iceholetracker;

import android.content.Context;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class HomeScreen extends ActionBarActivity {

    // ®LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_quit) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void GetGPSCoordinates(View v) {
        Toast msg = Toast.makeText(getApplicationContext(), "Store GPS coordinates dang nabbit!", Toast.LENGTH_LONG);
        msg.show();
    }

    public void SendGPSCoordinates(View v) {
        Toast msg = Toast.makeText(getApplicationContext(), "Send the CSV file dang nabbit!", Toast.LENGTH_LONG);
        msg.show();
    }
}
