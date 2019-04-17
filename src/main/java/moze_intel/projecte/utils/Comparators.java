package moze_intel.projecte.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

public final class Comparators
{
	public static final Comparator<ItemStack> ITEMSTACK_ASCENDING = (o1, o2) -> {
        if ((o1 == null && o2 == null))
        {
            return 0;
        }
        if (o1 == null)
        {
            return 1;
        }
        if (o2 == null)
        {
            return -1;
        }
        if (ItemHelper.areItemStacksEqualIgnoreNBT(o1, o2))
        {
            // Same item id, same meta
            return o1.stackSize - o2.stackSize;
        }
        else // Different id or different meta
        {
            // Different id
            if (o1.getItem() != o2.getItem())
            {
                return Item.getIdFromItem(o1.getItem()) - Item.getIdFromItem(o2.getItem());
            }
            else
            {
                // Different meta
                return o1.getItemDamage() - o2.getItemDamage();
            }

        }
    };
}