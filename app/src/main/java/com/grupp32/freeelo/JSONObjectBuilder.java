package com.grupp32.freeelo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;
import java.io.IOException;

/**
 * Created by Christoffer Nilsson on 2016-03-16.
 */
public abstract class JSONObjectBuilder {

    /**
     * Builds and returns a JSONObject from the given URL.
     * @param url the URL to parse from
     * @return a parsed JSONObject
     * @throws IOException
     * @throws JSONException
     */
    protected JSONObject buildObject(URL url) throws IOException, JSONException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));
        String res = "";
        String readLine;
        while ((readLine = in.readLine()) != null) {
            res += readLine;
        }
        in.close();
        return new JSONObject(res);
    }
}
