package amatsagu.advancementrecipes.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ResultContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes the fields {@code ItemCombinerMenu} keeps protected so subclasses (Smithing, ...) can be
 * gated from their own mixins.
 */
@Mixin(ItemCombinerMenu.class)
public interface ItemCombinerMenuAccessor {
	@Accessor("player")
	Player advancementRecipes$getPlayer();

	@Accessor("resultSlots")
	ResultContainer advancementRecipes$getResultSlots();
}
