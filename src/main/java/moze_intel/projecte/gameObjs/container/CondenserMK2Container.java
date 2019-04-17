package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.container.slots.SlotCondenserLock;
import moze_intel.projecte.gameObjs.container.slots.SlotGhost;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class CondenserMK2Container extends CondenserContainer
{
	public CondenserMK2Container(InventoryPlayer invPlayer, CondenserMK2Tile condenser)
	{
		super(invPlayer, condenser);
	}

	@Override
	void initSlots(InventoryPlayer invPlayer)
	{
		this.addSlotToContainer(new SlotCondenserLock(tile.getLock(), 0, 12, 6));

		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();

		//Condenser Inventory
		//Inputs
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 6; j++)
				this.addSlotToContainer(new ValidatedSlot(input, j + i * 6, 12 + j * 18, 26 + i * 18, s -> SlotPredicates.HAS_EMC.test(s) && !tile.isStackEqualToLock(s)));

		//Outputs
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 6; j++)
				this.addSlotToContainer(new ValidatedSlot(output, j + i * 6, 138 + j * 18, 26 + i * 18, s -> false));

		//Player Inventory
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 48 + j * 18, 154 + i * 18));

		//Player Hotbar
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new Slot(invPlayer, i, 48 + i * 18, 212));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
	{
		if (slotIndex == 0)
		{
			return null;
		}

		Slot slot = this.getSlot(slotIndex);

		if (slot == null || !slot.getHasStack())
		{
			return null;
		}

		ItemStack stack = slot.getStack();
		ItemStack newStack = stack.copy();

		if (slotIndex <= 84)
		{
			if (!this.mergeItemStack(stack, 85, 120, false))
			{
				return null;
			}
		}
		else if (!EMCHelper.doesItemHaveEmc(stack) || !this.mergeItemStack(stack, 1, 42, false))
		{
			return null;
		}

		if (stack.stackSize == 0)
		{
			slot.putStack(null);
		}
		else
		{
			slot.onSlotChanged();
		}

		slot.onPickupFromSlot(player, stack);
		return newStack;
	}
}
