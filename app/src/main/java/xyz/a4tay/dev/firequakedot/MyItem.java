package xyz.a4tay.dev.firequakedot;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by captain on 6/19/17.
 */
public class MyItem implements ClusterItem
    {
private final LatLng mPosition;
String mTitle;
String mSnippet;

public MyItem(double lat, double lng) {
mPosition = new LatLng(lat, lng);
}

public MyItem(double lat, double lng, String title, String snippet) {
mPosition = new LatLng(lat, lng);
mTitle = title;
mSnippet = snippet;
}

@Override
public LatLng getPosition() {
return mPosition;
}

@Override
public String getTitle() {
return mTitle;
}

@Override
public String getSnippet() {
return mSnippet;
}
}

