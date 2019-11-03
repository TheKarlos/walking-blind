package com.example.walkingblindnative;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TextGenerator {
    public TextGenerator(){

    }

    public static String getMessage(double lat, double lon, JSONArray array)
    {
        String out = "";
        for (int i=0; i < array.length(); i++)
        {
            try {
                JSONObject oneObject = array.getJSONObject(i);
                // Pulling items from the array
                String oneObjectsItem = oneObject.getString("Name");
                out += oneObjectsItem + " is nearby. ";
            } catch (JSONException e) {
                Log.e("TextGen", e.getMessage());
            }
        }

        Log.i("MESSAGE TO BE SAID", out);

        return out;
    }
}
