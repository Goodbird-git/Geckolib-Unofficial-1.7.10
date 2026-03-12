package software.bernie.geckolib3.resource;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.file.ItemDisplayFile;
import software.bernie.geckolib3.file.ItemDisplayFileLoader;
import software.bernie.geckolib3.network.NetworkHandler;
import software.bernie.geckolib3.network.PacketRemoveItemDisplay;
import software.bernie.geckolib3.network.PacketSendItemDisplay;
import software.bernie.geckolib3.watchers.ItemDisplayDirWatcher;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ItemDisplayLibrary {
    private HashMap<ResourceLocation, ItemDisplayFile> folderDisplays = new HashMap<>();
    public File folder;
    public ItemDisplayDirWatcher updateController;
    public static ItemDisplayLibrary instance;

    public ItemDisplayLibrary(File folder) {
        instance = this;
        this.folder = folder;
        this.folder.mkdirs();
        updateController = new ItemDisplayDirWatcher(folder);
        updateController.start();
    }

    public void reload(boolean sync) {
        folderDisplays.clear();
        recursiveWalk(this.folder);
        if (sync) syncAll();
    }

    public void syncPlayer(EntityPlayer player) {
        for (Map.Entry<ResourceLocation, ItemDisplayFile> entry : folderDisplays.entrySet()) {
            NetworkHandler.sendToPlayer(new PacketSendItemDisplay(entry.getValue(), entry.getKey().getResourcePath()), player);
        }
    }

    public void syncAll() {
        if (MinecraftServer.getServer() != null) {
            for (Map.Entry<ResourceLocation, ItemDisplayFile> entry : folderDisplays.entrySet()) {
                NetworkHandler.sendToAll(new PacketSendItemDisplay(entry.getValue(), entry.getKey().getResourcePath()));
            }
        }
    }

    public void syncAdd(ResourceLocation location) {
        if (MinecraftServer.getServer() != null) {
            NetworkHandler.sendToAll(new PacketSendItemDisplay(folderDisplays.get(location), location.getResourcePath()));
        }
    }

    public void syncRemove(ResourceLocation location) {
        if (MinecraftServer.getServer() != null) {
            NetworkHandler.sendToAll(new PacketRemoveItemDisplay(location.getResourcePath()));
        }
    }

    public void recursiveWalk(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                this.storeDisplay(file);
            }
            if (file.isDirectory()) {
                recursiveWalk(file);
            }
        }
    }

    public ItemDisplayFile get(ResourceLocation identifier) {
        return folderDisplays.get(identifier);
    }

    public String[] getFileNames() {
        String[] names = new String[folderDisplays.size()];
        int i = 0;
        for (ResourceLocation loc : folderDisplays.keySet()) {
            String path = loc.getResourcePath();
            names[i++] = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
        }
        return names;
    }

    public void remove(File file) {
        ResourceLocation location = new ResourceLocation("custom", getNameFromFile(file));
        folderDisplays.remove(location);
        syncRemove(location);
    }

    public String getNameFromFile(File file) {
        String folderPath = folder.getAbsolutePath();
        return file.getAbsolutePath().substring(folderPath.length() + 1).replace('\\', '/');
    }

    public void storeDisplay(File file) {
        ResourceLocation location = new ResourceLocation("custom", getNameFromFile(file));
        ItemDisplayFile display = loadDisplay(file);
        if (display != null) {
            folderDisplays.put(location, display);
            syncAdd(location);
        }
    }

    public ItemDisplayFile loadDisplay(File file) {
        try {
            return ItemDisplayFileLoader.getInstance().loadFromFile(file);
        } catch (Exception e) {
            return null;
        }
    }
}
