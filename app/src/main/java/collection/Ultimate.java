package collection;

import java.io.Serializable;

/**
 * @author Christoffer Nilsson
 */
public class Ultimate implements Serializable {

	private int maxRank;
	private double[] cooldowns;
	private String name;
	private String imageName;

	public Ultimate setMaxRank(int maxRank) {
		this.maxRank = maxRank;
		return this;
	}

	public Ultimate setCooldowns(double[] cooldowns) {
		this.cooldowns = cooldowns;
		return this;
	}

	public Ultimate setName(String name) {
		this.name = name;
		return this;
	}

	public Ultimate setImageName(String imageName) {
		this.imageName = imageName;
		return this;
	}

	public int getMaxRank() {
		return maxRank;
	}

	public double[] getCooldowns() {
		return cooldowns;
	}

	public String getName() {
		return name;
	}

	public String getImageName() {
		return imageName;
	}
}