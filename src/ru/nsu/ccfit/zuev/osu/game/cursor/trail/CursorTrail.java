package ru.nsu.ccfit.zuev.osu.game.cursor.trail;

import org.anddev.andengine.entity.particle.ParticleSystem;
import org.anddev.andengine.entity.particle.emitter.PointParticleEmitter;
import org.anddev.andengine.entity.particle.initializer.ScaleInitializer;
import org.anddev.andengine.entity.particle.modifier.AlphaModifier;
import org.anddev.andengine.entity.particle.modifier.ExpireModifier;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import javax.microedition.khronos.opengles.GL10;

import ru.nsu.ccfit.zuev.osu.game.cursor.main.CursorSprite;

public class CursorTrail extends ParticleSystem {

    private PointParticleEmitter emitter;
    private int baseSpawnRate;

    public CursorTrail(
            PointParticleEmitter emitter,
            int baseSpawnRate,
            float trailSize,
            TextureRegion pTextureRegion
    ) {
        // Increase the spawn rate for more particles
        super(emitter, baseSpawnRate * 2, baseSpawnRate * 2, baseSpawnRate * 2, pTextureRegion);

        this.emitter = emitter;
        this.baseSpawnRate = baseSpawnRate;

        setupTrail(trailSize);
        setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        setParticlesSpawnEnabled(false);
    }

    private void setupTrail(float trailSize) {
        // Adjust particle lifespan for a longer trail
        addParticleModifier(new ExpireModifier(0.5f));
        // Smooth fade-out over particle's life
        addParticleModifier(new AlphaModifier(1.0f, 0.0f, 0f, 0.5f));
        // Consistent particle size
        addParticleInitializer(new ScaleInitializer(trailSize));
    }

    public void updateTrail(float cursorSpeed) {
        // Calculate dynamic spawn rate based on cursor speed
        int dynamicSpawnRate = Math.min(baseSpawnRate + (int) (cursorSpeed * 10), baseSpawnRate * 4);

        // Adjust particle system properties dynamically
        setParticlesSpawnEnabled(true);
        setMinRate(dynamicSpawnRate);
        setMaxRate(dynamicSpawnRate * 2);
        setMaxParticles(dynamicSpawnRate * 4);
    }
}