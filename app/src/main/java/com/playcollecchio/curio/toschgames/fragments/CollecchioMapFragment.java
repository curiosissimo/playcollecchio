package com.playcollecchio.curio.toschgames.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.playcollecchio.curio.toschgames.Constants;
import com.playcollecchio.curio.toschgames.JsonConverter;
import com.playcollecchio.curio.toschgames.R;
import com.playcollecchio.curio.toschgames.models.Place;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.playcollecchio.curio.toschgames.models.Story;

import java.util.ArrayList;
import java.util.LinkedList;

import io.realm.Realm;

/**
 * Created by Utente on 22/10/2017.
 */

public class CollecchioMapFragment extends SupportMapFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        View.OnClickListener
{
    private ArrayList<Place> results;
    private GoogleMap mGoogleMap;
    private ImageView currentPosition;
    private Location myCurrentLocation;
    private static final String ACTION_GET_LOCATION = "GET_CURRENT_LOCATION";
    private BroadcastReceiver receiverLocation = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            context.unregisterReceiver(this);
            if(mGoogleMap!= null && myCurrentLocation!=null)
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude()), 20.0f));
        }
    };
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Marker mCurrLocationMarker;
    private LinkedList<LatLng> collecchioPositions;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if (getContext() != null)
        {
            String json = JsonConverter.loadJSONFromAsset(getContext(),"collecchioplaces.json");
            results = JsonConverter.createPOJO(json,"Place");
            Constants.NUMBER_OF_PLACES = results.size();
        }
        getMapAsync(this);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        activity.registerReceiver(receiverLocation,new IntentFilter(ACTION_GET_LOCATION));
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        if(currentPosition!=null)
            currentPosition.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mGoogleApiClient != null &&
                ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED && !mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        collecchioPositions = new LinkedList<>();
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        putMarkers();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                //mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            //mGoogleMap.setMyLocationEnabled(true);
        }

        if(getContext()!=null)
        {
            currentPosition = (ImageView) getActivity().findViewById(R.id.currentPosition);
            currentPosition.setVisibility(View.VISIBLE);
            currentPosition.setOnClickListener(this);
        }

        moveToMyCurrentPosition();

    }

    public void putMarkers()
    {
        Realm realm = Realm.getDefaultInstance();
        for (int i = 0; i < results.size(); i++)
        {
            collecchioPositions.add(
                    new LatLng(results.get(i).geometry.coordinates[0], results.get(i).geometry.coordinates[1]));
            MarkerOptions markerOptions = new MarkerOptions().position(collecchioPositions.get(i))
                    .title(results.get(i).properties.name);
            if(realm.where(Story.class).equalTo("id",results.get(i).properties.id).findAll().size()==0)
                mGoogleMap.addMarker(markerOptions
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.unknownplaceicona)));
            else
                mGoogleMap.addMarker(markerOptions
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.checkedplaceicon)));
        }
        realm.close();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(0.1F);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (getContext()!= null && ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }


    @Override
    public void onLocationChanged(Location location)
    {
        if(getContext()!=null)
            getContext().sendBroadcast(new Intent(ACTION_GET_LOCATION));
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        myCurrentLocation = location;
        LatLng latLng = new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mrmagooicona));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        if(getContext()!=null)
            Toast.makeText(getContext(),"Lat "+latLng.latitude + " Long "+latLng.longitude,Toast.LENGTH_LONG).show();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("Permesso di localizzazione richiesta")
                        .setMessage("Questa app richiede il permesso di localizzazione," +
                                " per favore accetta per utilizzare questa funzionalità")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                requestPermissions(
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        //mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "Permesso negato", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.currentPosition:
                moveToMyCurrentPosition();
                break;
        }
    }

    public void moveToMyCurrentPosition()
    {
        if(myCurrentLocation!=null)
        {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(
                    new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude())));
        }
    }
}
