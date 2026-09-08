package com.shouyun.workshop.summon;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class SummonPlacement {
	public static Vec3d find(ServerWorld world, Entity owner, Entity summon) {
		Vec3d forward = owner.getRotationVec(1).multiply(1, 0, 1).normalize().multiply(2);
		BlockPos center = BlockPos.ofFloored(owner.getPos().add(forward));
		for (int radius = 0; radius <= 3; radius++) {
			for (int x = -radius; x <= radius; x++) for (int z = -radius; z <= radius; z++) {
				if (Math.max(Math.abs(x), Math.abs(z)) != radius) continue;
				for (int dy : new int[]{0, 1, -1, 2, -2}) {
					BlockPos pos = center.add(x, dy, z);
					Vec3d point = Vec3d.ofBottomCenter(pos);
					if (safe(world, summon, pos, point)) return point;
				}
			}
		}
		return null;
	}

	private static boolean safe(ServerWorld world, Entity entity, BlockPos pos, Vec3d point) {
		if (!world.isChunkLoaded(pos) || !world.getWorldBorder().contains(pos)
				|| pos.getY() <= world.getBottomY() || pos.getY() + 2 >= world.getTopY()) return false;
		if (!world.getBlockState(pos.down()).isSideSolidFullSquare(world, pos.down(), net.minecraft.util.math.Direction.UP)) return false;
		Box box = entity.getDimensions(entity.getPose()).getBoxAt(point);
		// The whole body and its support must be inside already loaded, safe blocks.
		for (BlockPos part : BlockPos.iterate(BlockPos.ofFloored(box.minX, box.minY - 1, box.minZ),
				BlockPos.ofFloored(box.maxX, box.maxY, box.maxZ))) {
			if (!world.isChunkLoaded(part)) return false;
			var state = world.getBlockState(part);
			if (!state.getFluidState().isEmpty() || state.isOf(Blocks.FIRE) || state.isOf(Blocks.SOUL_FIRE)
					|| state.isOf(Blocks.MAGMA_BLOCK) || state.isOf(Blocks.CACTUS) || state.isOf(Blocks.CAMPFIRE)
					|| state.isOf(Blocks.SOUL_CAMPFIRE) || state.isOf(Blocks.SWEET_BERRY_BUSH)
					|| state.isOf(Blocks.POWDER_SNOW) || state.isOf(Blocks.WITHER_ROSE)) return false;
		}
		return world.isSpaceEmpty(entity, box) && world.getOtherEntities(entity, box).isEmpty();
	}
	private SummonPlacement() {}
}
