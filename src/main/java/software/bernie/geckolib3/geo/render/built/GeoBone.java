package software.bernie.geckolib3.geo.render.built;

import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GeoBone implements IBone, Serializable {
    private static final long serialVersionUID = 42L;
    public GeoBone parent;

    public List<GeoBone> childBones = new ArrayList<>();
    public List<GeoCube> childCubes = new ArrayList<>();

    public String name;
    private BoneSnapshot initialSnapshot;

    public Boolean mirror;
    public Double inflate;
    public Boolean dontRender;
    public boolean isHidden;
    public boolean areCubesHidden = false;
    public boolean hideChildBonesToo;
    public Boolean reset;

    private float scaleX = 1;
    private float scaleY = 1;
    private float scaleZ = 1;

    private float positionX;
    private float positionY;
    private float positionZ;

    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;

    private float rotateX;
    private float rotateY;
    private float rotateZ;

    public transient Object extraData;

    private transient Matrix4f modelSpaceXform;
    private transient Matrix4f localSpaceXform;
    private transient Matrix4f worldSpaceXform;
    private transient Matrix3f worldSpaceNormal;

    private transient boolean trackXform;
    public transient Matrix4f rotMat;

    {
        modelSpaceXform = new Matrix4f();
        modelSpaceXform.setIdentity();
        localSpaceXform = new Matrix4f();
        localSpaceXform.setIdentity();
        worldSpaceXform = new Matrix4f();
        worldSpaceXform.setIdentity();
        worldSpaceNormal = new Matrix3f();
        worldSpaceNormal.setIdentity();
        trackXform = false;
        rotMat = null;
    }

    @Override
    public void setModelRendererName(String modelRendererName) {
        this.name = modelRendererName;
    }

    @Override
    public void saveInitialSnapshot() {
        if (this.initialSnapshot == null) {
            this.initialSnapshot = new BoneSnapshot(this, true);
        }
    }

    @Override
    public BoneSnapshot getInitialSnapshot() {
        return this.initialSnapshot;
    }

    @Override
    public String getName() {
        return this.name;
    }

    // Boilerplate code incoming

    @Override
    public float getRotationX() {
        return rotateX;
    }

    @Override
    public float getRotationY() {
        return rotateY;
    }

    @Override
    public float getRotationZ() {
        return rotateZ;
    }

    @Override
    public float getPositionX() {
        return positionX;
    }

    @Override
    public float getPositionY() {
        return positionY;
    }

    @Override
    public float getPositionZ() {
        return positionZ;
    }

    @Override
    public float getScaleX() {
        return scaleX;
    }

    @Override
    public float getScaleY() {
        return scaleY;
    }

    @Override
    public float getScaleZ() {
        return scaleZ;
    }

    @Override
    public void setRotationX(float value) {
        this.rotateX = value;
    }

    @Override
    public void setRotationY(float value) {
        this.rotateY = value;
    }

    @Override
    public void setRotationZ(float value) {
        this.rotateZ = value;
    }

    @Override
    public void setPositionX(float value) {
        this.positionX = value;
    }

    @Override
    public void setPositionY(float value) {
        this.positionY = value;
    }

    @Override
    public void setPositionZ(float value) {
        this.positionZ = value;
    }

    @Override
    public void setScaleX(float value) {
        this.scaleX = value;
    }

    @Override
    public void setScaleY(float value) {
        this.scaleY = value;
    }

    @Override
    public void setScaleZ(float value) {
        this.scaleZ = value;
    }

    @Override
    public boolean isHidden() {
        return this.isHidden;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.setHidden(hidden, hidden);
    }

    @Override
    public void setPivotX(float value) {
        this.rotationPointX = value;
    }

    @Override
    public void setPivotY(float value) {
        this.rotationPointY = value;
    }

    @Override
    public void setPivotZ(float value) {
        this.rotationPointZ = value;
    }

    @Override
    public float getPivotX() {
        return this.rotationPointX;
    }

    @Override
    public float getPivotY() {
        return this.rotationPointY;
    }

    @Override
    public float getPivotZ() {
        return this.rotationPointZ;
    }

    @Override
    public boolean cubesAreHidden() {
        return areCubesHidden;
    }

    @Override
    public boolean childBonesAreHiddenToo() {
        return hideChildBonesToo;
    }

    @Override
    public void setCubesHidden(boolean hidden) {
        this.areCubesHidden = hidden;
    }

    @Override
    public void setHidden(boolean selfHidden, boolean skipChildRendering) {
        this.isHidden = selfHidden;
        this.hideChildBonesToo = skipChildRendering;
    }

    // ---- Matrix transform tracking (ported from GeckoLib 1.16) ----

    public GeoBone getParent() {
        return this.parent;
    }

    public boolean isTrackingXform() {
        return trackXform;
    }

    public void setTrackXform(boolean trackXform) {
        this.trackXform = trackXform;
    }

    public Matrix4f getModelSpaceXform() {
        setTrackXform(true);
        return modelSpaceXform;
    }

    public void setModelSpaceXform(Matrix4f xform) {
        this.modelSpaceXform.set(xform);
    }

    /**
     * Gets the position of a bone relative to the model.
     */
    public Point3f getModelPosition() {
        Matrix4f matrix = getModelSpaceXform();
        float x = matrix.m03;
        float y = matrix.m13;
        float z = matrix.m23;
        return new Point3f(-x * 16f, y * 16f, z * 16f);
    }

    public Matrix4f getLocalSpaceXform() {
        setTrackXform(true);
        return localSpaceXform;
    }

    public void setLocalSpaceXform(Matrix4f xform) {
        this.localSpaceXform.set(xform);
    }

    /**
     * Gets the position of a bone relative to the entity.
     */
    public Point3f getLocalPosition() {
        Matrix4f matrix = getLocalSpaceXform();
        return new Point3f(matrix.m03, matrix.m13, matrix.m23);
    }

    public Matrix4f getWorldSpaceXform() {
        setTrackXform(true);
        return worldSpaceXform;
    }

    public void setWorldSpaceXform(Matrix4f xform) {
        this.worldSpaceXform.set(xform);
    }

    public Matrix3f getWorldSpaceNormal() {
        return worldSpaceNormal;
    }

    public void setWorldSpaceNormal(Matrix3f normal) {
        this.worldSpaceNormal.set(normal);
    }

    /**
     * Gets the position of a bone relative to the world.
     */
    public Point3f getWorldPosition() {
        Matrix4f matrix = getWorldSpaceXform();
        return new Point3f(matrix.m03, matrix.m13, matrix.m23);
    }

    public void setModelPosition(Point3f pos) {
        GeoBone p = getParent();
        Matrix4f identity = new Matrix4f();
        identity.setIdentity();
        Matrix4f matrix = p == null ? identity : new Matrix4f(p.getModelSpaceXform());
        matrix.invert();
        float x = -(pos.x / 16f);
        float y = pos.y / 16f;
        float z = pos.z / 16f;
        // Transform point by inverted parent matrix
        float rx = matrix.m00 * x + matrix.m01 * y + matrix.m02 * z + matrix.m03;
        float ry = matrix.m10 * x + matrix.m11 * y + matrix.m12 * z + matrix.m13;
        float rz = matrix.m20 * x + matrix.m21 * y + matrix.m22 * z + matrix.m23;
        setPosition(-rx * 16f, ry * 16f, rz * 16f);
    }

    public Matrix4f getModelRotationMat() {
        Matrix4f matrix = new Matrix4f(getModelSpaceXform());
        removeMatrixTranslation(matrix);
        return matrix;
    }

    public static void removeMatrixTranslation(Matrix4f matrix) {
        matrix.m03 = 0;
        matrix.m13 = 0;
        matrix.m23 = 0;
    }

    public void setModelRotationMat(Matrix4f mat) {
        rotMat = mat;
    }

    // ---- Convenience position/rotation/scale utilities ----

    public void addPosition(float x, float y, float z) {
        setPositionX(getPositionX() + x);
        setPositionY(getPositionY() + y);
        setPositionZ(getPositionZ() + z);
    }

    public void setPosition(float x, float y, float z) {
        setPositionX(x);
        setPositionY(y);
        setPositionZ(z);
    }

    public void addRotation(float x, float y, float z) {
        setRotationX(getRotationX() + x);
        setRotationY(getRotationY() + y);
        setRotationZ(getRotationZ() + z);
    }

    public void setRotation(float x, float y, float z) {
        setRotationX(x);
        setRotationY(y);
        setRotationZ(z);
    }

    public void setScale(float x, float y, float z) {
        setScaleX(x);
        setScaleY(y);
        setScaleZ(z);
    }

    public void addRotationOffsetFromBone(GeoBone source) {
        setRotationX(getRotationX() + source.getRotationX() - source.getInitialSnapshot().rotationValueX);
        setRotationY(getRotationY() + source.getRotationY() - source.getInitialSnapshot().rotationValueY);
        setRotationZ(getRotationZ() + source.getRotationZ() - source.getInitialSnapshot().rotationValueZ);
    }
}
