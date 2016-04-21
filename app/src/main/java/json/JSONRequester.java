package json;

import android.util.Log;

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
	private final String HTTPS = "https://";
	private final String API_LOCATION = ".api.pvp.net/";
	private final String API_ADDITION = "api/lol/";
	private final String CURRENT_GAME_URL = API_LOCATION + "observer-mode/rest/consumer/getSpectatorGameInfo/";
	private final String SUMMONER_ID_URL = "/v1.4/summoner/by-name/";
	private final String RANKED_DATA_URL = "/v2.5/league/by-summoner/";
	private final String API_KEY = "?api_key=8088586e-a695-4cc5-80c2-be3b6fcec3e5";

	public JSONObject requestCurrentGameObject(int summonerId, String region) throws IOException, JSONException {
		String region_platform = new String(region);
		switch (region) {
			case "EUNE":
				region_platform = "EUN1";
				break;
			case "LAN":
				region_platform = "LA1";
				break;
			case "LAS":
				region_platform = "LA2";
				break;
			case "OCE":
				region_platform = "OC1";
				break;
			case "KR":
				break;
			case "RU":
				break;
			default:
				region_platform += "1";
				break;
		}
		Log.e("CURRENT GAME OBJECT URL", HTTPS + region + CURRENT_GAME_URL + region_platform + "/" + summonerId + API_KEY);
		return buildRootObject(new URL(HTTPS + region + CURRENT_GAME_URL + region_platform + "/" + summonerId + API_KEY));
	}

	/**
	 * Returns the ID associated with the given Summoner Name.
	 *
	 * @param summonerName the Summoner Name
	 * @return summoner ID associated with the given Summoner Name
	 */
	public JSONObject requestSummonerObject(String summonerName, String region) throws IOException, JSONException {
		String urlSummonerName = summonerName.replace(" ", "%20"); // Formats the Summoner Name to work in a URL context (spaces = %20).
		Log.e("SUMMONER OBJECT URL", HTTPS + region + API_LOCATION + API_ADDITION + region + SUMMONER_ID_URL + urlSummonerName + API_KEY);
		JSONObject root = buildRootObject(new URL(HTTPS + region + API_LOCATION + API_ADDITION + region + SUMMONER_ID_URL + urlSummonerName + API_KEY));
		return root.getJSONObject(summonerName.replace(" ", "").toLowerCase()); // In the JSON object, the name is lower case and without spaces.
	}

	public JSONObject requestRankedData(int summonerId, String region) throws IOException {
		JSONObject rankedData;
		try {
			Log.e("RANKED DATA URL", HTTPS + region + API_LOCATION + API_ADDITION + region.toLowerCase() + RANKED_DATA_URL + summonerId + "/entry" + API_KEY);
			rankedData = buildRootObject(new URL(HTTPS + region + API_LOCATION + API_ADDITION + region.toLowerCase() + RANKED_DATA_URL + summonerId + "/entry" + API_KEY)).
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
