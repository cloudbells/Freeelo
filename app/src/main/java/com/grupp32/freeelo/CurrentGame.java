package com.grupp32.freeelo;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Test summonerID: 27693209 on EUW

public class CurrentGame {
	private Summoner[] summoners;
	private JSONObject currentGame;
	private JSONObject runeList;
	private JSONObject masteryList;
	private JSONObject spellList;
	private JSONObject championList;
	private Context context;
	
	private String region;
	private final String ROOT_URL = ".api.pvp.net/";
	private final String API_URL = "api/lol/";
	private final String OBS_URL = "observer-mode/rest/consumer/getSpectatorGameInfo/";
	private final String VERSION = "/v1.4/";
	private final String SUM_BY_NAME = "summoner/by-name/";
	private final String API_KEY = "?api_key=8088586e-a695-4cc5-80c2-be3b6fcec3e5";
	private final String API_KEY2 = "&api_key=8088586e-a695-4cc5-80c2-be3b6fcec3e5";

	public CurrentGame(Context context, String summonerName, String region) {
		this.context = context;
		this.region = region;
		try {
			runeList = buildList(R.raw.runes).getJSONObject("data"); // text file
			masteryList = buildList(R.raw.masteries).getJSONObject("data");
			spellList = buildList(R.raw.spells).getJSONObject("data");
			championList = buildList(R.raw.champions).getJSONObject("data");
			int summonerId = getSummonerId(summonerName);
			buildCurrentGame(summonerId);
			buildSummonerArray(summonerId);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
	}
	
	private JSONObject buildRootObject(URL url) throws IOException, JSONException {
		Scanner scanner = new Scanner(url.openStream());
		String str = "";
		while (scanner.hasNext()) {
			str += scanner.nextLine();
		}
		scanner.close();
		return new JSONObject(str);
	}

	private JSONObject buildList(int resource) throws IOException, JSONException {
		Scanner scanner = new Scanner(context.getResources().openRawResource(resource));
		String str = "";
		while (scanner.hasNext()) {
			str += scanner.nextLine();
		}
		scanner.close();
		return new JSONObject(str);
	}
	
	private int getSummonerId(String summonerName) throws IOException, JSONException {
		String fixedSummonerName = summonerName;
		if (summonerName.contains(" ")) {
			fixedSummonerName = summonerName.replace(" ", "%20");
		}
		URL url = new URL("https://" + region + ROOT_URL + API_URL + region + VERSION + SUM_BY_NAME + fixedSummonerName + API_KEY);
		JSONObject root = buildRootObject(url);
		fixedSummonerName = summonerName.replace(" ", "").toLowerCase();
		JSONObject summoner = root.getJSONObject(fixedSummonerName);
		return summoner.getInt("id");
	}
	
	private void buildCurrentGame(int summonerId) throws IOException, JSONException {
		String region = "";
		if (this.region.toLowerCase().equals("euw")) {
			region = "EUW1/";
		} else if (this.region.toLowerCase().equals("na")) {
			region = "NA1/";
		} else if (this.region.toLowerCase().equals("eune")) {
			region = "EUN1/";
		} else if (this.region.toLowerCase().equals("br")) {
			region = "BR1/";
		} else if (this.region.toLowerCase().equals("kr")) {
			region = "KR/";
		} else if (this.region.toLowerCase().equals("lan")) {
			region = "LA1/";
		} else if (this.region.toLowerCase().equals("las")) {
			region = "LA2/";
		} else if (this.region.toLowerCase().equals("oce")) {
			region = "OC1/";
		} else if (this.region.toLowerCase().equals("tr")) {
			region = "TR1/";
		} else if (this.region.toLowerCase().equals("ru")) {
			region = "RU/";
		} else if (this.region.toLowerCase().equals("pbe")) {
			region = "PBE1/";
		}
		URL url = new URL("https://" + this.region + ROOT_URL + OBS_URL + region + summonerId + API_KEY);
		currentGame = buildRootObject(url);
	}

	private void buildSummonerArray(int summonerId) throws IOException, JSONException {
		JSONArray allSummoners = currentGame.getJSONArray("participants");
		int enemyTeamId = 100;
		Summoner[] summoners = new Summoner[5];
		int index = 0;
		// Finds the summoner in the array. When found, sets enemy team ID to the opposite team ID.
		for (int i = 0; i < 10; i++) {
			JSONObject summoner = allSummoners.getJSONObject(i);
			if (summoner.getInt("summonerId") == summonerId) {
				enemyTeamId = (summoner.getInt("teamId") == 100) ? 200 : 100;
			}
		}
		// Finds the enemy team summoners with the help of the enemy team ID.
		for (int i = 0; i < 10; i++) {
			JSONObject summoner = allSummoners.getJSONObject(i);
			if (summoner.getInt("teamId") == enemyTeamId) {
				String name = summoner.getString("summonerName");
				Champion champ = buildChampion(summoner.getInt("championId"));
				Spell spell1 = buildSpell(summoner.getInt("spell1Id"));
				Spell spell2 = buildSpell(summoner.getInt("spell2Id"));
				String masteries = buildMasteries(summoner);
				RuneCollection runes = buildRunes(summoner);
				Summoner newSummoner = new Summoner();
				populateSummonerFields(newSummoner, summoner.getInt("summonerId"));
				summoners[index++] = newSummoner.setName(name).setSpell1(spell1).
						setSpell2(spell2).setChampion(champ).setMasteries(masteries).
						setRunes(runes);
			}
		}

		this.summoners = summoners;
	}

	private String buildMasteries(JSONObject summoner) throws IOException, JSONException {
		JSONArray masteries = summoner.getJSONArray("masteries");
		int ferocity = 0;
		int cunning = 0;
		int resolve = 0;
		for (int i = 0; i < masteries.length(); i++) {
			JSONObject mastery = masteries.getJSONObject(i);
			int rank = mastery.getInt("rank");
			int id = mastery.getInt("masteryId");
			JSONObject masteryObject = masteryList.getJSONObject(Integer.toString(id));
			String tree = masteryObject.getString("masteryTree");
			switch (tree) {
				case "Ferocity":
					ferocity += rank;
					break;
				case "Cunning":
					cunning += rank;
					break;
				case "Resolve":
					resolve += rank;
					break;
				default: // This literally can't happen.
					break;
			}
		}
		return ferocity + "/" + cunning + "/" + resolve;
	}

	private Spell buildSpell(int spellId) throws IOException, JSONException {
		JSONObject spellObject = spellList.getJSONObject(Integer.toString(spellId));
		String spellName = spellObject.getString("name");
		JSONObject imageObject = spellObject.getJSONObject("image");
		String spellImage = imageObject.getString("full");
		int spellCooldown = spellObject.getJSONArray("cooldown").getInt(0);
		return new Spell(spellId, spellName, spellImage, spellCooldown);
	}

	private RuneCollection buildRunes(JSONObject summoner) throws IOException, JSONException {
		RuneCollection runeCollection = new RuneCollection();
		JSONArray runesArray = summoner.getJSONArray("runes"); // from summoner object
		for (int i = 0; i < runesArray.length(); i++) {
			// Gets the Rune JSONObject from the runeList by getting the runeId from the summoner JSONObject first
			JSONObject runeObject = runesArray.getJSONObject(i); // count and runeId are variables, (FROM summoner)
			JSONObject rune = runeList.getJSONObject(Integer.toString(runeObject.getInt("runeId"))); // Gets the rune from runes.json.
			int count = runeObject.getInt("count");
			String desc = rune.getString("description");
			JSONObject stats = rune.getJSONObject("stats");
			String statType = stats.names().getString(0);
			double stat = stats.getDouble(statType);
			runeCollection.add(new Rune(count, statType, stat, desc));
		}

		return runeCollection;
	}

	private Champion buildChampion(int championId) throws IOException, JSONException {
		JSONObject championData = championList.getJSONObject(Integer.toString(championId));
		String name = championData.getString("name");
		String title = championData.getString("title");
		String key = championData.getString("key");
		JSONObject image = championData.getJSONObject("image");
		String squareImageFull = image.getString("full");
		JSONArray spells = championData.getJSONArray("spells");
		JSONObject ultimate = spells.getJSONObject(3);
		String ultimateName = ultimate.getString("name");
		int ultimateMaxRank = ultimate.getInt("maxrank");
		JSONArray cooldown = ultimate.getJSONArray("cooldown");
		int cooldowns[] = new int[ultimateMaxRank];
		for(int i = 0; i < ultimateMaxRank; i++) {
			cooldowns[i] = cooldown.getInt(i);
		}
		JSONObject ultImg = ultimate.getJSONObject("image");
		String ultimateImage = ultImg.getString("full");
		return new Champion(championId, name, title, key, squareImageFull, ultimateName, ultimateMaxRank, cooldowns, ultimateImage);
	}

	private void populateSummonerFields(Summoner summoner, int summonerId) throws IOException, JSONException {
		URL url = new URL("https://" + this.region + ROOT_URL + API_URL + this.region.toLowerCase() + "/v2.5/league/by-summoner/" + summonerId + "/entry" + API_KEY);
		try {
			JSONObject summonerData = buildRootObject(url);

			JSONArray data = summonerData.getJSONArray(Integer.toString(summonerId));
			JSONObject sum = data.getJSONObject(0);
			JSONArray entries = sum.getJSONArray("entries");
			JSONObject entry = entries.getJSONObject(0);
			summoner.setLeaguePoints(entry.getInt("leaguePoints"))
					.setDivision(entry.getString("division"))
					.setWins(entry.getInt("wins"))
					.setLosses(entry.getInt("losses"))
					.setTier(sum.getString("tier"));
		} catch(FileNotFoundException e) {
			summoner.setLeaguePoints(0)
					.setDivision("")
					.setWins(0)
					.setLosses(0)
					.setTier("Provisional");
		}
	}

	public Summoner[] getSummoners() {
		return summoners;
	}
}