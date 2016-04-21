package json;

import android.content.Context;

import com.grupp32.activity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import collection.Champion;
import collection.Rune;
import collection.RuneCollection;
import collection.Spell;
import collection.Ultimate;

/**
 * @author Christoffer Nilsson.
 */
public class JSONParser {

	private JSONObject championList;
	private JSONObject masteryList;
	private JSONObject runeList;
	private JSONObject spellList;

	public JSONParser(Context context) {
		try {
			championList = parseResource(context.getResources().openRawResource(R.raw.champions)).getJSONObject("data");
			masteryList = parseResource(context.getResources().openRawResource(R.raw.masteries)).getJSONObject("data");
			runeList = parseResource(context.getResources().openRawResource(R.raw.runes)).getJSONObject("data");
			spellList = parseResource(context.getResources().openRawResource(R.raw.spells)).getJSONObject("data");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int parseSpellId(JSONObject participant, int spellNumber) throws JSONException {
		if (spellNumber == 2) {
			return participant.getInt("spell2Id");
		}
		return participant.getInt("spell1Id"); // Default is the first spell (if you try > 2 or 1).
	}

	public int parseTeamId(JSONArray participants, int summonerId) throws IOException, JSONException {
		int enemyTeamId = 0;
		for (int i = 0; i < 10; i++) {
			JSONObject participant = participants.getJSONObject(i);
			if (participant.getInt("summonerId") == summonerId) {
				enemyTeamId = (participant.getInt("teamId") == 100) ? 200 : 100;
				break;
			}
		}
		return enemyTeamId;
	}

	public int parseSummonerId(JSONObject participant, int type) throws JSONException {
		return (type == 1 ? participant.getInt("id") : participant.getInt("summonerId"));
	}

	public int parseChampionId(JSONObject participant) throws JSONException {
		return participant.getInt("championId");
	}

	public int parseLosses(JSONObject rankedEntries) throws JSONException {
		return rankedEntries.getInt("losses");
	}

	public int parseWins(JSONObject rankedEntries) throws JSONException {
		return rankedEntries.getInt("wins");
	}

	public int parseLeaguePoints(JSONObject rankedEntries) throws JSONException {
		return rankedEntries.getInt("leaguePoints");
	}

	public String parseSummonerName(JSONObject participant) throws JSONException {
		return participant.getString("summonerName");
	}

	public String parseDivision(JSONObject rankedEntries) throws JSONException {
		return rankedEntries.getString("division");
	}

	public String parseTier(JSONObject rankedData) throws JSONException {
		return rankedData.getString("tier");
	}

	public String parseMasteries(JSONObject participant) throws IOException, JSONException {
		JSONArray summonerMasteries = participant.getJSONArray("masteries");
		int ferocity = 0;
		int cunning = 0;
		int resolve = 0;
		for (int i = 0; i < summonerMasteries.length(); i++) {
			JSONObject summonerMastery = summonerMasteries.getJSONObject(i);
			int rank = summonerMastery.getInt("rank");
			int id = summonerMastery.getInt("masteryId");
			JSONObject listMastery = masteryList.getJSONObject(Integer.toString(id));
			String tree = listMastery.getString("masteryTree");
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

	public Spell parseSpell(int spellId) throws IOException, JSONException {
		JSONObject listSpell = spellList.getJSONObject(Integer.toString(spellId));
		String spellName = listSpell.getString("name");
		JSONObject spellImageObject = listSpell.getJSONObject("image");
		String spellImage = spellImageObject.getString("full");
		int spellCooldown = listSpell.getJSONArray("cooldown").getInt(0);
		return new Spell(spellId, spellName, spellImage, spellCooldown);
	}

	public RuneCollection parseRuneCollection(JSONObject participant) throws IOException, JSONException {
		RuneCollection runeCollection = new RuneCollection();
		JSONArray runesArray = participant.getJSONArray("runes"); // An array with objects each containing "count" and "runeId".
		for (int i = 0; i < runesArray.length(); i++) { // For each object:
			JSONObject summonerRune = runesArray.getJSONObject(i);
			JSONObject listRune = runeList.getJSONObject(Integer.toString(summonerRune.getInt("runeId"))); // Gets the rune from runeList.
			int count = summonerRune.getInt("count");
			String desc = listRune.getString("description");
			JSONObject stats = listRune.getJSONObject("stats");
			String statType = stats.names().getString(0);
			double stat = stats.getDouble(statType);
			runeCollection.add(new Rune(count, statType, stat, desc));
		}
		return runeCollection.finalizeStats();
	}

	public Champion parseChampion(int championId) throws IOException, JSONException {
		JSONObject champion = championList.getJSONObject(Integer.toString(championId));
		String name = champion.getString("name");
		String title = champion.getString("title");
		String key = champion.getString("key");
		String imageName = champion.getJSONObject("image").getString("full");
		JSONObject ultimateObject = champion.getJSONArray("spells").getJSONObject(3);
		String ultimateName = ultimateObject.getString("name");
		int ultimateMaxRank = ultimateObject.getInt("maxrank");
		JSONArray cooldown = ultimateObject.getJSONArray("cooldown");
		double ultimateCooldowns[] = new double[ultimateMaxRank];
		for (int i = 0; i < ultimateMaxRank; i++) {
			ultimateCooldowns[i] = cooldown.getDouble(i);
		}
		String ultimateImage = ultimateObject.getJSONObject("image").getString("full");
		Ultimate ultimate = new Ultimate().setName(ultimateName).setImage(ultimateImage).
				setCooldowns(ultimateCooldowns).setMaxRank(ultimateMaxRank);
		return new Champion().setChampionId(championId).setName(name).setTitle(title).setKey(key).
				setImageName(imageName).setUltimate(ultimate);
	}

	public JSONObject parseRankedEntries(JSONObject rankedData) throws JSONException {
		return rankedData.getJSONArray("entries").getJSONObject(0);
	}

	public JSONArray parseParticipants(JSONObject currentGame) throws JSONException {
		return currentGame.getJSONArray("participants");
	}

	private JSONObject parseResource(InputStream inputStream) throws IOException, JSONException {
		Scanner scanner = new Scanner(inputStream);
		String str = "";
		while (scanner.hasNext()) {
			str += scanner.nextLine();
		}
		scanner.close();
		return new JSONObject(str);
	}
}