package com.elytradev.davincisvessels.movingworld.common.chunk;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.movingworld.common.chunk.mobilechunk.MobileChunk;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public abstract class ChunkIO {
    public static void write(DataOutput out, MobileChunk chunk, Collection<BlockPos> blocks) throws IOException {
        out.writeShort(blocks.size());
        for (BlockPos p : blocks) {
            writeBlock(out, chunk, p);
        }
    }

    public static int writeAll(DataOutput out, MobileChunk chunk) throws IOException {
        int count = 0;
        for (int i = chunk.minX(); i < chunk.maxX(); i++) {
            for (int j = chunk.minY(); j < chunk.maxY(); j++) {
                for (int k = chunk.minZ(); k < chunk.maxZ(); k++) {
                    IBlockState state = chunk.getBlockState(new BlockPos(i, j, k));
                    if (state != null && state.getBlock() != Blocks.AIR) {
                        count++;
                    }
                }
            }
        }
        DavincisVesselsMod.LOG.debug("Writing mobile chunk data: " + count + " blocks");

        out.writeShort(count);
        for (int i = chunk.minX(); i < chunk.maxX(); i++) {
            for (int j = chunk.minY(); j < chunk.maxY(); j++) {
                for (int k = chunk.minZ(); k < chunk.maxZ(); k++) {
                    IBlockState state = chunk.getBlockState(new BlockPos(i, j, k));
                    if (state != null && state.getBlock() != Blocks.AIR) {
                        writeBlock(out, chunk.getBlockState(new BlockPos(i, j, k)), new BlockPos(i, j, k));
                    }
                }
            }
        }

        return count;
    }

    public static void writeBlock(DataOutput out, MobileChunk chunk, BlockPos pos) throws IOException {
        writeBlock(out, chunk.getBlockState(pos), pos);
    }

    public static void writeBlock(DataOutput out, IBlockState state, BlockPos pos) throws IOException {
        out.writeByte(pos.getX());
        out.writeByte(pos.getY());
        out.writeByte(pos.getZ());
        out.writeShort(Block.getIdFromBlock(state.getBlock()));
        out.writeInt(state.getBlock().getMetaFromState(state));
    }

    public static void read(DataInput in, MobileChunk chunk) throws IOException {
        int count = in.readShort();

        DavincisVesselsMod.LOG.debug("Reading mobile chunk data: " + count + " blocks");

        int x, y, z;
        int id;
        IBlockState state;
        for (int i = 0; i < count; i++) {
            x = in.readByte();
            y = in.readByte();
            z = in.readByte();
            id = in.readShort();
            state = Block.getBlockById(id).getStateFromMeta(in.readInt());
            chunk.setBlockState(new BlockPos(x, y, z), state);
        }
    }

    public static ByteBuf writeCompressed(MobileChunk chunk, Collection<BlockPos> blocks) {
        ByteBuf buffer = Unpooled.buffer();

        try {
            ChunkIO.writeCompressed(buffer, chunk, blocks);
        } catch (IOException e) {
        	DavincisVesselsMod.LOG.error(e);
        }

        return buffer;
    }

    public static void writeCompressed(ByteBuf buf, MobileChunk chunk, Collection<BlockPos> blocks) throws IOException {
        DataOutputStream out = preCompress(buf);
        write(out, chunk, blocks);
        postCompress(buf, out, blocks.size());
    }

    public static void writeAllCompressed(ByteBuf buf, MobileChunk chunk) throws IOException {
        DataOutputStream out = preCompress(buf);
        int count = writeAll(out, chunk);
        postCompress(buf, out, count);
    }

    private static DataOutputStream preCompress(ByteBuf data) throws IOException {
        ByteBufOutputStream bbos = new ByteBufOutputStream(data);
        return new DataOutputStream(new GZIPOutputStream(bbos));
    }

    private static void postCompress(ByteBuf data, DataOutputStream out, int count) throws IOException {
        out.flush();
        out.close();

        int byteswritten = data.writerIndex();
        float f = (float) byteswritten / (count * 9);
        DavincisVesselsMod.LOG.debug(String.format(Locale.ENGLISH, "%d blocks written. Efficiency: %d/%d = %.2f", count, byteswritten, count * 9, f));

        if (byteswritten > 32000) {
        	DavincisVesselsMod.LOG.warn("MobileChunk probably contains too many blocks");
        }
    }

    public static void readCompressed(ByteBuf data, MobileChunk chunk) throws IOException {
        DataInputStream in = new DataInputStream(new GZIPInputStream(new ByteBufInputStream(data)));
        read(in, chunk);
        in.close();
    }
}
