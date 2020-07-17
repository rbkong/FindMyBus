package com.example.findmybus;

import com.google.android.gms.maps.model.LatLng;

public class Bus {
    private String bus_id;
    private LatLng position;
    private String route_id;
    private String trip_id;

    public Bus(String busid, LatLng pos, String routeId, String tripid)
    {
        this.bus_id = busid;
        this.position = pos;
        this.route_id = routeId;
        this.trip_id = tripid;
    }

    public String getBus_id() {
        return bus_id;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public String getRoute_id() {
        return route_id;
    }

    public void setBus_id(String bus_id) {
        this.bus_id = bus_id;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }
}
