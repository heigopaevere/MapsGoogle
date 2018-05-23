package com.example.opilane.mapsgoogle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.Task;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean LocationPermissionGranted = false;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private GoogleMap gKaart;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private EditText stringTekst;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this,"Kaart on valmis", Toast.LENGTH_SHORT).show();
        gKaart = googleMap;
        if (LocationPermissionGranted){
            getSeadmeAsukoht();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED){}
                return;
        }
        gKaart.setMyLocationEnabled(true);
        gKaart.getUiSettings().setMyLocationButtonEnabled(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
    }


    private void getLocationPermission() {
        String[] permissions = (Manifest.permission.ACCESS_FINE_LOCATION,)
            Manifest.permission.ACCESS_COARSE_LOCATION
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                LocationPermissionGranted = true;
                kaivitakaart();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void kaivitakaart() {
    }

    private void getSeadmeAsukoht(){
        Log.d(TAG, "SeadmeAsukoht: seadme asukoha tuvastamine");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (LocationPermissionGranted){
                final Task Location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new GoogleMap.OnCircleClickListener();
            }
        }
    }
}
