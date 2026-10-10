package amatsagu.advancementrecipes.mixin.client;

import amatsagu.advancementrecipes.data.QuestData;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.gui.overlay.ingredients.IngredientGridTooltipHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(IngredientGridTooltipHelper.class)
public class IngredientGridTooltipHelperMixin {
	@Inject(method = "getIngredientTooltip", at = @At("RETURN"), remap = false)
	private <T> void advancementrecipes$addQuestTooltip(
			JeiTooltip tooltip,
			ITypedIngredient<T> typedIngredient,
			IIngredientRenderer<T> ingredientRenderer,
			IIngredientHelper<T> ingredientHelper,
			CallbackInfo ci
	) {
		typedIngredient.getItemStack().ifPresent(stack -> {
			Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
			List<Identifier> questIds = QuestData.getQuestsForItem(itemId);
			if (questIds != null && !questIds.isEmpty()) {
				boolean unlocked = QuestData.isItemUnlocked(itemId);
				for (Component line : QuestData.getQuestTooltipLines(questIds, unlocked)) {
					tooltip.add(line);
				}
			} else {
				tooltip.add(Component.literal("§7Unlocked by default"));
			}
		});
	}
}
