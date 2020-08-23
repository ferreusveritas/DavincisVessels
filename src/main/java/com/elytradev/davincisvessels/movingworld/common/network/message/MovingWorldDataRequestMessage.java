package com.elytradev.davincisvessels.movingworld.common.network.message;

import com.elytradev.davincisvessels.concrete.network.Message;
import com.elytradev.davincisvessels.concrete.network.NetworkContext;
import com.elytradev.davincisvessels.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.davincisvessels.concrete.network.annotation.type.ReceivedOn;
import com.elytradev.davincisvessels.movingworld.common.entity.EntityMovingWorld;
import com.elytradev.davincisvessels.movingworld.common.network.MovingWorldNetworking;
import com.elytradev.davincisvessels.movingworld.common.network.marshallers.EntityMarshaller;
import com.elytradev.davincisvessels.movingworld.common.tile.TileMovingMarkingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by darkevilmac on 1/29/2017.
 */
@ReceivedOn(Side.SERVER)
public class MovingWorldDataRequestMessage extends Message {

    @MarshalledAs(EntityMarshaller.MARSHALLER_NAME)
    public EntityMovingWorld movingWorld;

    public MovingWorldDataRequestMessage(NetworkContext ctx) {
        super(ctx);
    }

    public MovingWorldDataRequestMessage(EntityMovingWorld movingWorld) {
        super(MovingWorldNetworking.NETWORK);
        this.movingWorld = movingWorld;
    }

    @Override
    protected void handle(EntityPlayer sender) {
        if (movingWorld == null)
            return;

        NBTTagCompound tagCompound = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (TileEntity te : movingWorld.getMobileChunk().chunkTileEntityMap.values()) {
            NBTTagCompound nbt = new NBTTagCompound();
            if (te instanceof TileMovingMarkingBlock) {
                ((TileMovingMarkingBlock) te).writeNBTForSending(nbt);
            } else {
                te.writeToNBT(nbt);
            }
            list.appendTag(nbt);
        }
        tagCompound.setTag("list", list);

        // https://goo.gl/6VyCqo
        new MovingWorldTileChangeMessage(movingWorld, tagCompound).sendTo(sender);
    }
}
