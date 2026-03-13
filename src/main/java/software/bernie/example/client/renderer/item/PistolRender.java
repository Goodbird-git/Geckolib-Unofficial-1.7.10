package software.bernie.example.client.renderer.item;

import org.lwjgl.opengl.GL11;
import software.bernie.example.client.model.item.PistolModel;
import software.bernie.example.item.PistolItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class PistolRender extends GeoItemRenderer<PistolItem> {
	public PistolRender() {
		super(new PistolModel());
	}

	@Override
	protected void applyRenderTypeTransforms(ItemRenderType type) {
		switch (type) {
			case INVENTORY:
				// GUI display — apply rotation to show the pistol at an angle
				GL11.glTranslated(-1, -1, 0);
				GL11.glRotatef(90, 0, 1, 0);
				GL11.glRotatef(32, 0, 0, 1);
				GL11.glRotatef(50, 1, 0, 0);
				GL11.glScalef(0.7f, 0.7f, 0.7f);
				GL11.glTranslated(-0.25 / 16.0, -1.25 / 16.0, 0);
				break;
			case EQUIPPED:
				// Third person hand
				GL11.glTranslated(0, -0.5, 0);
				GL11.glScalef(0.3f, 0.3f, 0.3f);
				GL11.glTranslated(0, -1.0 / 16.0, -0.25 / 16.0);
				break;
			case EQUIPPED_FIRST_PERSON:
				// First person hand — large translation offsets from pistol.json
				GL11.glTranslated(-9.5 / 16.0, -6.75 / 16.0, -8.75 / 16.0);
				break;
			case ENTITY:
				// Dropped on ground
				GL11.glTranslated(0, -0.5, 0);
				GL11.glScalef(0.5f, 0.5f, 0.5f);
				break;
		}
	}
}
