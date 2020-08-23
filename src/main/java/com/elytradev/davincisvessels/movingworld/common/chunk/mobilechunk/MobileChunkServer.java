package com.elytradev.davincisvessels.movingworld.common.chunk.mobilechunk;

import com.elytradev.davincisvessels.movingworld.common.entity.EntityMovingWorld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MobileChunkServer extends MobileChunk {
    private Set<BlockPos> blockQueue;
    private Set<BlockPos> tileQueue;

    public MobileChunkServer(World world, EntityMovingWorld entityMovingWorld) {
        super(world, entityMovingWorld);
        blockQueue = new HashSet<>();
        tileQueue = new HashSet<>();
    }

    public Collection<BlockPos> getBlockQueue() {
        return blockQueue;
    }

    public Collection<BlockPos> getTileQueue() {
        return tileQueue;
    }

    @Override
    public boolean addBlockWithState(BlockPos pos, IBlockState blockState) {
        if (super.addBlockWithState(pos, blockState)) {
            blockQueue.add(pos);
            return true;
        }
        return false;
    }

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState state) {
        if (super.setBlockState(pos, state)) {
            blockQueue.add(pos);
            return true;
        }
        return false;
    }

    @Override
    public void setTileEntity(BlockPos pos, TileEntity tileentity) {
        tileQueue.add(pos);
        super.setTileEntity(pos, tileentity);
    }

    @Override
    public void removeChunkBlockTileEntity(BlockPos pos) {
        tileQueue.add(pos);
        super.removeChunkBlockTileEntity(pos);
    }

    @Override
    public void markTileDirty(BlockPos pos) {
        tileQueue.add(pos);
        super.markTileDirty(pos);
    }

    @Override
    public Side side() {
        return Side.SERVER;
    }
}
