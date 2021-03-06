package us.drullk.shizzel.appEng;

import appeng.api.config.SecurityPermissions;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.implementations.items.IMemoryCard;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.IConfigManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import us.drullk.shizzel.Shizzel;
import us.drullk.shizzel.appEng.enumList.AEParts;
import us.drullk.shizzel.container.appEng.ContainerChiselingTerminal;

import java.util.ArrayList;
import java.util.List;

public class PartChiselingTerminal extends AEPartAbstractRotateable implements IInventory, IGridTickable, ITerminalHost
{
    private static int invSize = 1;
    private static String NBTTagInv = "ShizzelAEInv";
    private static String NBTTagSlot = "Slot#";
    private static String NBTTagSortOrder = "SortOrder";
    private static String NBTTagSortDirection = "SortDirection";
    private static String NBTTagViewMode = "ViewMode";
    private static double powerDrain = 0.5D;

    private static SortOrder defSortOrder = SortOrder.NAME;
    private static SortDir defSortDirection = SortDir.ASCENDING;
    private static ViewItems defViewItems = ViewItems.ALL;

    private SortOrder sortOrder = PartChiselingTerminal.defSortOrder;
    private SortDir sortDirection = PartChiselingTerminal.defSortDirection;
    private ViewItems viewMode = PartChiselingTerminal.defViewItems;

    private ItemStack[] slots = new ItemStack[PartChiselingTerminal.invSize];

    private List<ContainerChiselingTerminal> listeners = new ArrayList<ContainerChiselingTerminal>();

    public PartChiselingTerminal()
    {
        super( AEParts.PartChiselingTerminal);
    }

