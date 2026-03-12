package software.bernie.geckolib3.file;

import java.io.Serializable;

/**
 * Holds per-context display transforms for an item model.
 * Loaded from JSON files in the item_displays/ folder.
 * Serializable for network sync from server to client.
 */
public class ItemDisplayFile implements Serializable {
    private static final long serialVersionUID = 1L;

    private float[] firstPersonTranslation;
    private float[] firstPersonRotation;
    private float[] firstPersonScale;

    private float[] thirdPersonTranslation;
    private float[] thirdPersonRotation;
    private float[] thirdPersonScale;

    private float[] inventoryTranslation;
    private float[] inventoryRotation;
    private float[] inventoryScale;

    private float[] groundTranslation;
    private float[] groundRotation;
    private float[] groundScale;

    public ItemDisplayFile() {
    }

    // First Person
    public float[] getFirstPersonTranslation() { return firstPersonTranslation; }
    public void setFirstPersonTranslation(float[] t) { this.firstPersonTranslation = t; }
    public float[] getFirstPersonRotation() { return firstPersonRotation; }
    public void setFirstPersonRotation(float[] r) { this.firstPersonRotation = r; }
    public float[] getFirstPersonScale() { return firstPersonScale; }
    public void setFirstPersonScale(float[] s) { this.firstPersonScale = s; }

    // Third Person
    public float[] getThirdPersonTranslation() { return thirdPersonTranslation; }
    public void setThirdPersonTranslation(float[] t) { this.thirdPersonTranslation = t; }
    public float[] getThirdPersonRotation() { return thirdPersonRotation; }
    public void setThirdPersonRotation(float[] r) { this.thirdPersonRotation = r; }
    public float[] getThirdPersonScale() { return thirdPersonScale; }
    public void setThirdPersonScale(float[] s) { this.thirdPersonScale = s; }

    // Inventory
    public float[] getInventoryTranslation() { return inventoryTranslation; }
    public void setInventoryTranslation(float[] t) { this.inventoryTranslation = t; }
    public float[] getInventoryRotation() { return inventoryRotation; }
    public void setInventoryRotation(float[] r) { this.inventoryRotation = r; }
    public float[] getInventoryScale() { return inventoryScale; }
    public void setInventoryScale(float[] s) { this.inventoryScale = s; }

    // Ground
    public float[] getGroundTranslation() { return groundTranslation; }
    public void setGroundTranslation(float[] t) { this.groundTranslation = t; }
    public float[] getGroundRotation() { return groundRotation; }
    public void setGroundRotation(float[] r) { this.groundRotation = r; }
    public float[] getGroundScale() { return groundScale; }
    public void setGroundScale(float[] s) { this.groundScale = s; }

    public boolean hasFirstPerson() { return firstPersonTranslation != null || firstPersonRotation != null || firstPersonScale != null; }
    public boolean hasThirdPerson() { return thirdPersonTranslation != null || thirdPersonRotation != null || thirdPersonScale != null; }
    public boolean hasInventory() { return inventoryTranslation != null || inventoryRotation != null || inventoryScale != null; }
    public boolean hasGround() { return groundTranslation != null || groundRotation != null || groundScale != null; }
}
