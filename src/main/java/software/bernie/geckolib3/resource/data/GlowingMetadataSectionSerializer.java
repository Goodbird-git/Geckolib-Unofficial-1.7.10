package software.bernie.geckolib3.resource.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.data.IMetadataSectionSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializer for emissive/glowing texture metadata sections.
 * Reads "glowsections" from .mcmeta files to define emissive texture regions.
 *
 * Example .mcmeta format:
 * <pre>
 * {
 *   "glowsections": {
 *     "sections": [
 *       { "x1": 0, "y1": 0, "x2": 16, "y2": 16 },
 *       { "x": 32, "y": 32, "w": 8, "h": 8 }
 *     ]
 *   }
 * }
 * </pre>
 *
 * Originally developed for Chocolate Quest Repoured by DerToaster98.
 */
@SuppressWarnings("rawtypes")
public class GlowingMetadataSectionSerializer implements IMetadataSectionSerializer {

	@Override
	public String getSectionName() {
		return "glowsections";
	}

	@Override
	public GlowingMetadataSection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
		JsonObject jsonObject = json.getAsJsonObject();
		if (jsonObject.has("sections")) {
			JsonArray sectionsArray = jsonObject.getAsJsonArray("sections");
			List<GlowingMetadataSection.Section> sections = new ArrayList<>();

			for (int i = 0; i < sectionsArray.size(); i++) {
				JsonElement element = sectionsArray.get(i);
				if (element.isJsonObject()) {
					JsonObject sectionObj = element.getAsJsonObject();
					int x1 = getInt(sectionObj, "x1", getInt(sectionObj, "x", 0));
					int y1 = getInt(sectionObj, "y1", getInt(sectionObj, "y", 0));
					int width = getInt(sectionObj, "w", 0);
					int height = getInt(sectionObj, "h", 0);
					int x2 = getInt(sectionObj, "x2", x1 + width);
					int y2 = getInt(sectionObj, "y2", y1 + height);
					sections.add(new GlowingMetadataSection.Section(x1, y1, x2, y2));
				}
			}

			if (!sections.isEmpty()) {
				return new GlowingMetadataSection(sections.stream());
			}
		}
		return null;
	}

	private static int getInt(JsonObject obj, String key, int defaultValue) {
		return obj.has(key) ? obj.get(key).getAsInt() : defaultValue;
	}
}
