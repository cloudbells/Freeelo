package collection;

import java.io.Serializable;

/**
 * Class represents a Spell in League of Legends.
 *
 * @author Christoffer Nilsson
 */
public class Spell implements Serializable {

    private int id;
    private int cooldown;
    private String name;
    private String image;

    /**
     * Constructor for Spell-objects, takes in parameters to fully initialize a Spell-object
     *
     * @param id       spell id provided from the API
     * @param name     spell name
     * @param image    spell image (file name + file extension, ie. "SummonerFlash.png")
     * @param cooldown spell cooldown
     */
    public Spell(int id, String name, String image, int cooldown) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.cooldown = cooldown;
    }

    /**
     * Returns current spell id.
     *
     * @return <code>int</code> - current spell id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns current spell name.
     *
     * @return <code>String</code> - current spell name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns current spell image (file name + file extension, ie. "SummonerFlash.png").
     *
     * @return <code>String</code> - current spell image
     */
    public String getImage() {
        return image;
    }

    /**
     * Returns current spell cooldown.
     *
     * @return <code>int</code> - current spell cooldown
     */
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public String toString() {
        return name;
    }
}