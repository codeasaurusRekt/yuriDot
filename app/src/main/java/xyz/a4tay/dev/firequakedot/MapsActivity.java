package xyz.a4tay.dev.firequakedot;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
    {
    private GoogleMap mMap;
    private ClusterManager<MyItem> mClusterManager;
    public static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        }

    public void onSettingsClick(MenuItem item)
        {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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
            final LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17.0f));
            final View dropDot = findViewById(R.id.btnDropDot);
            final RestfulDot dotFun = new RestfulDot();
            JSONObject getParam = new JSONObject();
            mClusterManager = new ClusterManager<>(this, mMap);
            mMap.setOnCameraIdleListener(mClusterManager);
            List<MyItem> items = new ArrayList<MyItem>();
            try
                {
                getParam.put("lat", String.valueOf(lastKnownLocation.getLatitude()));
                getParam.put("lng", String.valueOf(lastKnownLocation.getLongitude()));
                getParam.put("range", 1);
                } catch (JSONException e)
                {
                Toast.makeText(MapsActivity.this, "Creating **GET** JSON Exception, y'all", Toast.LENGTH_LONG).show();
                }
            try
                {
                JSONArray dotArray = RestfulDot.getURL(getParam).getJSONArray("locations");
                final LinearLayout lotsOfHash = (LinearLayout) findViewById(R.id.bottomSheet);
                lotsOfHash.removeAllViews();
                lotsOfHash.setOrientation(LinearLayout.VERTICAL);

                for (int i = 0; i < dotArray.length(); i++)
                    {
                    JSONObject eachDot = dotArray.getJSONObject(i);
                    Double dotLat = eachDot.getDouble("lat");
                    Double dotLng = eachDot.getDouble("lng");
                    final LatLng dotMarker = new LatLng(dotLat, dotLng);
                    int dotColor = eachDot.getInt("colorCode");
                    Marker addDot;
                    String hash = eachDot.getString("hash");
                    final Location dotLocation = new Location("");
                    dotLocation.setLatitude(dotLat);
                    dotLocation.setLongitude(dotLng);
                    float detailDistance = lastKnownLocation.distanceTo(dotLocation);
                    String clusterDotSnippet = ""+detailDistance;

                    if (!hash.equals("#emptyHash"))
                        {
                        final TextView hashEntry = new TextView(this);
                        hashEntry.setText(hash);
                        hashEntry.setTextSize(25);
                        hashEntry.setPadding(10, 5, 100, 10);
                        lotsOfHash.addView(hashEntry);
                        TextView details = new TextView(this);
                        details.setText(detailDistance + " meters away");
                        details.setTextSize(15);
                        details.setPadding(70, 10, 100, 100);
                        lotsOfHash.addView(details);
                        hashEntry.setOnClickListener(new View.OnClickListener()

                            {
                            public void onClick(View v)
                                {mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dotMarker, 17.0f));}});
                        }

                    items.add(new MyItem(dotLat, dotLng, hash, clusterDotSnippet));
                    mClusterManager.addItems(items);
