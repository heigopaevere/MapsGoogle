package com.example.opilane.mapsgoogle;


import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class Placeinfo {
    private String name;
    private String address;
    private String phoneNimber;
    private String id;
    private Uri websiteUri;
    private LatLng latLng;
    private float rating;
    private String attributions;

    public Placeinfo() {
        this.name = name;
        this.address = address;
        this.phoneNimber = phoneNimber;
        this.id = id;
        this.websiteUri = websiteUri;
        this.latLng = latLng;
        this.rating = rating;
        this.attributions = attributions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNimber() {
        return phoneNimber;
    }

    public void setPhoneNimber(String phoneNimber) {
        this.phoneNimber = phoneNimber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;

    }
    @Override
    public String toString() {
        return "Placeinfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNimber='" + phoneNimber + '\'' +
                ", id='" + id + '\'' +
                ", websiteUri=" + websiteUri +
                ", latLng=" + latLng +
                ", rating=" + rating +
                ", attributions='" + attributions + '\'' +
                '}';
    }
}
