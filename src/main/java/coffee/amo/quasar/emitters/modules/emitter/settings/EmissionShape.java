package coffee.amo.quasar.emitters.modules.emitter.settings;

import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public enum EmissionShape {
    CUBE((settings) -> {
        double x = settings.randomSource.nextDouble() * 2 - 1;
        double y = settings.randomSource.nextDouble() * 2 - 1;
        double z = settings.randomSource.nextDouble() * 2 - 1;
        double max = Math.max(Math.abs(x), Math.max(Math.abs(y), Math.abs(z)));
        Vec3 normal = new Vec3(x / max, y / max, z / max);
        Vec3 dimensions = settings.dimensions.get();
        if(!settings.fromSurface){
            normal = normal.scale(settings.randomSource.nextDouble()).normalize();
            dimensions = dimensions.multiply(
                    settings.randomSource.nextDouble(),
                    settings.randomSource.nextDouble(),
                    settings.randomSource.nextDouble()
            );
        }
        Vec3 pos = normal.multiply(dimensions);
        pos = pos.xRot((float) Math.toRadians(settings.rotation.get().x())).yRot((float) Math.toRadians(settings.rotation.get().y())).zRot((float) Math.toRadians(settings.rotation.get().z()));
        return pos.add(settings.position.get());
    }),
    SPHERE((settings) -> {
        double x = settings.randomSource.nextDouble() * 2 - 1;
        double y = settings.randomSource.nextDouble() * 2 - 1;
        double z = settings.randomSource.nextDouble() * 2 - 1;
        Vec3 normal = new Vec3(x, y, z).normalize();
        Vec3 dimensions = settings.dimensions.get();
        if(!settings.fromSurface){
            normal = normal.scale(settings.randomSource.nextDouble()).normalize();
            dimensions = dimensions.multiply(
                    settings.randomSource.nextDouble(),
                    settings.randomSource.nextDouble(),
                    settings.randomSource.nextDouble()
            );
        }
        Vec3 pos = normal.multiply(dimensions);
        pos = pos.xRot((float) Math.toRadians(settings.rotation.get().x())).yRot((float) Math.toRadians(settings.rotation.get().y())).zRot((float) Math.toRadians(settings.rotation.get().z()));
        return pos.add(settings.position.get());
    }),
    HEMISPHERE((settings) -> {
        double theta = settings.randomSource.nextDouble() * 2 * Math.PI;
        double phi = settings.randomSource.nextDouble() * Math.PI / 2;
        double x = Math.cos(theta) * Math.sin(phi);
        double y = Math.sin(theta) * Math.sin(phi);
        double z = Math.cos(phi);
        Vec3 normal = new Vec3(x, y, z).normalize();
        Vec3 dimensions = settings.dimensions.get();
        if(!settings.fromSurface){
            normal = normal.scale(settings.randomSource.nextDouble()).normalize();
            dimensions = dimensions.multiply(
                    settings.randomSource.nextDouble(),
                    settings.randomSource.nextDouble(),
                    settings.randomSource.nextDouble()
            );
        }
        Vec3 pos = normal.multiply(dimensions);
        pos = pos.xRot((float) Math.toRadians(settings.rotation.get().x())).yRot((float) Math.toRadians(settings.rotation.get().y())).zRot((float) Math.toRadians(settings.rotation.get().z()));
        return pos.add(settings.position.get());
    }),
    CYLINDER((settings) -> {
        double theta = settings.randomSource.nextDouble() * 2 * Math.PI;
        double x = Math.cos(theta);
        double y = Math.sin(theta);
        double z = settings.randomSource.nextDouble() * 2 - 1;
        Vec3 normal = new Vec3(x, y, z).normalize();
        Vec3 dimensions = settings.dimensions.get();
        if(!settings.fromSurface){
            normal = normal.scale(settings.randomSource.nextDouble()).normalize();
            dimensions = dimensions.multiply(
                    settings.randomSource.nextDouble(),
                    settings.randomSource.nextDouble(),
                    settings.randomSource.nextDouble()
            );
        }
        Vec3 pos = normal.multiply(dimensions);
        pos = pos.xRot((float) Math.toRadians(settings.rotation.get().x())).yRot((float) Math.toRadians(settings.rotation.get().y())).zRot((float) Math.toRadians(settings.rotation.get().z()));
        return pos.add(settings.position.get());
    }),
    TORUS((settings) -> {
        double theta = settings.randomSource.nextDouble() * 2 * Math.PI;
        double phi = settings.randomSource.nextDouble() * 2 * Math.PI;
        double x = Math.cos(theta) * (1 + 0.5 * Math.cos(phi));
        double y = Math.sin(theta) * (1 + 0.5 * Math.cos(phi));
        double z = settings.dimensions.get().z() * Math.sin(phi);
        Vec3 normal = new Vec3(x, y, z).normalize();
        Vec3 dimensions = settings.dimensions.get();
        if(!settings.fromSurface){
            normal = normal.scale(settings.randomSource.nextDouble()).normalize();
            dimensions = dimensions.multiply(
                    settings.randomSource.nextDouble(),
                    settings.randomSource.nextDouble(),
                    settings.randomSource.nextDouble()
            );
        }
        Vec3 pos = normal.multiply(dimensions);
        pos = pos.xRot((float) Math.toRadians(settings.rotation.get().x())).yRot((float) Math.toRadians(settings.rotation.get().y())).zRot((float) Math.toRadians(settings.rotation.get().z()));
        return pos.add(settings.position.get());
    }),
    DISC((settings) -> {
        double x = settings.randomSource.nextGaussian();
        double y = 0.0000001;
        double z = settings.randomSource.nextGaussian();
        Vec3 normal = new Vec3(x, y, z).normalize();
        Vec3 dimensions = settings.dimensions.get();
        if(!settings.fromSurface){
            normal = normal.scale(settings.randomSource.nextDouble()).normalize();
            dimensions = dimensions.multiply(
                    settings.randomSource.nextDouble(),
                    settings.randomSource.nextDouble(),
                    settings.randomSource.nextDouble()
            );
        }
        Vec3 pos = normal.multiply(dimensions);
        pos = pos.xRot((float) Math.toRadians(settings.rotation.get().x())).yRot((float) Math.toRadians(settings.rotation.get().y())).zRot((float) Math.toRadians(settings.rotation.get().z()));
        return pos.add(settings.position.get());
    }),
    PLANE((settings) -> {
        double x = settings.randomSource.nextGaussian() * 2 - 1;
        double y = settings.randomSource.nextGaussian() * 2 - 1;
        double z = settings.randomSource.nextGaussian() * 2 - 1;
        double max = Math.max(Math.abs(x), Math.max(Math.abs(y), Math.abs(z)));
        Vec3 normal = new Vec3(x / max, y / max, z / max).normalize();
        Vec3 dimensions = settings.dimensions.get();
        if(!settings.fromSurface){
            normal = normal.scale(settings.randomSource.nextDouble()).normalize();
            dimensions = dimensions.multiply(
                    settings.randomSource.nextDouble(),
                    settings.randomSource.nextDouble(),
                    settings.randomSource.nextDouble()
            );
        }
        Vec3 pos = normal.multiply(dimensions);
        pos = pos.xRot((float) Math.toRadians(settings.rotation.get().x())).yRot((float) Math.toRadians(settings.rotation.get().y())).zRot((float) Math.toRadians(settings.rotation.get().z()));
        return pos.add(settings.position.get());
    }),
    POINT((settings) -> {
        return settings.position.get();
    })
    ;

    Function<EmissionShapeSettings, Vec3> emissionShapeFunction;

    EmissionShape(Function<EmissionShapeSettings, Vec3> emissionShapeFunction) {
        this.emissionShapeFunction = emissionShapeFunction;
    }

    public Vec3 getPos(EmissionShapeSettings emissionShapeSettings) {
        return emissionShapeFunction.apply(emissionShapeSettings);
    }
}
