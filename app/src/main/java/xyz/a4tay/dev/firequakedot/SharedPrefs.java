package xyz.a4tay.dev.firequakedot;

import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by Daniel Montilla on 5/31/2017.
 */
public class SharedPrefs extends MapsActivity {
public static final String PREFERENCES = "SharedPrefs";

@Override
protected void onCreate(Bundle state)
    {
    super.onCreate(state);

    // Restore preferences
    SharedPreferences pref = this.getSharedPreferences("Test",0);
    SharedPreferences.Editor editor = pref.edit();
    editor.putString("VALUE", "test");
    editor.commit();
    }
}