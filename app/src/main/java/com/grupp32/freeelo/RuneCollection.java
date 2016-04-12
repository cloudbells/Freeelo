package com.grupp32.freeelo;

import java.util.ArrayList;

/**
 * Created by Christoffer Nilsson on 2016-04-07.
 */
public class RuneCollection {

    private ArrayList<String> runes = new ArrayList<String>();

    public RuneCollection add(String rune) {
        runes.add(rune);
        return this;
    }

    public String toString() {
        String res = "";
        for (String rune: runes) {
            res += rune;
        }
        return res;
    }
}
