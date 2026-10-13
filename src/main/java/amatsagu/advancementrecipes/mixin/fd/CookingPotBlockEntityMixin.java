package amatsagu.advancementrecipes.mixin.fd;

import amatsagu.advancementrecipes.data.OperatorHolder;
import amatsagu.advancementrecipes.data.RecipeGate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.refabricated.inventory.RecipeWrapper;

import java.util.Optional;

/** The pot cooks unattended, so it is gated by the operator recorded when its menu is opened. */
@Mixin(CookingPotBlockEntity.class)
public class CookingPotBlockEntityMixin {
	@Inject(method = "createMenu", at = @At("HEAD"))
	private void advancementrecipes$rememberOpener(int containerId, Inventory inventory, Player player,
			CallbackInfoReturnable<AbstractContainerMenu> cir) {
		if (player instanceof ServerPlayer serverPlayer && (Object) this instanceof OperatorHolder holder) {
			holder.advancementRecipes$setOperator(serverPlayer.getUUID());
		}
	}

	@Inject(method = "getMatchingRecipe", at = @At("RETURN"), cancellable = true)
	private void advancementrecipes$gateCooking(RecipeWrapper input, ServerLevel level,
			CallbackInfoReturnable<Optional<RecipeHolder<CookingPotRecipe>>> cir) {
		Optional<RecipeHolder<CookingPotRecipe>> recipe = cir.getReturnValue();
		if (recipe == null || recipe.isEmpty()) {
			return;
		}

		if (!RecipeGate.isAllowed(level.getServer(), (CookingPotBlockEntity) (Object) this, recipe.get())) {
			cir.setReturnValue(Optional.empty());
		}
	}
}
