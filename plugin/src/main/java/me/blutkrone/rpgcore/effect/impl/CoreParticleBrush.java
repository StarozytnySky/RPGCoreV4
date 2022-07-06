package me.blutkrone.rpgcore.effect.impl;

import me.blutkrone.rpgcore.effect.CoreEffect;
import me.blutkrone.rpgcore.hud.editor.bundle.EditorParticleBrush;
import me.blutkrone.rpgcore.util.ItemBuilder;
import me.blutkrone.rpgcore.util.collection.WeightedRandomMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;

/**
 * Adds this particle to the brush, which other particle effects will
 * use to draw their shape.
 */
public class CoreParticleBrush implements CoreEffect.IEffectPart {

    // chance to pick this particle brush
    private double weighting;
    // which particle should be emitted
    private Particle particle;
    // the speed of the emitted particle
    private double speed;
    // the amount of particles emitted
    private int amount;
    // material used in case its an itemized particle
    private Material material;
    private int model;
    // block data, in case it is available
    private BlockData block_data;
    // color used in case its an colored particle
    private int color;

    public CoreParticleBrush(EditorParticleBrush editor) {
        this.weighting = editor.weighting;
        this.particle = editor.particle;
        this.speed = editor.speed;
        this.amount = (int) editor.amount;
        this.material = editor.material;
        this.model = (int) editor.model;
        this.color = ChatColor.of("#" + editor.color).getColor().getRGB();
        try {
            this.block_data = this.material.createBlockData();
        } catch (Exception e) {
            this.block_data = Material.STONE.createBlockData();
        }
    }

    /**
     * Show this particle at the given location.
     *
     * @param where location to emit particle at.
     * @param viewers who to present the particle to
     */
    public void show(Location where, Collection<Player> viewers) {
        for (Player viewer : viewers) {
            if (particle == Particle.REDSTONE) {
                // Property: Color
                Color color = Color.fromRGB(this.color);
                viewer.spawnParticle(Particle.REDSTONE, where, amount, new Particle.DustOptions(color, 1));
            } else if (particle == Particle.SPELL_MOB) {
                // Property: Color
                Color color = Color.fromRGB(this.color);
                viewer.spawnParticle(Particle.SPELL_MOB, where, 0, (double) color.getRed() / 255.0D, (double) color.getGreen() / 255.0D, (double) color.getBlue() / 255.0D, 1.0D);
            } else if (particle == Particle.SPELL_MOB_AMBIENT) {
                // Property: Color
                Color color = Color.fromRGB(this.color);
                viewer.spawnParticle(Particle.SPELL_MOB_AMBIENT, where, 0, (double) color.getRed() / 255.0D, (double) color.getGreen() / 255.0D, (double) color.getBlue() / 255.0D, 1.0D);
            } else if (particle == Particle.NOTE) {
                // Property: Color
                Color color = Color.fromRGB(this.color);
                viewer.spawnParticle(Particle.NOTE, where, 0, (double) color.getBlue() / 24.0D, 0.0D, 0.0D, 1.0D);
            } else if (particle == Particle.ITEM_CRACK) {
                // Property: Direction, Material
                if (amount <= 0) {
                    double dX = where.getDirection().getX();
                    double dY = where.getDirection().getY();
                    double dZ = where.getDirection().getZ();
                    viewer.spawnParticle(Particle.ITEM_CRACK, where, 0, dX, dY, dZ, speed, ItemBuilder.of(material).model(model).build());
                } else {
                    viewer.spawnParticle(Particle.ITEM_CRACK, where, amount, ItemBuilder.of(material).model(model).build());
                }
            } else if (particle == Particle.BLOCK_CRACK) {
                // Property: Material
                viewer.spawnParticle(Particle.BLOCK_CRACK, where, amount, block_data);
            } else if (particle == Particle.BLOCK_DUST) {
                // Property: Direction, Material
                if (amount <= 0) {
                    double dX = where.getDirection().getX();
                    double dY = where.getDirection().getY();
                    double dZ = where.getDirection().getZ();
                    viewer.spawnParticle(Particle.BLOCK_DUST, where, 0, dX, dY, dZ, speed, block_data);
                } else {
                    viewer.spawnParticle(Particle.BLOCK_DUST, where, amount, block_data);
                }
            } else if (particle == Particle.FALLING_DUST) {
                // Property: Material
                viewer.spawnParticle(Particle.FALLING_DUST, where, amount, block_data);
            } else if (amount == 0) {
                // Property: Direction
                double dX = where.getDirection().getX();
                double dY = where.getDirection().getY();
                double dZ = where.getDirection().getZ();
                viewer.spawnParticle(particle, where, 0, dX, dY, dZ, (double) speed);
            } else {
                viewer.spawnParticle(particle, where, amount, 0.0D, 0.0D, 0.0D, (double) speed);
            }
        }
    }

    @Override
    public void process(Location where, Vector offset, WeightedRandomMap<CoreParticleBrush> brush, double scale, List<Player> viewing) {
        brush.add(this.weighting, this);
    }
}
