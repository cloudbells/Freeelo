package collection;

import android.util.Log;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Christoffer Nilsson on 2016-04-07.
 */
public class RuneCollection implements Serializable {

    private HashMap<String, Double> stats = new HashMap<String, Double>();
    private HashMap<String, String> descriptions = new HashMap<String, String>();
	private ArrayList<Rune> runes = new ArrayList<Rune>();
	private String finalizedStats = "";

	public void add(Rune rune) {
		runes.add(rune);
	}

	public Rune get(int index) throws IndexOutOfBoundsException {
        if (index > runes.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return runes.get(index);
	}

	public int size() {
		return runes.size();
	}

	public void finalizeStats() {
		for (Rune rune : runes) {
			String statType = rune.getStatType();
            String statType2 = rune.getStatType2();
            double stat = rune.getStat();
            double stat2 = rune.getStat2();
			addRune(statType, stat, rune);
            if (statType2 != "" && stat2 != 0) {
                Log.e("RuneCollection: ", "Found a HYBRID rune! Stat1: " + stat + ", Stat2: " + stat2);
                addRune(statType2, stat2, rune);
            }
		}
		fixDesc();
	}

    private void addRune(String statType, double stat, Rune rune) {
        if (!stats.containsKey(statType)) {
            stats.put(statType, stat * rune.getCount());
            descriptions.put(statType, rune.getDescription());
        } else {
            double tempStat = stats.get(statType);
            stats.remove(statType);
            stats.put(statType, tempStat + stat * rune.getCount());
            descriptions.put(statType, rune.getDescription());
        }
    }

    private void fixDesc() {
        Iterator it = stats.entrySet().iterator();
        boolean percent = false;
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            double value = (double) entry.getValue();
            String description = descriptions.get(key);
            percent = false;
            if (key.contains("Percent")) {
                percent = true;
            }
            if (key.contains("Regen")) {
                value *= 5;
            }
            String shortDesc = ""; // cooldowns per level
            if (description.contains("(")) { // Remove the "at level 18" stuff
                shortDesc = description.substring(description.indexOf(" "), description.indexOf("(") - 1);
            } else {
                shortDesc = description.substring(description.indexOf(" "), description.length());
            }
            // The following is necessary for hybrid runes to work.
            if (key.equals("rFlatMagicPenetrationMod")) {
                shortDesc = " magic penetration";
                Log.e("RuneCollection: ", shortDesc);
            } else if (key.equals("rFlatArmorPenetrationMod")) {
                shortDesc = " armor penetration";
                Log.e("RuneCollection: ", shortDesc);
            }
            String finalStats = "";
            // If the desc contains the percent sign, add it to the finalStats and format, else dont add and format
            if (percent) {
                finalStats = new DecimalFormat("0.00").format(value * 100) + "%";
            } else {
                finalStats = new DecimalFormat("0.00").format(value);
            }
            // Adds a '+' if the stat doesn't begin with a '-' (which means it's positive).
            if (!Double.toString(value).startsWith("-")) {
                finalStats = "+" + finalStats;
            }
            finalizedStats += finalStats + shortDesc + "\n";
        }
    }

	public String toString() {
		return finalizedStats;
	}
}