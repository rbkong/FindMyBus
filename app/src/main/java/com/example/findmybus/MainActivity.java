package com.example.findmybus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.wearable.Asset;
import com.opencsv.CSVReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MainActivity" ;
    GoogleMap gmap;
    FusedLocationProviderClient fusedLocationClient;

    TextView lbladdr;
    TextView lblzip;
    EditText editaddr;
    EditText editzipCode;
    Switch swicth;
    private RequestQueue mQueue;
    public static List<Bus> ghost;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        init();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Spinner dir_spin = findViewById(R.id.direction);
        Spinner bus_spin = findViewById(R.id.bus_number);
        ArrayList<String> directions = new ArrayList<>();
        directions.add("EAST");
        directions.add("WEST");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, directions);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        dir_spin.setAdapter(arrayAdapter);

        ArrayList<String> busnum = new ArrayList<>();
        busnum.add("1");
        busnum.add("2");
        busnum.add("4");
        busnum.add("6");
        busnum.add("12");
        busnum.add("15");
        busnum.add("17");
        busnum.add("18");
        busnum.add("19");
        busnum.add("20");
        busnum.add("21");
        busnum.add("22");
        busnum.add("23");
        busnum.add("25");
        busnum.add("27");
        busnum.add("29");
        busnum.add("31");
        busnum.add("40");
        busnum.add("43");
        busnum.add("45");
        busnum.add("49");
        busnum.add("50");
        busnum.add("52");
        busnum.add("53");
        busnum.add("54");
        busnum.add("61");
        busnum.add("62");
        busnum.add("63");
        busnum.add("64");
        busnum.add("65");
        busnum.add("66");
        busnum.add("67");
        busnum.add("68");
        busnum.add("71");
        busnum.add("72");
        busnum.add("75");
        busnum.add("77");
        busnum.add("78");
        busnum.add("82");
        busnum.add("93");
        busnum.add("94");
        busnum.add("96");
        busnum.add("99");
        ArrayAdapter<String> barrayAdapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, busnum);
        barrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        bus_spin.setAdapter(barrayAdapter);
        mQueue = Volley.newRequestQueue(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        gmap = googleMap;
        LatLng louisville = new LatLng(38.2527, -85.7585);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(louisville));
        gmap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }


    public void findData(View view) throws InterruptedException {
        if(swicth.isChecked())
        {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                gmap.clear();
                                LatLng newLocation = new LatLng(location.getLatitude(),
                                        location.getLongitude());
                                gmap.addMarker(new MarkerOptions().position(newLocation)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.client))
                                        .title("Your Location"));
                                gmap.animateCamera(CameraUpdateFactory. newLatLngZoom(newLocation, 12));
                            }
                            else
                            {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        R.string.noLocation, Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    });

        }else
        {
            gmap.clear();
            Geocoder geo = new Geocoder(this);
            List<Address> addresses = null;

            String fullAddress = editaddr.getText() + ", " + "Louisville, " + "KY " +
                    editzipCode.getText();

            try {
                addresses = geo.getFromLocationName(fullAddress, 1);
            } catch (IOException ex)
            {
                Toast toast = Toast.makeText(getApplicationContext(), ex.toString()
                ,Toast.LENGTH_SHORT);
                toast.show();
            }

            LatLng point = new LatLng(addresses.get(0).getLatitude(),addresses.get(0).getLongitude());

            gmap.addMarker(new MarkerOptions().position(point)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.client))
                    .title("Your Location"));
            gmap.animateCamera(CameraUpdateFactory. newLatLngZoom(point, 12));
        }

        Spinner number, dirx;
        number = findViewById(R.id.bus_number);
        dirx = findViewById(R.id.direction);
        String BusID = (String) number.getSelectedItem();
        String DirX = ((String) dirx.getSelectedItem()).equals("EAST")? "E":
                ((String) dirx.getSelectedItem()).equals("WEST")? "W" : "N";
        getBuses(BusID, DirX);

    }

    public void init()
    {
       lbladdr = findViewById(R.id.lbl_address);
        lblzip = findViewById(R.id.lbl_zip);
        editaddr = findViewById(R.id.edit_address);
        editzipCode = findViewById(R.id.edit_zip);
        swicth = findViewById(R.id.use_gps);

        lbladdr.setEnabled(true);
        lblzip.setEnabled(true);
        editaddr.setEnabled(true);
        editzipCode.setEnabled(true);
    }
    public void updateForm(View view)
    {
        if(!swicth.isChecked())
        {
            lbladdr.setEnabled(true);
            lblzip.setEnabled(true);
            editaddr.setEnabled(true);
            editzipCode.setEnabled(true);
        }else {
            lbladdr.setEnabled(false);
            lblzip.setEnabled(false);
            editaddr.setEnabled(false);
            editzipCode.setEnabled(false);
        }
    }

    private void getBuses(final String _bus_id, final String direction)
    {
        //final List<Bus> buses = new ArrayList<Bus>();
        String url = "http://gtfsrealtime.ridetarc.org/realtime/vehicle/vehiclepositions.json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int count = 1;
                        try {
                            JSONArray entity = response.getJSONArray("entity");

                            for(int i = 0; i <= entity.length(); ++i)
                            {
                                JSONObject item = entity.getJSONObject(i);
                                JSONObject vehicle = item.getJSONObject("vehicle");
                                JSONObject position = vehicle.getJSONObject("position");
                                JSONObject trip = vehicle.getJSONObject("trip");
                                LatLng coords = new LatLng(position.getDouble("latitude"),
                                        position.getDouble("longitude"));
                                String routeId = trip.getString("route_id");
                                String trip_id = trip.getString("trip_id");
                                JSONObject busVehicle = vehicle.getJSONObject("vehicle");
                                String bus_id = busVehicle.getString("id");
                                Bus newBus = new Bus(bus_id, coords, routeId, trip_id);

                                if(newBus.getRoute_id().equals(_bus_id))
                                {
                                    String ret = ReadCsv(newBus.getTrip_id());
                                    if(ret.equals(direction))
                                    {
                                        gmap.addMarker(new MarkerOptions().position(newBus.getPosition())
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon))
                                                .title("Bus " + newBus.getRoute_id()));
                                    }
                                }



                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private String ReadCsv(String trip_id)
    {
        try {
            InputStreamReader is = new InputStreamReader(getAssets().open("trips.csv"));
            BufferedReader reader = new BufferedReader(is);
            reader.readLine();
            String line;
            while((line = reader.readLine() )!= null)
            {
                String[] blocks = line.split(",");
                if(blocks[2].equals(trip_id))
                {
                    if(blocks[5].equals("0"))
                    {
                        return "E";
                    }
                    else
                    {
                        return "W";
                    }
                }
            }
            return "N";
        } catch (IOException e) {
            e.printStackTrace();
            return "X";
        }

    }
}
