package amatsagu.advancementrecipes.data;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;

/** Only these blocks ever store an operator, so chests and barrels don't get an owner tag. */
public final class Stations {
	private Stations() {
	}

	public static boolean isGated(BlockEntity blockEntity) {
		return blockEntity instanceof AbstractFurnaceBlockEntity
				|| blockEntity instanceof CrafterBlockEntity;
	}
}
