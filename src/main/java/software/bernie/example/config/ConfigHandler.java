package software.bernie.example.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigHandler {
    public static Configuration config;
    public static boolean enableExamples;
    public ConfigHandler() {
    }

    public static void init(File file) {
        config = new Configuration(file);
        syncConfig();
    }

    public static void load() {
        config.load();
    }
    public static void syncConfig() {
        config.addCustomCategoryComment("general","General Settings");
        enableExamples = config.getBoolean("tablename","general",false,"Database table name");
        config.save();
    }
}
