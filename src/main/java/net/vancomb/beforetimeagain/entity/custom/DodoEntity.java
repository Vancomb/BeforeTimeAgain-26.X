package net.vancomb.beforetimeagain.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.ChickenSoundVariants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.vancomb.beforetimeagain.particle.ModParticles;
import net.vancomb.beforetimeagain.sound.ModSounds;
import org.jspecify.annotations.Nullable;

/**
 * The Dodo entity.
 *
 * Behavior summary:
 * - Wanders around, swims if it ends up in water
 * - Gets tempted by fish (will follow a player holding one)
 * - Panics when hurt: screams briefly, then runs away
 * - Occasionally sits down → rests → falls asleep → wakes up → stands back up
 * - Anything hits it while sleeping = instantly awake so it can flee
 */
public class DodoEntity extends Animal {

    // ===========================================================
    // SYNCED ENTITY DATA
    // ===========================================================
    // These are values that need to exist on BOTH the server and the client.
    // The server is the source of truth, but the client needs to know about
    // them too (e.g. to play the right animation). Vanilla auto-syncs these
    // for us; we just declare them here.

    // True while the panic goal is actively running. Server reads it from
    // vanilla, then writes it here so the client knows to play the run animation.
    private static final EntityDataAccessor<Boolean> IS_PANICKING =
            SynchedEntityData.defineId(DodoEntity.class, EntityDataSerializers.BOOLEAN);

    // Which sleep phase the dodo is currently in (see PHASE_* constants below).
    private static final EntityDataAccessor<Integer> SLEEP_PHASE =
            SynchedEntityData.defineId(DodoEntity.class, EntityDataSerializers.INT);

    // The absolute world tick when the current phase started.
    // Subtract this from the current tick to find out how long the dodo
    // has been in its current phase.
    private static final EntityDataAccessor<Long> SLEEP_PHASE_START_TICK =
            SynchedEntityData.defineId(DodoEntity.class, EntityDataSerializers.LONG);


    // ===========================================================
    // SLEEP CYCLE CONSTANTS
    // ===========================================================
    // The full cycle:
    //   AWAKE → SITTING_DOWN → RESTING → FALL_SLEEP → SLEEPING
    //         → WAKING_UP → STANDING_UP → AWAKE
    //
    // Some phases play a short one-shot animation and auto-advance after
    // a fixed time (the *_DURATION constants). Others are open-ended and
    // wait for a random roll (RESTING, SLEEPING — see CHANCE_* constants).

    public static final int PHASE_AWAKE        = 0;
    public static final int PHASE_SITTING_DOWN = 1;
    public static final int PHASE_RESTING      = 2;
    public static final int PHASE_FALL_SLEEP   = 3;   // was 6
    public static final int PHASE_SLEEPING     = 4;   // was 3
    public static final int PHASE_WAKING_UP    = 5;   // was 4
    public static final int PHASE_STANDING_UP  = 6;   // was 5


    // How long each one-shot transition phase lasts, in ticks (20 ticks = 1 second).
    // These should match the lengths of the corresponding animations.
    private static final int DOWN_DURATION       = 10;
    private static final int FALL_SLEEP_DURATION = 10;
    private static final int WAKE_UP_DURATION    = 10;
    private static final int STAND_UP_DURATION   = 15;

    // Minimum time the dodo MUST stay in these open-ended phases before
    // it's even allowed to roll for transition. Stops it from immediately
    // standing back up the tick after sitting down.
    private static final int MIN_REST_DURATION  = 100;   // 5 seconds
    private static final int MIN_SLEEP_DURATION = 200;  // 10 seconds

    // Per-tick chance denominators. Each tick we roll random.nextInt(N);
    // if it equals 0, the transition fires. Bigger N = rarer.
    private static final int CHANCE_START_SITTING   = 1200;   // ~1 per minute
    private static final int CHANCE_START_SLEEPING  = 200;   // once min-rest is satisfied
    private static final int CHANCE_WAKE_UP         = 400;          // once min-sleep is satisfied


