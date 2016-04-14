package com.grupp32.freeelo;

/**
 * Created by Alexander on 2016-04-14.
 */
public class Rune {
    private int count;
    private String statType; // ie. "rFlatMagicPenetrationMod"
    private double stat;
    private String description;

    public Rune(int count, String statType, double stat, String description) {
        this.count = count;
        this.statType = statType;
        this.stat = stat;
        this.description = description;
    }

    public double getStat() {
        return stat;
    }

    public int getCount() {
        return count;
    }

    public String getStatType() {
        return statType;
    }

    public String getDescription() {
        return description;
    }
}
