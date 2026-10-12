package amatsagu.advancementrecipes.data;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;

/**
 * The unattended stations whose recipes are hard-gated by the block's recorded operator. Kept in one
 * place so the operator is only ever written onto blocks that actually care about it (instead of
 * bloating every chest/barrel with an owner tag).
 */
public final class Stations {
	private Stations() {
	}

	public static boolean isGated(BlockEntity blockEntity) {
		return blockEntity instanceof AbstractFurnaceBlockEntity
				|| blockEntity instanceof CrafterBlockEntity;
	}
}
