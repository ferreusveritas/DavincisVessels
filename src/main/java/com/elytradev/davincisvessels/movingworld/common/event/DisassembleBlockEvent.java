package com.elytradev.davincisvessels.movingworld.common.event;

import com.elytradev.davincisvessels.movingworld.common.chunk.LocatedBlock;
import com.elytradev.davincisvessels.movingworld.common.entity.EntityMovingWorld;
import net.minecraftforge.fml.common.eventhandler.Event;

public class DisassembleBlockEvent extends Event {

    public final EntityMovingWorld movingWorld;
    public final LocatedBlock block;

    public DisassembleBlockEvent(EntityMovingWorld movingWorld, LocatedBlock block) {
        this.movingWorld = movingWorld;
        this.block = block;
    }
}
