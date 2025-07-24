/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.util.json;

import com.eliotlash.mclib.math.IValue;
import com.eliotlash.molang.MolangException;
import com.eliotlash.molang.MolangParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.math.NumberUtils;
import software.bernie.geckolib3.core.ConstantValue;
import software.bernie.geckolib3.core.easing.EasingType;
import software.bernie.geckolib3.core.keyframe.KeyFrame;
import software.bernie.geckolib3.core.keyframe.VectorKeyFrameList;
import software.bernie.geckolib3.util.AnimationUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper class to convert json to keyframes
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class JsonKeyFrameUtils {
    private static VectorKeyFrameList<KeyFrame<IValue>> convertJson(List<Map.Entry<String, JsonElement>> element,
                                                                    boolean isRotation, MolangParser parser) throws NumberFormatException, MolangException {
        IValue previousXValue = null;
        IValue previousYValue = null;
        IValue previousZValue = null;

        List<KeyFrame<IValue>> xKeyFrames = new ArrayList();
        List<KeyFrame<IValue>> yKeyFrames = new ArrayList();
        List<KeyFrame<IValue>> zKeyFrames = new ArrayList();

        for (int i = 0; i < element.size(); i++) {
            Map.Entry<String, JsonElement> keyframe = element.get(i);
            Map.Entry<String, JsonElement> previousKeyFrame = i == 0 ? null : element.get(i - 1);

            Double previousKeyFrameLocation = previousKeyFrame == null ? 0
                : Double.parseDouble(previousKeyFrame.getKey());
            Double currentKeyFrameLocation = NumberUtils.isNumber(keyframe.getKey())
                ? Double.parseDouble(keyframe.getKey())
                : 0;
            Double animationTimeDifference = currentKeyFrameLocation - previousKeyFrameLocation;

            JsonArray vectorJsonArray = getKeyFrameVector(keyframe.getValue());
            IValue xValue = parseExpression(parser, vectorJsonArray.get(0));
            IValue yValue = parseExpression(parser, vectorJsonArray.get(1));
            IValue zValue = parseExpression(parser, vectorJsonArray.get(2));

            IValue currentXValue = isRotation && xValue instanceof ConstantValue
                ? ConstantValue.fromDouble(Math.toRadians(-xValue.get()))
                : xValue;
            IValue currentYValue = isRotation && yValue instanceof ConstantValue
                ? ConstantValue.fromDouble(Math.toRadians(-yValue.get()))
                : yValue;
            IValue currentZValue = isRotation && zValue instanceof ConstantValue
                ? ConstantValue.fromDouble(Math.toRadians(zValue.get()))
                : zValue;
            KeyFrame<IValue> xKeyFrame;
            KeyFrame<IValue> yKeyFrame;
            KeyFrame<IValue> zKeyFrame;

            if (keyframe.getValue().isJsonObject() && hasEasingType(keyframe.getValue())) {
                EasingType easingType = getEasingType(keyframe.getValue());
                if (hasEasingArgs(keyframe.getValue())) {
                    List<IValue> easingArgs = getEasingArgs(keyframe.getValue());
                    xKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
                        i == 0 ? currentXValue : previousXValue, currentXValue, easingType, easingArgs);
                    yKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
                        i == 0 ? currentYValue : previousYValue, currentYValue, easingType, easingArgs);
                    zKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
                        i == 0 ? currentZValue : previousZValue, currentZValue, easingType, easingArgs);
                } else {
                    xKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
                        i == 0 ? currentXValue : previousXValue, currentXValue, easingType);
                    yKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
                        i == 0 ? currentYValue : previousYValue, currentYValue, easingType);
                    zKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
                        i == 0 ? currentZValue : previousZValue, currentZValue, easingType);

                }
            } else {
                xKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
                    i == 0 ? currentXValue : previousXValue, currentXValue);
                yKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
                    i == 0 ? currentYValue : previousYValue, currentYValue);
                zKeyFrame = new KeyFrame(AnimationUtils.convertSecondsToTicks(animationTimeDifference),
                    i == 0 ? currentZValue : previousZValue, currentZValue);
            }

            previousXValue = currentXValue;
            previousYValue = currentYValue;
            previousZValue = currentZValue;

            xKeyFrames.add(xKeyFrame);
            yKeyFrames.add(yKeyFrame);
            zKeyFrames.add(zKeyFrame);
        }

        return new VectorKeyFrameList<>(xKeyFrames, yKeyFrames, zKeyFrames);
    }

    private static JsonArray getKeyFrameVector(JsonElement element) {
        if (element.isJsonArray()) {
            return element.getAsJsonArray();
        } else {
            return element.getAsJsonObject().get("vector").getAsJsonArray();
        }
    }

    private static boolean hasEasingType(JsonElement element) {
        return element.getAsJsonObject().has("easing") || element.getAsJsonObject().has("lerp_mode");
    }

    private static boolean hasEasingArgs(JsonElement element) {
        return element.getAsJsonObject().has("easingArgs");
    }

    private static EasingType getEasingType(JsonElement element) {
        final String easingString;
        if (element.getAsJsonObject().has("easing"))
            easingString = element.getAsJsonObject().get("easing").getAsString();
        else
            easingString = element.getAsJsonObject().get("lerp_mode").getAsString();
        try {
            final String uppercaseEasingString = Character.toUpperCase(easingString.charAt(0))
                + easingString.substring(1);
            EasingType easing = EasingType.valueOf(uppercaseEasingString);
            return easing;
        } catch (Exception e) {
            System.err.println("[GeckoLib] " + "Unknown easing type: {}" + easingString);
            throw new RuntimeException(e);
        }
    }

    private static List<IValue> getEasingArgs(JsonElement element) {
        JsonObject asJsonObject = element.getAsJsonObject();
        JsonElement easingArgs = asJsonObject.get("easingArgs");
        JsonArray asJsonArray = easingArgs.getAsJsonArray();
        return JsonAnimationUtils.convertJsonArrayToList(asJsonArray);
    }

    /**
     * Convert json to a rotation key frame vector list. This method also converts
     * degrees to radians.
     *
     * @param element The keyframe parent json element
     * @param parser
     * @return the vector key frame list
     * @throws NumberFormatException The number format exception
     */
    public static VectorKeyFrameList<KeyFrame<IValue>> convertJsonToKeyFrames(
        List<Map.Entry<String, JsonElement>> element, MolangParser parser)
        throws NumberFormatException, MolangException {
        List<Map.Entry<String, JsonElement>> list = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : element) {
            if (entry.getValue() instanceof JsonObject && !((JsonObject) entry.getValue()).has("vector")) {
                JsonObject entryObj = ((JsonObject) entry.getValue());
                String timestamp = entry.getKey();
                double time = NumberUtils.isNumber(timestamp) ? Double.parseDouble(timestamp) : 0;

                if (entryObj.has("pre")) {
                    JsonElement postElement = entryObj.get("pre");
                    JsonArray array = postElement.isJsonArray() ? postElement.getAsJsonArray() : postElement.getAsJsonObject().getAsJsonArray("vector");
                    JsonObject object = new JsonObject();
                    object.add("vector", array);
                    if (entryObj.has("lerp_mode")) {
                        object.add("lerp_mode", entryObj.get("lerp_mode"));
                    }
                    list.add(new AbstractMap.SimpleEntry<>(timestamp, object));
                }

                if (entryObj.has("post")) {
                    JsonElement postElement = entryObj.get("post");
                    JsonArray array = postElement.isJsonArray() ? postElement.getAsJsonArray() : postElement.getAsJsonObject().getAsJsonArray("vector");
                    JsonObject object = new JsonObject();
                    object.add("vector", array);
                    if (entryObj.has("lerp_mode")) {
                        object.add("lerp_mode", entryObj.get("lerp_mode"));
                    }
                    list.add(new AbstractMap.SimpleEntry<>(String.valueOf(time + 0.0000001), object));
                }

                continue;
            }

            list.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
        }
        return convertJson(list, false, parser);
    }

    /**
     * Convert json to normal json keyframes
     *
     * @param element The keyframe parent json element
     * @param parser
     * @return the vector key frame list
     * @throws NumberFormatException
     */
    public static VectorKeyFrameList<KeyFrame<IValue>> convertJsonToRotationKeyFrames(
        List<Map.Entry<String, JsonElement>> element, MolangParser parser)
        throws NumberFormatException, MolangException {
        List<Map.Entry<String, JsonElement>> list = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : element) {
            if (entry.getValue() instanceof JsonObject && !((JsonObject) entry.getValue()).has("vector")) {
                JsonObject entryObj = ((JsonObject) entry.getValue());
                String timestamp = entry.getKey();
                double time = NumberUtils.isNumber(timestamp) ? Double.parseDouble(timestamp) : 0;

                if (entryObj.has("pre")) {
                    JsonElement postElement = entryObj.get("pre");
                    JsonArray array = postElement.isJsonArray() ? postElement.getAsJsonArray() : postElement.getAsJsonObject().getAsJsonArray("vector");
                    JsonObject object = new JsonObject();
                    object.add("vector", array);
                    if (entryObj.has("lerp_mode")) {
                        object.add("lerp_mode", entryObj.get("lerp_mode"));
                    }
                    list.add(new AbstractMap.SimpleEntry<>(timestamp, object));
                }

                if (entryObj.has("post")) {
                    JsonElement postElement = entryObj.get("post");
                    JsonArray array = postElement.isJsonArray() ? postElement.getAsJsonArray() : postElement.getAsJsonObject().getAsJsonArray("vector");
                    JsonObject object = new JsonObject();
                    object.add("vector", array);
                    if (entryObj.has("lerp_mode")) {
                        object.add("lerp_mode", entryObj.get("lerp_mode"));
                    }
                    list.add(new AbstractMap.SimpleEntry<>(String.valueOf(time + 0.0000001), object));
                }

                continue;
            }

            list.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
        }
        VectorKeyFrameList<KeyFrame<IValue>> frameList = convertJson(list, true, parser);
        return new VectorKeyFrameList(frameList.xKeyFrames, frameList.yKeyFrames, frameList.zKeyFrames);
    }

    public static IValue parseExpression(MolangParser parser, JsonElement element) throws MolangException {
        if (element.getAsJsonPrimitive().isString()) {
            return parser.parseJson(element);
        } else {
            return ConstantValue.fromDouble(element.getAsDouble());
        }
    }

}
