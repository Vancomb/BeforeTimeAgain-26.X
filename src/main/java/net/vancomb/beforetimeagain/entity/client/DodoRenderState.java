package net.vancomb.beforetimeagain.entity.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class DodoRenderState extends LivingEntityRenderState {
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

}
