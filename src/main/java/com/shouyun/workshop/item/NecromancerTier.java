package com.shouyun.workshop.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public enum NecromancerTier {
	WOODEN(32, 8, 1, 60, 20, 0, .10F, Items.WOODEN_SWORD),
	STONE(48, 7, 1, 60, 20, .15F, .25F, Items.STONE_SWORD),
	IRON(96, 6, 2, 90, 24, .45F, .60F, Items.IRON_SWORD),
	GOLDEN(48, 5, 2, 90, 22, .55F, .70F, Items.GOLDEN_SWORD),
	DIAMOND(192, 4, 3, 120, 30, .80F, 1, Items.DIAMOND_SWORD),
	NETHERITE(256, 4, 4, 120, 36, 1, 1, Items.NETHERITE_SWORD);

	public final int durability, cooldownSeconds, limit, lifetimeSeconds, health;
	public final float armorChance, swordChance;
	private final Item sword;

	NecromancerTier(int durability, int cooldown, int limit, int lifetime, int health,
			float armorChance, float swordChance, Item sword) {
		this.durability = durability;
		this.cooldownSeconds = cooldown;
		this.limit = limit;
		this.lifetimeSeconds = lifetime;
		this.health = health;
		this.armorChance = armorChance;
		this.swordChance = swordChance;
		this.sword = sword;
	}

	public String id() { return name().toLowerCase(java.util.Locale.ROOT) + "_necromancer_staff"; }

	public void equip(ZombieEntity zombie) {
		var random = zombie.getRandom();
		for (EquipmentSlot slot : EquipmentSlot.values()) zombie.equipStack(slot, ItemStack.EMPTY);
		zombie.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(health);
		zombie.setHealth(health);
		zombie.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(this == GOLDEN ? .253 : .23);
		zombie.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(this == NETHERITE ? .2 : 0);
		zombie.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0);
		if (random.nextFloat() < swordChance) zombie.equipStack(EquipmentSlot.MAINHAND, new ItemStack(sword));
		EquipmentSlot[] slots = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
		for (int i = 0; i < slots.length; i++) {
			if (random.nextFloat() < armorChance) {
				NecromancerTier armorTier = this == NETHERITE && random.nextFloat() >= .75F ? DIAMOND : this;
				zombie.equipStack(slots[i], new ItemStack(armorTier.armor()[i]));
			}
		}
		for (EquipmentSlot slot : EquipmentSlot.values()) zombie.setEquipmentDropChance(slot, 0);
	}

	private Item[] armor() {
		return switch (this) {
			case WOODEN, STONE -> new Item[]{Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET};
			case IRON -> new Item[]{Items.IRON_BOOTS, Items.IRON_LEGGINGS, Items.IRON_CHESTPLATE, Items.IRON_HELMET};
			case GOLDEN -> new Item[]{Items.GOLDEN_BOOTS, Items.GOLDEN_LEGGINGS, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_HELMET};
			case DIAMOND -> new Item[]{Items.DIAMOND_BOOTS, Items.DIAMOND_LEGGINGS, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_HELMET};
			case NETHERITE -> new Item[]{Items.NETHERITE_BOOTS, Items.NETHERITE_LEGGINGS, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_HELMET};
		};
	}
}
