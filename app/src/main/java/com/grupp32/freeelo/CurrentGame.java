package com.grupp32.freeelo;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CurrentGame {
	private Summoner[] summoners;
	private JSONObject currentGame;
	
	private String region;
	private final String ROOT_URL = ".api.pvp.net/";
	private final String API_URL = "api/lol/";
	private final String OBS_URL = "observer-mode/rest/consumer/getSpectatorGameInfo/";
	private final String VERSION = "/v1.4/";
	private final String SUM_BY_NAME = "summoner/by-name/";
	private final String API_KEY = "?api_key=8088586e-a695-4cc5-80c2-be3b6fcec3e5";

	public CurrentGame(String summonerName, String region) {
		this.region = region;
		try {
			int summonerId = getSummonerId(summonerName);
			buildCurrentGame(summonerId);
			buildSummonerArray(summonerId);
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
	}
	
	private JSONObject buildRootObject(URL url) throws IOException, JSONException {
		Scanner scanner = new Scanner(url.openStream());
		String str = new String();
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
		}
		URL url = new URL("https://" + this.region + ROOT_URL + OBS_URL + region + summonerId + API_KEY);
		currentGame = buildRootObject(url);
	}

	private void buildSummonerArray(int summonerId) throws JSONException {
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
				summoners[index++] = new Summoner(summoner.getString("summonerName"), summoner.getInt("spell1Id"),
						summoner.getInt("spell2Id"), summoner.getInt("championId"));
			}
		}
		this.summoners = summoners;
	}
	
	public Summoner[] getSummoners() {
		return summoners;
	}
}