package com.grupp32.freeelo;

/**
 * Created by Christoffer Nilsson on 2016-04-05.
 */
public class Spell {

    private int id;
    private String name;
    private String image;
    private int cooldown;

    public Spell(int id, String name, String image, int cooldown) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.cooldown = cooldown;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public int getCooldown() {
        return cooldown;
    }
}
