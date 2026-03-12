package software.bernie.geckolib3.watchers;

import software.bernie.geckolib3.resource.ItemDisplayLibrary;

import java.io.File;

public class ItemDisplayDirWatcher extends AbstractDirWatcher {
    public ItemDisplayDirWatcher(File file) {
        super(file.toPath(), true);
    }

    @Override
    public void processCreate(File path) {
        String name = path.getName();
        if (!name.endsWith(".json")) return;
        ItemDisplayLibrary.instance.storeDisplay(path);
    }

    @Override
    public void processDelete(File path) {
        String name = path.getName();
        if (!name.endsWith(".json")) return;
        ItemDisplayLibrary.instance.remove(path);
    }

    @Override
    public void processModify(File path) {
        String name = path.getName();
        if (!name.endsWith(".json")) return;
        ItemDisplayLibrary.instance.storeDisplay(path);
    }
}
