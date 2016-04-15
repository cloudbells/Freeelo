package com.grupp32.freeelo;

import java.io.Serializable;

public class Summoner implements Serializable {
	private String name;
	private Spell spell1;
	private Spell spell2;
	private Champion champion;
	private String masteries;
	private RuneCollection runes;

	private String tier;
	private String division;
	private int wins;
	private int losses;
	private int leaguePoints;

	public Summoner setName(String name) {
		this.name = name;
		return this;
	}

	public Summoner setChampion(Champion champion) {
		this.champion = champion;
		return this;
	}

	public Summoner setSpell1(Spell spell1) {
		this.spell1 = spell1;
		return this;
	}

	public Summoner setSpell2(Spell spell2) {
		this.spell2 = spell2;
		return this;
	}

	public Summoner setMasteries(String masteries) {
		this.masteries = masteries;
		return this;
	}

	public Summoner setRunes(RuneCollection runes) {
		this.runes = runes;
		return this;
	}

	public Summoner setLeaguePoints(int leaguePoints) {
		this.leaguePoints = leaguePoints;
		return this;
	}

	public Summoner setLosses(int losses) {
		this.losses = losses;
		return this;
	}

	public Summoner setDivision(String division) {
		this.division = division;
		return this;
	}

	public Summoner setTier(String tier) {
		this.tier = tier;
		return this;
	}

	public Summoner setWins(int wins) {
		this.wins = wins;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public Spell getSpell1() {
		return spell1;
	}
	
	public Spell getSpell2() {
		return spell2;
	}
	
	public Champion getChampion() {
		return champion;
	}

	public String getMasteries() {
		return masteries;
	}

	public RuneCollection getRunes() {
		return runes;
	}

	public int getWins() {
		return wins;
	}

	public int getLeaguePoints() {
		return leaguePoints;
	}

	public int getLosses() {
		return losses;
	}

	public String getDivision() {
		return division;
	}

	public String getTier() {
		return tier;
	}

	@Override
	public String toString() {
		return "Summoner{" +
				"name='" + name + '\'' +
				", spell1=" + spell1 +
				", spell2=" + spell2 +
				", champion=" + champion +
				", masteries='" + masteries + '\'' +
				", runes=" + runes +
				", tier='" + tier + '\'' +
				", division='" + division + '\'' +
				", wins=" + wins +
				", losses=" + losses +
				", leaguePoints=" + leaguePoints +
				'}';
	}
}