package collection;

/**
 * @author Alexander Johansson
 */
public class Rune {

	private int count;
	private double stat;
	private String statType; // i.e. "rFlatMagicPenetrationMod".
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