package com.olegsagenadatrytwo.googlemapslocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.olegsagenadatrytwo.googlemapslocation.model.AddressResponse;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final String TAG = "MainActivity";
    public static final String KEY = "AIzaSyBw_dqcm1S5EL43DT-ytOuqlyRWtIdBzrk";
    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=AIzaSyBw_dqcm1S5EL43DT-ytOuqlyRWtIdBzrk";
    public static final String BASE_URL2 = "https://maps.googleapis.com/maps/api/geocode/json?address=high+st+hasting&components=country:GB&key=AIzaSyBw_dqcm1S5EL43DT-ytOuqlyRWtIdBzrk";


    private TextView tvLocation;
    private Location myLocation;

    private AddressResponse addressResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        checkPermission();
    }

    private void checkPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
        }
    }

    FusedLocationProviderClient fuseLocationProviderCliend;
    public void getLocation(){
        fuseLocationProviderCliend = LocationServices.getFusedLocationProviderClient(this);
        fuseLocationProviderCliend.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        myLocation = location;
                        Log.d(TAG, "onSuccess: " + location.toString());
                        tvLocation.setText(
                                "Longitude: " + location.getLongitude() + " \n" +
                                        "Latitude: " + location.getLatitude()
                        );
                        getGeocodeAddress();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: ");
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void goToSecond(View view) {

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("location", myLocation);
        startActivity(intent);

    }

    public void showLocation(View view) {
        getLocation();
    }

    public void getGeocodeAddress(){
        String currentLatLng = myLocation.getLatitude() + "," + myLocation.getLongitude();
        final OkHttpClient okHttpClient;
        final Request request;

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("maps.googleapis.com")
                .addPathSegment("maps")
                .addPathSegment("api")
                .addPathSegment("geocode")
                .addPathSegment("json")
                .addQueryParameter("latlng", currentLatLng)
                .addQueryParameter("key", KEY)
                .build();

        okHttpClient = new OkHttpClient();
        request = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                //Log.d(TAG, "onResponse: " + response.body().string());
                addressResponse = gson.fromJson(response.body().string(), AddressResponse.class);
                Log.d(TAG, "onResponse: " + addressResponse.getResults().size());
            }
        });

    }
}
