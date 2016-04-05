package com.grupp32.freeelo;

import java.io.Serializable;

public class Summoner implements Serializable {

	private String name;
	private Spell spell1;
	private Spell spell2;
	private Champion champion;
	private String masteries;
	
	public Summoner(String name, Champion champion, Spell spell1, Spell spell2, String masteries) {
		this.name = name;
		this.champion = champion;
		this.spell1 = spell1;
		this.spell2 = spell2;
		this.masteries = masteries;
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
}