package us.drullk.shizzel.networking.appEng;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.storage.data.IAEItemStack;
import us.drullk.shizzel.appEng.enumList.EnumCache;
import us.drullk.shizzel.container.appEng.ContainerChiselingTerminal;

public class PacketChiselingTerminalServer
        extends AbstractPacketServer
{
    private static final byte MODE_REQUEST_FULL_LIST = 1;
    private static final byte MODE_REQUEST_EXTRACTION = 2;
    private static final byte MODE_REQUEST_DEPOSIT = 3;
    private static final byte MODE_REQUEST_DEPOSIT_REGION = 5;
    private static final byte MODE_REQUEST_SET_SORT = 6;
    private static final byte MODE_REQUEST_AUTO_CRAFT = 8;

    /**
     * Extracted or deposited item.
     */
    private IAEItemStack itemStack;

    /**
     * Mouse button being held.
     */
    private int mouseButton;

    /**
     * True if shift is being held.
     */
    private boolean isShiftHeld;

    /**
     * Identifies a region by a slot inside of it.
     */
    private int slotNumber;

    /**
     * Order to sort the items.
     */
    private SortOrder sortingOrder;

    /**
     * Direction to sort the items.
     */
    private SortDir sortingDirection;

    /**
     * What mode is used to view the items.
     */
    private ViewItems viewMode;

    /**
     * Items to set the crafting grid to.
     */
    private IAEItemStack[] gridItems;

    /**
     * Create a packet to request to autocraft the specified item.
     *
     * @param player
     * @param result
     * @return
     */
    public PacketChiselingTerminalServer createRequestAutoCraft( final EntityPlayer player, final IAEItemStack result )
    {
        // Set the player
        this.entityPlayer = player;

        // Set the mode
        this.mode = PacketChiselingTerminalServer.MODE_REQUEST_AUTO_CRAFT;

        // Set the result
        this.itemStack = result;

        return this;
    }

    public PacketChiselingTerminalServer createRequestDeposit( final EntityPlayer player, final int mouseButton )
    {
        // Set the player
        this.entityPlayer = player;

        // Set the mode
        this.mode = PacketChiselingTerminalServer.MODE_REQUEST_DEPOSIT;

        // Set the button
        this.mouseButton = mouseButton;

        return this;
    }

    /**
     * Create a packet requesting that a region(inventory) be deposited into the
     * ME network.
     *
     * @param player
     * @param slotNumber
     * @return
     */
    public PacketChiselingTerminalServer createRequestDepositRegion( final EntityPlayer player, final int slotNumber )
    {
        // Set the player
        this.entityPlayer = player;

        // Set the mode
        this.mode = PacketChiselingTerminalServer.MODE_REQUEST_DEPOSIT_REGION;

        // Set the slot number
        this.slotNumber = slotNumber;

        return this;
    }

    /**
     * Creates a packet letting the server know the user would like to
     * extract the specified itemstack from the ME network.
     *
     * @param player
     * @param itemStack
     * @param mouseButton
     * @return
     */
    public PacketChiselingTerminalServer createRequestExtract( final EntityPlayer player, final IAEItemStack itemStack, final int mouseButton,
            final boolean isShiftHeld )
    {
        // Set player
        this.entityPlayer = player;

        // Set mode
        this.mode = PacketChiselingTerminalServer.MODE_REQUEST_EXTRACTION;

        // Set stack
        this.itemStack = itemStack;

        // Set mouse button
        this.mouseButton = mouseButton;

        // Set shift
        this.isShiftHeld = isShiftHeld;

        return this;
    }

    /**
     * Create a packet in full list mode.
     * This will request a full list of all items in
     * the ME network. Use only when needed.
     *
     * @param player
     */
    public PacketChiselingTerminalServer createRequestFullList( final EntityPlayer player )
    {
        // Set the player
        this.entityPlayer = player;

        // Set the mode
        this.mode = PacketChiselingTerminalServer.MODE_REQUEST_FULL_LIST;

        return this;
    }

    /**
     * Create a packet to request the sorting order and direction.
     *
     * @param player
     * @param order
     * @param direction
     * @return
     */
    public PacketChiselingTerminalServer createRequestSetSort( final EntityPlayer player, final SortOrder order, final SortDir direction,
            final ViewItems viewMode )
    {
        // Set the player
        this.entityPlayer = player;

        // Set the mode
        this.mode = PacketChiselingTerminalServer.MODE_REQUEST_SET_SORT;

        // Set the sorts
        this.sortingDirection = direction;
        this.sortingOrder = order;
        this.viewMode = viewMode;

        return this;
    }

    @Override
    public void execute()
    {
        // If the player is not null, and they have the ACT container open
        if( ( this.entityPlayer != null ) && ( this.entityPlayer.openContainer instanceof ContainerChiselingTerminal) )
        {
            switch ( this.mode )
            {
            case PacketChiselingTerminalServer.MODE_REQUEST_FULL_LIST:
                // Request the full list
                ( (ContainerChiselingTerminal)this.entityPlayer.openContainer ).onClientRequestFullUpdate(this.entityPlayer);
                break;

            case PacketChiselingTerminalServer.MODE_REQUEST_EXTRACTION:
                // Request extraction
                ( (ContainerChiselingTerminal)this.entityPlayer.openContainer ).onClientRequestExtract(this.entityPlayer, this.itemStack,
                        this.mouseButton, this.isShiftHeld);
                break;

            case PacketChiselingTerminalServer.MODE_REQUEST_DEPOSIT:
                // Request deposit
                ( (ContainerChiselingTerminal)this.entityPlayer.openContainer ).onClientRequestDeposit(this.entityPlayer, this.mouseButton);
                break;

            case PacketChiselingTerminalServer.MODE_REQUEST_DEPOSIT_REGION:
                // Request deposit region
                ( (ContainerChiselingTerminal)this.entityPlayer.openContainer ).onClientRequestDepositRegion(this.entityPlayer, this.slotNumber);
                break;

            case PacketChiselingTerminalServer.MODE_REQUEST_SET_SORT:
                // Request set sort
                ( (ContainerChiselingTerminal)this.entityPlayer.openContainer ).onClientRequestSetSort(this.sortingOrder,
                        this.sortingDirection, this.viewMode);
                break;

            case PacketChiselingTerminalServer.MODE_REQUEST_AUTO_CRAFT:
                // Request auto-crafting
                ( (ContainerChiselingTerminal)this.entityPlayer.openContainer ).onClientRequestAutoCraft(this.entityPlayer, this.itemStack);
                break;
            }
        }
    }

    @Override
    public void readData( final ByteBuf stream )
    {
        switch ( this.mode )
        {
        case PacketChiselingTerminalServer.MODE_REQUEST_EXTRACTION:
            // Read the item
            this.itemStack = AbstractPacket.readAEItemStack( stream );

            // Read the mouse button
            this.mouseButton = stream.readInt();

            // Read the shift status
            this.isShiftHeld = stream.readBoolean();
            break;

        case PacketChiselingTerminalServer.MODE_REQUEST_DEPOSIT:
            // Read the mouse button
            this.mouseButton = stream.readInt();
            break;

        case PacketChiselingTerminalServer.MODE_REQUEST_DEPOSIT_REGION:
            // Read the slot number
            this.slotNumber = stream.readInt();
            break;

        case PacketChiselingTerminalServer.MODE_REQUEST_SET_SORT:
            // Read sorts
            this.sortingDirection = EnumCache.AE_SORT_DIRECTIONS[stream.readInt()];
            this.sortingOrder = EnumCache.AE_SORT_ORDERS[stream.readInt()];
            this.viewMode = EnumCache.AE_VIEW_ITEMS[stream.readInt()];
            break;

        case PacketChiselingTerminalServer.MODE_REQUEST_AUTO_CRAFT:
            // Read the requested item
            this.itemStack = AbstractPacket.readAEItemStack( stream );
            break;
        }

    }

    @Override
    public void writeData( final ByteBuf stream )
    {
        switch ( this.mode )
        {
        case PacketChiselingTerminalServer.MODE_REQUEST_EXTRACTION:
            // Write the itemstack
            AbstractPacket.writeAEItemStack( this.itemStack, stream );

            // Write the mouse button
            stream.writeInt( this.mouseButton );

            // Write the shift status
            stream.writeBoolean( this.isShiftHeld );
            break;

        case PacketChiselingTerminalServer.MODE_REQUEST_DEPOSIT:
            // Write the mouse button
            stream.writeInt( this.mouseButton );
            break;

        case PacketChiselingTerminalServer.MODE_REQUEST_DEPOSIT_REGION:
            // Write the slot number to the stream
            stream.writeInt( this.slotNumber );
            break;

        case PacketChiselingTerminalServer.MODE_REQUEST_SET_SORT:
            // Write the sorts
            stream.writeInt( this.sortingDirection.ordinal() );
            stream.writeInt( this.sortingOrder.ordinal() );
            stream.writeInt( this.viewMode.ordinal() );
            break;

        case PacketChiselingTerminalServer.MODE_REQUEST_AUTO_CRAFT:
            // Write the requested item
            AbstractPacket.writeAEItemStack( this.itemStack, stream );
            break;
        }
    }

}
