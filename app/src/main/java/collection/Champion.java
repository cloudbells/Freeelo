package collection;

import java.io.Serializable;

/**
 * @author Alexander Johansson, Christoffer Nilsson
 */
public class Champion implements Serializable {

	private int championId;
	private String name;
	private String title;
	private String key;
	private String imageName;
	private Ultimate ultimate;
	private int wins;
	private int losses;

	public Champion setChampionId(int championId) {
		this.championId = championId;
		return this;
	}

	public Champion setName(String name) {
		this.name = name;
		return this;
	}

	public Champion setTitle(String title) {
		this.title = title;
		return this;
	}

	public Champion setKey(String key) {
		this.key = key;
		return this;
	}

	public Champion setImageName(String imageName) {
		this.imageName = imageName;
		return this;
	}

	public Champion setUltimate(Ultimate ultimate) {
		this.ultimate = ultimate;
		return this;
	}

	public Champion setWins(int wins) {
		this.wins = wins;
		return this;
	}

	public Champion setLosses(int losses) {
		this.losses = losses;
		return this;
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

	public String getImageName() {
		return imageName;
	}

	public String getUltimateName() {
		return ultimate.getName();
	}

	public int getUltimateMaxRank() {
		return ultimate.getMaxRank();
	}

	public double[] getUltimateCooldowns() {
		return ultimate.getCooldowns();
	}

	public String getUltimateImage() {
		return ultimate.getImage();
	}

	public int getWins() {
		return wins;
	}

	public int getLosses() {
		return losses;
	}

	@Override
	public String toString() {
		return "Champion{" +
				"championId=" + championId +
				", name='" + name + '\'' +
				", title='" + title + '\'' +
				", key='" + key + '\'' +
				", imageName='" + imageName + '\'' +
				", ultimate=" + ultimate +
				", wins=" + wins +
				", losses=" + losses +
				'}';
	}
}
