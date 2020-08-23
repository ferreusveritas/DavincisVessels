package com.elytradev.davincisvessels.movingworld.common.network.message;

import java.io.IOException;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.concrete.network.Message;
import com.elytradev.davincisvessels.concrete.network.NetworkContext;
import com.elytradev.davincisvessels.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.davincisvessels.concrete.network.annotation.type.ReceivedOn;
import com.elytradev.davincisvessels.movingworld.common.chunk.ChunkIO;
import com.elytradev.davincisvessels.movingworld.common.entity.EntityMovingWorld;
import com.elytradev.davincisvessels.movingworld.common.network.MovingWorldNetworking;
import com.elytradev.davincisvessels.movingworld.common.network.marshallers.ByteBufMarshaller;
import com.elytradev.davincisvessels.movingworld.common.network.marshallers.EntityMarshaller;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Sends MobileChunk block data to clients.
 */
@ReceivedOn(Side.CLIENT)
public class MovingWorldBlockChangeMessage extends Message {

    @MarshalledAs(EntityMarshaller.MARSHALLER_NAME)
    public EntityMovingWorld movingWorld;
    @MarshalledAs(ByteBufMarshaller.MARSHALLER_NAME)
    public ByteBuf compressedChunkData;

    public MovingWorldBlockChangeMessage(NetworkContext ctx) {
        super(ctx);
    }

    public MovingWorldBlockChangeMessage(EntityMovingWorld movingWorld, ByteBuf compressedChunkData) {
        super(MovingWorldNetworking.NETWORK);
        this.movingWorld = movingWorld;
        this.compressedChunkData = compressedChunkData;
    }

    @Override
    protected void handle(EntityPlayer sender) {
        if (movingWorld == null || movingWorld.getMobileChunk() == null || compressedChunkData == null)
            return;

        try {
            compressedChunkData.resetReaderIndex();
            compressedChunkData.resetWriterIndex();
            ChunkIO.readCompressed(compressedChunkData, movingWorld.getMobileChunk());
        } catch (IOException e) {
        	DavincisVesselsMod.LOG.error(e);
        }
    }
}