    private boolean isSlotSafe( final int slotRequest )
    {
        // Condition whether it's safe to get an item or no.
        return(( slotRequest >= 0) && (slotRequest < PartChiselingTerminal.invSize));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(IPartRenderHelper iPartRenderHelper, RenderBlocks renderBlocks)
    {
        Tessellator ts = Tessellator.instance;

        //TODO: GUI stuff. Fun.
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderStatic(int i, int i1, int i2, IPartRenderHelper iPartRenderHelper, RenderBlocks renderBlocks)
    {
        //TODO: Block Render.
    }

    @Override
    public IIcon getBreakingTexture() {
        return null; //TODO: Make texture for default breaking particles
    }

    @Override
    public int cableConnectionRenderTo() {
        return 3;
    }

    @Override
    public void getBoxes(IPartCollisionHelper iPartCollisionHelper) {
        //TODO: Collision Boxes
    }

    @Override
    public IConfigManager getConfigManager() {
        return null; // Useless
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(2, 20, false, false); // Every 2 to 20 MC ticks
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode iGridNode, int ticksSinceLastRequest)
    {
        //TODO: Find out what this is.
        //Looks like it is a call to the Part's functions. Possibly important
        return null;
    }

    @Override
    public int getSizeInventory() {
        return PartChiselingTerminal.invSize;
    }

    @Override
    public ItemStack getStackInSlot(int slotRequest) {
        return (isSlotSafe(slotRequest) ? this.slots[slotRequest] : null);
    }

    @Override
    public ItemStack decrStackSize(int slotRequest, int amount)
    {
        ItemStack returnedStack = null;

        // Prevent AOBE with slot array
        if (isSlotSafe(slotRequest))
        {
            ItemStack stackInSlot = this.slots[slotRequest];

            // Check if ItemStack is not null
            if (stackInSlot != null)
            {
                if (amount >= stackInSlot.stackSize)
                {
                    returnedStack = stackInSlot.copy();

                    this.slots[slotRequest].stackSize = 0;
                }
                else
                {
                    returnedStack = stackInSlot.splitStack(amount);
                }

                if( this.slots[slotRequest].stackSize == 0 )
                {
                    this.slots[slotRequest] = null;
                }

                this.notifyListeners(slotRequest);
            }
        }

        return returnedStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slotRequest) {
        return (isSlotSafe(slotRequest) ? this.slots[slotRequest] : null);
    }

    @Override
    public void setInventorySlotContents(int slotRequest, ItemStack stackInSlot)
    {
        if (setInventorySlotContentsNoListener(slotRequest, stackInSlot))
        {
            this.notifyListeners(slotRequest);
        }
    }

    public boolean setInventorySlotContentsNoListener(int slotRequest, ItemStack stackInSlot)
    {
        if(isSlotSafe(slotRequest))
        {
            this.slots[slotRequest] = stackInSlot;

            return true;
        }
        else
        {
            return false;
        }
    }

    private void notifyListeners(int slotRequest)
    {
        //TODO: Listener stuff
    }

    @Override
    public String getInventoryName() {
        return Shizzel.MOD_ID + ".terminal.chiseling.inventory";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64; // lol
    }

    @Override
    public void markDirty() {
        this.markForSave();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
        return true;
    }

    @Override
    public void openInventory() {
        // Useless
    }

    @Override
    public void closeInventory() {
        // Useless
    }

    @Override
    public boolean isItemValidForSlot(int slotRequest, ItemStack is)
    {
        if (isSlotSafe(slotRequest))
        {
            if(is == null)
            {
                return true;
            }

            //TODO: Do Chisel slots

            return true;
        }
        else
        {
           return false;
        }
    }

    @Override
    public IMEMonitor<IAEItemStack> getItemInventory() {
        return this.getGridBlock().getItemMonitor();
    }

    @Override
    public IMEMonitor<IAEFluidStack> getFluidInventory() {
        return null; // Useless
    }

    @Override
    public boolean doesPlayerHavePermissionToOpenGui(EntityPlayer player)
    {
        if (this.doesPlayerHavePermission( player, SecurityPermissions.EXTRACT ) && this.doesPlayerHavePermission(player, SecurityPermissions.INJECT))
       {
            return true;
       }

        return false;
    }

    @Override
    public void getDrops(List<ItemStack> drops, boolean wrenched)
    {
        // Were we wrenched?
        if(wrenched)
        {
            // Inventory is saved when wrenched
            return;
        }

        // TODO: Needs more things
    }

    @Override
    public double getPowerUsage()
    {
        return PartChiselingTerminal.powerDrain;
    }

    @Override
    public int getLightLevel()
    { //Brightness of block
        return(this.isActive() ? 9 : 0);
    }

    @Override
    public Object getServerGuiElement(EntityPlayer player)
    {
        return null; //TODO: ContainerGUIThing new ContainerPartArcaneCraftingTerminal( this, player );
    }

    public SortDir getSortingDirection()
    {
        return this.sortDirection;
    }

    public SortOrder getSortOrder()
    {
        return this.sortOrder;
    }

    public ViewItems getViewMode()
    {
        return this.viewMode;
    }

    public World getWorldObj()
    {
        return this.TE.getWorldObj();
    }

    @Override
    public boolean onActivate(EntityPlayer entityPlayer, Vec3 pos)
    {
        ItemStack heldItem = entityPlayer.inventory.getCurrentItem();

        if((heldItem != null) && (heldItem.getItem() instanceof IMemoryCard))
        {
            IMemoryCard memoryCard = (IMemoryCard)heldItem.getItem();

            String settingsName = memoryCard.getSettingsName(heldItem);

            //TODO: Do memorycard things

            return true;
        }

        return super.onActivate(entityPlayer, pos);
    }

    @Override
    public void readFromNBT(NBTTagCompound NBTData )
    {
        super.readFromNBT(NBTData);

        if(NBTData.hasKey(PartChiselingTerminal.NBTTagInv))
        {
            NBTTagList NBTList = (NBTTagList)NBTData.getTag(PartChiselingTerminal.NBTTagInv);

            for(int i = 0; i < NBTList.tagCount(); i++)
            {
                NBTTagCompound NBTCompound = NBTList.getCompoundTagAt(i);

                int slot = NBTCompound.getByte(PartChiselingTerminal.NBTTagSlot);

                if(isSlotSafe(slot))
                {
                    ItemStack stackFromSlot = ItemStack.loadItemStackFromNBT(NBTCompound);

                    //TODO: Do more things here

                    this.slots[slot] = stackFromSlot;
                }
            }
        }

        // Modes (Buttons on the left side of the ME Access Terminal)
        // TODO: Do more things in these following three if-chunks

        if (NBTData.hasKey(PartChiselingTerminal.NBTTagSortOrder))
        {

        }

        if (NBTData.hasKey(PartChiselingTerminal.NBTTagSortDirection))
        {

        }

        if (NBTData.hasKey(PartChiselingTerminal.NBTTagViewMode))
        {

        }
    }

    public void registerListener(ContainerChiselingTerminal container)
    {
        if(!this.listeners.contains(container))
        {
            this.listeners.add(container);
        }
    }
    public void removeListener(ContainerChiselingTerminal container) {
        this.listeners.remove(container);
    }

    public void setSorts(SortOrder sortOrder, SortDir sortDirection, ViewItems viewMode)
    {
        this.sortOrder = sortOrder;

        this.sortDirection = sortDirection;

        this.viewMode = viewMode;

        this.markDirty();
    }

    @Override
    public void writeToNBT(NBTTagCompound NBTData)
    {
        super.writeToNBT(NBTData);

        NBTTagList tagList = new NBTTagList();

        for(int i = 0; i < PartChiselingTerminal.invSize; i++)
        {
            if(this.slots[i] != null)
            {
                NBTTagCompound tagCompound = new NBTTagCompound();

                tagCompound.setByte(PartChiselingTerminal.NBTTagSlot, (byte)i);

                tagList.appendTag(tagCompound);
            }
        }

        if(tagList.tagCount() > 0)
        {
            NBTData.setTag(PartChiselingTerminal.NBTTagInv, tagList);
        }

        if(this.sortDirection != PartChiselingTerminal.defSortDirection)
        {
            NBTData.setInteger(PartChiselingTerminal.NBTTagSortDirection, this.sortDirection.ordinal());
        }

        if(this.sortOrder != PartChiselingTerminal.defSortOrder)
        {
            NBTData.setInteger(PartChiselingTerminal.NBTTagSortOrder, this.sortOrder.ordinal());
        }

        if(this.viewMode != PartChiselingTerminal.defViewItems)
        {
            NBTData.setInteger(PartChiselingTerminal.NBTTagViewMode, this.viewMode.ordinal());
        }
    }
}
