package amatsagu.advancementrecipes.mixin;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {
	@Inject(method = "rebuildSelected", at = @At("RETURN"), cancellable = true)
	private void advancementrecipes$reorderPacks(Collection<String> ids, CallbackInfoReturnable<List<Pack>> cir) {
		List<Pack> selected = new ArrayList<>(cir.getReturnValue());
		int tomsIndex = -1;
		int arIndex = -1;

		for (int i = 0; i < selected.size(); i++) {
			String id = selected.get(i).getId();
			if (id.equals("toms_storage") || id.endsWith("/toms_storage")) {
				tomsIndex = i;
			} else if (id.equals("advancement_recipes") || id.endsWith("/advancement_recipes")) {
				arIndex = i;
			}
		}

		if (tomsIndex != -1 && arIndex != -1 && arIndex < tomsIndex) {
			Pack arPack = selected.remove(arIndex);
			int newTomsIndex = -1;
			for (int i = 0; i < selected.size(); i++) {
				String id = selected.get(i).getId();
				if (id.equals("toms_storage") || id.endsWith("/toms_storage")) {
					newTomsIndex = i;
					break;
				}
			}
			if (newTomsIndex != -1) {
				selected.add(newTomsIndex + 1, arPack);
				cir.setReturnValue(List.copyOf(selected));
			}
		}
	}
}
