package version;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import json.JSONParser;
import json.JSONRequester;

/**
 * Created by Christoffer Nilsson on 2016-05-12.
 */
public class VersionUtil {

    private String region;
    private String latestPatch;
    private String champVersion;
    private String masteryVersion;
    private String runeVersion;
    private String spellVersion;
    private Context context;
    private JSONParser parser;
    private JSONRequester requester;
    private ResourceUtil resourceUtil;

    public VersionUtil(Context context, JSONParser parser, JSONRequester requester, ResourceUtil resourceUtil, String region) {
        this.context = context;
        this.parser = parser;
        this.requester = requester;
        this.resourceUtil = resourceUtil;
        this.region = region.toLowerCase();
        try {
            if (!isFirstTime()) {
                resourceUtil.buildResources();
                champVersion = resourceUtil.getChampions().getString("version");
                masteryVersion = resourceUtil.getMasteries().getString("version");
                runeVersion = resourceUtil.getRunes().getString("version");
                spellVersion = resourceUtil.getSpells().getString("version");
            }

            latestPatch = requester.requestPatchData(this.region).getString(0);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public void updateVersion() {
        Log.e("VersionUtil:", "CHECKING VERSION");
        Log.e("VERSION CHECK:", "Latest patch: " + latestPatch + ", ALL OTHERS: " + champVersion + ", " + masteryVersion + ", " + runeVersion + ", " + spellVersion);
        try {
            Log.e("VersionUtil:", "UPDATING RESOURCES");
            Log.e("VersionUtil:", "REQUESTING STATIC DATA");
            JSONObject champions = requester.requestStaticChampionData(region);
            JSONObject masteries = requester.requestStaticMasteryData(region);
            JSONObject runes = requester.requestStaticRuneData(region);
            JSONObject spells = requester.requestStaticSpellData(region);
            champVersion = champions.getString("version");
            masteryVersion = masteries.getString("version");
            runeVersion = runes.getString("version");
            spellVersion = spells.getString("version");
            Log.e("VersionUtil:", "WRITING RESOURCES TO INTERNAL STORAGE");
            resourceUtil.writeResource("champions.json", champions.toString());
            resourceUtil.writeResource("masteries.json", masteries.toString());
            resourceUtil.writeResource("runes.json", runes.toString());
            resourceUtil.writeResource("spells.json", spells.toString());
            Log.e("JSONParser:", "UPDATING RESOURCES");
            resourceUtil.buildResources();
            parser.updateResources();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isFirstTime() {
        File champions = context.getFileStreamPath("champions.json");
        File masteries = context.getFileStreamPath("masteries.json");
        File runes = context.getFileStreamPath("runes.json");
        File spells = context.getFileStreamPath("spells.json");
        if (!champions.exists() || !masteries.exists() || !runes.exists() || !spells.exists()) {
            return true;
        }
        return false;
    }

    public boolean isLatestVersion()  {
        if (champVersion.equals(latestPatch) && masteryVersion.equals(latestPatch) &&
                runeVersion.equals(latestPatch) && spellVersion.equals(latestPatch)) {
            return true;
        }
        return false;
    }

    public String getVersion() {
        return latestPatch;
    }
}