package com.shouyun.workshop.gametest;

import com.shouyun.workshop.entity.ModEntities;
import com.shouyun.workshop.entity.SummonedZombieEntity;
import com.shouyun.workshop.item.ModItems;
import com.shouyun.workshop.item.NecromancerTier;
import com.shouyun.workshop.summon.NecromancerSummons;
import com.shouyun.workshop.util.CombatTargeting;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.ServerWorldProperties;

public final class NecromancerGameTests implements FabricGameTest {
	@GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 100)
	public void followsMovingOwnerAndObeysAttackOrder(TestContext context) {
		var player = player(context);
		for (BlockPos pos : BlockPos.iterate(new BlockPos(0, 1, 0), new BlockPos(14, 1, 7))) context.setBlockState(pos, Blocks.STONE);
		equip(player, NecromancerTier.WOODEN);
		context.assertTrue(NecromancerSummons.summon(player, Hand.MAIN_HAND, NecromancerTier.WOODEN), "Test summon must spawn");
		var summons = context.getWorld().getEntitiesByClass(SummonedZombieEntity.class,
				player.getBoundingBox().expand(8), candidate -> player.getUuid().equals(candidate.ownerUuid()));
		context.assertEquals(summons.size(), 1, "Exactly one owned summon must be tracked");
		var zombie = summons.getFirst();
		player.setPosition(context.getAbsolute(new Vec3d(12.5, 2, 2.5)));
		double initialDistance = zombie.squaredDistanceTo(player);
		context.runAtTick(65, () -> {
			context.assertTrue(zombie.squaredDistanceTo(player) < initialDistance / 2,
					"Idle summon must close most of the distance to a moving owner");
			var pig = context.spawnMob(EntityType.PIG, new Vec3d(8, 2, 5));
			context.assertEquals(NecromancerSummons.orderAttack(player, pig), 1,
					"Owner attack must order every owned summon");
			context.assertEquals(zombie.getTarget(), pig, "Attack order has priority over autonomous targeting");
			context.assertTrue(zombie.canFight(pig), "A direct order may target a passive mob");
			context.complete();
		});
	}
	@GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 180)
	public void sharedCooldownCapAndDurability(TestContext context) {
		var player = player(context);
		ItemStack wood = equip(player, NecromancerTier.WOODEN);
		context.assertTrue(NecromancerSummons.summon(player, Hand.MAIN_HAND, NecromancerTier.WOODEN), "First summon must succeed");
		context.assertEquals(wood.getDamage(), 1, "Successful summon costs exactly one durability");
		ItemStack iron = equip(player, NecromancerTier.IRON);
		context.assertFalse(NecromancerSummons.summon(player, Hand.MAIN_HAND, NecromancerTier.IRON), "Changing material cannot bypass cooldown");
		context.assertEquals(iron.getDamage(), 0, "Cooldown failure is free");
		context.runAtTick(165, () -> {
			player.equipStack(EquipmentSlot.MAINHAND, wood);
			context.assertFalse(NecromancerSummons.summon(player, Hand.MAIN_HAND, NecromancerTier.WOODEN), "Wood cap must count the existing summon");
			context.assertEquals(wood.getDamage(), 1, "Cap failure is free");
			player.equipStack(EquipmentSlot.MAINHAND, iron);
			context.assertTrue(NecromancerSummons.summon(player, Hand.MAIN_HAND, NecromancerTier.IRON), "Cap failure must not apply cooldown; iron can extend the shared group");
			context.assertEquals(NecromancerSummons.count(player), 2, "Mixed tiers share one count");
			context.assertEquals(iron.getDamage(), 1, "Iron summon costs one durability");
			context.complete();
		});
	}

	@GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 10)
	public void blockedPlacementDoesNotConsumeDurabilityOrCooldown(TestContext context) {
		var player = player(context);
		var stack = equip(player, NecromancerTier.DIAMOND);
		// Move above all supporting terrain, outside the vertical search range.
		player.setPosition(player.getPos().add(0, 12, 0));
		context.assertFalse(NecromancerSummons.summon(player, Hand.MAIN_HAND, NecromancerTier.DIAMOND), "Void must reject spawning");
		context.assertEquals(stack.getDamage(), 0, "No safe position costs no durability");
		player.setPosition(context.getAbsolute(new Vec3d(2.5, 2, 2.5)));
		floor(context);
		context.assertTrue(NecromancerSummons.summon(player, Hand.MAIN_HAND, NecromancerTier.DIAMOND), "Failed placement must not start cooldown");
		context.complete();
	}

	@GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 30)
	public void friendlyTargetsEquipmentAndNoDrops(TestContext context) {
		var player = player(context);
		var zombie = context.spawnEntity(ModEntities.SUMMONED_ZOMBIE, new Vec3d(4, 2, 4));
		zombie.bind(player, NecromancerTier.NETHERITE);
		var other = context.spawnEntity(ModEntities.SUMMONED_ZOMBIE, new Vec3d(5, 2, 5));
		other.bind(player, NecromancerTier.WOODEN);
		var hostile = context.spawnMob(EntityType.ZOMBIE, new Vec3d(6, 2, 4));
		var pig = context.spawnMob(EntityType.PIG, new Vec3d(6, 2, 6));
		context.assertTrue(zombie.canFight(hostile), "Hostile zombie is a valid target");
		var slime = context.spawnMob(EntityType.SLIME, new Vec3d(6, 2, 5));
		context.assertTrue(zombie.canFight(slime), "Hostile mobs outside the HostileEntity class hierarchy are also valid targets");
		slime.discard();
		context.assertFalse(zombie.canFight(player), "Never target players");
		context.assertFalse(zombie.canFight(other), "Never target summons");
		context.assertFalse(zombie.canFight(pig), "Never target passive animals");
		context.assertFalse(zombie.tryAttack(player), "Attack entry point also protects owner");
		context.assertFalse(CombatTargeting.canAffect(player, zombie), "Owner area attacks protect summons");
		context.assertEquals(zombie.getMaxHealth(), 36F, "Elite health is bounded");
		context.assertTrue(zombie.getMainHandStack().isOf(net.minecraft.item.Items.NETHERITE_SWORD), "Elite carries a netherite sword");
		for (var slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET})
			context.assertFalse(zombie.getEquippedStack(slot).isEmpty(), "Elite has every armor slot");
		zombie.damage(context.getWorld().getDamageSources().playerAttack(player), 1000);
		context.runAtTick(25, () -> {
			context.assertTrue(context.getWorld().getEntitiesByClass(ItemEntity.class, zombie.getBoundingBox().expand(2), e -> true).isEmpty(), "Summon leaves no equipment or loot");
			context.assertTrue(context.getWorld().getEntitiesByClass(ExperienceOrbEntity.class, zombie.getBoundingBox().expand(2), e -> true).isEmpty(), "Summon leaves no XP");
			context.complete();
		});
	}

	@GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 10)
	public void expirationDiscardsWithoutWaitingAFullMinute(TestContext context) {
		var player = player(context);
		var zombie = context.spawnEntity(ModEntities.SUMMONED_ZOMBIE, new Vec3d(4, 2, 4));
		zombie.bind(player, NecromancerTier.WOODEN);
		context.assertEquals(zombie.remainingTicks(), 1200, "Wood lifetime is sixty seconds");
		// Advance and restore the world clock synchronously: no other entity gets a tick at the altered time.
		var properties = (ServerWorldProperties) context.getWorld().getLevelProperties();
		long now = properties.getTime();
		try {
			properties.setTime(now + 1200);
			zombie.tick();
			context.assertTrue(zombie.isRemoved(), "An expired summon must be discarded");
		} finally { properties.setTime(now); }
		context.complete();
	}

	private static ItemStack equip(ServerPlayerEntity player, NecromancerTier tier) {
		var stack = new ItemStack(ModItems.NECROMANCER_STAVES.get(tier));
		player.equipStack(EquipmentSlot.MAINHAND, stack);
		return stack;
	}
	private static void floor(TestContext context) {
		for (BlockPos pos : BlockPos.iterate(new BlockPos(0, 1, 0), new BlockPos(7, 1, 7))) context.setBlockState(pos, Blocks.STONE);
	}
	@SuppressWarnings("removal")
	private static ServerPlayerEntity player(TestContext context) {
		floor(context);
		var player = context.createMockCreativeServerPlayerInWorld();
		player.changeGameMode(GameMode.SURVIVAL);
		player.setPosition(context.getAbsolute(new Vec3d(2.5, 2, 2.5)));
		player.setYaw(-90);
		return player;
	}
}
