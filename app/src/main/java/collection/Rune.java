package collection;

import java.io.Serializable;

/**
 * @author Alexander Johansson
 */
public class Rune implements Serializable {

	private int count;
	private double stat;
    private double stat2;
	private String statType; // i.e. "rFlatMagicPenetrationMod".
    private String statType2;
	private String description;

	public Rune(int count, String statType, String statType2, double stat, double stat2, String description) {
		this.count = count;
		this.statType = statType;
        this.statType2 = statType2;
		this.stat = stat;
        this.stat2 = stat2; // If hybrid
		this.description = description;
	}

	public double getStat() {
		return stat;
	}

    /**
     * Returns the second stat if it is a hybrid rune. If it isn't, this will
     * return 0.
     * @return double
     */
    public double getStat2() {
        return stat2;
    }

	public int getCount() {
		return count;
	}

	public String getStatType() {
		return statType;
	}

    public String getStatType2() {
        return statType2;
    }

	public String getDescription() {
		return description;
	}
}