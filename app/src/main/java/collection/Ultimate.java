package collection;

/**
 * @author Christoffer Nilsson
 */
public class Ultimate {

	private int maxRank;
	private double[] cooldowns;
	private String name;
	private String image;

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

	public Ultimate setImage(String image) {
		this.image = image;
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

	public String getImage() {
		return image;
	}
}