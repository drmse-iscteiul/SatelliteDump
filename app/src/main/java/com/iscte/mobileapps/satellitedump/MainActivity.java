package com.iscte.mobileapps.satellitedump;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity implements
        DumpFragment.OnFragmentInteractionListener,
        DashFragment.OnFragmentInteractionListener,
        HelpFragment.OnFragmentInteractionListener,
        LocationListener,
        GpsStatus.Listener,
        ListFragment.OnListFragmentInteractionListener {

    private static final int RC_LOCATION = 123;
    private Fragment fragDump = new DumpFragment();
    private Fragment fragDash = new DashFragment();
    private Fragment fragHelp = new HelpFragment();
    private Fragment fragList = new ListFragment();
    private  FragmentManager fragmentManager = getSupportFragmentManager();
    protected String history = "";
    public  TextView etLat, etLgt, etNmea;
    private static final String TAG = "TESTING_LOG";
    private static final String TAG_TOUCH = "PENIS";
    public boolean gettingNMEA = true;
    public ArrayList<String> nmeaHistory = new ArrayList<String>();
    public ArrayList<NmeaItem> nmeaItems = new ArrayList<NmeaItem>();
    public ArrayList<String> allMessages = new ArrayList<>();
    public ArrayList<String> cenas= new ArrayList<String>() ;
    public NmeaListAdapter adapter;
    public Double lat;
    public Double log;
    public Double alt;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dump:
                    showDump();
                    return true;
                case R.id.navigation_list:
                    showList();
                    return true;
                case R.id.navigation_dashboard:
                  //  Intent myIntent = new Intent(MainActivity.this, MapActivity.class);
                  //  startActivityForResult(myIntent, 0);
                    showDash();
                    return true;
                case R.id.navigation_help:
                    showHelp();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        ListView list = (ListView) findViewById(R.id.listView);

        SateliteLocator cl= new SateliteLocator();

        //new FetchWarsInfo().execute();

        showDump();

        //etLat = (EditText) findViewById(R.id.etLat);
        // etLgt = (EditText) findViewById(R.id.etLgt);

        locationSetup();

    }

    @AfterPermissionGranted(RC_LOCATION)
    private void locationSetup() {
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, this);
            locationManager.addGpsStatusListener(this);
            /*locationManager.addNmeaListener(new OnNmeaMessageListener() {
                @Override
                public void onNmeaMessage(String s, long l) {
                    if(etNmea != null && gettingNMEA) {
                        history = s + "\n" + history ;
                        etNmea.setText(history);
                        processToHistory(s);
                    }
                }
            });*/

            locationManager.addNmeaListener(new GpsStatus.NmeaListener() { // ESTE E O DEPRECATED, MAS E O QUE FUNCIONA NO MEU. BASTA COMENTAR ESTE E METER O DE CIMA PARA ENTREGAR
                @Override
                public void onNmeaReceived(long timestamp, String s) {
                    if(etNmea != null && gettingNMEA) {
                        /* GPGGA,GPGSA,GPGSV,GPRMC,GPVTG,GLGSV,PGLOR,BDGSA,BDGSV,IMGSA,QZGSA,GNGSA */
                        /*if(s.contains("GPGSA")){
                            NmeaHandler nmeahand = new NmeaHandler();
                            Log.d(TAG,s);
                            LinkedHashMap<String, String> hmap = nmeahand.decodeMessage(s);
                            for(Map.Entry<String, String> pair: hmap.entrySet()){
                                Log.d(TAG, pair.getKey() + ": " + pair.getValue());
                            }
                        }*/

                        if(!s.contains("PGLOR") && !s.contains("IMGSA") ) {    // Vamos esconder estas porque não funcionam
                             history = s + "\n" + history;
                            etNmea.setText(history);
                            processToHistory(s);
                        }
                    }
                }
            });
        } else {
            EasyPermissions.requestPermissions(new pub.devrel.easypermissions.PermissionRequest.Builder(this, RC_LOCATION, perms)
                    .setRationale("App need this to know your location")
                    .setPositiveButtonText("OK")
                    .setNegativeButtonText("NO")
                    .build());
        }
    }

    protected  void processToHistory(String newMessage){

        allMessages.add(newMessage);
        int rem = -1;

        for(int i = 0 ; i < nmeaHistory.size(); i++){
            if(nmeaHistory.get(i).substring(0,6).equals(newMessage.substring(0,6))) {
                rem = i;
                break;
            }
        }
        if(rem == -1) { // mean it is the first from its type

            nmeaHistory.add(newMessage);
            String messageName= nmeaHistory.get(nmeaHistory.size()-1).substring(0,6);

            NmeaItem item = new NmeaItem();
            item.setName(messageName);
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            String formatted = format.format(date);
            item.setTelephone(formatted);
            nmeaItems.add(item);
            if(adapter != null)
                adapter.notifyDataSetChanged();

        }else{ // not the first

            nmeaHistory.remove(rem);
            nmeaHistory.add(newMessage);

        }
}

   public void generateSatPosArray(){
            int x = 0;
            for(int i = 0 ; i < nmeaHistory.size(); i++){
               switch (nmeaHistory.get(i).substring(0,6)) {
                   case "GPGSV":



                       cenas.add(x,nmeaHistory.get(i));
                        x++;
                       break;
                   case "GLGSV":
                       cenas.add(x,nmeaHistory.get(i));
                       x++;
                       break;
                   case "BDGSV":
                       cenas.add(x,nmeaHistory.get(i));
                       x++;
                       break;
               }
           }


   }

    public ArrayList<NmeaItem> getNmeaItems() {
        return nmeaItems;
    }

    protected void showDump(){
        gettingNMEA=true;
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragDump)
                .commit();
    }

    protected void showList(){

        gettingNMEA=false;

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragList)
                .commit();
    }

    protected void showDash(){
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragDash)
                .commit();
    }

    protected void showHelp(){
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragHelp)
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onGpsStatusChanged(int i) {
    }

    @Override
    public void onLocationChanged(Location loc) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(loc.getTime());
        String formatted = format.format(date);
        Log.d(TAG, "loc changed- lat: "+ loc.getLatitude() + ", long: " +  loc.getLongitude() + ", alt: " + loc.getAltitude() + ", local time: " + formatted);

            lat=loc.getLatitude();
            log=loc.getLongitude();
            alt=loc.getAltitude();

    }

    public Double getLat() {
        return lat;
    }

    public Double getLog() {
        return log;
    }

    public Double getAlt() {
        return alt;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                Log.d(TAG,provider + " status: OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d(TAG,provider + " status: TEMPORARILY_UNAVAILABLE");
                break;
            case LocationProvider.AVAILABLE:
                Log.d(TAG,provider + " status: AVAILABLE");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG,"Provider Enabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG,"Provider Disabled: " + provider);
    }


    @Override
    public void onListFragmentInteraction(NmeaItem item) {
        Log.d(TAG_TOUCH,"touch the list ->>> " + item.getMessage());
    }

    /**
     * Async task class to get json response by making HTTP call
     * Async task class is used because
     * you cannot create a network connection on main thread
     */

    public class FetchWarsInfo2 extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        static final String URL_STRING =
                "https://www.n2yo.com/rest/v1/satellite/above/38.746/-9.151/105/45/20/&apiKey=4NVUTZ-5FAJ8Y-2ZYN4A-3TMF";
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
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
            Toast.makeText(getApplicationContext(),
                    "Connection successful.",Toast.LENGTH_SHORT).show();

            try{
                if(response!=null && !response.equals("")){
                    /*
                    converting JSON response string into JSONArray
                    */

                    JSONObject jObject = new JSONObject(response);
                    JSONArray responseArray = jObject.getJSONArray("above");;
                    if(responseArray.length()>0){
                        /*
                        Iterating JSON object from JSON Array one by one
                        */
                        for(int i=0;i<responseArray.length();i++){
                            JSONObject battleObj = responseArray.getJSONObject(i);

                            Log.d("PILAGORDA", battleObj.toString());

                            //creating object of model class(ModelWarDetails)
                            //ModelWarDetails modelWarDetails = new ModelWarDetails();
                            /*
                            fetching data based on key from JSON and setting into model class
                            */
                            //modelWarDetails.setName(battleObj.optString("name"));
                            //modelWarDetails.setAttacker_king
                            //        (battleObj.optString("attacker_king"));
                            //modelWarDetails.setDefender_king
                            //        (battleObj.optString("defender_king"));
                            //modelWarDetails.setLocation(battleObj.optString("location"));

                            //adding data into List
                            //listWarDetails.add(modelWarDetails);



                        }

                    }
                }else {
                    Toast.makeText(getApplicationContext(),
                            "Error in fetching data.",Toast.LENGTH_SHORT).show();
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

