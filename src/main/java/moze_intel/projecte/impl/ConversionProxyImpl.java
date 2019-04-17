package moze_intel.projecte.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import moze_intel.projecte.api.proxy.IConversionProxy;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.lang3.ClassUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class ConversionProxyImpl implements IConversionProxy
{

	public static final ConversionProxyImpl instance = new ConversionProxyImpl();

	final Map<Object, NormalizedSimpleStack> fakes = Maps.newHashMap();

	@Override
	public void addConversion(int amount, @Nonnull Object output, @Nonnull Map<Object, Integer> ingredients) {
		NormalizedSimpleStack nssOut = objectToNSS(output);
		IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
		for (Map.Entry<Object, Integer> entry: ingredients.entrySet()) {
			NormalizedSimpleStack nss = objectToNSS(entry.getKey());
			ingredientMap.addIngredient(nss, entry.getValue());
		}
		List<APIConversion> conversionsFromMod;
		String modId = getActiveMod();
		if (storedConversions.containsKey(modId)) {
			conversionsFromMod = storedConversions.get(modId);
		} else {
			conversionsFromMod = Lists.newLinkedList();
			storedConversions.put(modId, conversionsFromMod);
		}
		conversionsFromMod.add(new APIConversion(amount, nssOut, ImmutableMap.copyOf(ingredientMap.getMap())));
	}

	public final Map<String, List<APIConversion>> storedConversions = Maps.newHashMap();

	public NormalizedSimpleStack objectToNSS(Object object)
	{
		if (object instanceof Block) {
			return objectToNSS(new ItemStack((Block) object));
		} else if (object instanceof Item) {
			return objectToNSS(new ItemStack((Item)object));
		}

		if (object instanceof ItemStack) {
			return NormalizedSimpleStack.getFor((ItemStack) object);
		} else if (object instanceof FluidStack) {
			return NormalizedSimpleStack.getFor(((FluidStack) object).getFluid());
		} else if (object instanceof String) {
			return NormalizedSimpleStack.forOreDictionary((String) object);
		} else if (object != null && object.getClass().equals(Object.class)) {
			if (fakes.containsKey(object)) return fakes.get(object);

			NormalizedSimpleStack nss = NormalizedSimpleStack.createFake("" + fakes.size() + " by " + getActiveMod());
			fakes.put(object, nss);
			return nss;
		} else {
			throw new IllegalArgumentException("Can not turn " + object + " (" + ClassUtils.getPackageCanonicalName(object, "") + ") into NormalizedSimpleStack. need ItemStack, FluidStack, String or 'Object'");
		}
	}

	private String getActiveMod() {
		ModContainer activeMod = Loader.instance().activeModContainer();
		return activeMod == null ? "unknown Mod" : activeMod.getModId();
	}

	public static class APIConversion
	{
		public final int amount;
		public final NormalizedSimpleStack output;
		public final ImmutableMap<NormalizedSimpleStack, Integer> ingredients;

		private APIConversion(int amount, NormalizedSimpleStack output, ImmutableMap<NormalizedSimpleStack, Integer> ingredients)
		{
			this.amount = amount;
			this.output = output;
			this.ingredients = ingredients;
		}
	}
}