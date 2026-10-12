package amatsagu.advancementrecipes.mixin.fd;

import amatsagu.advancementrecipes.data.RecipeGate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.Optional;

/**
 * Hard-blocks the Farmer's Delight cutting board: an unlocked {@code farmersdelight:cutting} recipe
 * is required to process the stored item with a tool.
 */
@Mixin(CuttingBoardBlockEntity.class)
public class CuttingBoardBlockEntityMixin {
	@Inject(method = "getMatchingRecipe", at = @At("RETURN"), cancellable = true)
	private void advancementrecipes$gateCutting(ItemStack tool, Player player, ServerLevel level,
			CallbackInfoReturnable<Optional<RecipeHolder<CuttingBoardRecipe>>> cir) {
		Optional<RecipeHolder<CuttingBoardRecipe>> recipe = cir.getReturnValue();
		if (recipe == null || recipe.isEmpty() || !(player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		if (!RecipeGate.isAllowed(serverPlayer, recipe.get())) {
			cir.setReturnValue(Optional.empty());
		}
	}
}
