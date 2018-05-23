package com.example.opilane.mapsgoogle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean LocationPermissionGranted = false;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private GoogleMap gKaart;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private AutoCompleteTextView otsinguTekst;
    private ImageView gps_ikoon, info_ikoon, kaart_ikoon;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168),
            new LatLng(71, 136));
    private Placeinfo mPlace;

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
        kavita();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        otsinguTekst = findViewById(R.id.searchText);
        gps_ikoon = findViewById(R.id.gps_ikoon);
        info_ikoon = findViewById(R.id.info_ikoon);
        kaart_ikoon = findViewById(R.id.kaart_ikoon);
        getLocationPermission();

    }
    private void kavita(){
        Log.d(TAG,"Lähtestamine");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this,this)
                .build();

        placeAutocompleteAdapter = new PlaceAutocompleteAdapter
                (this,mGoogleApiClient,LAT_LNGBOUNDS, null);

        otsinguTekst.setAdapter(placeAutocompleteAdapter);

        otsinguTekst.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || keyEvent.getAction() == keyEvent.ACTION_DOWN
                || keyEvent.getAction() == keyEvent.KEYCODE_ENTER){
                    geoLocate();
                }
                return false;
            }
        });
        gps_ikoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"GPS nupul vajutatud");
                getSeadmeAsukoht();
            }
        });
    }

    private void geoLocate() {
        Log.d(TAG,"Geolocating");
        String otsing = otsinguTekst.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> nimekiri = new ArrayList<>();
        try {
            nimekiri = geocoder.getFromLocationName(otsing,1);
        }
        catch (IOException e){
            Log.e(TAG, "Geolocate viga: " + e.getMessage());

        }
        if (nimekiri.size() > 0){
            Address address = nimekiri.get(0);
            Log.d(TAG,"Geolocate leidis ühe asukoha: " + address.toString());
            liigutaKaamerat(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
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
                Location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MapActivity.this,"Asukoht on tuvastatud",
                                    Toast.LENGTH_LONG).show();
                            Location currentLocation = (Location)task.getResult();
                            liigutaKaamerat(new LatLng(currentLocation.getLatitude(),
                                    currentLocation.getLongitude()),DEFAULT_ZOOM, "Minu asukoht");
                        }
                        else {
                            Toast.makeText(MapActivity.this,"Asukoht ei olnud võimalik " +
                                            "tuvastada", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
        catch (SecurityException e){
            Log.e(TAG,"SecurityException" + e.getMessage());
        }
    }
    private void liigutaKaamerat(LatLng latLng, float zoom, String pealkiri) {
        Log.d(TAG,"Lat: " + latLng.latitude + "lng: " +latLng.longitude);
        gKaart.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (!pealkiri.equals("My Location")){
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(pealkiri);
            gKaart.addMarker(markerOptions);
        }
    }
}
