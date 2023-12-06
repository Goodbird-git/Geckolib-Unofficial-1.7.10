//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package software.bernie.geckolib3.core.keyframe;

import com.eliotlash.mclib.math.IValue;

import java.io.Serializable;

public class BoneAnimation implements Serializable {
    private static final long serialVersionUID = 42L;
    public String boneName;
    public VectorKeyFrameList<KeyFrame<IValue>> rotationKeyFrames;
    public VectorKeyFrameList<KeyFrame<IValue>> positionKeyFrames;
    public VectorKeyFrameList<KeyFrame<IValue>> scaleKeyFrames;

    public BoneAnimation() {
    }
}
