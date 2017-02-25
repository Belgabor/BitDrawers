package mods.belgabor.bitdrawers.storage;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IFractionalDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.*;
import com.jaquadro.minecraft.storagedrawers.storage.BaseDrawerData;
import com.jaquadro.minecraft.storagedrawers.storage.ICentralInventory;
import mods.belgabor.bitdrawers.BitDrawers;
import mods.belgabor.bitdrawers.core.BDLogger;
import mods.belgabor.bitdrawers.core.BitHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

/**
 * Created by Belgabor on 02.06.2016.
 * Based on CompDrawerData by jaquadro
 */
public class BitDrawerData extends BaseDrawerData implements IFractionalDrawer, IVoidable, IShroudable, IItemLockable, IQuantifiable
{
    //private static final ItemStack nullStack = new ItemStack((Item)null);

    private ICentralInventory central;
    private int slot;

    public BitDrawerData (ICentralInventory centralInventory, int slot) {
        this.slot = slot;
        this.central = centralInventory;
    }

    @Override
    public @Nonnull ItemStack getStoredItemPrototype () {
        return central.getStoredItemPrototype(slot);
    }

    @Override
    public IDrawer setStoredItem (@Nonnull ItemStack itemPrototype, int amount) {
        if (BitDrawers.config.debugTrace)
            BDLogger.info("setStoredItem %d %s %d", slot, itemPrototype.isEmpty()?"EMPTY":itemPrototype.getDisplayName(), amount);
        IDrawer target = central.setStoredItem(slot, itemPrototype, amount);
        refresh();

        return target;
    }

    /*
    @Override
    public IDrawer setStoredItemRedir (ItemStack itemPrototype, int amount) {
        IDrawer target = central.setStoredItem(slot, itemPrototype, amount);
        refresh();

        return target;
    }
    */


    @Override
    public boolean areItemsEqual(@Nonnull ItemStack item) {
        ItemStack protoStack = this.getStoredItemPrototype();
        return BitHelper.areItemsEqual(item, protoStack);
    }

    @Override
    public int getStoredItemCount () {
        return central.getStoredItemCount(slot);
    }

    @Override
    public void setStoredItemCount (int amount) {
        central.setStoredItemCount(slot, amount);
    }

    @Override
    public int getMaxCapacity () {
        return central.getMaxCapacity(slot);
    }

    @Override
    public int getMaxCapacity (@Nonnull ItemStack itemPrototype) {
        return central.getMaxCapacity(slot, itemPrototype);
    }

    @Override
    public int getRemainingCapacity () {
        return central.getRemainingCapacity(slot);
    }

    @Override
    public int getStoredItemStackSize () {
        return central.getStoredItemStackSize(slot);
    }
    
    @Override
    public int getDefaultMaxCapacity () {
        return central.getDefaultMaxCapacity(slot);
    }

    @Override
    protected int getItemCapacityForInventoryStack () {
        return central.getItemCapacityForInventoryStack(slot);
    }

    @Override
    public boolean canItemBeStored (@Nonnull ItemStack itemPrototype) {
        if (getStoredItemPrototype().isEmpty() && !isItemLocked(LockAttribute.LOCK_EMPTY)) {
            return (!BitHelper.getBit(itemPrototype).isEmpty()) || (!BitHelper.getBlock(itemPrototype).isEmpty());
        }

        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean canItemBeExtracted (@Nonnull ItemStack itemPrototype) {
        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean isEmpty () {
        return getStoredItemPrototype().isEmpty();
    }

    @Override
    public void writeToNBT (NBTTagCompound tag) {
        central.writeToNBT(slot, tag);
    }

    @Override
    public void readFromNBT (NBTTagCompound tag) {
        central.readFromNBT(slot, tag);
        refresh();
    }

    @Override
    public int getConversionRate () {
        return central.getConversionRate(slot);
    }

    @Override
    public int getStoredItemRemainder () {
        return central.getStoredItemRemainder(slot);
    }

    @Override
    public boolean isSmallestUnit () {
        return central.isSmallestUnit(slot);
    }

    public void refresh () {
        reset();
        refreshOreDictMatches();
    }

    @Override
    public boolean isVoid () {
        return central.isVoidSlot(slot);
    }

    @Override
    public boolean isShrouded () {
        return central.isShroudedSlot(slot);
    }

    @Override
    public boolean setIsShrouded (boolean state) {
        return central.setIsSlotShrouded(slot, state);
    }
    
    @Override
    public boolean isShowingQuantity () {
        return central.isSlotShowingQuantity(slot);
    }

    @Override
    public boolean setIsShowingQuantity (boolean state) {
        return central.setIsSlotShowingQuantity(slot, state);
    }

    @Override
    public boolean isItemLocked (LockAttribute attr) {
        return central.isLocked(slot, attr);
    }

    @Override
    public boolean canItemLock (LockAttribute attr) {
        return false;
    }

    @Override
    public void setItemLocked (LockAttribute attr, boolean isLocked) { }
}
