package com.iscte.mobileapps.satellitedump;

import java.util.HashMap;

public class NmeaHandler {

    private String LOG_TAG = "NmeaHandler_log";

    public HashMap decodeMessage(String msg){

        /*
            Messages recieved can be :
                    GPGGA,GPGSA,GPGSV,GPRMC,GPVTG,GLGSV,PGLOR,BDGSA,BDGSV,IMGSA,QZGSA,GNGSA

         */

        HashMap msg_params = new HashMap();

        if(msg.contains("GPGGA")){

            msg_params = decodeGPGGA(msg);

        } else if(msg.contains("GPGSA")) {

            //msg_params = decodeGPGSA(msg);
            
        } else if(msg.contains("GPGSV")) {

            //msg_params = decodeGPGSV(msg);

        } else if(msg.contains("GPRMC")) {

            //msg_params = decodeGPRMC(msg);

        } else if(msg.contains("GPVTG")) {

            //msg_params = decodeGPVTG(msg);


        } else if(msg.contains("GLGSV")) {

            //msg_params = decodeGLGSV(msg);


        } else if(msg.contains("PGLOR")) {

            //msg_params = decodePGLOR(msg);


        } else if(msg.contains("BDGSA")) {

            //msg_params = decodeBDGSA(msg);


        } else if(msg.contains("BDGSV")) {

            //msg_params = decodeBDGSV(msg);


        } else if(msg.contains("IMGSA")) {

            //msg_params = decodeIMGSA(msg);


        } else if(msg.contains("QZGSA")) {

            //msg_params = decodeQZGSA(msg);


        } else if(msg.contains("GNGSA")) {

            //msg_params = decodeGNGSA(msg);


        }


        return msg_params;

    }

    private HashMap decodeGPGGA(String msg){

        /*
        $GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47

        Where:
             GGA          Global Positioning System Fix Data
             123519       Fix taken at 12:35:19 UTC
             4807.038,N   Latitude 48 deg 07.038' N
             01131.000,E  Longitude 11 deg 31.000' E
             1            Fix quality: 0 = invalid
                                       1 = GPS fix (SPS)
                                       2 = DGPS fix
                                       3 = PPS fix
                                       4 = Real Time Kinematic
                                       5 = Float RTK
                                       6 = estimated (dead reckoning) (2.3 feature)
                                       7 = Manual input mode
                                       8 = Simulation mode
             08           Number of satellites being tracked
             0.9          Horizontal dilution of position
             545.4,M      Altitude, Meters, above mean sea level
             46.9,M       Height of geoid (mean sea level) above WGS84
                              ellipsoid
             (empty field) time in seconds since last DGPS update
             (empty field) DGPS station ID number
             *47          the checksum data, always begins with *

         */

        HashMap<String, String> h = new HashMap<String, String>();

        String[] msg_parts = msg.split(",");

        h.put("Type", "Global Positioning System Fix Data");

        h.put("Taken at", msg_parts[1].substring(0,2) + ":" + msg_parts[1].substring(2,4) + ":" + msg_parts[1].substring(4) + " UTC");

        h.put("Latitude", msg_parts[2].substring(0,2) + " deg " + msg_parts[2].substring(2) + "' " + msg_parts[3]);

        h.put("Longitude", msg_parts[4].substring(0,3).replaceFirst("^0+(?!$)", "") + " deg " + msg_parts[4].substring(3) + "' " + msg_parts[5]);

        switch (Integer.parseInt(msg_parts[6])){
            case 0:
                h.put("Fix Quality", "Invalid");
                break;
            case 1:
                h.put("Fix Quality", "GPS fix (SPS)");
                break;
            case 2:
                h.put("Fix Quality", "DGPS fix");
                break;
            case 3:
                h.put("Fix Quality", "PPS fix");
                break;
            case 4:
                h.put("Fix Quality", "Real Time Kinematic");
                break;
            case 5:
                h.put("Fix Quality", "Float RTK");
                break;
            case 6:
                h.put("Fix Quality", "Estimated (dead reckoning)");
                break;
            case 7:
                h.put("Fix Quality", "Manual input mode");
                break;
            case 8:
                h.put("Fix Quality", "Simulation mode");
                break;
        }

        h.put("Number of satellites being tracked", msg_parts[7].replaceFirst("^0+(?!$)", ""));

        h.put("Horizontal dilution of position", msg_parts[8]);

        h.put("Altitude above mean sea level", msg_parts[9] + " " + msg_parts[10]);

        h.put("Height of geoid (mean sea level) above WGS84 ellipsoid", msg_parts[11] + " " + msg_parts[12]);


        return h;
    }


}
