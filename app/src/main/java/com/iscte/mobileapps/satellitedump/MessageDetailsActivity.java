package com.iscte.mobileapps.satellitedump;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Map;

public class MessageDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        String message = "";
        LinkedHashMap<String, String> msg_params = new LinkedHashMap<String, String>();

        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        if(!extrasBundle.isEmpty()){
            boolean hasMessage = extrasBundle.containsKey("message");
            if(hasMessage){
                message = extrasBundle.getString("message");
                Toast.makeText(getApplicationContext(), "Entrei no detalhe com a mensagem: " + message, Toast.LENGTH_SHORT).show();
            }
        }

        if(!message.isEmpty()){

            NmeaHandler nmeaHandler = new NmeaHandler();
            msg_params = nmeaHandler.decodeMessage(message);

            LinearLayout table = (LinearLayout) this.findViewById(R.id.table_layout);

            for(Map.Entry<String, String> pair: msg_params.entrySet()){

                LinearLayout row = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.attrib_row, null);
                ((TextView)row.findViewById(R.id.attrib_name)).setText(pair.getKey()+ ": ");
                ((TextView)row.findViewById(R.id.attrib_value)).setText(pair.getValue());
                table.addView(row);

                // Inflate your row "template" and fill out the fields.
                //TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.attrib_row, null);
                //((TextView)row.findViewById(R.id.attrib_name)).setText(pair.getKey()+ ": ");
                //((TextView)row.findViewById(R.id.attrib_value)).setText(pair.getValue());
                //table.addView(row);


                Log.d("CARALHOOOOO", pair.getKey() + ": " + pair.getValue());
            }
            //table.requestLayout();     // Not sure if this is needed.


        }

    }
}
