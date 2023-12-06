package software.bernie.geckolib3.particles.components.expiration;

import software.bernie.geckolib3.particles.components.IComponentParticleUpdate;
import software.bernie.geckolib3.particles.emitter.BedrockEmitter;
import software.bernie.geckolib3.particles.emitter.BedrockParticle;
import net.minecraft.block.Block;

public class BedrockComponentExpireInBlocks extends BedrockComponentExpireBlocks implements IComponentParticleUpdate
{
    @Override
    public void update(BedrockEmitter emitter, BedrockParticle particle)
    {
        if (particle.dead || emitter.world == null)
        {
            return;
        }

        Block current = this.getBlock(emitter, particle);

        for (Block block : this.blocks)
        {
            if (block == current)
            {
                particle.dead = true;

                return;
            }
        }
    }
}