package com.iscte.mobileapps.satellitedump;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class NmeaHandler {

    private String LOG_TAG = "NmeaHandler_log";

    public LinkedHashMap<String, String> decodeMessage(String msg){

        /*
            Messages recieved can be :
                    GPGGA,GPGSA,GPGSV,GPRMC,GPVTG,GLGSV,PGLOR,BDGSA,BDGSV,IMGSA,QZGSA,GNGSA

         */

        LinkedHashMap<String, String> msg_params = new LinkedHashMap<String, String>();

        String messageType = msg.substring(1,6);

        switch(messageType){
            case "GPGGA":
                msg_params = decodeGPGGA(msg);
                break;
            case "GPGSA":
                msg_params = decodeGPGSA(msg);
                break;
            case "GPGSV":
                msg_params = decodeGPGSV(msg);
                break;
            case "GPRMC":
                msg_params = decodeGPRMC(msg);
                break;
            case "GPVTG":
                msg_params = decodeGPVTG(msg);
                break;
            case "GLGSV":
                msg_params = decodeGLGSV(msg);
                break;
            case "PGLOR":
                msg_params = decodePGLOR(msg);
                break;
            case "BDGSA":
                msg_params = decodeBDGSA(msg);
                break;
            case "BDGSV":
                msg_params = decodeBDGSV(msg);
                break;
            case "IMGSA":
                msg_params = decodeIMGSA(msg);
                break;
            case "QZGSA":
                msg_params = decodeQZGSA(msg);
                break;
            case "GNGSA":
                msg_params = decodeGNGSA(msg);
                break;
        }

        return msg_params;

    }

    private LinkedHashMap<String, String> decodeGPGGA(String msg){

        /*
        $GPGGA,123519.00,4807.545050,N,01131.454149,E,1,08,0.9,545.4,M,46.9,M,,*47

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

        LinkedHashMap<String, String> h = new LinkedHashMap<String, String>();

        String[] msg_parts = msg.split(",");

        String takenAt = "";
        String latitude = "";
        String longitude = "";
        String fixQuality = "";
        String numSatTracked = "";
        String horDilutPosit = "";
        String altAboveSea = "";
        String heightOfGeoid = "";

        h.put("Type", "Global Positioning System Fix Data");

        if(msg_parts[1] != null && !msg_parts[1].isEmpty())
            takenAt = msg_parts[1].substring(0, 2) + ":" + msg_parts[1].substring(2, 4) + ":" + msg_parts[1].substring(4) + " UTC";
        h.put("Taken at", takenAt);

        if(msg_parts[2] != null && !msg_parts[2].isEmpty() && msg_parts[3] != null && !msg_parts[3].isEmpty())
            latitude = msg_parts[2].substring(0,2) + " deg " + msg_parts[2].substring(2) + "' " + msg_parts[3];
        h.put("Latitude", latitude);

        if(msg_parts[4] != null && !msg_parts[4].isEmpty() && msg_parts[5] != null && !msg_parts[5].isEmpty())
            longitude = msg_parts[4].substring(0,3).replaceFirst("^0+(?!$)", "") + " deg " + msg_parts[4].substring(3) + "' " + msg_parts[5];
        h.put("Longitude", longitude);

        if(msg_parts[6] != null && !msg_parts[6].isEmpty())
            switch (Integer.parseInt(msg_parts[6])){
            case 0:
                fixQuality = "Invalid";
                break;
            case 1:
                fixQuality = "GPS fix (SPS)";
                break;
            case 2:
                fixQuality = "DGPS fix";
                break;
            case 3:
                fixQuality = "PPS fix";
                break;
            case 4:
                fixQuality = "Real Time Kinematic";
                break;
            case 5:
                fixQuality = "Float RTK";
                break;
            case 6:
                fixQuality = "Estimated (dead reckoning)";
                break;
            case 7:
                fixQuality = "Manual input mode";
                break;
            case 8:
                fixQuality = "Simulation mode";
                break;
        }
        h.put("Fix Quality", fixQuality);

        if(msg_parts[7] != null && !msg_parts[7].isEmpty())
            numSatTracked = msg_parts[7].replaceFirst("^0+(?!$)", "");
        h.put("Number of satellites being tracked", numSatTracked);

        if(msg_parts[8] != null && !msg_parts[8].isEmpty())
            horDilutPosit = msg_parts[8];
        h.put("Horizontal dilution of position", horDilutPosit);

        if(msg_parts[9] != null && !msg_parts[9].isEmpty())
            altAboveSea = msg_parts[9] + " " + msg_parts[10];
        h.put("Altitude above mean sea level", altAboveSea);

        if(msg_parts[11] != null && !msg_parts[11].isEmpty() && msg_parts[12] != null && !msg_parts[12].isEmpty())
            heightOfGeoid = msg_parts[11] + " " + msg_parts[12];
        h.put("Height of geoid (mean sea level) above WGS84 ellipsoid", heightOfGeoid);

        return h;
    }

    private LinkedHashMap<String, String> decodeGPGSA(String msg){

        /*

        GPS DOP and active satellites
        eg1. $GPGSA,A,3,,,,,,16,18,,22,24,,,3.6,2.1,2.2*3C
        eg2. $GPGSA,A,3,19,28,14,18,27,22,31,39,,,,,1.7,1.0,1.3*35

        2    = Mode:
               1=Fix not available
               2=2D
               3=3D
        3-14 = IDs of SVs used in position fix (null for unused fields)
        15   = PDOP
        16   = HDOP
        17   = VDOP

        -----------------------------------

         $GPGSA,A,3,04,05,,09,12,,,24,,,,,2.5,1.3,2.1*39

        Where:
             GSA      Satellite status
             A        Auto selection of 2D or 3D fix: - M = Manual, forced to operate in 2D or 3D
                                                      - A = Automatic, 3D/2D
             3        3D fix - values include: 1 = no fix
                                               2 = 2D fix
                                               3 = 3D fix
             04,05... PRNs of satellites used for fix (space for 12)
             2.5      PDOP (dilution of precision)
             1.3      Horizontal dilution of precision (HDOP)
             2.1      Vertical dilution of precision (VDOP)
             *39      the checksum data, always begins with *

         */

        LinkedHashMap<String, String> h = new LinkedHashMap<String, String>();

        String[] msg_parts = msg.split(",");

        String modeSelectionD = "";
        String tresDFix = "";
        String satIdConcat = "";
        String PDOP = "";
        String HDOP = "";
        String VDOP= "";


        h.put("Type","GPS Dilution of precision and active satellites");

        if(msg_parts[1] != null && !msg_parts[1].isEmpty())
            switch(msg_parts[1]){
                case "A":
                    modeSelectionD = "Automatic, 3D/2D";
                    break;
                case "M":
                    modeSelectionD = "Manual, forced to operate in 2D or 3D";
                    break;
            }
        h.put("Mode selection of 2D or 3D fix",modeSelectionD);

        if(msg_parts[2] != null && !msg_parts[2].isEmpty())
            switch(msg_parts[2]) {
                case "1":
                    tresDFix = "No fix";
                    break;
                case "2":
                    tresDFix = "2D fix";
                    break;
                case "3":
                    tresDFix = "3D fix";
                    break;
            }
        h.put("3D fix", tresDFix);

        for(int i = 3; i < 15; i++){
            if(!msg_parts[i].equals("") && msg_parts[i] != null){
                if(satIdConcat.equals(""))
                    satIdConcat = msg_parts[i];
                else
                    satIdConcat += (" / " + msg_parts[i]);
            }
        }
        h.put("PRNs of satellites used for fix", satIdConcat);

        if(msg_parts[15] != null && !msg_parts[15].isEmpty())
            PDOP = msg_parts[15];
        h.put("PDOP (dilution of precision)", PDOP);

        if(msg_parts[16] != null && !msg_parts[16].isEmpty())
            HDOP = msg_parts[16];
        h.put("Horizontal dilution of precision (HDOP)", HDOP);

        if(msg_parts[17] != null && !msg_parts[17].isEmpty())
            VDOP = msg_parts[17].split("\\*")[0];
        h.put("Vertical dilution of precision (VDOP)", VDOP);

        return h;

    }

    private LinkedHashMap<String, String> decodeGPGSV(String msg){

        /*

          $GPGSV,2,1,08,01,40,083,46,02,17,308,41,12,07,344,39,14,22,228,45*75

        Where:
              GSV          Satellites in view
              2            Number of sentences for full data
              1            sentence 1 of 2
              08           Number of satellites in view

              01           Satellite PRN number
              40           Elevation, degrees
              083          Azimuth, degrees
              46           SNR - higher is better
                   for up to 4 satellites per sentence
              *75          the checksum data, always begins with *

          -----------------------

          GPS Satellites in view
            eg. $GPGSV,3,1,11,03,03,111,00,04,15,270,00,06,01,010,00,13,06,292,00*74
                $GPGSV,3,2,11,14,25,170,00,16,57,208,39,18,67,296,40,19,40,246,00*74
                $GPGSV,3,3,11,22,42,067,42,24,14,311,43,27,05,244,00,,,,*4D


                $GPGSV,1,1,13,02,02,213,,03,-3,000,,11,00,121,,14,13,172,05*67


            1    = Total number of messages of this type in this cycle
            2    = Message number
            3    = Total number of SVs in view
            4    = SV PRN number
            5    = Elevation in degrees, 90 maximum
            6    = Azimuth, degrees from true north, 000 to 359
            7    = SNR, 00-99 dB (null when not tracking)
            8-11 = Information about second SV, same as field 4-7
            12-15= Information about third SV, same as field 4-7
            16-19= Information about fourth SV, same as field 4-7


         */

        LinkedHashMap<String, String> h = new LinkedHashMap<String, String>();

        String[] msg_parts = msg.split(",");

        h.put("","");

        return h;

    }

    private LinkedHashMap<String, String> decodeGPRMC(String msg){

        /*


         */

        LinkedHashMap<String, String> h = new LinkedHashMap<String, String>();

        String[] msg_parts = msg.split(",");

        h.put("","");

        return h;

    }

    private LinkedHashMap<String, String> decodeGPVTG(String msg){

        /*


         */

        LinkedHashMap<String, String> h = new LinkedHashMap<String, String>();

        String[] msg_parts = msg.split(",");

        h.put("","");

        return h;

    }

    private LinkedHashMap<String, String> decodeGLGSV(String msg){

        /*


         */

        LinkedHashMap<String, String> h = new LinkedHashMap<String, String>();

        String[] msg_parts = msg.split(",");

        h.put("","");

        return h;

    }

    private LinkedHashMap<String, String> decodePGLOR(String msg){

        /*


         */

        LinkedHashMap<String, String> h = new LinkedHashMap<String, String>();

        String[] msg_parts = msg.split(",");

        h.put("","");

        return h;

    }

    private LinkedHashMap<String, String> decodeBDGSA(String msg){

        /*


         */

        LinkedHashMap<String, String> h = new LinkedHashMap<String, String>();

        String[] msg_parts = msg.split(",");

        h.put("","");

        return h;

    }

    private LinkedHashMap<String, String> decodeBDGSV(String msg){

        /*


         */

        LinkedHashMap<String, String> h = new LinkedHashMap<String, String>();

        String[] msg_parts = msg.split(",");

        h.put("","");

        return h;

    }

    private LinkedHashMap<String, String> decodeIMGSA(String msg){

        /*


         */

        LinkedHashMap<String, String> h = new LinkedHashMap<String, String>();

        String[] msg_parts = msg.split(",");

        h.put("","");

        return h;

    }

    private LinkedHashMap<String, String> decodeQZGSA(String msg){

        /*


         */

        LinkedHashMap<String, String> h = new LinkedHashMap<String, String>();

        String[] msg_parts = msg.split(",");

        h.put("","");

        return h;

    }

    private LinkedHashMap<String, String> decodeGNGSA(String msg){

        /*


         */

        LinkedHashMap<String, String> h = new LinkedHashMap<String, String>();

        String[] msg_parts = msg.split(",");


        h.put("","");

        return h;

    }


}
