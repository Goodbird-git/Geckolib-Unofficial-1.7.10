package software.bernie.geckolib3.file;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Loads item display JSON files and parses them into ItemDisplayFile objects.
 * Supports both custom format keys (first_person, third_person, inventory, ground)
 * and Blockbench Java export keys (firstperson_righthand, thirdperson_righthand, gui, ground).
 */
public class ItemDisplayFileLoader {
    private static ItemDisplayFileLoader INSTANCE;

    public static ItemDisplayFileLoader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ItemDisplayFileLoader();
        }
        return INSTANCE;
    }

    public ItemDisplayFile loadFromFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
        }
        reader.close();
        return parse(sb.toString());
    }

    public ItemDisplayFile loadFromStream(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
        }
        reader.close();
        return parse(sb.toString());
    }

    private ItemDisplayFile parse(String json) {
        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(json).getAsJsonObject();

        if (!root.has("display")) return null;

        ItemDisplayFile data = new ItemDisplayFile();
        JsonObject display = root.getAsJsonObject("display");

        // First person
        JsonObject fp = getContext(display, "first_person", "firstperson_righthand");
        if (fp != null) {
            data.setFirstPersonTranslation(parseArray(fp, "translation"));
            data.setFirstPersonRotation(parseArray(fp, "rotation"));
            data.setFirstPersonScale(parseArray(fp, "scale"));
        }

        // Third person
        JsonObject tp = getContext(display, "third_person", "thirdperson_righthand");
        if (tp != null) {
            data.setThirdPersonTranslation(parseArray(tp, "translation"));
            data.setThirdPersonRotation(parseArray(tp, "rotation"));
            data.setThirdPersonScale(parseArray(tp, "scale"));
        }

        // Inventory
        JsonObject inv = getContext(display, "inventory", "gui");
        if (inv != null) {
            data.setInventoryTranslation(parseArray(inv, "translation"));
            data.setInventoryRotation(parseArray(inv, "rotation"));
            data.setInventoryScale(parseArray(inv, "scale"));
        }

        // Ground
        JsonObject gnd = getContext(display, "ground", "ground");
        if (gnd != null) {
            data.setGroundTranslation(parseArray(gnd, "translation"));
            data.setGroundRotation(parseArray(gnd, "rotation"));
            data.setGroundScale(parseArray(gnd, "scale"));
        }

        return data;
    }

    private JsonObject getContext(JsonObject display, String key, String blockbenchKey) {
        if (display.has(key)) {
            return display.getAsJsonObject(key);
        } else if (blockbenchKey != null && display.has(blockbenchKey)) {
            return display.getAsJsonObject(blockbenchKey);
        }
        return null;
    }

    private float[] parseArray(JsonObject obj, String key) {
        if (!obj.has(key)) return null;
        JsonArray arr = obj.getAsJsonArray(key);
        float[] result = new float[3];
        for (int i = 0; i < 3 && i < arr.size(); i++) {
            result[i] = arr.get(i).getAsFloat();
        }
        return result;
    }
}
