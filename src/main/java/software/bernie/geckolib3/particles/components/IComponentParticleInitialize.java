package software.bernie.geckolib3.particles.components;

import software.bernie.geckolib3.particles.emitter.BedrockEmitter;
import software.bernie.geckolib3.particles.emitter.BedrockParticle;

public interface IComponentParticleInitialize extends IComponentBase {
    public void apply(BedrockEmitter emitter, BedrockParticle particle);
}
