package com.grupp32.freeelo;

import java.io.Serializable;

public class Summoner implements Serializable {
	private String name;
	private Spell spell1;
	private Spell spell2;
	private Champion champion;
	private String masteries;
	private RuneCollection runes;

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
	
	public String toString() {
		return name;
	}

	public RuneCollection getRunes() {
		return runes;
	}
}