    // ===========================================================
    // ANIMATION STATES
    // ===========================================================
    // One AnimationState per animation. The client reads these to decide
    // which animation should be playing right now. We start/stop them
    // based on what the dodo is doing (see setupAnimationStates below).
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState swimAnimationState = new AnimationState();
    public final AnimationState runAnimationState = new AnimationState();
    public final AnimationState screamAnimationState = new AnimationState();
    public final AnimationState downAnimationState = new AnimationState();
    public final AnimationState restAnimationState = new AnimationState();
    public final AnimationState fallSleepAnimationState = new AnimationState();
    public final AnimationState sleepAnimationState = new AnimationState();
    public final AnimationState wakeUpAnimationState = new AnimationState();
    public final AnimationState upAnimationState = new AnimationState();


    // ===========================================================
    // INTERNAL STATE
    // ===========================================================

    // Counts down from 20 (1 second) after the dodo gets hit, so the
    // scream animation plays for exactly that long even if the actual
    // damage event was instantaneous.
    private int screamTimer = 0;

    // Last tick's hurtTime value. We compare it to the current value
    // each tick to detect "did the dodo just get hit this exact tick?"
    private int lastHurtTime = 0;


    public DodoEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        // Replace vanilla's look control with ours so we can suppress
        // head tracking during sleep.
        this.lookControl = new DodoLookControl(this);
    }


    // ===========================================================
    // CONTROL OVERRIDES
    // ===========================================================
    // Vanilla calls this to build the "body rotation" controller. Body
    // rotation is separate from head rotation — it's what makes the dodo's
    // body slowly rotate to face the direction it's walking. We replace
    // vanilla's with our own so we can freeze it during sleep.
    @Override
    protected BodyRotationControl createBodyControl() {
        return new DodoBodyControl(this);
    }


    //BREEDABLE

     @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Tags.Items.FOODS_RAW_FISH);

    }

    // ===========================================================
    // SYNCED DATA REGISTRATION
    // ===========================================================
    // This is where the synced data fields are actually instantiated for
    // each individual dodo. Has to declare every accessor we defined above.
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_PANICKING, false);
        builder.define(SLEEP_PHASE, PHASE_AWAKE);
        builder.define(SLEEP_PHASE_START_TICK, 0L);
    }


    // ===========================================================
    // SLEEP CYCLE HELPERS
    // ===========================================================

    private int getSleepPhase() {
        return this.entityData.get(SLEEP_PHASE);
    }

    /** How many ticks have passed since the current phase started. */
    private long getSleepPhaseTime() {
        return this.level().getGameTime() - this.entityData.get(SLEEP_PHASE_START_TICK);
    }

    /**
     * Server-side only: switch to a new phase and reset the phase timer.
     * Don't call this on the client — the synced fields will sync down naturally.
     */
    private void setSleepPhase(int phase) {
        this.entityData.set(SLEEP_PHASE, phase);
        this.entityData.set(SLEEP_PHASE_START_TICK, this.level().getGameTime());
    }

    /**
     * True if the dodo is doing anything sleep-related (sitting, resting, sleeping, etc).
     * This drives all the "freeze the dodo in place" logic — movement, body/head rotation.
     */
    public boolean isInSleepCycle() {
        return getSleepPhase() != PHASE_AWAKE;
    }


    // ===========================================================
    // SLEEP CYCLE STATE MACHINE (server-side)
    // ===========================================================
    // Each tick on the server, look at where we are in the cycle and
    // decide whether to advance to the next phase. Two kinds of transitions:
    //   - Time-based (e.g. SITTING_DOWN ends after DOWN_DURATION ticks)
    //   - Roll-based (e.g. RESTING rolls each tick to maybe fall asleep)
    private void tickSleepCycle() {
        int phase = getSleepPhase();
        long phaseTime = getSleepPhaseTime();

        switch (phase) {
            case PHASE_AWAKE -> {
                // Only sit down if the dodo is genuinely idle: not panicking,
                // not in water, not actively walking. Keeps it from sitting
                // mid-stride or while fleeing.
                if (!this.entityData.get(IS_PANICKING)
                        && !this.isInWater()
                        && this.getDeltaMovement().horizontalDistance() < 0.05
                        && this.random.nextInt(CHANCE_START_SITTING) == 0) {
                    setSleepPhase(PHASE_SITTING_DOWN);
                }
            }
            case PHASE_SITTING_DOWN -> {
                // One-shot transition. As soon as the down animation has
                // played long enough, slide into the actual rest pose.
                if (phaseTime >= DOWN_DURATION) {
                    setSleepPhase(PHASE_RESTING);
                }
            }
            case PHASE_RESTING -> {
                // Rest for at least the minimum, then roll to fall asleep.
                // Could rest indefinitely if the rolls keep failing.
                if (phaseTime >= MIN_REST_DURATION
                        && this.random.nextInt(CHANCE_START_SLEEPING) == 0) {
                    setSleepPhase(PHASE_FALL_SLEEP);
                }
            }
            case PHASE_FALL_SLEEP -> {
                if (phaseTime >= FALL_SLEEP_DURATION) {
                    setSleepPhase(PHASE_SLEEPING);
                }
            }
            case PHASE_SLEEPING -> {
                // Sleep for at least the minimum, then roll to wake up.
                if (phaseTime >= MIN_SLEEP_DURATION
                        && this.random.nextInt(CHANCE_WAKE_UP) == 0) {
                    setSleepPhase(PHASE_WAKING_UP);
                }
            }
            case PHASE_WAKING_UP -> {
                if (phaseTime >= WAKE_UP_DURATION) {
                    setSleepPhase(PHASE_STANDING_UP);
                }
            }
            case PHASE_STANDING_UP -> {
                if (phaseTime >= STAND_UP_DURATION) {
                    setSleepPhase(PHASE_AWAKE);
                }
            }
        }
    }


    // ===========================================================
    // AI GOALS
    // ===========================================================
    // Goals are vanilla's way of giving an entity decision-making behavior.
    // Lower priority numbers = higher priority. Only one goal in each
    // priority bracket runs at a time, so the order matters.
    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));                                                       // don't drown
        goalSelector.addGoal(1, new PanicGoal(this, 1.6d));                                     // run when hurt (1.6x speed)
        goalSelector.addGoal(2, new TemptGoal(this, 1.25d, stack -> stack.is(Tags.Items.FOODS_RAW_FISH), false));     // follow fish
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1f));                    // wander around
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 5f, 0.2f));    // glance at nearby players
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));                                             // idle head turning
        super.registerGoals();
    }


    // ===========================================================
    // ATTRIBUTES (stats)
    // ===========================================================
    // Base stats. MOVEMENT_SPEED interacts with the panic goal multiplier
    // (1.6x) — so panic speed = 0.20 * 1.6 = 0.32.
    public static AttributeSupplier.Builder createDodoAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10d)
                .add(Attributes.MOVEMENT_SPEED, 0.20D)
                .add(Attributes.TEMPT_RANGE, 16d)
                .add(Attributes.FOLLOW_RANGE, 16d);
    }


    // ===========================================================
    // MOVEMENT GATE
    // ===========================================================
    // Vanilla calls travel() every tick to actually MOVE the entity based
    // on whatever input the AI requested. By zeroing the input here when
    // sleeping, we kill all goal-driven movement at the source — TemptGoal
    // can request the dodo move toward a fish all it wants, but nothing
    // happens. This is way cleaner than trying to undo movement after the
    // fact. Vertical motion is preserved so gravity still works.
    @Override
    public void travel(Vec3 input) {
        if (isInSleepCycle() && this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0, 1, 0));
            input = Vec3.ZERO;
        }
        super.travel(input);
    }


    // ===========================================================
    // TICK
    // ===========================================================
    // Called every tick on both sides. Server handles state changes,
    // client handles visuals.
    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            // Mirror vanilla's panic state into our synced field so the
            // client can see it and play the run animation accordingly.
            this.entityData.set(IS_PANICKING, super.isPanicking());

            tickSleepCycle();

            // Cancel any active path while sleeping. Movement is already
            // blocked in travel() — this just stops the pathfinder from
            // burning CPU calculating paths it can't execute.
            if (isInSleepCycle()) {
                this.getNavigation().stop();
            }
        }

        // Detect a fresh damage event by watching for hurtTime jumping up.
        // hurtTime is a vanilla counter that gets set high every time the
        // entity takes damage and decreases each tick. If this tick's value
        // is bigger than last tick's, damage just happened.
        if (this.hurtTime > this.lastHurtTime) {
            this.screamTimer = 20;
        }
        this.lastHurtTime = this.hurtTime;

        if (this.screamTimer > 0) {
            this.screamTimer--;
        }

        // Client-only: drive animation states + spawn ambient particles.
        if (this.level().isClientSide()) {
            this.setupAnimationStates();

            // Steady stream of Z particles every half-second while sleeping.
           // if (getSleepPhase() == PHASE_SLEEPING && this.tickCount % 10 == 0) {
            //    spawnSleepParticles();
            //}
        }
    }


    // ===========================================================
    // ANIMATION STATE MACHINE
    // ===========================================================
    // Every client tick, decide which single animation should be playing
    // and make sure all the others are stopped. Priority order:
    //   water > scream > run > sleep cycle > idle (fallback)
    // Higher priority means: if both conditions are true, this one wins.
    private void setupAnimationStates() {

        this.idleAnimationState.startIfStopped(this.tickCount);

        if (this.isInWater()) {
            stopAllExcept(this.swimAnimationState);
            this.swimAnimationState.startIfStopped(this.tickCount);
        }
        else if (this.screamTimer > 0) {
            stopAllExcept(this.screamAnimationState);
            this.screamAnimationState.startIfStopped(this.tickCount);
        }
        else if (this.entityData.get(IS_PANICKING)) {
            stopAllExcept(this.runAnimationState);
            this.runAnimationState.startIfStopped(this.tickCount);
        }
        else if (isInSleepCycle()) {
            // Each sleep phase has its own animation. Pick the one matching
            // the current phase.
            switch (getSleepPhase()) {
                case PHASE_SITTING_DOWN -> {
                    stopAllExcept(this.downAnimationState);
                    this.downAnimationState.startIfStopped(this.tickCount);
                }
                case PHASE_RESTING -> {
                    stopAllExcept(this.restAnimationState);
                    this.restAnimationState.startIfStopped(this.tickCount);
                }
                case PHASE_FALL_SLEEP -> {
                    stopAllExcept(this.fallSleepAnimationState);
                    this.fallSleepAnimationState.startIfStopped(this.tickCount);
                }
                case PHASE_SLEEPING -> {
                    stopAllExcept(this.sleepAnimationState);
                    this.sleepAnimationState.startIfStopped(this.tickCount);
                }
                case PHASE_WAKING_UP -> {
                    stopAllExcept(this.wakeUpAnimationState);
                    this.wakeUpAnimationState.startIfStopped(this.tickCount);
                }
                case PHASE_STANDING_UP -> {
                    stopAllExcept(this.upAnimationState);
                    this.upAnimationState.startIfStopped(this.tickCount);
                }
            }
        }
        else {
            // Fallback: nothing else is happening, so just idle.
            stopAllExcept(this.idleAnimationState);
        }
    }

    /**
     * Stops every animation except the one we want to keep playing.
     * Cleaner than writing 9 stop() calls inline every time we switch animations.
     */
    private void stopAllExcept(AnimationState keep) {
        if (keep != idleAnimationState)        idleAnimationState.stop();
        if (keep != swimAnimationState)        swimAnimationState.stop();
        if (keep != runAnimationState)         runAnimationState.stop();
        if (keep != screamAnimationState)      screamAnimationState.stop();
        if (keep != downAnimationState)        downAnimationState.stop();
        if (keep != restAnimationState)        restAnimationState.stop();
        if (keep != fallSleepAnimationState)   fallSleepAnimationState.stop();
        if (keep != sleepAnimationState)       sleepAnimationState.stop();
        if (keep != wakeUpAnimationState)      wakeUpAnimationState.stop();
        if (keep != upAnimationState)          upAnimationState.stop();
    }


    // ===========================================================
    // DAMAGE HANDLING
    // ===========================================================
    // Called when the dodo takes damage. We let vanilla do the actual
    // damage math, then add our own reactions: feather puff particles,
    // and instant-wake if it was sleeping (so the panic goal can take over).
    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        boolean wasHurt = super.hurtServer(level, source, damage);
        if (wasHurt) {
            spawnHurtParticles(level);

            // Snap to AWAKE so panic can immediately take control.
            // The scream animation still plays via the scream timer trigger.
            if (isInSleepCycle()) {
                setSleepPhase(PHASE_AWAKE);
            }
        }
        return wasHurt;
    }


    // ===========================================================
    // SAVE / LOAD (NBT PERSISTENCE)
    // ===========================================================
    // Synced data fields don't auto-save to disk — only position, health,
    // and a few core fields do. We have to explicitly save anything we
    // want to survive chunk unloads / world reloads.
    //
    // For sleep state we save the phase and the absolute tick when it
    // started (matching how the camel saves its pose timing). On load,
    // any time spent unloaded counts as "in the phase" — so a dodo that
    // was mid-sit-down comes back already done sitting. That's fine —
    // a tiny visual snap, but the dodo ends up in a valid steady state.

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("SleepPhase", getSleepPhase());
        output.putLong("SleepPhaseStartTick", this.entityData.get(SLEEP_PHASE_START_TICK));
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        // Direct entityData.set instead of setSleepPhase — calling our
        // helper would reset the start tick to "now," wiping the saved value.
        this.entityData.set(SLEEP_PHASE, input.getIntOr("SleepPhase", PHASE_AWAKE));
        this.entityData.set(SLEEP_PHASE_START_TICK, input.getLongOr("SleepPhaseStartTick", 0L));
    }



    // ===========================================================
    // PARTICLE EFFECTS
    // ===========================================================

    /**
     * Burst of feather particles when hurt. Spawned server-side so all
     * nearby players see the same burst.
     */
    private void spawnHurtParticles(ServerLevel level) {
        ParticleOptions particleType = ModParticles.DODO_PARTICLES.get();

        for (int i = 0; i < 8; i++) {
            // Small random offsets so the feathers don't all spawn at
            // the exact same point — looks more like a poof.
            double offsetX = (this.random.nextDouble() - 0.5) * 0.15;
            double offsetY = this.random.nextDouble() * this.getBbHeight();
            double offsetZ = (this.random.nextDouble() - 0.5) * 0.15;

            double velocityX = (this.random.nextDouble() - 0.5) * 0.01;
            double velocityY = 0.0;
            double velocityZ = (this.random.nextDouble() - 0.5) * 0.01;

            level.sendParticles(
                    particleType,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    1,
                    velocityX, velocityY, velocityZ,
                    0.002
            );
        }
    }

    /**
     * Single Z particle drifting up-and-to-the-right while sleeping.
     * Called every 10 ticks for a steady cartoon-style stream. Spawned
     * client-side because particles are pure visuals — no point sending
     * them over the network.
     */
    private void spawnSleepParticles() {
        // Fixed offsets and velocities so every Z follows the same path —
        // creates a clean diagonal stream instead of a chaotic puff.
        double offsetX = -0.4;
        double offsetY = this.getBbHeight() * 0.5;
        double offsetZ = 0.0;

        double velocityX = 0.02;
        double velocityY = 0.03;
        double velocityZ = 0.0;

        this.level().addParticle(
                ModParticles.SLEEP_PARTICLES.get(),
                this.getX() + offsetX,
                this.getY() + offsetY,
                this.getZ() + offsetZ,
                velocityX, velocityY, velocityZ
        );
    }


    // ===========================================================
    // SOUNDS (Chicken for now)
    // ===========================================================
    // Borrowing chicken sounds as placeholders. Replace with custom
    // dodo sounds once we have audio assets.
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        if (isInSleepCycle()) return null;
        return ModSounds.DODO_TRILLING.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.DODO_STARTLED.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.CHICKEN_SOUNDS.get(ChickenSoundVariants.SoundSet.CLASSIC).adultSounds().deathSound().value();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.CHICKEN_STEP.value(), 0.15F, 1.0F);
    }


    // ===========================================================
    // CUSTOM CONTROLS (suppressed during sleep)
    // ===========================================================
    // Vanilla has separate controllers for head rotation, body rotation,
    // and movement. We swap in custom versions of the head and body
    // controllers that do nothing while the dodo is sleeping. Movement
    // is already blocked at the travel() level above.
    //
    // Why static: these inner classes don't need access to outer-class
    // state, just the dodo instance we pass in. Static is slightly leaner.

    /** Head rotation controller. Stops the dodo's head from tracking targets while sleeping. */
    private static class DodoLookControl extends LookControl {
        private final DodoEntity dodo;

        DodoLookControl(DodoEntity dodo) {
            super(dodo);
            this.dodo = dodo;
        }

        @Override
        public void tick() {
            if (!dodo.isInSleepCycle()) {
                super.tick();
            }
        }
    }

    /** Body rotation controller. Stops the dodo's body from rotating to face movement direction while sleeping. */
    private static class DodoBodyControl extends BodyRotationControl {
        private final DodoEntity dodo;

        DodoBodyControl(DodoEntity dodo) {
            super(dodo);
            this.dodo = dodo;
        }

        @Override
        public void clientTick() {
            if (!dodo.isInSleepCycle()) {
                super.clientTick();
            }
        }
    }
}