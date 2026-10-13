package amatsagu.advancementrecipes.mixin;

import amatsagu.advancementrecipes.data.OperatorHolder;
import amatsagu.advancementrecipes.data.Stations;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseContainerBlockEntity.class)
public class BaseContainerBlockEntityMixin {
	@Inject(method = "createMenu(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/inventory/AbstractContainerMenu;",
			at = @At("HEAD"))
	private void advancementrecipes$rememberOpener(int containerId, Inventory inventory, Player player,
			CallbackInfoReturnable<AbstractContainerMenu> cir) {
		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		BlockEntity blockEntity = (BlockEntity) (Object) this;
		if (blockEntity instanceof OperatorHolder holder && Stations.isGated(blockEntity)) {
			holder.advancementRecipes$setOperator(serverPlayer.getUUID());
		}
	}
}
