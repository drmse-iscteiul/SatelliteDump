package com.iscte.mobileapps.satellitedump;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class DashFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap map;
    MapView mapView;
    View view;
    public Double lat;
    public Double log;
    public Double alt;

    private OnFragmentInteractionListener mListener;

    public DashFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dash, container, false);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = (MapView) view.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());
        map = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

       // lat=((MainActivity) getActivity()).getLat();
       // log=((MainActivity) getActivity()).getLog();
       // alt=((MainActivity) getActivity()).getAlt();


      //  googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, log)).title("ISCTE-IUL").snippet("Come visit us!"));

        NmeaHandler nmeaHandler = new NmeaHandler();
        CoordinatesCalculator cc= new CoordinatesCalculator();
        ArrayList<String> nmeaHistoryDash = ((MainActivity) getActivity()).allMessages;
        int earthRadius = 6371;

        for(int i = 0 ; i < nmeaHistoryDash.size(); i++){
            switch (nmeaHistoryDash.get(i).substring(0,6)) {
                case "$GPGSV":
                    double azimuth = 0.0;
                    double elevation = 0.0;
                    if((nmeaHandler.decodeMessage(nmeaHistoryDash.get(i)).get("Azimuth (2)") != null))
                        azimuth = Double.valueOf(nmeaHandler.decodeMessage(nmeaHistoryDash.get(i)).get("Azimuth (2)").split(" degrees")[0]);
                    if((nmeaHandler.decodeMessage(nmeaHistoryDash.get(i)).get("Elevation (2)")!=null))
                        elevation = Double.valueOf(nmeaHandler.decodeMessage(nmeaHistoryDash.get(i)).get("Elevation (2)").split(" degrees")[0]);
                    double x= cc.getX(earthRadius, azimuth, elevation );
                    double y= cc.getY(earthRadius, azimuth, elevation);
                    double z= cc.getZ(earthRadius, azimuth);
                    double lat= cc.latitudeCalculator(z,earthRadius);
                    double log = cc.longitudeCalculator(y,x);
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, log)).title("GPGSV 1"));
                    CameraPosition iscte = CameraPosition.builder().target(new LatLng(lat, log)).zoom(16).bearing(0).tilt(45).build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(iscte));
                    break;

            }
        }

       // double x= cc.getX(6371, 67, 24 );
       // double y= cc.getY(6371, 67, 24);
       // double z= cc.getZ(6371, 67);

        //double lat= cc.latitudeCalculator(z,6371);
        //double log = cc.longitudeCalculator(y,x);

          //googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, log)).title("Hello world"));

         //CameraPosition iscte = CameraPosition.builder().target(new LatLng(lat, log)).zoom(16).bearing(0).tilt(45).build();

          //googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(iscte));



          //A nossa localizacao
        //   googleMap.addMarker(new MarkerOptions().position(new LatLng(((MainActivity) getActivity()).getLat(), ((MainActivity) getActivity()).getLog())).title("Hello world"));
        //CameraPosition iscte = CameraPosition.builder().target(new LatLng(lat, log)).zoom(16).bearing(0).tilt(45).build();
        //googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(iscte));

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }
}
