package com.iscte.mobileapps.satellitedump;


public class CoordinatesCalculator {

    public double latitudeCalculator(double z, double r){

        return Math.asin(z/r);

    }

    public double longitudeCalculator(double y, double x){

        return Math.atan2(y,x);

    }

    public double getX(double r, double azimuth, double elevation){

        return r * Math.sin(azimuth) * Math.cos(elevation);

    }

    public double getY(double r, double azimuth, double elevation){

        return r * Math.sin(azimuth) * Math.sin(elevation);

    }

    public double getZ(double r, double azimuth){

        return r * Math.cos(azimuth);

    }


}
