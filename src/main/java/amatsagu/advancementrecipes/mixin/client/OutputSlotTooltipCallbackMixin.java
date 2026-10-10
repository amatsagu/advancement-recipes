package amatsagu.advancementrecipes.mixin.client;

import amatsagu.advancementrecipes.data.QuestData;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.library.gui.recipes.OutputSlotTooltipCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OutputSlotTooltipCallback.class)
public class OutputSlotTooltipCallbackMixin {
	@Shadow(remap = false)
	@Final
	private Identifier recipeId;

	@Inject(method = "onRichTooltip", at = @At("RETURN"), remap = false)
	private void advancementrecipes$addRecipeQuestTooltip(
			IRecipeSlotView recipeSlotView,
			ITooltipBuilder tooltip,
			CallbackInfo ci
	) {
		if (recipeId == null) {
			return;
		}
		Identifier questId = QuestData.getQuestForRecipe(recipeId);
		if (questId != null) {
			boolean completed = QuestData.isQuestCompleted(questId);
			String formattedQuest = QuestData.formatQuestWithCategory(questId);
			if (!completed) {
				tooltip.add(Component.literal("§cLocked: Requires Quest " + formattedQuest));
			} else {
				tooltip.add(Component.literal("§7Unlocked by Quest: " + formattedQuest));
			}
		} else {
			tooltip.add(Component.literal("§7Unlocked by default"));
		}
	}
}
