package version;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Christoffer Nilsson on 2016-05-12.
 */
public class ResourceUtil {

    private Context context;
    private JSONObject champions;
    private JSONObject masteries;
    private JSONObject runes;
    private JSONObject spells;

    public ResourceUtil(Context context) {
        this.context = context;
    }

    public void writeResource(String resourceName, String content) throws IOException {
        FileOutputStream outputStream = context.openFileOutput(resourceName, Context.MODE_PRIVATE);
        outputStream.write(content.getBytes());
        outputStream.close();
    }

    public JSONObject getChampions() {
        return champions;
    }

    public JSONObject getMasteries() {
        return masteries;
    }

    public JSONObject getRunes() {
        return runes;
    }

    public JSONObject getSpells() {
        return spells;
    }

    public void buildResources() {
        try {
            champions = buildResource("champions.json");
            masteries = buildResource("masteries.json");
            runes = buildResource("runes.json");
            spells = buildResource("spells.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject buildResource(String resource) throws IOException, JSONException {
        Scanner scanner = new Scanner(context.openFileInput(resource));
        String str = "";
        while (scanner.hasNext()) {
            str += scanner.nextLine();
        }
        scanner.close();
        return new JSONObject(str);
    }
}
