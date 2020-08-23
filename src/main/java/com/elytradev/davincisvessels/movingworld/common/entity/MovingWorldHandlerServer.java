package com.elytradev.davincisvessels.movingworld.common.entity;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.movingworld.common.chunk.ChunkIO;
import com.elytradev.davincisvessels.movingworld.common.chunk.mobilechunk.MobileChunkServer;
import com.elytradev.davincisvessels.movingworld.common.network.message.MovingWorldBlockChangeMessage;
import com.elytradev.davincisvessels.movingworld.common.network.message.MovingWorldTileChangeMessage;
import com.elytradev.davincisvessels.movingworld.common.tile.TileMovingMarkingBlock;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public abstract class MovingWorldHandlerServer extends MovingWorldHandlerCommon {
    protected boolean firstChunkUpdate;

    public MovingWorldHandlerServer(EntityMovingWorld entitymovingWorld) {
        super(entitymovingWorld);
        firstChunkUpdate = true;
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        return false;
    }

    private MobileChunkServer getMobileChunkServer() {
        if (this.getMovingWorld() != null && this.getMovingWorld().getMobileChunk() != null && this.getMovingWorld().getMobileChunk().side().isServer())
            return (MobileChunkServer) this.getMovingWorld().getMobileChunk();
        else
            return null;
    }

    @Override
    public void onChunkUpdate() {
        super.onChunkUpdate();
        if (getMobileChunkServer() != null) {
            if (!firstChunkUpdate) {
                if (!getMobileChunkServer().getBlockQueue().isEmpty()) {
                    new MovingWorldBlockChangeMessage(getMovingWorld(),
                            ChunkIO.writeCompressed(getMovingWorld().getMobileChunk(), getMobileChunkServer().getBlockQueue())).sendToAllWatching(getMovingWorld());

                    DavincisVesselsMod.LOG.debug("MobileChunk block change detected, sending packet to all players watching " + getMovingWorld().toString());
                }
                if (!getMobileChunkServer().getTileQueue().isEmpty()) {
                    NBTTagCompound tagCompound = new NBTTagCompound();
                    NBTTagList list = new NBTTagList();
                    for (BlockPos tilePosition : getMobileChunkServer().getTileQueue()) {
                        NBTTagCompound nbt = new NBTTagCompound();
                        if (getMobileChunkServer().getTileEntity(tilePosition) == null)
                            continue;

                        TileEntity te = getMobileChunkServer().getTileEntity(tilePosition);
                        if (te instanceof TileMovingMarkingBlock) {
                            ((TileMovingMarkingBlock) te).writeNBTForSending(nbt);
                        } else {
                            te.writeToNBT(nbt);
                        }
                        list.appendTag(nbt);
                    }
                    tagCompound.setTag("list", list);

                    new MovingWorldTileChangeMessage(getMovingWorld(), tagCompound).sendToAllWatching(getMovingWorld());
                    DavincisVesselsMod.LOG.debug("MobileChunk tile change detected, sending packet to all players watching " + getMovingWorld().toString());
                }
            }
            getMobileChunkServer().getTileQueue().clear();
            getMobileChunkServer().getBlockQueue().clear();
        }
        firstChunkUpdate = false;
    }
}

