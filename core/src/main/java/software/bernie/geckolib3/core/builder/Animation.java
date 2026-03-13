//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package software.bernie.geckolib3.core.builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.keyframe.BoneAnimation;
import software.bernie.geckolib3.core.keyframe.EventKeyFrame;
import software.bernie.geckolib3.core.keyframe.ParticleEventKeyFrame;

public class Animation implements Serializable {
    private static final long serialVersionUID = 42L;
    public String animationName;
    public Double animationLength;
    public ILoopType loop;
    public List<BoneAnimation> boneAnimations;
    public List<EventKeyFrame<String>> soundKeyFrames;
    public List<ParticleEventKeyFrame> particleKeyFrames;
    public List<EventKeyFrame<String>> customInstructionKeyframes;

    public Animation() {
        this.loop = EDefaultLoopTypes.LOOP;
        this.soundKeyFrames = new ArrayList<>();
        this.particleKeyFrames = new ArrayList<>();
        this.customInstructionKeyframes = new ArrayList<>();
    }

    /**
     * Deep copies an animation via serialization.
     */
    public static Animation copy(Animation animation) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(animation);
            oos.flush();
            oos.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Animation) ois.readObject();
        } catch (Exception e) {
            return animation;
        }
    }
}
