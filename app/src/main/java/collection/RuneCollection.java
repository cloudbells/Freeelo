package collection;

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
	private ArrayList<Rune> runes = new ArrayList<Rune>();
	private String finalizedStats = "";

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

	public RuneCollection finalizeStats() {
		HashMap<String, Double> stats = new HashMap<String, Double>();
		HashMap<String, String> descriptions = new HashMap<String, String>();
		for (Rune rune : runes) {
			String statType = rune.getStatType();
			if (!stats.containsKey(statType)) {
				stats.put(statType, rune.getStat() * rune.getCount());
				descriptions.put(statType, rune.getDescription());
			} else if (stats.containsKey(statType)) {
				double tempStat = stats.get(statType);
				stats.remove(statType);
				stats.put(statType, tempStat + rune.getStat() * rune.getCount());
				descriptions.put(statType, rune.getDescription());
			}
		}
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
			if (description.contains("(")) {
				shortDesc = description.substring(description.indexOf(" "), description.indexOf("(") - 1);
			} else {
				shortDesc = description.substring(description.indexOf(" "), description.length());
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
		return this;
	}

	public String toString() {
		return finalizedStats;
	}
}