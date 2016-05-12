package json;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.grupp32.activity.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Christoffer on 2016-04-19, Alexander Johansson (rankedChampData)
 */
public class JSONRequester {

	private JSONParser parser;
    private Context context;

	private final String API_KEY = "api_key=8088586e-a695-4cc5-80c2-be3b6fcec3e5";
	private final String CURRENT_GAME_URL = "https://%s.api.pvp.net/observer-mode/rest/consumer/getSpectatorGameInfo/%s/%s?";
	private final String SUMMONER_ID_URL = "https://%s.api.pvp.net/api/lol/%s/v1.4/summoner/by-name/%s?";
	private final String RANKED_DATA_URL = "https://%s.api.pvp.net/api/lol/%s/v2.5/league/by-summoner/%s/entry?";
	private final String RANKED_CHAMP_DATA_URL = "https://%s.api.pvp.net/api/lol/%s/v1.3/stats/by-summoner/%s/ranked?";
	private final String PATCH_DATA_URL = "https://global.api.pvp.net/api/lol/static-data/%s/v1.2/versions?";
    private final String CHAMP_STATIC_URL = "https://global.api.pvp.net/api/lol/static-data/%s/v1.2/champion?dataById=true&champData=image,spells&";
    private final String MASTERY_STATIC_URL = "https://global.api.pvp.net/api/lol/static-data/%s/v1.2/mastery?masteryListData=masteryTree&";
    private final String RUNE_STATIC_URL = "https://global.api.pvp.net/api/lol/static-data/%s/v1.2/rune?runeListData=stats&";
    private final String SPELL_STATIC_URL = "https://global.api.pvp.net/api/lol/static-data/%s/v1.2/summoner-spell?dataById=true&spellData=cooldown,image&";

	public JSONRequester(Context context, JSONParser parser) {
        this.context = context;
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

	public JSONArray requestPatchData(String region) throws IOException, JSONException {
        return buildRootArray(new URL(String.format(PATCH_DATA_URL, region) + API_KEY));
	}

    public JSONObject requestRankedData(String summonerIds, String region) throws IOException, JSONException {
        return buildRootObject(new URL(String.format(RANKED_DATA_URL, region, region.toLowerCase(), summonerIds) + API_KEY));
    }

    public JSONObject requestStaticChampionData(String region) throws IOException, JSONException {
        return buildRootObject(new URL(String.format(CHAMP_STATIC_URL, region) + API_KEY));
    }

    public JSONObject requestStaticMasteryData(String region) throws IOException, JSONException {
        return buildRootObject(new URL(String.format(MASTERY_STATIC_URL, region) + API_KEY));
    }

    public JSONObject requestStaticRuneData(String region) throws IOException, JSONException {
        return buildRootObject(new URL(String.format(RUNE_STATIC_URL, region) + API_KEY));
    }

    public JSONObject requestStaticSpellData(String region) throws IOException, JSONException {
        return buildRootObject(new URL(String.format(SPELL_STATIC_URL, region) + API_KEY));
    }

    public JSONObject requestSummonerObject(String summonerName, String region) throws IOException, JSONException {
        String urlSummonerName = summonerName.replace(" ", "%20"); // Formats the Summoner Name to work in a URL context (spaces = %20).
        JSONObject root = buildRootObject(new URL(String.format(SUMMONER_ID_URL, region, region, urlSummonerName) + API_KEY));
        return (JSONObject) parser.parse(root, summonerName.replace(" ", "").toLowerCase()); // In the JSON object, the name is lower case and without spaces.
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

	private JSONArray buildRootArray(URL url) throws IOException, JSONException {
		Scanner scanner = new Scanner(url.openStream());
		String str = "";
		while (scanner.hasNext()) {
			str += scanner.nextLine();
		}
		scanner.close();
		return new JSONArray(str);
	}
}