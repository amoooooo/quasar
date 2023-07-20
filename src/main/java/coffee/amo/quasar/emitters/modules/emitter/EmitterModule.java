package coffee.amo.quasar.emitters.modules.emitter;

import net.minecraft.world.phys.Vec3;

public class EmitterModule implements BaseEmitterModule {
    /**
     * Position of the emitter
     */
    Vec3 position;
    /**
     * Maximum number of ticks the emitter will be active for
     */
    int maxLifetime;
    /**
     * Current number of ticks the emitter has been active for
     */
    int currentLifetime = 0;
    /**
     * Whether or not the emitter will loop. If true, the emitter will reset after maxLifetime ticks
     */
    boolean loop = false;
    /**
     * The rate at which particles are emitted. <count> particles per <rate> ticks.
     * E.G. rate = 2, count = 1 means 1 particle every 2 ticks
     */
    int rate = 1;
    /**
     * The number of particles emitted per <rate> ticks
     */
    int count = 1;

    /**
     * Whether or not the emitter has completed its lifetime
     */
    boolean complete = false;

    /**
     * Constructs a new emitter module
     *
     * @param position    Position of the emitter
     * @param maxLifetime Maximum number of ticks the emitter will be active for
     * @param loop        Whether or not the emitter will loop. If true, the emitter will reset after maxLifetime ticks
     * @param rate        The rate at which particles are emitted. <count> particles per <rate> ticks.
     * @param count       The number of particles emitted per <rate> ticks
     */
    public EmitterModule(Vec3 position, int maxLifetime, boolean loop, int rate, int count) {
        this.position = position;
        this.maxLifetime = maxLifetime;
        this.loop = loop;
        this.rate = rate;
        this.count = count;
    }

    /**
     * Tick the emitter. This is run to track the basic functionality of the emitter.
     */
    public void tick(Runnable action) {
        currentLifetime++;
        action.run();
        if (currentLifetime >= maxLifetime) {
            if (loop) {
                currentLifetime = 0;
            } else {
                complete = true;
            }
        }
    }

    /**
     * Whether or not the emitter has completed its lifetime
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * Position of the emitter
     */
    public Vec3 getPosition() {
        return position;
    }

    /**
     * The rate at which particles are emitted. <count> particles per <rate> ticks.
     */
    public int getRate() {
        return rate;
    }

    /**
     * The number of particles emitted per <rate> ticks
     */
    public int getCount() {
        return count;
    }

    /**
     * Number of ticks the emitter has been active for
     */
    public int getCurrentLifetime() {
        return currentLifetime;
    }

    /**
     * Maximum number of ticks the emitter will be active for
     */
    public int getMaxLifetime() {
        return maxLifetime;
    }

    /**
     * Whether or not the emitter will loop. If true, the emitter will reset after maxLifetime ticks
     */
    public boolean getLoop() {
        return loop;
    }

    /**
     * Whether or not the emitter has completed its lifetime
     */
    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    /**
     * Set the position of the emitter
     */
    public void setPosition(Vec3 position) {
        this.position = position;
    }

    /**
     * Set the rate at which particles are emitted. <count> particles per <rate> ticks.
     */
    public void setRate(int rate) {
        this.rate = rate;
    }

    /**
     * Set the number of particles emitted per <rate> ticks
     */
    public void setCount(int count) {
        this.count = count;
    }
}
