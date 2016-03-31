package com.grupp32.freeelo;

public class Summoner {

	private String name;
	private int summSpell1;
	private int summSpell2;
	private int champion;
	
	public Summoner(String name, int summSpell1, int summSpell2, int champion) {
		this.name = name;
		this.summSpell1 = summSpell1;
		this.summSpell2 = summSpell2;
		this.champion = champion;
	}
	
	public String getName() {
		return name;
	}
	
	public int getSummSpell1() {
		return summSpell1;
	}
	
	public int getSummSpell2() {
		return summSpell2;
	}
	
	public int getChampion() {
		return champion;
	}
	
	public String toString() {
		return name + ", spell1: " + summSpell1;
	}
}