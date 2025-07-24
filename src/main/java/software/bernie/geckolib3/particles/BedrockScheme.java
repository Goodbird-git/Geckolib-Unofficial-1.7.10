package software.bernie.geckolib3.particles;

import com.eliotlash.mclib.math.Variable;
import com.eliotlash.molang.MolangParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.particles.components.BedrockComponentBase;
import software.bernie.geckolib3.particles.components.IComponentBase;
import software.bernie.geckolib3.particles.components.IComponentEmitterInitialize;
import software.bernie.geckolib3.particles.components.IComponentEmitterUpdate;
import software.bernie.geckolib3.particles.components.IComponentParticleInitialize;
import software.bernie.geckolib3.particles.components.IComponentParticleMorphRender;
import software.bernie.geckolib3.particles.components.IComponentParticleRender;
import software.bernie.geckolib3.particles.components.IComponentParticleUpdate;
import software.bernie.geckolib3.particles.components.motion.BedrockComponentInitialSpeed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BedrockScheme {
    public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("geckolib3", "textures/default_particles.png");
    public static final Gson JSON_PARSER = new GsonBuilder()
        .registerTypeAdapter(BedrockScheme.class, new BedrockSchemeJsonAdapter())
        .create();

    /* Particles identifier */
    public String identifier = "";

    /* Particles name in BedrockLibrary */
    public String name = "";

    /* Particle description */
    public BedrockMaterial material = BedrockMaterial.OPAQUE;
    public ResourceLocation texture = DEFAULT_TEXTURE;

    /* Particle's curves */
    public Map<String, BedrockCurve> curves = new HashMap<String, BedrockCurve>();

    /* Particle's components */
    public List<BedrockComponentBase> components = new ArrayList<BedrockComponentBase>();
    public List<IComponentEmitterInitialize> emitterInitializes;
    public List<IComponentEmitterUpdate> emitterUpdates;
    public List<IComponentParticleInitialize> particleInitializes;
    public List<IComponentParticleUpdate> particleUpdates;
    public List<IComponentParticleRender> particleRender;
    public List<IComponentParticleMorphRender> particleMorphRender;

    private boolean factory;

    /* MoLang integration */
    public MolangParser parser;
    public boolean toReload = false;

    public static BedrockScheme parse(String json) {
        return JSON_PARSER.fromJson(json, BedrockScheme.class);
    }

    public static BedrockScheme parse(JsonElement json) {
        return JSON_PARSER.fromJson(json, BedrockScheme.class);
    }

    public static JsonElement toJson(BedrockScheme scheme) {
        return JSON_PARSER.toJsonTree(scheme);
    }

    /**
     * Probably it's very expensive, but it's much easier than implementing copy methods
     * to every component in the particle system...
     */
    public static BedrockScheme dupe(BedrockScheme scheme) {
        return parse(toJson(scheme));
    }

    public BedrockScheme() {
        this.parser = new MolangParser();

        /* Default variables */
        this.parser.register(new Variable("variable.particle_age", 0));
        this.parser.register(new Variable("variable.particle_lifetime", 0));
        this.parser.register(new Variable("variable.particle_random_1", 0));
        this.parser.register(new Variable("variable.particle_random_2", 0));
        this.parser.register(new Variable("variable.particle_random_3", 0));
        this.parser.register(new Variable("variable.particle_random_4", 0));

        this.parser.register(new Variable("variable.particle_speed.length", 0));
        this.parser.register(new Variable("variable.particle_speed.x", 0));
        this.parser.register(new Variable("variable.particle_speed.y", 0));
        this.parser.register(new Variable("variable.particle_speed.z", 0));
        this.parser.register(new Variable("variable.particle_bounces", 0));

        this.parser.register(new Variable("variable.emitter_age", 0));
        this.parser.register(new Variable("variable.emitter_lifetime", 0));
        this.parser.register(new Variable("variable.emitter_random_1", 0));
        this.parser.register(new Variable("variable.emitter_random_2", 0));
        this.parser.register(new Variable("variable.emitter_random_3", 0));
        this.parser.register(new Variable("variable.emitter_random_4", 0));
    }

    public BedrockScheme factory(boolean factory) {
        this.factory = factory;

        return this;
    }

    public boolean isFactory() {
        return this.factory;
    }

    public void setup() {
        this.getOrCreate(BedrockComponentInitialSpeed.class);

        this.emitterInitializes = this.getComponents(IComponentEmitterInitialize.class);
        this.emitterUpdates = this.getComponents(IComponentEmitterUpdate.class);
        this.particleInitializes = this.getComponents(IComponentParticleInitialize.class);
        this.particleUpdates = this.getComponents(IComponentParticleUpdate.class);
        this.particleRender = this.getComponents(IComponentParticleRender.class);
        this.particleMorphRender = this.getComponents(IComponentParticleMorphRender.class);

        /* Link variables with curves */
        for (Map.Entry<String, BedrockCurve> entry : this.curves.entrySet()) {
            entry.getValue().variable = this.parser.variables.get(entry.getKey());
        }
    }

    public <T extends IComponentBase> List<T> getComponents(Class<T> clazz) {
        List<T> list = new ArrayList<T>();

        for (BedrockComponentBase component : this.components) {
            if (clazz.isAssignableFrom(component.getClass())) {
                list.add((T) component);
            }
        }

        if (list.size() > 1) {
            Collections.sort(list, Comparator.comparingInt(IComponentBase::getSortingIndex));
        }

        return list;
    }

    public <T extends BedrockComponentBase> T get(Class<T> clazz) {
        for (BedrockComponentBase component : this.components) {
            if (clazz.isAssignableFrom(component.getClass())) {
                return (T) component;
            }
        }

        return null;
    }

    public <T extends BedrockComponentBase> T getExact(Class<T> clazz) {
        for (BedrockComponentBase component : this.components) {
            if (clazz.equals(component.getClass())) {
                return (T) component;
            }
        }

        return null;
    }

    public <T extends BedrockComponentBase> T add(Class<T> clazz) {
        T result = null;

        try {
            result = clazz.getConstructor().newInstance();

            this.components.add(result);
            this.setup();
        } catch (Exception e) {
        }

        return result;
    }

    /**
     * This method gets the component using isAssignableFrom() method. It can also get sub-classes
     *
     * @param clazz target class
     * @param <T>
     * @return the component object
     */
    public <T extends BedrockComponentBase> T getOrCreate(Class<T> clazz) {
        return this.getOrCreate(clazz, clazz);
    }

    /**
     * This method gets the component by its exact class and no sub-classes.
     *
     * @param clazz target class
     * @param <T>
     * @return the component object
     */
    public <T extends BedrockComponentBase> T getOrCreateExact(Class<T> clazz) {
        return this.getOrCreateExact(clazz, clazz);
    }

    /**
     * This method gets the component using isAssignableFrom() method. It can also get sub-classes. If clazz hasn't been found it will add the subclass parameter.
     *
     * @param clazz target class
     * @param clazz alternative class too add in case target class doesnt exist
     * @param <T>
     * @return the component object
     */
    public <T extends BedrockComponentBase> T getOrCreate(Class<T> clazz, Class subclass) {
        T result = this.get(clazz);

        if (result == null) {
            result = (T) this.add(subclass);
        }

        return result;
    }

    public <T extends BedrockComponentBase> T getOrCreateExact(Class<T> clazz, Class subclass) {
        T result = this.getExact(clazz);

        if (result == null) {
            result = (T) this.add(subclass);
        }

        return result;
    }

    public <T extends BedrockComponentBase> T remove(Class<T> clazz) {
        Iterator<BedrockComponentBase> it = this.components.iterator();

        while (it.hasNext()) {
            BedrockComponentBase component = it.next();

            if (clazz.isAssignableFrom(component.getClass())) {
                it.remove();

                return (T) component;
            }
        }

        return null;
    }

    public <T extends BedrockComponentBase> T replace(Class<T> clazz, Class subclass) {
        this.remove(clazz);

        return (T) this.add(subclass);
    }

    /**
     * Update curve values
     */
    public void updateCurves() {
        for (BedrockCurve curve : this.curves.values()) {
            if (curve.variable != null) {
                curve.variable.set(curve.compute());
            }
        }
    }
}
