package software.bernie.example.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import net.geckominecraft.client.renderer.GlStateManager;
import software.bernie.example.client.model.entity.BikeModel;
import software.bernie.example.client.model.entity.GeoNpcModel;
import software.bernie.example.entity.BikeEntity;
import software.bernie.example.entity.GeoNpcEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.particles.emitter.BedrockEmitter;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.Map;

public class GeoNpcRenderer extends GeoEntityRenderer<GeoNpcEntity> {
    public GeoNpcRenderer() {
        super(new GeoNpcModel());
    }

    @Override
    public void renderAfter(GeoModel model, GeoNpcEntity animatable, float ticks, float red, float green, float blue, float alpha) {
        super.renderAfter(model,animatable,ticks,red,green,blue,alpha);
        if(model.getBone("held_item").isPresent() && animatable.getHeldItem()!=null) {
            GeoBone bone = model.getBone("held_item").get();
            renderItem(animatable,bone,ticks);
        }

        //drawParticles(model,animatable,ticks);
    }

    public GeoBone[] getPathFromRoot(GeoBone bone){
        ArrayList<GeoBone> bones = new ArrayList<>();
        while(bone!=null){
            bones.add(0,bone);
            bone = bone.parent;
        }
        return bones.toArray(new GeoBone[0]);
    }

    public void renderItem(GeoNpcEntity animatable, GeoBone locator, float ticks) {

        GlStateManager.pushAttrib();
        GL11.glPushMatrix();
        try {
            float scale = 0.5f;
            GL11.glScaled(scale, scale, scale);

            GeoBone[] bonePath = getPathFromRoot(locator);
            for(int i=0;i< bonePath.length;i++) {
                GeoBone b = bonePath[i];
                GL11.glTranslatef(b.getPositionX()/(16*scale), b.getPositionY()/(16*scale), b.getPositionZ()/(16*scale));
                GL11.glTranslatef(b.getPivotX()/(16*scale), b.getPivotY()/(16*scale), b.getPivotZ()/(16*scale));

                GL11.glRotated(b.getRotationZ() / Math.PI * 180, 0, 0, 1);
                GL11.glRotated(b.getRotationY() / Math.PI * 180, 0, 1, 0);
                GL11.glRotated(b.getRotationX() / Math.PI * 180, 1, 0, 0);

                GL11.glScalef(b.getScaleX(), b.getScaleY(), b.getScaleZ());
                GL11.glTranslatef(-b.getPivotX()/(16*scale), -b.getPivotY()/(16*scale), -b.getPivotZ()/(16*scale));
            }
            GL11.glRotatef(250, 1, 0, 0);
            GL11.glRotatef(40, 0, 1, 0);
            GL11.glTranslatef(-0.4f, -0.55f, 1.7f);
            ItemStack stack = animatable.getHeldItem();
            RenderManager.instance.itemRenderer.renderItem(animatable, stack, 0, IItemRenderer.ItemRenderType.INVENTORY);
        } finally {
            GL11.glPopMatrix();
            GlStateManager.popAttrib();
        }
    }


    @Override
    public boolean isBoneRenderOverriden(GeoNpcEntity entity, GeoBone bone) {
        return bone.name.equals("held_item");
    }
}
