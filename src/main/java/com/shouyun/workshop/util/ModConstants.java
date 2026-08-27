package com.shouyun.workshop.util;

public final class ModConstants {
	public static final double GLASS_HAMMER_TOTAL_DAMAGE = 10.0;
	public static final double NETHERITE_HAMMER_TOTAL_DAMAGE = 12.0;
	public static final double HAMMER_ATTACK_SPEED = 0.8;
	public static final int NETHERITE_HAMMER_DURABILITY = 2031;

	public static final double GLASS_SHARD_RADIUS = 3.0;
	public static final float GLASS_SHARD_DAMAGE = 4.0F;
	public static final float GLASS_SHARD_SELF_DAMAGE = 2.0F;
	public static final int GLASS_SHARD_PARTICLES = 42;

	public static final double NETHERITE_SHOCKWAVE_RADIUS = 5.5;
	public static final float NETHERITE_SHOCKWAVE_DAMAGE = 5.0F;
	public static final double NETHERITE_HORIZONTAL_KNOCKBACK = 1.5;
	public static final double NETHERITE_VERTICAL_KNOCKBACK = 0.65;
	public static final double NETHERITE_MIN_KNOCKBACK_MULTIPLIER = 0.35;
	public static final int NETHERITE_SHOCKWAVE_PARTICLES = 36;
	public static final double NETHERITE_BLAST_RADIUS = 2.25;
	public static final float NETHERITE_BLAST_BONUS_DAMAGE = 3.0F;
	public static final int NETHERITE_BLAST_PARTICLES = 3;
	public static final double NETHERITE_BLOCK_BLAST_RADIUS = 1.75;
	public static final int NETHERITE_HAMMER_NUMB_DURATION_TICKS = 40;
	public static final int NETHERITE_HAMMER_COOLDOWN_TICKS = 35;
	public static final int NETHERITE_HAMMER_WEAKNESS_AMPLIFIER = 0;
	public static final int NETHERITE_HAMMER_MINING_FATIGUE_AMPLIFIER = 0;

	public static final double[] WHIRLWIND_RADII = {0.0, 3.0, 4.0, 4.0};
	public static final float[] WHIRLWIND_DAMAGE = {0.0F, 4.0F, 5.0F, 5.0F};
	public static final int WHIRLWIND_COOLDOWN_TICKS = 30;
	public static final int[] WHIRLWIND_RING_PARTICLES = {0, 20, 28, 28};
	public static final float[] SWORD_QI_DAMAGE = {0.0F, 0.0F, 5.0F, 5.0F};
	public static final double SWORD_QI_SPEED = 1.25;
	public static final int SWORD_QI_LIFETIME_TICKS = 20;
	public static final double SWORD_QI_MAX_DISTANCE = 25.0;
	public static final int SWORD_QI_PARTICLES_PER_TICK = 2;

	public static final double WIND_HORIZONTAL_SPEED = 0.35;
	public static final double WIND_TAKEOFF_VERTICAL_SPEED = 0.42;
	public static final double WIND_VELOCITY_SMOOTHING = 0.25;
	public static final int WIND_TAKEOFF_CHARGE_TICKS = 40;
	public static final int WIND_HUNGER_INTERVAL_TICKS = 140;
	public static final int WIND_HUNGER_COST = 1;
	public static final int WIND_PARTICLE_INTERVAL_TICKS = 5;

	private ModConstants() {
	}
}
