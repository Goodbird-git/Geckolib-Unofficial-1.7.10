package com.eliotlash.mclib.math;

import software.bernie.geckolib3.resource.GeckoLibCache;

public class GeckoVariable extends Variable{
    public GeckoVariable(String name, double value) {
        super(name, value);
    }

    public double get() {
        if(this!= GeckoLibCache.getInstance().parser.getVariable(getName())){
            return GeckoLibCache.getInstance().parser.getVariable(getName()).get();
        }
        return super.get();
    }
}
