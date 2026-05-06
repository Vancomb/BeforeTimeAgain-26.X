package net.vancomb.beforetimeagain.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.chicken.ChickenSoundVariants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/** The Dodo entity — a passive creature that idles, wanders, swims in water, and panics (screams + runs) when hit. Extends PathfinderMob to inherit standard ground-pathing AI behavior.*/
public class DodoEntity extends PathfinderMob {

    // ===========================================================
    // SYNCED ENTITY DATA
    // ===========================================================
    // SynchedEntityData is the standard way to share state between
    // server and client. The server sets the value, vanilla auto-syncs
    // it across the network, and the client reads it for rendering.
    //
    // We use this for IS_PANICKING because AI goals only run server-side,
    // but animation decisions happen client-side. The server detects
    // panic via super.isPanicking(), syncs it through this field, and
    // the client reads it to play the run animation.
    private static final EntityDataAccessor<Boolean> IS_PANICKING =
            SynchedEntityData.defineId(DodoEntity.class, EntityDataSerializers.BOOLEAN);


    // ===========================================================
    // ANIMATION STATES
    // ===========================================================
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState swimAnimationState = new AnimationState();
    public final AnimationState runAnimationState = new AnimationState();
    public final AnimationState screamAnimationState = new AnimationState();


    // ===========================================================
    // SCREAM TIMING
    // ===========================================================
    private int screamTimer = 0;
    private int lastHurtTime = 0;


    public DodoEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }


    // ===========================================================
    // SYNCED DATA REGISTRATION
    // ===========================================================
    // Required override — tells vanilla what synced fields this entity
    // has and their default values.
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_PANICKING, false);
    }


    // ===========================================================
    // AI GOALS
    // ===========================================================
    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 2d));
        goalSelector.addGoal(2, new TemptGoal(this, 1.25d, stack -> stack.is(ItemTags.FISHES), false));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1f));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 5f, 0.2f));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));super.registerGoals();

    }


    // ===========================================================
    // ATTRIBUTES (stats)
    // ===========================================================
    public static AttributeSupplier.Builder createDodoAttributes() {
        return PathfinderMob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10d)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.TEMPT_RANGE, 16d)
                .add(Attributes.FOLLOW_RANGE, 16d);
    }


    // ===========================================================
    // TICK
    // ===========================================================
    @Override
    public void tick() {
        super.tick();

        // Sync panic state from server to client.
        // super.isPanicking() reads the AI goal state, which is only
        // accurate on the server. We push it to the synced field so
        // the client can read it via this.entityData.get(IS_PANICKING).
        if (!this.level().isClientSide()) {
            this.entityData.set(IS_PANICKING, super.isPanicking());
        }

        // Detect when hurtTime just got set (jumped up from previous tick)
        if (this.hurtTime > this.lastHurtTime) {
            this.screamTimer = 20;
        }
        this.lastHurtTime = this.hurtTime;

        if (this.screamTimer > 0) {
            this.screamTimer--;
        }

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }
    }


    // ===========================================================
    // ANIMATION STATE MACHINE
    // ===========================================================
    // Priority order: water > scream > run > idle
    private void setupAnimationStates() {

        // DEFAULT: keep idle running
        this.idleAnimationState.startIfStopped(this.tickCount);


        // PRIORITY 1: Swimming
        if (this.isInWater()) {
            this.idleAnimationState.stop();
            this.runAnimationState.stop();
            this.screamAnimationState.stop();
            this.swimAnimationState.startIfStopped(this.tickCount);
        }

        // PRIORITY 2: Screaming (recently hit)
        else if (this.screamTimer > 0) {
            this.idleAnimationState.stop();
            this.runAnimationState.stop();
            this.swimAnimationState.stop();
            this.screamAnimationState.startIfStopped(this.tickCount);
        }

        // PRIORITY 3: Running (panic goal active, synced from server)
        else if (this.entityData.get(IS_PANICKING)) {
            this.idleAnimationState.stop();
            this.swimAnimationState.stop();
            this.screamAnimationState.stop();
            this.runAnimationState.startIfStopped(this.tickCount);
        }

        // FALLBACK: nothing special — idle wins
        else {
            this.swimAnimationState.stop();
            this.runAnimationState.stop();
            this.screamAnimationState.stop();
        }
    }

    //Sounds


    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.CHICKEN_SOUNDS.get(ChickenSoundVariants.SoundSet.CLASSIC).adultSounds().ambientSound().value();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.CHICKEN_SOUNDS.get(ChickenSoundVariants.SoundSet.CLASSIC).adultSounds().hurtSound().value();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.CHICKEN_SOUNDS.get(ChickenSoundVariants.SoundSet.CLASSIC).adultSounds().deathSound().value();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.CHICKEN_STEP.value(), 0.15F, 1.0F);
    }

}

