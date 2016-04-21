package collection;

import java.io.Serializable;

/**
 * @author Christoffer Nilsson
 */
public class Spell implements Serializable {

	private int id;
	private int cooldown;
	private String name;
	private String image;

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

	@Override
	public String toString() {
		return name;
	}
}