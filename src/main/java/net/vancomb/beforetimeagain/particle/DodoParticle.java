package net.vancomb.beforetimeagain.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class DodoParticle extends SingleQuadParticle {
    public DodoParticle(ClientLevel level, double x, double y, double z,
                        double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet.first());

        this.friction = 0.9f;  // CHANGED: Increased from 0.8 to 0.9 - particles slow down faster
        this.lifetime = 20;    // CHANGED: Reduced from 60 to 20 ticks (1 second) - fade out quickly
        this.setSpriteFromAge(spriteSet);

        // CHANGED: Positive gravity so feathers fall down (was negative, making them float up)
        this.gravity = 0.05f + (level.getRandom().nextFloat() * 0.05f);  // Random gravity 0.05-0.1

        // ADDED: Make particles smaller (0.1-0.2 scale instead of default 1.0)
        this.quadSize = 0.02f + (level.getRandom().nextFloat() * 0.04f);  // Random size 0.1-0.2

        // ADDED: Start semi-transparent and fade out over lifetime
        this.alpha = 0.8f;  // Start at 60% opacity (translucent)
    }

    @Override
    public void tick() {
        super.tick();

        // ADDED: Fade out over time - alpha decreases as particle ages
        // When age reaches lifetime, alpha will be nearly 0
        this.alpha = 0.8f * (1.0f - ((float)this.age / (float)this.lifetime));

        // ADDED: Optionally make particles shrink as they fade
        // Uncomment the line below if you want feathers to also get smaller as they disappear
        // this.quadSize *= 0.95f;  // Shrink to 95% of previous size each tick
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;  // CHANGED: From OPAQUE to TRANSLUCENT so alpha/transparency works
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z,
                                                 double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new DodoParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}