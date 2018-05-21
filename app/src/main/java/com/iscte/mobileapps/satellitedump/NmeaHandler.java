package com.iscte.mobileapps.satellitedump;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

        String numberOfMessages = "";
        String messageNumber = "";
        String numberOfSatelitesInView = "";
        String[] satelitePRNNumber = {"","","",""};
        String[] elevation = {"","","",""};
        String[] azimuth = {"","","",""};
        String[] SNR = {"","","",""};


        h.put("Type","GPS Satellites in view");

        if(msg_parts[1] != null && !msg_parts[1].isEmpty())
            numberOfMessages = msg_parts[1];
        h.put("Number of sentences for full data",numberOfMessages);

        if(msg_parts[2] != null && !msg_parts[2].isEmpty())
            messageNumber = msg_parts[2];
        h.put("Message number",messageNumber);

        if(msg_parts[3] != null && !msg_parts[3].isEmpty())
            numberOfSatelitesInView = msg_parts[3];
        h.put("Number of satellites in view",numberOfSatelitesInView);

        int x = 4;
        for(int i=0; i<4; i++){
            if(msg_parts.length > x) {
                if(msg_parts[x] != null && !msg_parts[x].isEmpty()) {
                    satelitePRNNumber[i] = msg_parts[x];
                    h.put("Satellite (" + String.valueOf(i+1) + ")",satelitePRNNumber[i]);
                }
                if(msg_parts[x+1] != null && !msg_parts[x+1].isEmpty()) {
                    elevation[i] = msg_parts[x + 1];
                    h.put("Elevation (" + String.valueOf(i+1) + ")",elevation[i] + " degrees");
                }
                if(msg_parts[x+2] != null && !msg_parts[x+2].isEmpty()) {
                    azimuth[i] = msg_parts[x + 2];
                    h.put("Azimuth (" + String.valueOf(i+1) + ")",azimuth[i] + " degrees from true North");
                }
                if(msg_parts[x+3] != null && !msg_parts[x+3].isEmpty() && !msg_parts[x+3].contains("*")) {
                    SNR[i] = msg_parts[x + 3];
                    h.put("SNR (" + String.valueOf(i+1) + ")",SNR[i] + " dB");
                }
                x += 4;
            }
        }

        return h;

    }

    private LinkedHashMap<String, String> decodeGPRMC(String msg){

        /*

        NMEA has its own version of essential gps pvt (position, velocity, time) data. It is called RMC, The Recommended Minimum, which will look similar to:
        $GPRMC,123519,A,4807.038,N,01131.000,E,022.4,084.4,230394,003.1,W*6A

        Where:
             RMC          Recommended Minimum sentence C
             123519       Fix taken at 12:35:19 UTC
             A            Status A=active or V=Void.
             4807.038,N   Latitude 48 deg 07.038' N
             01131.000,E  Longitude 11 deg 31.000' E
             022.4        Speed over the ground in knots
             084.4        Track angle in degrees True
             230394       Date - 23rd of March 1994
             003.1,W      Magnetic Variation
             *6A          The checksum data, always begins with *

         ----------------------------------------------


         Recommended minimum specific GPS/Transit data
            eg1. $GPRMC,081836,A,3751.65,S,14507.36,E,000.0,360.0,130998,011.3,E*62
            eg2. $GPRMC,225446,A,4916.45,N,12311.12,W,000.5,054.7,191194,020.3,E*68


                       225446       Time of fix 22:54:46 UTC
                       A            Navigation receiver warning A = OK, V = warning
                       4916.45,N    Latitude 49 deg. 16.45 min North
                       12311.12,W   Longitude 123 deg. 11.12 min West
                       000.5        Speed over ground, Knots
                       054.7        Course Made Good, True
                       191194       Date of fix  19 November 1994
                       020.3,E      Magnetic variation 20.3 deg East
                       *68          mandatory checksum


            eg3. $GPRMC,220516,A,5133.82,N,00042.24,W,173.8,231.8,130694,004.2,W*70
                          1    2    3    4    5     6    7    8      9     10  11 12


                  1   220516     Time Stamp
                  2   A          validity - A-ok, V-invalid
                  3   5133.82    current Latitude
                  4   N          North/South
                  5   00042.24   current Longitude
                  6   W          East/West
                  7   173.8      Speed in knots
                  8   231.8      True course
                  9   130694     Date Stamp
                  10  004.2      Variation
                  11  W          East/West
                  12  *70        checksum


            eg4. $GPRMC,hhmmss.ss,A,llll.ll,a,yyyyy.yy,a,x.x,x.x,ddmmyy,x.x,a*hh
            1    = UTC of position fix
            2    = Data status (V=navigation receiver warning)
            3    = Latitude of fix
            4    = N or S
            5    = Longitude of fix
            6    = E or W
            7    = Speed over ground in knots
            8    = Track made good in degrees True
            9    = UT date
            10   = Magnetic variation degrees (Easterly var. subtracts from true course)
            11   = E or W
            12   = Checksum

         */

        LinkedHashMap<String, String> h = new LinkedHashMap<String, String>();

        String[] msg_parts = msg.split(",");

        String timeOfFix = "";
        String statusOfNavigationReceiver = "";
        String latitude = "";
        String longitude = "";
        String speedOverGround = "";
        String courseMadeGood = "";
        String dateOfFix = "";
        String magneticVariation = "";


        h.put("Type","Recommended minimum specific GPS/Transit data");

        if(msg_parts[1] != null && !msg_parts[1].isEmpty())
            timeOfFix = msg_parts[1].substring(0, 2) + ":" + msg_parts[1].substring(2, 4) + ":" + msg_parts[1].substring(4) + " UTC";
        h.put("Time of fix", timeOfFix);

        if(msg_parts[2] != null && !msg_parts[2].isEmpty())
            switch (msg_parts[2]){
                case "A":
                    statusOfNavigationReceiver = "Ok";
                    break;
                case "V":
                    statusOfNavigationReceiver = "Invalid (Navigation receiver warning)";
                    break;
            }
        h.put("Data Status", statusOfNavigationReceiver);

        if(msg_parts[3] != null && !msg_parts[3].isEmpty() && msg_parts[4] != null && !msg_parts[4].isEmpty())
            latitude = msg_parts[3].substring(0,2) + " deg " + msg_parts[3].substring(2) + "' " + msg_parts[4];
        h.put("Latitude", latitude);

        if(msg_parts[5] != null && !msg_parts[5].isEmpty() && msg_parts[6] != null && !msg_parts[6].isEmpty())
            longitude = msg_parts[5].substring(0,3).replaceFirst("^0+(?!$)", "") + " deg " + msg_parts[5].substring(3) + "' " + msg_parts[6];
        h.put("Longitude", longitude);

        if(msg_parts[7] != null && !msg_parts[7].isEmpty()){
            speedOverGround = msg_parts[7];
            h.put("Speed over ground",speedOverGround + " Knots");
        } else {
            h.put("Speed over ground",speedOverGround);
        }

        if(msg_parts[8] != null && !msg_parts[8].isEmpty()){
            courseMadeGood = msg_parts[8];
            h.put("Course made good, true",courseMadeGood + " Degrees");
        } else {
            h.put("Course made good, true",courseMadeGood);
        }

        if(msg_parts[9] != null && !msg_parts[9].isEmpty()){
            String month = "";
            switch (msg_parts[9].substring(2,4)){
                case "01":
                    month = "January";
                    break;
                case "02":
                    month = "February";
                    break;
                case "03":
                    month = "March";
                    break;
                case "04":
                    month = "April";
                    break;
                case "05":
                    month = "May";
                    break;
                case "06":
                    month = "June";
                    break;
                case "07":
                    month = "July";
                    break;
                case "08":
                    month = "August";
                    break;
                case "09":
                    month = "September";
                    break;
                case "10":
                    month = "October";
                    break;
                case "11":
                    month = "November";
                    break;
                case "12":
                    month = "December";
                    break;
            }
            dateOfFix = month + " " + msg_parts[9].substring(0,2) + ", 20" + msg_parts[9].substring(4);
        }
        h.put("Date of fix", dateOfFix);

        if(msg_parts[10] != null && !msg_parts[10].isEmpty() && msg_parts[11] != null && !msg_parts[11].isEmpty())
            magneticVariation = msg_parts[10] + " deg " + msg_parts[11];
        h.put("Magnetic variation", magneticVariation);

        return h;

    }

    private LinkedHashMap<String, String> decodeGPVTG(String msg){

        /*

        Velocity made good. The gps receiver may use the LC prefix instead of GP if it is emulating Loran output.
          $GPVTG,054.7,T,034.4,M,005.5,N,010.2,K*48

        where:
                VTG          Track made good and ground speed
                054.7,T      True track made good (degrees)
                034.4,M      Magnetic track made good
                005.5,N      Ground speed, knots
                010.2,K      Ground speed, Kilometers per hour
                *48          Checksum

         -------------

         Track Made Good and Ground Speed.
        eg1. $GPVTG,360.0,T,348.7,M,000.0,N,000.0,K*43
        eg2. $GPVTG,054.7,T,034.4,M,005.5,N,010.2,K


                   054.7,T      True track made good
                   034.4,M      Magnetic track made good
                   005.5,N      Ground speed, knots
                   010.2,K      Ground speed, Kilometers per hour


        eg3. $GPVTG,t,T,,,s.ss,N,s.ss,K*hh
        1    = Track made good
        2    = Fixed text 'T' indicates that track made good is relative to true north
        3    = not used
        4    = not used
        5    = Speed over ground in knots
        6    = Fixed text 'N' indicates that speed over ground in in knots
        7    = Speed over ground in kilometers/hour
        8    = Fixed text 'K' indicates that speed over ground is in kilometers/hour
        9    = Checksum

         */

        LinkedHashMap<String, String> h = new LinkedHashMap<String, String>();

        String[] msg_parts = msg.split(",");

        h.put("","");

        return h;

    }

    private LinkedHashMap<String, String> decodeGLGSV(String msg){

        /*

          $GLGSV,2,1,08,01,40,083,46,02,17,308,41,12,07,344,39,14,22,228,45*75

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

          GLONASS Satellites in view
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

        String numberOfMessages = "";
        String messageNumber = "";
        String numberOfSatelitesInView = "";
        String[] satelitePRNNumber = {"","","",""};
        String[] elevation = {"","","",""};
        String[] azimuth = {"","","",""};
        String[] SNR = {"","","",""};


        h.put("Type","GLONASS Satellites in view");

        if(msg_parts[1] != null && !msg_parts[1].isEmpty())
            numberOfMessages = msg_parts[1];
        h.put("Number of sentences for full data",numberOfMessages);

        if(msg_parts[2] != null && !msg_parts[2].isEmpty())
            messageNumber = msg_parts[2];
        h.put("Message number",messageNumber);

        if(msg_parts[3] != null && !msg_parts[3].isEmpty())
            numberOfSatelitesInView = msg_parts[3];
        h.put("Number of satellites in view",numberOfSatelitesInView);

        int x = 4;
        for(int i=0; i<4; i++){
            if(msg_parts.length > x) {
                if(msg_parts[x] != null && !msg_parts[x].isEmpty()) {
                    satelitePRNNumber[i] = msg_parts[x];
                    h.put("Satellite (" + String.valueOf(i+1) + ")",satelitePRNNumber[i]);
                }
                if(msg_parts[x+1] != null && !msg_parts[x+1].isEmpty()) {
                    elevation[i] = msg_parts[x + 1];
                    h.put("Elevation (" + String.valueOf(i+1) + ")",elevation[i] + " degrees");
                }
                if(msg_parts[x+2] != null && !msg_parts[x+2].isEmpty()) {
                    azimuth[i] = msg_parts[x + 2];
                    h.put("Azimuth (" + String.valueOf(i+1) + ")",azimuth[i] + " degrees from true North");
                }
                if(msg_parts[x+3] != null && !msg_parts[x+3].isEmpty() && !msg_parts[x+3].contains("*")) {
                    SNR[i] = msg_parts[x + 3];
                    h.put("SNR (" + String.valueOf(i+1) + ")",SNR[i] + " dB");
                }
                x += 4;
            }
        }

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

             Same structure as a GSA message, but this one is provenient from (BeiDou Navigation Satellite System)

                     BeiDou DOP and active satellites
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


        h.put("Type","Dilution of precision and active satellites (BeiDou Navigation Satellite System)");

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

    private LinkedHashMap<String, String> decodeBDGSV(String msg){

        /*

          $BDGSV,2,1,08,01,40,083,46,02,17,308,41,12,07,344,39,14,22,228,45*75

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

          BeiDou Navigation Satellite System Satellites in view
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

        String numberOfMessages = "";
        String messageNumber = "";
        String numberOfSatelitesInView = "";
        String[] satelitePRNNumber = {"","","",""};
        String[] elevation = {"","","",""};
        String[] azimuth = {"","","",""};
        String[] SNR = {"","","",""};


        h.put("Type","BeiDou Satellites in view");

        if(msg_parts[1] != null && !msg_parts[1].isEmpty())
            numberOfMessages = msg_parts[1];
        h.put("Number of sentences for full data",numberOfMessages);

        if(msg_parts[2] != null && !msg_parts[2].isEmpty())
            messageNumber = msg_parts[2];
        h.put("Message number",messageNumber);

        if(msg_parts[3] != null && !msg_parts[3].isEmpty())
            numberOfSatelitesInView = msg_parts[3];
        h.put("Number of satellites in view",numberOfSatelitesInView);

        int x = 4;
        for(int i=0; i<4; i++){
            if(msg_parts.length > x) {
                if(msg_parts[x] != null && !msg_parts[x].isEmpty()) {
                    satelitePRNNumber[i] = msg_parts[x];
                    h.put("Satellite (" + String.valueOf(i+1) + ")",satelitePRNNumber[i]);
                }
                if(msg_parts[x+1] != null && !msg_parts[x+1].isEmpty()) {
                    elevation[i] = msg_parts[x + 1];
                    h.put("Elevation (" + String.valueOf(i+1) + ")",elevation[i] + " degrees");
                }
                if(msg_parts[x+2] != null && !msg_parts[x+2].isEmpty()) {
                    azimuth[i] = msg_parts[x + 2];
                    h.put("Azimuth (" + String.valueOf(i+1) + ")",azimuth[i] + " degrees from true North");
                }
                if(msg_parts[x+3] != null && !msg_parts[x+3].isEmpty() && !msg_parts[x+3].contains("*")) {
                    SNR[i] = msg_parts[x + 3];
                    h.put("SNR (" + String.valueOf(i+1) + ")",SNR[i] + " dB");
                }
                x += 4;
            }
        }

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

             Same structure as a GSA message, but this one is provenient from (Quasi-Zenith Satellite System (QZSS))

                     QZSS DOP and active satellites
                    eg1. $QZGSA,A,3,,,,,,16,18,,22,24,,,3.6,2.1,2.2*3C
                    eg2. $QZGSA,A,3,19,28,14,18,27,22,31,39,,,,,1.7,1.0,1.3*35

                    2    = Mode:
                           1=Fix not available
                           2=2D
                           3=3D
                    3-14 = IDs of SVs used in position fix (null for unused fields)
                    15   = PDOP
                    16   = HDOP
                    17   = VDOP

                    -----------------------------------

                     $QZGSA,A,3,04,05,,09,12,,,24,,,,,2.5,1.3,2.1*39

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


        h.put("Type","Dilution of precision and active satellites (Quasi-Zenith Satellite System (QZSS))");

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

    private LinkedHashMap<String, String> decodeGNGSA(String msg){

        /*

             Same structure as a GSA message, but this one is provenient from (Global Navigation Satellite System (GLONASS))

                     GLONASS DOP and active satellites
                    eg1. $GNGSA,A,3,,,,,,16,18,,22,24,,,3.6,2.1,2.2*3C
                    eg2. $GNGSA,A,3,19,28,14,18,27,22,31,39,,,,,1.7,1.0,1.3*35

                    2    = Mode:
                           1=Fix not available
                           2=2D
                           3=3D
                    3-14 = IDs of SVs used in position fix (null for unused fields)
                    15   = PDOP
                    16   = HDOP
                    17   = VDOP

                    -----------------------------------

                     $GNGSA,A,3,04,05,,09,12,,,24,,,,,2.5,1.3,2.1*39

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


        h.put("Type","Dilution of precision and active satellites (Global Navigation Satellite System or GLONASS)");

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


}
