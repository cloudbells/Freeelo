package json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Christoffer on 2016-04-19, Alexander Johansson (rankedChampData)
 */
public class JSONRequester {

	private JSONParser parser;

	private final String API_KEY = "?api_key=8088586e-a695-4cc5-80c2-be3b6fcec3e5";
	private final String CURRENT_GAME_URL = "https://%s.api.pvp.net/observer-mode/rest/consumer/getSpectatorGameInfo/%s/%s";
	private final String SUMMONER_ID_URL = "https://%s.api.pvp.net/api/lol/%s/v1.4/summoner/by-name/%s";
	private final String RANKED_DATA_URL = "https://%s.api.pvp.net/api/lol/%s/v2.5/league/by-summoner/%s/entry";
	private final String RANKED_CHAMP_DATA_URL = "https://%s.api.pvp.net/api/lol/%s/v1.3/stats/by-summoner/%s/ranked";

	public JSONRequester(JSONParser parser) {
		this.parser = parser;
	}

	public JSONObject requestCurrentGameObject(int summonerId, String region) throws IOException, JSONException {
		String platformRegion = region;
		switch (region) {
			case "EUNE":
				platformRegion = "EUN1";
				break;
			case "LAN":
				platformRegion = "LA1";
				break;
			case "LAS":
				platformRegion = "LA2";
				break;
			case "OCE":
				platformRegion = "OC1";
				break;
			case "KR":
				break;
			case "RU":
				break;
			default:
				platformRegion += "1";
				break;
		}
		return buildRootObject(new URL(String.format(CURRENT_GAME_URL, region, platformRegion, summonerId) + API_KEY));
	}

	public JSONObject requestSummonerObject(String summonerName, String region) throws IOException, JSONException {
		String urlSummonerName = summonerName.replace(" ", "%20"); // Formats the Summoner Name to work in a URL context (spaces = %20).
		JSONObject root = buildRootObject(new URL(String.format(SUMMONER_ID_URL, region, region, urlSummonerName) + API_KEY));
		return (JSONObject) parser.parse(root, summonerName.replace(" ", "").toLowerCase()); // In the JSON object, the name is lower case and without spaces.
	}

	public JSONObject requestRankedData(String summonerIds, String region) throws IOException {
		JSONObject rankedData;
		try {
			rankedData = buildRootObject(new URL(String.format(RANKED_DATA_URL, region, region.toLowerCase(), summonerIds) + API_KEY));
		} catch (JSONException e) {
			return new JSONObject();
		}

		return rankedData;
	}

	public JSONArray requestRankedChampionData(int summonerId, String region) throws IOException {
		JSONArray rankedChampData;
		try {
			rankedChampData = buildRootObject(new URL(String.format(RANKED_CHAMP_DATA_URL, region, region.toLowerCase(), summonerId) + API_KEY)).
					getJSONArray("champions");
		} catch (JSONException e) {
			return new JSONArray();
		}

		return rankedChampData;
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