package com.iscte.mobileapps.satellitedump;

import android.os.StrictMode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;

public class SateliteLocator {

    private URL url;
    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;
    HttpURLConnection connection = null;

 public SateliteLocator(){

     InputStream is;

     StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
     StrictMode.setThreadPolicy(policy);

     try{
         URL url = new URL(" https://www.n2yo.com/rest/v1/satellite/4NVUTZ-5FAJ8Y-2ZYN4A-3TMF");
         connection = (HttpURLConnection) url.openConnection();


         connection.setRequestMethod(REQUEST_METHOD);
         connection.setReadTimeout(READ_TIMEOUT);
         connection.setConnectTimeout(CONNECTION_TIMEOUT);

         //Connect to our url
         connection.connect();


         InputStream stream = connection.getInputStream();
         //String data = convertStreamToString(stream);
         int sz = stream.available();
         byte[] b = new byte[sz];
         stream.read(b);
         stream.close();
         String data = new String(b);
         getTLE();

     } catch (MalformedURLException e) {
         //bad  URL, tell the user
     } catch (IOException e) {
         //network error/ tell the user
     }
 }

    public static String getTLE(){



        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            //HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            //TLE https://www.n2yo.com/rest/v1/satellite/tle/25544&apiKey=4NVUTZ-5FAJ8Y-2ZYN4A-3TMF
            //POSITION https://www.n2yo.com/rest/v1/satellite/positions/25544/41.702/-76.014/0/2/&apiKey=4NVUTZ-5FAJ8Y-2ZYN4A-3TMF
            //virtual passes http://www.n2yo.com/rest/v1/satellite/visualpasses/25544/41.702/-76.014/0/2/300/&apiKey=4NVUTZ-5FAJ8Y-2ZYN4A-3TMF
            // radio passes https://www.n2yo.com/rest/v1/satellite/radiopasses/25544/41.702/-76.014/0/2/40/&apiKey=4NVUTZ-5FAJ8Y-2ZYN4A-3TMF
            //whats up https://www.n2yo.com/rest/v1/satellite/above/41.702/-76.014/0/70/18/&apiKey=4NVUTZ-5FAJ8Y-2ZYN4A-3TMF

            //HttpResponse httpResponse = httpclient.execute(new HttpGet("https://www.n2yo.com/rest/v1/satellite/above/41.702/-76.014/0/70/18/&apiKey=4NVUTZ-5FAJ8Y-2ZYN4A-3TMF"));

            // receive response as inputStream
            //inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
              result = convertInputStreamToString(inputStream);

            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}
