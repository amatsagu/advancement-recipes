package amatsagu.advancementrecipes.mixin;

import amatsagu.advancementrecipes.data.RecipeGate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.StonecutterMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Hard-blocks the stonecutter: a player cannot select a recipe they have not unlocked. */
@Mixin(StonecutterMenu.class)
public class StonecutterMenuMixin {
	@Inject(method = "clickMenuButton", at = @At("HEAD"), cancellable = true)
	private void advancementrecipes$gateSelection(Player player, int index, CallbackInfoReturnable<Boolean> cir) {
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}
		var entries = ((StonecutterMenu) (Object) this).getVisibleRecipes().entries();
		if (index < 0 || index >= entries.size()) {
			return;
		}
		var recipe = entries.get(index).recipe().recipe().orElse(null);
		if (recipe != null && !RecipeGate.isAllowed(serverPlayer, recipe)) {
			cir.setReturnValue(false);
		}
	}
}
