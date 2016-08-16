package mods.belgabor.bitdrawers.client.model;

import com.google.common.collect.ImmutableList;
import com.jaquadro.minecraft.chameleon.model.ProxyBuilderModel;
import com.jaquadro.minecraft.chameleon.resources.register.DefaultRegister;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.EnumCompDrawer;
import com.jaquadro.minecraft.storagedrawers.block.modeldata.DrawerStateModelData;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerDecoratorModel;
import com.jaquadro.minecraft.storagedrawers.client.model.component.DrawerSealedModel;
import mods.belgabor.bitdrawers.block.BlockBitDrawers;
import mods.belgabor.bitdrawers.core.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Belgabor on 19.07.2016.
 * Based on CompDrawerModel by jaquadro
 */

public final class BitDrawerModel
{
    public static class Register extends DefaultRegister
    {
        public Register () {
            super(BlockRegistry.bitDrawer);
        }

        @Override
        public List<IBlockState> getBlockStates () {
            List<IBlockState> states = new ArrayList<IBlockState>();

            for (EnumCompDrawer drawer : EnumCompDrawer.values()) {
                for (EnumFacing dir : EnumFacing.HORIZONTALS) {
                    states.add(BlockRegistry.bitDrawer.getDefaultState()
                        .withProperty(BlockBitDrawers.SLOTS, drawer)
                        .withProperty(BlockBitDrawers.FACING, dir));
                }
            }

            return states;
        }

        @Override
        public IBakedModel getModel (IBlockState state, IBakedModel existingModel) {
            return new Model(existingModel);
        }

        @Override
        public IBakedModel getModel (ItemStack stack, IBakedModel existingModel) {
            return new Model(existingModel);
        }
    }

    public static class Model extends ProxyBuilderModel
    {
        public Model (IBakedModel parent) {
            super(parent);
        }

        @Override
        protected IBakedModel buildModel (IBlockState state, IBakedModel parent) {
            EnumCompDrawer drawer = (EnumCompDrawer)state.getValue(BlockBitDrawers.SLOTS);
            EnumFacing dir = state.getValue(BlockDrawers.FACING);

            if (!(state instanceof IExtendedBlockState))
                return parent;

            IExtendedBlockState xstate = (IExtendedBlockState)state;
            DrawerStateModelData stateModel = xstate.getValue(BlockDrawers.STATE_MODEL);

            try {
                if (!DrawerDecoratorModel.shouldHandleState(stateModel))
                    return parent;

                return new DrawerDecoratorModel(parent, xstate, drawer, dir, stateModel);
            }
            catch (Throwable t) {
                return parent;
            }
        }

        @Override
        public ItemOverrideList getOverrides () {
            return itemHandler;
        }
    }

    private static class ItemHandler extends ItemOverrideList
    {
        public ItemHandler () {
            super(ImmutableList.<ItemOverride>of());
        }

        @Override
        public IBakedModel handleItemState (IBakedModel parent, ItemStack stack, World world, EntityLivingBase entity) {
            if (stack == null)
                return parent;

            if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("tile", Constants.NBT.TAG_COMPOUND))
                return parent;

            Block block = Block.getBlockFromItem(stack.getItem());
            IBlockState state = block.getStateFromMeta(stack.getMetadata());

            return new DrawerSealedModel(parent, state, true);
        }
    }

    private static final ItemHandler itemHandler = new ItemHandler();
}
