package com.iscte.mobileapps.satellitedump;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
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

}

