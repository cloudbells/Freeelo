package com.grupp32.freeelo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Christoffer Nilsson on 2016-04-07.
 */
public class RuneCollection implements Serializable {

    private ArrayList<String> runes = new ArrayList<String>();

    public void add(String runeType) {
        runes.add(runeType);
    }

    public String get(int index) {
        if (index <= runes.size()) {
            return runes.get(index);
        }
        return "NULL";
    }

    public int size() {
        return runes.size();
    }

    public String toString() {
        String res = "";
        for (String rune: runes) {
            res += rune;
        }
        return res;
    }
}
