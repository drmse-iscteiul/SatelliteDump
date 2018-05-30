package com.iscte.mobileapps.satellitedump;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class DashFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap map;
    MapView mapView;
    View view;
    public Double lat;
    public Double log;
    public Double alt;
    public ArrayList<Satellite> satelliteArrayList = new ArrayList<Satellite>();

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

        new FetchWarsInfo().execute();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());
        map = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

       lat=((MainActivity) getActivity()).getLat();
       log=((MainActivity) getActivity()).getLog();
       alt=((MainActivity) getActivity()).getAlt();

        for(Satellite s: satelliteArrayList){
            googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(s.getSatlat()), Double.valueOf(s.getSatlng()))).title(s.getSatid() + "-" + s.getSatname()).icon(BitmapDescriptorFactory.fromResource(R.drawable.sat_marker)));
        }

        CameraPosition our_pos = CameraPosition.builder().target(new LatLng(lat, log)).zoom(5).bearing(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(our_pos));

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }


    /**
     * Async task class to get json response by making HTTP call
     * Async task class is used because
     * you cannot create a network connection on main thread
     */
    public class FetchWarsInfo extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        double user_lat =((MainActivity) getActivity()).getLat();
        double user_log =((MainActivity) getActivity()).getLog();
        double user_alt =((MainActivity) getActivity()).getAlt();

        //static final String URL_STRING_STATIC = "https://www.n2yo.com/rest/v1/satellite/above/38.746/-9.151/105/45/20/&apiKey=4NVUTZ-5FAJ8Y-2ZYN4A-3TMF";
        final String URL_STRING = "https://www.n2yo.com/rest/v1/satellite/above/" + user_lat + "/" + user_log + "/" + user_alt +"/90/20/&apiKey=4NVUTZ-5FAJ8Y-2ZYN4A-3TMF";
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait. Fetching data..");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            /*
            creatingURLConnection is a function use to establish connection
            */
            response = creatingURLConnection(URL_STRING);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            Toast.makeText(getActivity().getApplicationContext(),"Connection successful.",Toast.LENGTH_SHORT).show();

            try{
                if(response!=null && !response.equals("")){
                    /*
                    converting JSON response string into JSONArray
                    */
                    JSONObject jObject = new JSONObject(response);
                    JSONArray responseArray = jObject.getJSONArray("above");
                    if(responseArray.length()>0){
                        /*
                        Iterating JSON object from JSON Array one by one
                        */
                        for(int i=0;i<responseArray.length();i++){
                            JSONObject satelliteObj = responseArray.getJSONObject(i);

                            Log.d("SATELLITE_INFO", satelliteObj.toString());

                            Satellite sat = new Satellite();
                            sat.setSatid(satelliteObj.optString("satid"));
                            sat.setSatname(satelliteObj.optString("satname"));
                            sat.setSatlat(satelliteObj.optString("satlat"));
                            sat.setSatlng(satelliteObj.optString("satlng"));
                            sat.setSatalt(satelliteObj.optString("satalt"));

                            satelliteArrayList.add(sat);

                        }

                    }
                }else {
                    Toast.makeText(getActivity().getApplicationContext(),"Error in fetching data.",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public String creatingURLConnection (String GET_URL) {
        String response = "";
        HttpURLConnection conn ;
        StringBuilder jsonResults = new StringBuilder();
        try {
            //setting URL to connect with
            URL url = new URL(GET_URL);
            //creating connection
            conn = (HttpURLConnection) url.openConnection();
            /*
            converting response into String
            */
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
            response = jsonResults.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }

}
