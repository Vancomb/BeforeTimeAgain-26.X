package net.vancomb.beforetimeagain.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class SleepParticle extends SingleQuadParticle {
    private final float startSize;  // Store initial size

    public SleepParticle(ClientLevel level, double x, double y, double z,
                         double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        // CHANGED: Use the exact velocities passed in, not random ones
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet.first());

        this.friction = 0.98f;  // Very gentle slowdown
        this.lifetime = 50;     // 2.5 seconds
        this.setSpriteFromAge(spriteSet);

        // Gentle upward float
        this.gravity = -0.01f;  // Negative = float upward

        // Start SMALL - will grow over time
        this.startSize = 0.05f;
        this.quadSize = this.startSize;

        // Start fairly visible
        this.alpha = 0.9f;

        // ADDED: Set exact velocities from parameters (no modification)
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
    }

    @Override
    public void tick() {
        super.tick();

        // Calculate age progress (0.0 = just spawned, 1.0 = about to die)
        float ageProgress = (float)this.age / (float)this.lifetime;

        // Fade out over lifetime
        this.alpha = 0.9f * (1.0f - ageProgress);

        // GROW over time - start small (0.05), end large (0.25)
        // Z's get bigger as they rise and fade
        this.quadSize = this.startSize + (ageProgress * 0.2f);  // 0.05 -> 0.25
    }

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z,
                                                 double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new SleepParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}