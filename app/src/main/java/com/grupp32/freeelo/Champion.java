package com.grupp32.freeelo;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Alexander on 2016-04-04.
 */
public class Champion implements Serializable {
    private int championId;
    private String name;
    private String title;
    private String key;
    private String squareImageFull;
    private String ultimateName;
    private int ultimateMaxRank;
    private int[] ultimateCooldown;
    private String ultimateImage;

    public Champion(int championId, String name, String title, String key, String squareImageFull, String ultimateName, int ultimateMaxRank, int[] ultimateCooldown, String ultimateImage) {
        this.championId = championId;
        this.name = name;
        this.title = title;
        this.key = key;
        this.squareImageFull = squareImageFull;
        this.ultimateName = ultimateName;
        this.ultimateMaxRank = ultimateMaxRank;
        this.ultimateCooldown = ultimateCooldown;
        this.ultimateImage = ultimateImage;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getKey() {
        return key;
    }

    public String getSquareImageFull() {
        return squareImageFull;
    }

    public String getUltimateName() {
        return ultimateName;
    }

    public int getUltimateMaxRank() {
        return ultimateMaxRank;
    }

    public int[] getUltimateCooldown() {
        return ultimateCooldown;
    }

    public String getUltimateImage() {
        return ultimateImage;
    }

    @Override
    public String toString() {
        return "Champion{" +
                "championId=" + championId +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", key='" + key + '\'' +
                ", squareImageFull='" + squareImageFull + '\'' +
                ", ultimateName='" + ultimateName + '\'' +
                ", ultimateMaxRank=" + ultimateMaxRank +
                ", ultimateCooldown=" + Arrays.toString(ultimateCooldown) +
                ", ultimateImage='" + ultimateImage + '\'' +
                '}';
    }
}
