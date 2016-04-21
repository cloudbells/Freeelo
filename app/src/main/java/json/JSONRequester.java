package json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import collection.Summoner;

/**
 * Created by Christoffer on 2016-04-19.
 */
public class JSONRequester {

	private final String CURRENT_GAME_URL = "https://euw.api.pvp.net/observer-mode/rest/consumer/getSpectatorGameInfo/";
	private final String SUMMONER_ID_URL = "https://euw.api.pvp.net/api/lol/euw/v1.4/summoner/by-name/";
	private final String RANKED_DATA_URL = "https://euw.api.pvp.net/api/lol/euw/v2.5/league/by-summoner/";
	private final String API_KEY = "?api_key=8088586e-a695-4cc5-80c2-be3b6fcec3e5";

	public JSONObject requestCurrentGameObject(int summonerId, String region) throws IOException, JSONException {
		switch (region) {
			case "EUNE":
				region = "EUN1";
				break;
			case "LAN":
				region = "LA1";
				break;
			case "LAS":
				region = "LA2";
				break;
			case "OCE":
				region = "OC1";
				break;
			default:
				region += "1";
				break;
		}
		return buildRootObject(new URL(CURRENT_GAME_URL + region + "/" + summonerId + API_KEY));
	}

	/**
	 * Returns the ID associated with the given Summoner Name.
	 *
	 * @param summonerName the Summoner Name
	 * @return summoner ID associated with the given Summoner Name
	 */
	public JSONObject requestSummonerObject(String summonerName) throws IOException, JSONException {
		String urlSummonerName = summonerName.replace(" ", "%20"); // Formats the Summoner Name to work in a URL context (spaces = %20).
		JSONObject root = buildRootObject(new URL(SUMMONER_ID_URL + urlSummonerName + API_KEY));
		return root.getJSONObject(summonerName.replace(" ", "").toLowerCase()); // In the JSON object, the name is lower case and without spaces.
	}

	public JSONObject requestRankedData(int summonerId) throws IOException {
		JSONObject rankedData;
		try {
			rankedData = buildRootObject(new URL(RANKED_DATA_URL + summonerId + "/entry" + API_KEY)).
					getJSONArray(Integer.toString(summonerId)).getJSONObject(0);
		} catch (JSONException e) {
			return new JSONObject();
		}
		return rankedData;
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
}
