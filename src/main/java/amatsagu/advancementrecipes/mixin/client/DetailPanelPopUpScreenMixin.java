package amatsagu.advancementrecipes.mixin.client;

import net.a5ho999.modernadvancements.client.screen.popup.DetailPanelPopUpScreen;
import net.a5ho999.modernadvancements.network.ModernAdvancementsPackets;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DetailPanelPopUpScreen.class)
public class DetailPanelPopUpScreenMixin {
	@Shadow(remap = false)
	@Final
	private Font font;

	/**
	 * @author Amatsagu
	 * @reason Separate actual completion rewards (XP/loot/function) from recipe unlocks
	 */
	@Overwrite(remap = false)
	private int renderRewards(
			GuiGraphicsExtractor graphics,
			int x,
			int y,
			int width,
			ModernAdvancementsPackets.SimpleRewards rewards
	) {
		if (rewards == null || rewards.isEmpty()) {
			return y;
		}

		boolean hasActualRewards = rewards.experience() > 0 || !rewards.loot().isEmpty() || rewards.hasFunction();
		boolean hasRecipes = !rewards.recipes().isEmpty();

		// 1. Render actual rewards if present
		if (hasActualRewards) {
			graphics.text(this.font, Component.translatable("gui.advancements.text.rewards"), x, y, -3355444, true);
			y += 14;

			if (rewards.experience() > 0) {
				graphics.text(this.font, Component.literal("+" + rewards.experience() + " XP"), x + 8, y, -8892, false);
				y += 12;
			}

			for (Identifier loot : rewards.loot()) {
				for (FormattedCharSequence line : this.font.split(Component.literal(loot.toString()), width - 8)) {
					graphics.text(this.font, line, x + 8, y, -5579265, false);
					y += 12;
				}
			}

			if (rewards.hasFunction()) {
				graphics.text(this.font, Component.literal("Function"), x + 8, y, -4486913, false);
				y += 12;
			}
		}

		// 2. Render recipe unlocks separately if present
		if (hasRecipes) {
			if (hasActualRewards) {
				y += 4;
			}
			graphics.text(this.font, Component.translatable("gui.advancements.text.unlocks"), x, y, -3355444, true);
			y += 14;

			for (Identifier recipe : rewards.recipes()) {
				String label = getRecipeDisplayName(recipe);
				for (FormattedCharSequence line : this.font.split(Component.literal("• " + label), width - 8)) {
					graphics.text(this.font, line, x + 8, y, -17528, false);
					y += 12;
				}
			}
		}

		return y + 8;
	}

	/**
	 * @author Amatsagu
	 * @reason Calculate height accounting for separate rewards and unlocks headers
	 */
	@Overwrite(remap = false)
	private int calculateRewardsHeight(int width, ModernAdvancementsPackets.SimpleRewards rewards) {
		if (rewards == null || rewards.isEmpty()) {
			return 0;
		}

		boolean hasActualRewards = rewards.experience() > 0 || !rewards.loot().isEmpty() || rewards.hasFunction();
		boolean hasRecipes = !rewards.recipes().isEmpty();

		int height = 0;

		if (hasActualRewards) {
			height += 14;
			if (rewards.experience() > 0) {
				height += 12;
			}
			for (Identifier loot : rewards.loot()) {
				height += this.font.split(Component.literal(loot.toString()), width - 8).size() * 12;
			}
			if (rewards.hasFunction()) {
				height += 12;
			}
		}

		if (hasRecipes) {
			if (hasActualRewards) {
				height += 4;
			}
			height += 14;
			for (Identifier recipe : rewards.recipes()) {
				String label = getRecipeDisplayName(recipe);
				height += this.font.split(Component.literal("• " + label), width - 8).size() * 12;
			}
		}

		return height + 8;
	}

	private static String getRecipeDisplayName(Identifier recipe) {
		Item item = BuiltInRegistries.ITEM.getValue(recipe);
		if (item != null && item != Items.AIR) {
			return item.getName(item.getDefaultInstance()).getString();
		}
		String path = recipe.getPath();
		int slash = path.lastIndexOf('/');
		String rawName = slash >= 0 ? path.substring(slash + 1) : path;
		return rawName.replace('_', ' ');
	}
}
