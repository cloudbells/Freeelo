package collection;

import android.content.Context;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import json.JSONParser;
import json.JSONRequester;

/**
 * @author Christoffer Nilsson, Alexander Johansson
 */
public class CurrentGame {

	private JSONRequester requester;
	private JSONParser parser;
	private Summoner[] summoners = new Summoner[5];

	public CurrentGame(Context context, String summonerName, String region) throws IOException, JSONException {
		parser = new JSONParser(context);
		requester = new JSONRequester(parser);
		JSONObject summonerObject = requester.requestSummonerObject(summonerName, region);
		int summonerId = (int) parser.parse(summonerObject, "id");
		JSONObject currentGame = requester.requestCurrentGameObject(summonerId, region);
		JSONArray participants = (JSONArray) parser.parse(currentGame, "participants");
		int enemyTeamId = parser.parseTeamId(participants, summonerId);
		initSummoners(participants, enemyTeamId, region);
	}

	private void initSummoners(JSONArray participants, int enemyTeamId, String region) throws IOException, JSONException {
		int index = 0;
		String summonerIds = "";
		for (int i = 0; i < 10; i++) {
			JSONObject participant = participants.getJSONObject(i);
			if (participant.getInt("teamId") == enemyTeamId) {
				String name = (String) parser.parse(participant, "summonerName");
				int championId = (int) parser.parse(participant, "championId");
				Champion champ = parser.parseChampion(championId);
				Spell spell1 = parser.parseSpell(parser.parseSpellId(participant, 1));
				Spell spell2 = parser.parseSpell(parser.parseSpellId(participant, 2));
				String masteries = parser.parseMasteries(participant);
				RuneCollection runes = parser.parseRuneCollection(participant);

				int summonerId = (int) parser.parse(participant, "summonerId");
				try {
					JSONArray rankedChampData = requester.requestRankedChampionData(summonerId, region);
					setRankedChampionData(championId, rankedChampData, champ);
				} catch (IOException e) {
					setRankedChampionData(championId, null, champ);
				}

				summoners[index] = new Summoner().setName(name).setSpell1(spell1).
						setSpell2(spell2).setChampion(champ).setMasteries(masteries).setRunes(runes).setSummonerId(summonerId);

				if(summonerIds.isEmpty()) {
					summonerIds += summonerId;
				} else {
					summonerIds += "," + summonerId;
				}

				index++;
			}
		}

		initRankedData(summonerIds, region);
	}

	private void initRankedData(String summonerIds, String region) throws IOException, JSONException {
		JSONObject rankedDataArr = requester.requestRankedData(summonerIds, region);
		for(Summoner summoner : summoners) {
			JSONObject rankedData = rankedDataArr.getJSONArray(Integer.toString(summoner.getSummonerId())).getJSONObject(0);
			setRankedData(rankedData, summoner);
		}
	}

	private void setRankedData(JSONObject rankedData, Summoner summoner) throws JSONException {
		if (rankedData == null) {
			summoner.setDivision("").setLeaguePoints(0).setLosses(0).setWins(0).setTier("Provisional");
		} else {
			String tier = (String) parser.parse(rankedData, "tier");
			JSONObject rankedEntries = parser.parseRankedEntries(rankedData);
			int leaguePoints = (int) parser.parse(rankedEntries, "leaguePoints");
			String division = (String) parser.parse(rankedEntries, "division");
			int losses = (int) parser.parse(rankedEntries, "losses");
			int wins = (int) parser.parse(rankedEntries, "wins");
			summoner.setDivision(division).setLeaguePoints(leaguePoints).setLosses(losses).setWins(wins).setTier(tier);
		}
	}

	public void setRankedChampionData(int championId, JSONArray rankedChampData, Champion champion) throws JSONException {
		if(rankedChampData == null) {
			champion.setWins(0).setLosses(0);
		} else {
			int wins = 0;
			int losses = 0;
			for(int index = 0; index < rankedChampData.length(); index++) {
				JSONObject entry = rankedChampData.getJSONObject(index);
				if((int) parser.parse(entry, "id") == championId) {
					JSONObject stats = (JSONObject) parser.parse(entry, "stats");
					wins = (int) parser.parse(stats, "totalSessionsWon");
					losses = (int) parser.parse(stats, "totalSessionsLost");
				}
			}

			champion.setWins(wins).setLosses(losses);
		}
	}

	public Summoner[] getSummoners() {
		return summoners;
	}
}