//                    switch (dotColor)
//                        {
//                        case 1:
//                            if (!hash.equals("#emptyHash"))
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)).title(hash));
//                                addDot.showInfoWindow();
//                                }
//                            else
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));
//                                }
//                            break;
//                        case 2:
//                            if (!hash.equals("#emptyHash"))
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.reddot)).title(hash));
//                                addDot.showInfoWindow();
//                                }
//                            else
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.reddot)));
//                                }
//                            break;
//                        case 3:
//                            if (!hash.equals("#emptyHash"))
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.greendot)).title(hash));
//                                addDot.showInfoWindow();
//                                }
//                            else
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.greendot)));
//                                }
//                            break;
//                        case 4:
//                            if (!hash.equals("#emptyHash"))
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.yellowdot)).title(hash));
//                                addDot.showInfoWindow();
//                                }
//                            else
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.yellowdot)));
//                                }
//                            break;
//                        case 5:
//                            if (!hash.equals("#emptyHash"))
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.orangedot)).title(hash));
//                                addDot.showInfoWindow();
//                                }
//                            else
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.orangedot)));
//                                }
//                            break;
//                        case 6:
//                            if (!hash.equals("#emptyHash"))
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinkdot)).title(hash));
//                                addDot.showInfoWindow();
//                                }
//                            else
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinkdot)));
//                                }
//                            break;
//                        case 7:
//                            if (!hash.equals("#emptyHash"))
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.purpledot)).title(hash));
//                                addDot.showInfoWindow();
//                                }
//                            else
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.purpledot)));
//                                }
//                            break;
//                        case 8:
//                            if (!hash.equals("#emptyHash"))
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.navydot)).title(hash));
//                                addDot.showInfoWindow();
//                                }
//                            else
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.navydot)));
//                                }
//                            break;
//                        default:
//                            if (!hash.equals("#emptyHash"))
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)).title(hash));
//                                addDot.showInfoWindow();
//                                }
//                            else
//                                {
//                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));
//                                }
//                            break;
//                        }
                    }
                } catch (java.lang.Exception e)
                {
                Toast.makeText(MapsActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }

            mClusterManager
                    .setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
                    @Override
                    public boolean onClusterClick(final Cluster<MyItem> cluster) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            cluster.getPosition(), (float) Math.floor(mMap
                                    .getCameraPosition().zoom + 1)), 300,
                            null);
                    return true;
                    }
                    });


            dropDot.setOnClickListener(new View.OnClickListener()
                {
                public void onClick(View v)
                    {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    Time dTime = new Time();
                    dTime.setToNow();
                    VisibleRegion vr = mMap.getProjection().getVisibleRegion();
                    Location bounds = new Location("bounds");
                    bounds.setLatitude(vr.latLngBounds.northeast.latitude);
                    bounds.setLongitude(vr.latLngBounds.southwest.longitude);
                    Location center = new Location("center");
                    center.setLatitude(vr.latLngBounds.getCenter().latitude);
                    center.setLongitude(vr.latLngBounds.getCenter().longitude);
                    float disBetween = center.distanceTo(bounds);
                    double range = disBetween / 1000;
                    DecimalFormat f = new DecimalFormat("##.00");
                    JSONObject dotParam = new JSONObject();
                    JSONObject getParam = new JSONObject();
                    Random randID = new Random();
//                    SharedPreferences prfs = getSharedPreferences("Test", Context.MODE_PRIVATE);
                    Integer oldDotID = 1;
//                    String potentialDotID = lat + randID.nextInt(99) + lon + randID.nextInt(99) + dTime.format2445().toString();
                    String newDotHash = "";
                    int colorCode = randID.nextInt(9);

                    try
                        {
                        dotParam.put("oldDotID", oldDotID);
//                        dotParam.put("potentialDotID", potentialDotID);
                        dotParam.put("lat", String.valueOf(lat));
                        dotParam.put("lng", String.valueOf(lon));
                        dotParam.put("hash", newDotHash);
                        dotParam.put("colorCode", colorCode);
                        } catch (JSONException e)
                        {
                        Toast.makeText(MapsActivity.this, "JSON Exception, y'all", Toast.LENGTH_LONG).show();
                        }

                    try
                        {
//                        Toast.makeText(MapsActivity.this, dotFun.putURL("http://dev.4tay.xyz:8080/yuri/api/location?" + dotFun.getPutDataString(dotParam)), Toast.LENGTH_LONG).show();
                        RestfulDot.putURL("http://dev.4tay.xyz:8080/yuri/api/location?" + RestfulDot.getPutDataString(dotParam));
                        } catch (java.lang.Exception e)
                        {
                        Toast.makeText(MapsActivity.this, "java.lang exception, y'all", Toast.LENGTH_LONG).show();
                        }

                    try
                        {
                        getParam.put("lat", String.valueOf(lat));
                        getParam.put("lng", String.valueOf(lon));
                        getParam.put("range", f.format(range));
                        } catch (JSONException e)
                        {
                        Toast.makeText(MapsActivity.this, "Creating **GET** JSON Exception, y'all", Toast.LENGTH_LONG).show();
                        }

                    try
                        {
                        JSONArray dotArray = RestfulDot.getURL(getParam).getJSONArray("locations");

                        final LinearLayout lotsOfHash = (LinearLayout) findViewById(R.id.bottomSheet);
                        lotsOfHash.removeAllViews();
                        lotsOfHash.setOrientation(LinearLayout.VERTICAL);

                        for (int i = 0; i < dotArray.length(); i++)
                            {
                            JSONObject eachDot = dotArray.getJSONObject(i);
                            Double dotLat = eachDot.getDouble("lat");
                            Double dotLng = eachDot.getDouble("lng");
                            final LatLng dotMarker = new LatLng(dotLat, dotLng);
                            int dotColor = eachDot.getInt("colorCode");
                            Marker addDot;
                            String hash = eachDot.getString("hash");
                            if (!hash.equals("#emptyHash"))
                                {
                                TextView hashEntry = new TextView(MapsActivity.this);
                                hashEntry.setText(hash);
                                hashEntry.setTextSize(25);
                                hashEntry.setPadding(10, 5, 100, 10);
                                lotsOfHash.addView(hashEntry);
                                TextView details = new TextView(MapsActivity.this);
                                Location dotLocation = new Location("");
                                dotLocation.setLatitude(dotLat);
                                dotLocation.setLongitude(dotLng);
                                float detailDistance = location.distanceTo(dotLocation);
                                details.setText(detailDistance + " meters away");
                                details.setTextSize(15);
                                details.setPadding(70, 10, 100, 100);
                                lotsOfHash.addView(details);
                                hashEntry.setOnClickListener(new View.OnClickListener()
                                    {
                                    public void onClick(View v)
                                        {mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dotMarker, 17.0f));}});
                                }

                            switch (dotColor)
                                {
                                case 1:
                                    if (!hash.equals("#emptyHash"))
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)).title(hash));
                                        addDot.showInfoWindow();
                                        }
                                    else
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));
                                        }
                                    break;
                                case 2:
                                    if (!hash.equals("#emptyHash"))
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.reddot)).title(hash));
                                        addDot.showInfoWindow();
                                        }
                                    else
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.reddot)));
                                        }
                                    break;
                                case 3:
                                    if (!hash.equals("#emptyHash"))
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.greendot)).title(hash));
                                        addDot.showInfoWindow();
                                        }
                                    else
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.greendot)));
                                        }
                                    break;
                                case 4:
                                    if (!hash.equals("#emptyHash"))
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.yellowdot)).title(hash));
                                        addDot.showInfoWindow();
                                        }
                                    else
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.yellowdot)));
                                        }
                                    break;
                                case 5:
                                    if (!hash.equals("#emptyHash"))
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.orangedot)).title(hash));
                                        addDot.showInfoWindow();
                                        }
                                    else
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.orangedot)));
                                        }
                                    break;
                                case 6:
                                    if (!hash.equals("#emptyHash"))
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinkdot)).title(hash));
                                        addDot.showInfoWindow();
                                        }
                                    else
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinkdot)));
                                        }
                                    break;
                                case 7:
                                    if (!hash.equals("#emptyHash"))
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.purpledot)).title(hash));
                                        addDot.showInfoWindow();
                                        }
                                    else
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.purpledot)));
                                        }
                                    break;
                                case 8:
                                    if (!hash.equals("#emptyHash"))
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.navydot)).title(hash));
                                        addDot.showInfoWindow();
                                        }
                                    else
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.navydot)));
                                        }
                                    break;
                                default:
                                    if (!hash.equals("#emptyHash"))
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)).title(hash));
                                        addDot.showInfoWindow();
                                        }
                                    else
                                        {
                                        addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));
                                        }
                                    break;
                                }
                            }
                        } catch (java.lang.Exception e)
                        {
                        Toast.makeText(MapsActivity.this, "java.lang exception ON THE GET, y'all", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            dropDot.setOnLongClickListener(new View.OnLongClickListener()
                {
                @Override
                public boolean onLongClick(View v)
                    {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
//                    builder.setTitle("#");

// Set up the input
                    final EditText input = new EditText(MapsActivity.this);
                    input.setHint("#");
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("Drop", new DialogInterface.OnClickListener()
                        {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                            {

                            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            double lat = location.getLatitude();
                            double lon = location.getLongitude();
                            Time dTime = new Time();
                            dTime.setToNow();
                            VisibleRegion vr = mMap.getProjection().getVisibleRegion();
                            Location bounds = new Location("bounds");
                            bounds.setLatitude(vr.latLngBounds.northeast.latitude);
                            bounds.setLongitude(vr.latLngBounds.southwest.longitude);
                            Location center = new Location("center");
                            center.setLatitude(vr.latLngBounds.getCenter().latitude);
                            center.setLongitude(vr.latLngBounds.getCenter().longitude);
                            float disBetween = center.distanceTo(bounds);
                            double range = disBetween / 1000;
                            DecimalFormat f = new DecimalFormat("##.00");
                            JSONObject dotParam = new JSONObject();
                            JSONObject getParam = new JSONObject();
                            Random randID = new Random();
                            SharedPreferences prfs = getSharedPreferences("Test", Context.MODE_PRIVATE);
                            String oldDotID = prfs.getString("VALUE", lat + randID.nextInt(99) + lon + randID.nextInt(99) + dTime.format2445().toString());
                            String potentialDotID = lat + randID.nextInt(99) + lon + randID.nextInt(99) + dTime.format2445().toString();
                            String preHash = "#" + input.getText();
                            String newDotHash = preHash.replace(" ", "");
                            int colorCode = randID.nextInt(9);

                            try
                                {
                                dotParam.put("oldDotID", oldDotID);
                                dotParam.put("potentialDotID", potentialDotID);
                                dotParam.put("lat", String.valueOf(lat));
                                dotParam.put("lng", String.valueOf(lon));
                                dotParam.put("hash", newDotHash);
                                dotParam.put("colorCode", colorCode);
                                } catch (JSONException e)
                                {
                                Toast.makeText(MapsActivity.this, "JSON Exception, y'all", Toast.LENGTH_LONG).show();
                                }

                            try
                                {
//                        Toast.makeText(MapsActivity.this, dotFun.putURL("http://dev.4tay.xyz:8080/yuri/api/location?" + dotFun.getPutDataString(dotParam)), Toast.LENGTH_LONG).show();
                                RestfulDot.putURL("http://dev.4tay.xyz:8080/yuri/api/location?" + RestfulDot.getPutDataString(dotParam));
                                } catch (java.lang.Exception e)
                                {
                                Toast.makeText(MapsActivity.this, "java.lang exception, y'all", Toast.LENGTH_LONG).show();
                                }

                            try
                                {
                                getParam.put("lat", String.valueOf(lat));
                                getParam.put("lng", String.valueOf(lon));
                                getParam.put("range", f.format(range));
                                } catch (JSONException e)
                                {
                                Toast.makeText(MapsActivity.this, "Creating **GET** JSON Exception, y'all", Toast.LENGTH_LONG).show();
                                }

                            try
                                {
                                JSONArray dotArray = RestfulDot.getURL(getParam).getJSONArray("locations");
                                final LinearLayout lotsOfHash = (LinearLayout) findViewById(R.id.bottomSheet);
                                lotsOfHash.removeAllViews();
                                lotsOfHash.setOrientation(LinearLayout.VERTICAL);
                                for (int i = 0; i < dotArray.length(); i++)
                                    {
                                    JSONObject eachDot = dotArray.getJSONObject(i);
                                    Double dotLat = eachDot.getDouble("lat");
                                    Double dotLng = eachDot.getDouble("lng");
                                    final LatLng dotMarker = new LatLng(dotLat, dotLng);
                                    int dotColor = eachDot.getInt("colorCode");
                                    Marker addDot;
                                    String hash = eachDot.getString("hash");
                                    if (!hash.equals("#emptyHash"))
                                        {
                                        TextView hashEntry = new TextView(MapsActivity.this);
                                        hashEntry.setText(hash);
                                        hashEntry.setTextSize(25);
                                        hashEntry.setPadding(10, 5, 100, 10);
                                        lotsOfHash.addView(hashEntry);

                                        TextView details = new TextView(MapsActivity.this);
                                        Location dotLocation = new Location("");
                                        dotLocation.setLatitude(dotLat);
                                        dotLocation.setLongitude(dotLng);
                                        float detailDistance = location.distanceTo(dotLocation);
                                        details.setText(detailDistance + " meters away");
                                        details.setTextSize(15);
                                        details.setPadding(70, 10, 100, 100);
                                        lotsOfHash.addView(details);
                                        hashEntry.setOnClickListener(new View.OnClickListener()
                                            {
                                            public void onClick(View v)
                                                {mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dotMarker, 17.0f));}});
                                        }

                                    switch (dotColor)
                                        {
                                        case 1:
                                            if (!hash.equals("#emptyHash"))
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)).title(hash));
                                                addDot.showInfoWindow();
                                                }
                                            else
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));
                                                }
                                            break;
                                        case 2:
                                            if (!hash.equals("#emptyHash"))
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.reddot)).title(hash));
                                                addDot.showInfoWindow();
                                                }
                                            else
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.reddot)));
                                                }
                                            break;
                                        case 3:
                                            if (!hash.equals("#emptyHash"))
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.greendot)).title(hash));
                                                addDot.showInfoWindow();
                                                }
                                            else
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.greendot)));
                                                }
                                            break;
                                        case 4:
                                            if (!hash.equals("#emptyHash"))
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.yellowdot)).title(hash));
                                                addDot.showInfoWindow();
                                                }
                                            else
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.yellowdot)));
                                                }
                                            break;
                                        case 5:
                                            if (!hash.equals("#emptyHash"))
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.orangedot)).title(hash));
                                                addDot.showInfoWindow();
                                                }
                                            else
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.orangedot)));
                                                }
                                            break;
                                        case 6:
                                            if (!hash.equals("#emptyHash"))
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinkdot)).title(hash));
                                                addDot.showInfoWindow();
                                                }
                                            else
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.pinkdot)));
                                                }
                                            break;
                                        case 7:
                                            if (!hash.equals("#emptyHash"))
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.purpledot)).title(hash));
                                                addDot.showInfoWindow();
                                                }
                                            else
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.purpledot)));
                                                }
                                            break;
                                        case 8:
                                            if (!hash.equals("#emptyHash"))
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.navydot)).title(hash));
                                                addDot.showInfoWindow();
                                                }
                                            else
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.navydot)));
                                                }
                                            break;
                                        default:
                                            if (!hash.equals("#emptyHash"))
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)).title(hash));
                                                addDot.showInfoWindow();
                                                }
                                            else
                                                {
                                                addDot = mMap.addMarker(new MarkerOptions().position(dotMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot)));
                                                }
                                            break;
                                        }
                                    }
                                } catch (java.lang.Exception e)
                                {
                                Toast.makeText(MapsActivity.this, "java.lang exception ON THE GET, y'all", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                            {
                            dialog.cancel();
                            }
                        });

                    builder.show();
                    return true;
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
                    } else
                    {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                }
            }
        }
    }