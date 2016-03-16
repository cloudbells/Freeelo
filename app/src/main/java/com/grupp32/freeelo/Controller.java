package com.grupp32.freeelo;

import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Christoffer Nilsson on 2016-03-16.
 */
public class Controller extends JSONObjectBuilder {

    // Variables.
    private final String KEY = "?api_key=8088586e-a695-4cc5-80c2-be3b6fcec3e5"; // Chris nyckel
    private final String API_URL = "https://euw.api.pvp.net/api/lol/";
    private final String API_VERSION = "/v1.4";

    public int getSummonerId(String summonerName, String region) throws IOException, JSONException {
        System.out.print(API_URL + region + API_VERSION + "/summoner/by-name/" + summonerName + KEY);
        JSONObject obj = buildObject(new URL(API_URL + region + API_VERSION + "/summoner/by-name/" + summonerName + KEY));
        JSONObject summoner = obj.getJSONObject(summonerName);
        return summoner.getInt("id");
    }
}
