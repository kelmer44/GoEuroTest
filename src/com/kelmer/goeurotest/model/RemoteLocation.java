package com.kelmer.goeurotest.model;

import org.json.JSONException;
import org.json.JSONObject;

public class RemoteLocation {

    public String type;
    public int id;
    public double latitude, longitude;
    public String name;
    public String country;
    
    
    public RemoteLocation(final String type, final int id, final double lat, final double lon, final String name, final String country)
    {
        this.type = type;
        this.id = id;
        this.latitude = lat;
        this.longitude = lon;
        this.name = name;
        this.country = country;
    }

    
    
    public RemoteLocation(final int id, final double lat, final double lon, final String name, final String country)
    {
        this.id = id;
        this.latitude = lat;
        this.longitude = lon;
        this.name = name;
    }

    public RemoteLocation(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLat(double lat) {
        this.latitude = lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLon(double lon) {
        this.longitude = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getNameCountry()
    {
        return this.getName() + " (" + this.getCountry() + ")";
    }
    /**
     * Generate from Json Object, this way each domain object knows how to parse itself
     * @param res JSONobject
     * @return instance of a Location object
     * @throws JSONException
     */
    public static RemoteLocation fromJson(final JSONObject res) throws JSONException
    {
        String type = res.getString("type");
        final Integer id = res.getInt("_id");
        final String nameCountry = res.getString("name");
        final String name = nameCountry.substring(0, nameCountry.lastIndexOf(',')).trim();
        final String country = nameCountry.substring(nameCountry.lastIndexOf(',') + 1).trim();

        final JSONObject ref = res.getJSONObject("geo_position");

        
        final double lat = ref.getDouble("latitude");
        final double lon = ref.getDouble("longitude");
        
        return new RemoteLocation(type, id, lat, lon, name, country);
    }
    
    
    
    @Override
    public String toString()
    {
        //for landscape search
        return name; 
    }



    public String getCountry() {
        return country;
    }



    public void setCountry(String country) {
        this.country = country;
    }

}
