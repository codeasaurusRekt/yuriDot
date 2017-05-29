package xyz.a4tay.dev.firequakedot;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.format.Time;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
    {
    private GoogleMap mMap;
    public static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        }

    @Override
    public void onMapReady(GoogleMap googleMap)
        {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
            {
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            final LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
            final RestfulDot dotFun = new RestfulDot();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17.0f));
            View dropDot = findViewById(R.id.btnDropDot);
            JSONObject getParam = new JSONObject();

            try
                {
                getParam.put("lat", String.valueOf(lastKnownLocation.getLatitude()));
                getParam.put("lng", String.valueOf(lastKnownLocation.getLongitude()));
                getParam.put("range",1);
                }
            catch(JSONException e)
                {
                Toast.makeText(MapsActivity.this, "Creating **GET** JSON Exception, y'all", Toast.LENGTH_LONG).show();
                }
            try
                {
                JSONArray dotArray = dotFun.getURL(getParam).getJSONArray("locations");

                for (int i = 0; i < dotArray.length(); i++)
                    {
                    JSONObject childJSONObject = dotArray.getJSONObject(i);
                    Double dotLat = childJSONObject.getDouble("lat");
                    Double dotLng = childJSONObject.getDouble("lng");
                    LatLng dotMarker = new LatLng(dotLat,dotLng);
                    Random dotRan = new Random();
                    int dotColor = dotRan.nextInt(8);
                    Marker addDot;
                    switch (dotColor)
                        {
                        case 1: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));
                                break;
                        case 2: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.reddot)));
                                break;
                        case 3: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.greendot)));
                                break;
                        case 4: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.yellowdot)));
                                break;
                        case 5: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.orangedot)));
                                break;
                        case 6: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinkdot)));
                                break;
                        case 7: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.purpledot)));
                                break;
                        case 8: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.navydot)));
                                break;
                        default:addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));
                            break;
                        }
                    }
                }
            catch(java.lang.Exception e)
                {
                Toast.makeText(MapsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }


            dropDot.setOnClickListener(new View.OnClickListener()
                {
                public void onClick(View v)
                    {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    Time dTime = new Time();
                    dTime.setToNow();
                    VisibleRegion vr = mMap.getProjection().getVisibleRegion();
                    Location bounds = new Location("bounds");
                    bounds.setLatitude(vr.latLngBounds.northeast.latitude);
                    bounds.setLongitude(vr.latLngBounds.southwest.longitude);
                    Location center=new Location("center");
                    center.setLatitude( vr.latLngBounds.getCenter().latitude);
                    center.setLongitude( vr.latLngBounds.getCenter().longitude);
                    float disBetween = center.distanceTo(bounds);
                    double range = disBetween/1000;
                    DecimalFormat f = new DecimalFormat("##.00");
                    JSONObject dotParam = new JSONObject();
                    JSONObject getParam = new JSONObject();

                    try
                        {
                        dotParam.put("lat", String.valueOf(lat));
                        dotParam.put("lng", String.valueOf(lon));
                        dotParam.put("range", f.format(range));
                        dotParam.put("dtime", dTime.format2445().toString());
                        }
                    catch(JSONException e)
                        {
                        Toast.makeText(MapsActivity.this, "JSON Exception, y'all", Toast.LENGTH_LONG).show();
                        }
                    try
                        {
//                        Toast.makeText(MapsActivity.this, dotFun.postURL("http://dev.4tay.xyz:8080/yuri/api/location?" + dotFun.getPostDataString(dotParam)), Toast.LENGTH_LONG).show();
                        dotFun.postURL("http://dev.4tay.xyz:8080/yuri/api/location?" + dotFun.getPostDataString(dotParam));
                        }
                    catch(java.lang.Exception e)
                        {
                        Toast.makeText(MapsActivity.this, "java.lang exception, y'all", Toast.LENGTH_LONG).show();
                        }
                    try
                        {
                        getParam.put("lat", String.valueOf(lat));
                        getParam.put("lng", String.valueOf(lon));
                        getParam.put("range",f.format(range));
                        }
                    catch(JSONException e)
                        {
                        Toast.makeText(MapsActivity.this, "Creating **GET** JSON Exception, y'all", Toast.LENGTH_LONG).show();
                        }
                    try
                        {
                        JSONArray dotArray = dotFun.getURL(getParam).getJSONArray("locations");

                        for (int i = 0; i < dotArray.length(); i++)
                            {
                            JSONObject childJSONObject = dotArray.getJSONObject(i);
                            Double dotLat = childJSONObject.getDouble("lat");
                            Double dotLng = childJSONObject.getDouble("lng");
                            LatLng dotMarker = new LatLng(dotLat,dotLng);
                            Random dotRan = new Random();
                            int dotColor = dotRan.nextInt(9);
                            Marker addDot;
                            switch (dotColor)
                                {
                                case 1: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));
                                    break;
                                case 2: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.reddot)));
                                    break;
                                case 3: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.greendot)));
                                    break;
                                case 4: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.yellowdot)));
                                    break;
                                case 5: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.orangedot)));
                                    break;
                                case 6: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinkdot)));
                                    break;
                                case 7: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.purpledot)));
                                    break;
                                case 8: addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.navydot)));
                                    break;
                                default:addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));
                                    break;
                                }
                            }
                        }
                    catch(java.lang.Exception e)
                        {
                        Toast.makeText(MapsActivity.this, "java.lang exception ON THE GET, y'all", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } else
            {
            Toast.makeText(MapsActivity.this, "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
                {
                mMap.setMyLocationEnabled(true);
                }

            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)
                {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION))
                    {
                    }
                else
                    {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                }
            }
        }
    }