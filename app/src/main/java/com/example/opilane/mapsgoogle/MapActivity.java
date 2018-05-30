package com.example.opilane.mapsgoogle;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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
    private Marker marker;
    private static int PLACE_PICKER_REQUEST = 1;

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
        otsinguTekst.setOnClickListener(autocompleteClickListener);
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter
                (this,mGoogleApiClient,LAT_LNG_BOUNDS, null);

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
        asukohaInfo.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: asukoha info nupule klikiti");
                try {
                    if (marker.isInfoWindowShown()){
                        marker.hideInfoWindow();
                    }else
                }
            }
        });
        asukohaKaart.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int PLACE_PICKER_REQUEST = 1;
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(MapActivity.this),PLACE_PICKER_REQUEST);
                }
                catch (GooglePlayServicesNotAvailableException e){
                    Log.d(TAG,"GooglePlayServicesRepairException: " + e.getMessage());
                }
                catch (GooglePlayServicesNotAvailableException e){
                    Log.d(TAG,"GooglePlayServicesNotAvailableException: " + e.getMessage());
                }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(this,data );
                String toastMsg = String.format("Place: %s", place.getName());
                PendingResult<PlaceBuffer> placeBufferPendingResult = Places.GeoDataApi.
                        getPlaceById(mGoogleApiClient,place.getId());
            }
        }
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
    private void liigutaKaamerat(LatLng latLng, float zoom, Placeinfo placeinfo) {
        Log.d(TAG,"Lat: " + latLng.latitude + "lng: " +latLng.longitude);
        gKaart.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        gKaart.clear();
        if (placeinfo !=null){
            try {
                String snippet = "Aadress: " + placeinfo.getAddress() + "\n" +
                        "Telefoni number: " + placeinfo.getPhoneNimber() + "\n" +
                        "Veebilehekülg: " + placeinfo.getWebsiteUri() + "\n" +
                        "Reiting: " + placeinfo.getRating() + "\n";

                MarkerOptions options = new MarkerOptions().position(latLng).
                        title(placeinfo.getName()).
                        snippet(snippet);
                marker = gKaart.addMarker(options);
            }
            catch (NullPointerException e){
                Log.d(TAG,"moveCamera: NullpointerException: " + e.getMessage());
            }
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private AdapterView.OnItemClickListener autocompleteClickListener =
            new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(i);
            PendingResult<PlaceBuffer> placeBufferPendingResult = Places.GeoDataApi.getPlaceById(
                    mGoogleApiClient, placeId);
                placeBufferPendingResult.setResultCallback(updatePlaceDetailsCallback);
        }
    };
    private ResultCallback<PlaceBuffer> updatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()){
                Log.d(TAG, "Place query dod not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);
            try {
                mPlace = new Placeinfo();
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setAttributions(place.getAttributions().toString());
                mPlace.setId(place.getId().toString());
                mPlace.setLatLng(place.getLatLng());
                mPlace.setRating(place.getRating());
                mPlace.setPhoneNimber(place.getPhoneNumber().toString());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG,"Place details: " + mPlace.toString());
            }
            catch (NullPointerException e){
                Log.d(TAG,"NullPointerException: " + e.getMessage());
            }
            liigutaKaamerat(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude),DEFAULT_ZOOM, mPlace);
            places.release();
        }
    };
}
