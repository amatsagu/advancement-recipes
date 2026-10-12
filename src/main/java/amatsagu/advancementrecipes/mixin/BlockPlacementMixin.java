package amatsagu.advancementrecipes.mixin;

import amatsagu.advancementrecipes.data.OperatorHolder;
import amatsagu.advancementrecipes.data.Stations;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Records the placing player as the station's operator. */
@Mixin(Block.class)
public class BlockPlacementMixin {
	@Inject(method = "setPlacedBy", at = @At("TAIL"))
	private void advancementrecipes$rememberPlacer(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo ci) {
		if (!(placer instanceof ServerPlayer player)) {
			return;
		}
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof OperatorHolder holder && Stations.isGated(blockEntity)) {
			holder.advancementRecipes$setOperator(player.getUUID());
		}
	}
}
