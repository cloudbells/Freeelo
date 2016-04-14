package com.grupp32.freeelo;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Christoffer Nilsson on 2016-04-07.
 */
public class RuneCollection implements Serializable {

    private ArrayList<Rune> runes = new ArrayList<Rune>();

    public void add(Rune rune) {
        runes.add(rune);
    }

    public Rune get(int index) {
        if (index <= runes.size()) {
            return runes.get(index);
        }
        return null;
    }

    public int size() {
        return runes.size();
    }

    public String toString() {
        String res = "";
        boolean percent;
        String desc;
        int count;
        double stat;
        for(Rune rune: runes) {
            desc = rune.getDescription();
            stat = rune.getStat();
            count = rune.getCount();

            percent = false;
            if (desc.contains("%")) {
                percent = true;
            }

            if (desc.contains("sec")) {
                stat *= 5;
            }
            String shortDesc = ""; // cooldowns per level
            if (desc.contains("(")) {
                shortDesc = desc.substring(desc.indexOf(" "), desc.indexOf("(") - 1);
            } else {
                shortDesc = desc.substring(desc.indexOf(" "), desc.length());
            }
            String finalStats = "";
            // If the desc contains the percent sign, add it to the finalStats and format, else dont add and format
            if (percent) {
                finalStats = new DecimalFormat("0.00").format(stat * count * 100) + "%";
            } else {
                finalStats = new DecimalFormat("0.00").format(stat * count);
            }
            // Adds a '+' if the stat doesn't begin with a '-' (which means it's positive).
            if (!Double.toString(stat).startsWith("-")) {
                finalStats = "+" + finalStats;
            }

            res += finalStats + shortDesc + "\n";
        }
        return res;
    }
}
