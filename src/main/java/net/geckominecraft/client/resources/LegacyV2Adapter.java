package net.geckominecraft.client.resources;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class LegacyV2Adapter implements IResourcePack {
    private final IResourcePack pack;

    public LegacyV2Adapter(IResourcePack packIn) {
        this.pack = packIn;
    }

    public InputStream getInputStream(ResourceLocation location) throws IOException {
        return this.pack.getInputStream(this.fudgePath(location));
    }

    private ResourceLocation fudgePath(ResourceLocation p_191382_1_) {
        String s = p_191382_1_.getResourcePath();

        if (!"lang/swg_de.lang".equals(s) && s.startsWith("lang/") && s.endsWith(".lang")) {
            int i = s.indexOf(95);

            if (i != -1) {
                final String s1 = s.substring(0, i + 1) + s.substring(i + 1, s.indexOf(46, i)).toUpperCase() + ".lang";
                return new ResourceLocation(p_191382_1_.getResourceDomain(), "") {
                    public String getResourcePath() {
                        return s1;
                    }
                };
            }
        }

        return p_191382_1_;
    }

    public boolean resourceExists(ResourceLocation location) {
        return this.pack.resourceExists(this.fudgePath(location));
    }

    public Set<String> getResourceDomains() {
        return this.pack.getResourceDomains();
    }

    @Nullable
    public IMetadataSection getPackMetadata(IMetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
        return this.pack.getPackMetadata(metadataSerializer, metadataSectionName);
    }

    public BufferedImage getPackImage() throws IOException {
        return this.pack.getPackImage();
    }

    public String getPackName() {
        return this.pack.getPackName();
    }
}
