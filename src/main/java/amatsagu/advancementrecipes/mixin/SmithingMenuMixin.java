package amatsagu.advancementrecipes.mixin;

import amatsagu.advancementrecipes.data.RecipeGate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public class SmithingMenuMixin {
	@Inject(method = "createResult", at = @At("TAIL"))
	private void advancementrecipes$gateResult(CallbackInfo ci) {
		ItemCombinerMenuAccessor accessor = (ItemCombinerMenuAccessor) (Object) this;
		Player player = accessor.advancementRecipes$getPlayer();
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		ResultContainer resultSlots = accessor.advancementRecipes$getResultSlots();
		var recipe = resultSlots.getRecipeUsed();
		if (recipe != null && !RecipeGate.isAllowed(serverPlayer, recipe)) {
			resultSlots.setItem(0, ItemStack.EMPTY);
		}
	}
}
