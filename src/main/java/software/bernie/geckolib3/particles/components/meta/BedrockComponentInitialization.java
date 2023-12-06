package software.bernie.geckolib3.particles.components.meta;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import software.bernie.geckolib3.particles.components.BedrockComponentBase;
import software.bernie.geckolib3.particles.components.IComponentEmitterInitialize;
import software.bernie.geckolib3.particles.components.IComponentEmitterUpdate;
import software.bernie.geckolib3.particles.emitter.BedrockEmitter;
import com.eliotlash.mclib.math.IValue;
import com.eliotlash.molang.MolangException;
import com.eliotlash.molang.MolangParser;
import com.eliotlash.molang.expressions.MolangAssignment;
import com.eliotlash.molang.expressions.MolangExpression;
import com.eliotlash.molang.expressions.MolangMultiStatement;

import java.util.Map;

public class BedrockComponentInitialization extends BedrockComponentBase implements IComponentEmitterInitialize, IComponentEmitterUpdate
{
    /* Standard BedrockEdition variables - global inside an emitter */
    public MolangExpression creation = MolangParser.ZERO;
    public MolangExpression update = MolangParser.ZERO;

    /* Blockbuster specific expression - local inside a particle (added by Chryfi) */
    public MolangExpression particleUpdate = MolangParser.ZERO;

    public BedrockComponentBase fromJson(JsonElement elem, MolangParser parser) throws MolangException
    {
        if (!elem.isJsonObject()) return super.fromJson(elem, parser);

        JsonObject element = elem.getAsJsonObject();

        if (element.has("creation_expression")) this.creation = parser.parseGlobalJson(element.get("creation_expression"));
        if (element.has("per_update_expression")) this.update = parser.parseGlobalJson(element.get("per_update_expression"));
        if (element.has("particle_update_expression")) this.particleUpdate = parser.parseGlobalJson(element.get("particle_update_expression"));

        return super.fromJson(element, parser);
    }

    @Override
    public JsonElement toJson()
    {
        JsonObject object = new JsonObject();

        if (!MolangExpression.isZero(this.creation)) object.add("creation_expression", this.creation.toJson());
        if (!MolangExpression.isZero(this.update)) object.add("per_update_expression", this.update.toJson());
        if (!MolangExpression.isZero(this.particleUpdate)) object.add("particle_update_expression", this.particleUpdate.toJson());

        return object;
    }

    @Override
    public void apply(BedrockEmitter emitter)
    {
        emitter.initialValues.clear();

        this.creation.get();
        this.cacheInitialValues(this.creation, emitter);

        if (emitter.variables != null)
        {
            for (Map.Entry<String, IValue> entry : emitter.variables.entrySet())
            {
                emitter.initialValues.put(entry.getKey(), entry.getValue().get());
            }
        }
    }

    @Override
    public void update(BedrockEmitter emitter)
    {
        this.update.get();
        this.cacheInitialValues(this.update, emitter);

        emitter.replaceVariables();
    }

    private void cacheInitialValues(MolangExpression e, BedrockEmitter emitter)
    {
        if (e instanceof MolangMultiStatement)
        {
            MolangMultiStatement statement = (MolangMultiStatement) e;

            for (MolangExpression expression : statement.expressions)
            {
                if (expression instanceof MolangAssignment)
                {
                    this.cacheInitialValue((MolangAssignment) expression, emitter);
                }
            }
        }
        else if (e instanceof MolangAssignment)
        {
            this.cacheInitialValue((MolangAssignment) e, emitter);
        }
    }

    private void cacheInitialValue(MolangAssignment assignment, BedrockEmitter emitter)
    {
        emitter.initialValues.put(assignment.variable.getName(), assignment.variable.get());
    }
}
