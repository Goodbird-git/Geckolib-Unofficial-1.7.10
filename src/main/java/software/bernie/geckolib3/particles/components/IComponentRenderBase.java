package software.bernie.geckolib3.particles.components;

import software.bernie.geckolib3.particles.emitter.BedrockEmitter;
import software.bernie.geckolib3.particles.emitter.BedrockParticle;
import net.minecraft.client.renderer.Tessellator;

public interface IComponentRenderBase extends IComponentBase
{
    public void render(BedrockEmitter emitter, BedrockParticle particle, Tessellator builder, float partialTicks);

    public void renderOnScreen(BedrockParticle particle, int x, int y, float scale, float partialTicks);

    public void preRender(BedrockEmitter emitter, float partialTicks);

    public void postRender(BedrockEmitter emitter, float partialTicks);
}
