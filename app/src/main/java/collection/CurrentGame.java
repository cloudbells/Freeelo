package collection;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import json.JSONParser;
import json.JSONRequester;

public class CurrentGame {

	private JSONRequester requester = new JSONRequester();
	private JSONParser parser;
	private Summoner[] summoners = new Summoner[5];

	public CurrentGame(Context context, String summonerName, String region) {
		parser = new JSONParser(context);
		try {
			JSONObject summonerObject = requester.requestSummonerObject(summonerName, region);
			int summonerId = parser.parseSummonerId(summonerObject, 1);
			JSONObject currentGame = requester.requestCurrentGameObject(summonerId, region);
			JSONArray participants = parser.parseParticipants(currentGame);
			int enemyTeamId = parser.parseTeamId(participants, summonerId);
			initSummoners(participants, enemyTeamId, region);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initSummoners(JSONArray participants, int enemyTeamId, String region) throws IOException, JSONException {
		int index = 0;
		for (int i = 0; i < 10; i++) {
			JSONObject participant = participants.getJSONObject(i);
			if (participant.getInt("teamId") == enemyTeamId) {
				String name = parser.parseSummonerName(participant);
				Champion champ = parser.parseChampion(parser.parseChampionId(participant));
				Spell spell1 = parser.parseSpell(parser.parseSpellId(participant, 1));
				Spell spell2 = parser.parseSpell(parser.parseSpellId(participant, 2));
				String masteries = parser.parseMasteries(participant);
				RuneCollection runes = parser.parseRuneCollection(participant);
				summoners[index] = new Summoner().setName(name).setSpell1(spell1).
						setSpell2(spell2).setChampion(champ).setMasteries(masteries).setRunes(runes);
				try {
					JSONObject rankedData = requester.requestRankedData(parser.parseSummonerId(participant, 0), region);
					setRankedData(participant, rankedData, summoners[index++]);
				} catch(FileNotFoundException e) {
					setRankedData(participant, null, summoners[index++]);
				}
			}
		}
	}

	private void setRankedData(JSONObject participant, JSONObject rankedData, Summoner summoner) throws JSONException {
		if (rankedData == null) {
			summoner.setDivision("").setLeaguePoints(0).setLosses(0).setWins(0).setTier("Provisional");
		} else {
			String tier = parser.parseTier(rankedData);
			JSONObject rankedEntries = parser.parseRankedEntries(rankedData);
			int leaguePoints = parser.parseLeaguePoints(rankedEntries);
			String division = parser.parseDivision(rankedEntries);
			int losses = parser.parseLosses(rankedEntries);
			int wins = parser.parseWins(rankedEntries);
			summoner.setDivision(division).setLeaguePoints(leaguePoints).setLosses(losses).setWins(wins).setTier(tier);
		}
	}

	public Summoner[] getSummoners() {
		return summoners;
	}
}