package com.leclowndu93150.chisel.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Custom star particle for Holystone blocks.
 * Features sinusoidal scale animation, random rotation, and fade in/out effects.
 */
@OnlyIn(Dist.CLIENT)
public class HolystoneStarParticle extends TextureSheetParticle {

    private final float initialScale;
    private final float rotationOffset;

    protected HolystoneStarParticle(ClientLevel level, double x, double y, double z,
                                     double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.lifetime = 80 + this.random.nextInt(10); // 80-90 ticks
        this.initialScale = 0.15f + this.random.nextFloat() * 0.1f; // 0.15 to 0.25
        this.quadSize = this.initialScale;
        this.rotationOffset = this.random.nextFloat() * Mth.TWO_PI;

        // Set velocity
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        // Golden/white tint for holy effect
        this.rCol = 1.0f;
        this.gCol = 0.95f + this.random.nextFloat() * 0.05f;
        this.bCol = 0.8f + this.random.nextFloat() * 0.2f;

        // Full brightness
        this.alpha = 1.0f;

        // Particles pass through blocks
        this.hasPhysics = false;

        this.pickSprite(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        // Move particle
        this.move(this.xd, this.yd, this.zd);

        // Gentle slow down
        this.xd *= 0.98;
        this.yd *= 0.98;
        this.zd *= 0.98;

        // Sinusoidal scale animation
        float progress = (float) this.age / (float) this.lifetime;
        float sineWave = Mth.sin(progress * Mth.PI * 4 + this.rotationOffset);
        this.quadSize = this.initialScale * (0.5f + 0.5f * (1.0f + sineWave * 0.3f));

        // Fade in/out effect (20 ticks fade)
        int fadeTime = 20;
        if (this.age < fadeTime) {
            this.alpha = (float) this.age / fadeTime;
        } else if (this.age > this.lifetime - fadeTime) {
            this.alpha = (float) (this.lifetime - this.age) / fadeTime;
        } else {
            this.alpha = 1.0f;
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        // Full brightness - makes the particle glow
        return 0xF000F0;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                        double x, double y, double z,
                                        double xSpeed, double ySpeed, double zSpeed) {
            return new HolystoneStarParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
