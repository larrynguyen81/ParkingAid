/*
 * A data object for storing longitude and latitude for gps locations
 */
package Framework.Gps;

import java.io.Serializable;

/**
 *
 * @author George Hatt/Michal Zak
 */
public class GpsTag implements Serializable{
    static final long serialVersionUID = 4L;
    private double longitude,latitude,altitude;
    private String name;

    public GpsTag(String name, double latitude, double longitude, double altitude){
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    public GpsTag(String name, double latitude, double longitude){
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;

    }
    
    /**
     * returns the longitude for the gps location
     * @return
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * returns the latitude for the gps location
     * @return
     */
    public double getLatitude() {
        return latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    /**
     * return the name associated with the gps location
     * @return
     */
    public String getName() {
        return name;
    }
    
    @Override
    public String toString(){
        return name +": "+longitude+","+latitude;
    }

    public static boolean isSameLocation(GpsTag a, GpsTag b){

        if(a == null || b == null){
            return false;
        }

        if (a.getLatitude() != b.getLatitude()) {
                return false;
        }

        if (a.getLongitude() != b.getLongitude()) {
                return false;
            }


        return true;
    }
}
