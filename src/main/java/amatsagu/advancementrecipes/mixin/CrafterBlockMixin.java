package amatsagu.advancementrecipes.mixin;

import amatsagu.advancementrecipes.data.OperatorHolder;
import amatsagu.advancementrecipes.data.RecipeGate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@link CrafterBlock} overrides {@code setPlacedBy} without calling super, so the placer has to be
 * recorded here instead of by the generic placement mixin.
 */
@Mixin(CrafterBlock.class)
public class CrafterBlockMixin {
	@Inject(method = "setPlacedBy", at = @At("HEAD"))
	private void advancementrecipes$rememberPlacer(Level level, BlockPos pos, BlockState state, LivingEntity placer,
			ItemStack stack, CallbackInfo ci) {
		if (placer instanceof ServerPlayer player && level.getBlockEntity(pos) instanceof OperatorHolder holder) {
			holder.advancementRecipes$setOperator(player.getUUID());
		}
	}

	@Inject(method = "dispenseFrom", at = @At("HEAD"), cancellable = true)
	private void advancementrecipes$gateCraft(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo ci) {
		if (!(level.getBlockEntity(pos) instanceof CrafterBlockEntity crafter)) {
			return;
		}

		var result = CrafterBlock.getPotentialResults(level, crafter.asCraftInput());
		if (result.isPresent() && !RecipeGate.isAllowed(level.getServer(), crafter, result.get())) {
			ci.cancel();
		}
	}
}
