package com.elytradev.davincisvessels.movingworld.api;

import com.elytradev.davincisvessels.movingworld.common.chunk.mobilechunk.MobileChunk;
import com.elytradev.davincisvessels.movingworld.common.entity.EntityMovingWorld;
import net.minecraft.util.math.BlockPos;

public interface IMovingTile {

    void setParentMovingWorld(EntityMovingWorld movingWorld, BlockPos chunkPos);

    EntityMovingWorld getParentMovingWorld();

    void setParentMovingWorld(EntityMovingWorld entityMovingWorld);

    BlockPos getChunkPos();

    void setChunkPos(BlockPos chunkPos);

    /**
     * Called each tick from the mobilechunk, I advise strongly against any major modifications to
     * the chunk.
     */
    void tick(MobileChunk mobileChunk);

}
