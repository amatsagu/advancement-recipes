package amatsagu.advancementrecipes.mixin;

import amatsagu.advancementrecipes.data.RecipeGate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Hard-blocks smelting/blasting/smoking: an input may only be inserted into the furnace when the
 * station's operator has unlocked the matching cooking recipe.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {
	private static final int ADVANCEMENT_RECIPES_INPUT_SLOT = 0;

	@Shadow
	private RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;

	@Inject(method = "canPlaceItem", at = @At("HEAD"), cancellable = true)
	private void advancementrecipes$gateInput(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (slot != ADVANCEMENT_RECIPES_INPUT_SLOT || stack.isEmpty()) {
			return;
		}
		AbstractFurnaceBlockEntity furnace = (AbstractFurnaceBlockEntity) (Object) this;
		if (!(furnace.getLevel() instanceof ServerLevel level)) {
			return;
		}
		var recipe = quickCheck.getRecipeFor(new SingleRecipeInput(stack), level);
		if (recipe.isPresent() && !RecipeGate.isAllowed(level.getServer(), furnace, recipe.get())) {
			cir.setReturnValue(false);
		}
	}
}
