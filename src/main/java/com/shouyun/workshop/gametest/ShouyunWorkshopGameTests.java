package com.shouyun.workshop.gametest;

import com.shouyun.workshop.enchantment.ModEnchantments;
import com.shouyun.workshop.entity.SwordQiEntity;
import com.shouyun.workshop.flight.WindFlightManager;
import com.shouyun.workshop.handler.HammerHandler;
import com.shouyun.workshop.handler.WhirlwindHandler;
import com.shouyun.workshop.item.ModItems;
import com.shouyun.workshop.network.WindFlightInputPayload;
import com.shouyun.workshop.util.ModConstants;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.world.GameMode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class ShouyunWorkshopGameTests implements FabricGameTest {
	@GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 100)
	public void glassHammerShattersAndDamagesAreaOnce(TestContext context) {
		ServerPlayerEntity player = createPlayer(context, new Vec3d(2.0, 2.0, 2.0));
		PigEntity primary = context.spawnMob(EntityType.PIG, new Vec3d(3.0, 2.0, 2.0));
		PigEntity nearby = context.spawnMob(EntityType.PIG, new Vec3d(4.0, 2.0, 2.0));
		PigEntity far = context.spawnMob(EntityType.PIG, new Vec3d(7.0, 2.0, 2.0));
		ItemStack stack = new ItemStack(ModItems.GLASS_HAMMER);
		player.equipStack(EquipmentSlot.MAINHAND, stack);
		context.setBlockState(new BlockPos(3, 1, 3), Blocks.STONE);

		primary.setAiDisabled(true);
		nearby.setAiDisabled(true);
		far.setAiDisabled(true);

		context.runAtTick(65, () -> {
			player.setPosition(context.getAbsolute(new Vec3d(2.0, 2.0, 2.0)));
			primary.setPosition(context.getAbsolute(new Vec3d(3.0, 2.0, 2.0)));
			nearby.setPosition(context.getAbsolute(new Vec3d(4.0, 2.0, 2.0)));
			far.setPosition(context.getAbsolute(new Vec3d(7.0, 2.0, 2.0)));
			primary.extinguish();
			nearby.extinguish();
			far.extinguish();
			primary.setHealth(primary.getMaxHealth());
			nearby.setHealth(nearby.getMaxHealth());
			far.setHealth(far.getMaxHealth());
			HammerHandler.shatterGlassHammer(player, primary, stack);

			context.assertTrue(stack.isEmpty(), "Glass hammer must be consumed after a hit");
			context.assertEquals(primary.getHealth(), primary.getMaxHealth(),
					"Primary target must not receive duplicate shard damage");
			context.assertTrue(Math.abs(nearby.getHealth() - (nearby.getMaxHealth() - 4.0F)) < 0.001F,
					"Nearby target must receive exactly one shard hit");
			context.assertEquals(far.getHealth(), far.getMaxHealth(), "Out-of-range target must be unaffected");
			context.assertTrue(Math.abs(player.getHealth() - 18.0F) < 0.001F,
					"The wielder must receive glass shard self-damage");
			context.expectBlock(Blocks.STONE, new BlockPos(3, 1, 3));
			context.complete();
		});
	}

	@GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 80)
	public void netheriteHammerShockwaveExcludesOwnerAndPushesOutward(TestContext context) {
		ServerPlayerEntity player = createPlayer(context, new Vec3d(2.0, 2.0, 2.0));
		ZombieEntity primary = context.spawnMob(EntityType.ZOMBIE, new Vec3d(3.0, 2.0, 2.0));
		ZombieEntity east = context.spawnMob(EntityType.ZOMBIE, new Vec3d(5.0, 2.0, 2.0));
		Vec3d playerVelocity = player.getVelocity();

		HammerHandler.createNetheriteShockwave(player, primary);

		context.assertTrue(east.getHealth() < 15.2F && east.getHealth() > 14.8F,
				"Shockwave target must receive one armor-adjusted damage instance");
		context.assertTrue(east.getVelocity().x > 0.0, "Entity east of the center must be pushed east");
		context.assertTrue(east.getVelocity().y > 0.0, "Shockwave must launch targets upward");
		context.assertEquals(player.getVelocity(), playerVelocity, "Shockwave must not move its owner");
		context.assertEquals(primary.getHealth(), 20.0F, "Primary target must not receive duplicate shockwave damage");
		context.complete();
	}

	@GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 80)
	public void whirlwindLevelOneUsesThreeBlockRadius(TestContext context) {
		ServerPlayerEntity player = createPlayer(context, new Vec3d(2.0, 2.0, 2.0));
		ZombieEntity inside = context.spawnMob(EntityType.ZOMBIE, new Vec3d(4.5, 2.0, 2.0));
		ZombieEntity outside = context.spawnMob(EntityType.ZOMBIE, new Vec3d(5.5, 2.0, 2.0));

		WhirlwindHandler.activate(player, 1);

		context.assertTrue(inside.getHealth() < 16.2F && inside.getHealth() > 15.8F,
				"Whirlwind I must damage targets inside three blocks exactly once");
		context.assertEquals(outside.getHealth(), 20.0F, "Whirlwind I must not damage targets outside three blocks");
		context.assertEquals(player.getHealth(), 20.0F, "Whirlwind must never damage its user");
		context.complete();
	}

	@GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 80)
	public void swordQiFliesHitsOnceAndDisappears(TestContext context) {
		ServerPlayerEntity player = createPlayer(context, new Vec3d(2.0, 2.0, 2.0));
		player.setYaw(-90.0F);
		ZombieEntity target = context.spawnMob(EntityType.ZOMBIE, new Vec3d(7.0, 3.6, 2.0));
		SwordQiEntity swordQi = new SwordQiEntity(context.getWorld(), player, ModConstants.SWORD_QI_DAMAGE[2]);
		context.getWorld().spawnEntity(swordQi);

		context.runAtTick(12, () -> {
			context.assertTrue(target.getHealth() < 20.0F, "Sword qi must damage a target during flight");
			context.assertTrue(swordQi.isRemoved(), "Sword qi must disappear after its first hit");
			context.complete();
		});
	}

	@GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 220)
	public void windFlightTogglesHoversConsumesHungerAndCleansUp(TestContext context) {
		ServerPlayerEntity player = createPlayer(context, new Vec3d(2.0, 2.0, 2.0));
		context.setBlockState(new BlockPos(2, 1, 2), Blocks.STONE);
		player.setPosition(context.getAbsolute(new Vec3d(2.0, 2.0, 2.0)));
		player.setOnGround(true);
		player.setVelocity(Vec3d.ZERO);
		player.setYaw(-90.0F);
		ItemStack sword = new ItemStack(net.minecraft.item.Items.DIAMOND_SWORD);
		RegistryEntry.Reference<Enchantment> whirlwind = context.getWorld().getRegistryManager()
				.get(RegistryKeys.ENCHANTMENT).getEntry(ModEnchantments.WHIRLWIND_SLASH).orElseThrow();
		sword.addEnchantment(whirlwind, 1);
		player.equipStack(EquipmentSlot.MAINHAND, sword);
		player.getHungerManager().setFoodLevel(20);

		context.runAtTick(45, () -> {
			context.assertFalse(WindFlightManager.isFlying(player), "Wind flight must not activate without the toggle key");
			WindFlightManager.toggle(player);
			context.assertTrue(WindFlightManager.isFlying(player), "A whirlwind-enchanted sword must allow toggled flight");
			WindFlightManager.updateInput(player,
					(byte) (WindFlightInputPayload.FORWARD | WindFlightInputPayload.ASCEND));
		});
		context.runAtTick(55, () -> {
			context.assertTrue(player.getVelocity().x > 0.0,
					"Forward input at yaw -90 must move the player east");
			context.assertTrue(player.getVelocity().y > 0.0, "Jump input must make wind flight ascend");
			WindFlightManager.updateInput(player,
					(byte) (WindFlightInputPayload.FORWARD | WindFlightInputPayload.DESCEND));
		});
		context.runAtTick(60, () -> {
			context.assertTrue(player.getVelocity().y < 0.0, "Sneak input must make wind flight descend");
			WindFlightManager.updateInput(player, (byte) WindFlightInputPayload.FORWARD);
		});
		context.runAtTick(65, () -> {
			context.assertTrue(Math.abs(player.getVelocity().y) < 0.001,
					"Releasing vertical controls must make wind flight hover");
			WindFlightManager.updateInput(player, (byte) WindFlightInputPayload.RIGHT);
		});
		context.runAtTick(70, () -> {
			context.assertTrue(player.getVelocity().z > 0.0,
					"Right input at yaw -90 must move the player south");
			WindFlightManager.updateInput(player, (byte) WindFlightInputPayload.LEFT);
		});
		context.runAtTick(75, () -> {
			context.assertTrue(player.getVelocity().z < 0.0,
					"Left input at yaw -90 must move the player north");
			WindFlightManager.updateInput(player, (byte) WindFlightInputPayload.FORWARD);
		});
		context.runAtTick(190, () -> {
			context.assertEquals(player.getHungerManager().getFoodLevel(), 19,
					"Seven seconds of wind flight must consume one hunger point");
			player.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
		});
		context.runAtTick(195, () -> {
			context.assertFalse(WindFlightManager.isFlying(player), "Switching items must end wind flight");
			context.assertFalse(player.hasNoGravity(), "Ending wind flight must restore gravity");
			context.complete();
		});
	}

	@SuppressWarnings("removal")
	private static ServerPlayerEntity createPlayer(TestContext context, Vec3d relativePos) {
		ServerPlayerEntity player = context.createMockCreativeServerPlayerInWorld();
		player.changeGameMode(GameMode.SURVIVAL);
		player.getAbilities().invulnerable = false;
		player.timeUntilRegen = 0;
		player.setPosition(context.getAbsolute(relativePos));
		player.setOnGround(true);
		player.setVelocity(Vec3d.ZERO);
		return player;
	}
}
