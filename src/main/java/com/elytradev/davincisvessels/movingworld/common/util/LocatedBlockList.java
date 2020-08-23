package com.elytradev.davincisvessels.movingworld.common.util;

import com.elytradev.davincisvessels.movingworld.MovingWorldLib;
import com.elytradev.davincisvessels.movingworld.common.chunk.LocatedBlock;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class LocatedBlockList extends ArrayList<LocatedBlock> {

	private static final long serialVersionUID = 2535299704881545017L;
	private HashBiMap<BlockPos, LocatedBlock> posMap;

    public LocatedBlockList() {
        super();
        posMap = HashBiMap.create();
    }

    public LocatedBlockList(int initialSize) {
        super(initialSize);
    }

    @Override
    public boolean add(LocatedBlock locatedBlock) {
        if (!posMap.containsKey(locatedBlock.blockPos))
            posMap.put(locatedBlock.blockPos, locatedBlock);
        return super.add(locatedBlock);
    }

    @Override
    public void add(int index, LocatedBlock locatedBlock) {
        if (!posMap.containsKey(locatedBlock.blockPos))
            posMap.put(locatedBlock.blockPos, locatedBlock);
        super.add(index, locatedBlock);
    }

    public ArrayList<LocatedBlockList> getSortedAssemblyBlocks() {
        ArrayList<LocatedBlockList> lbListList = new ArrayList<>();

        lbListList.add(getHighPriorityAssemblyBlocks());
        lbListList.add(getStandardPriorityAssemblyBlocks());
        lbListList.add(getLowPriorityAssemblyBlocks());

        return lbListList;
    }

    public ArrayList<LocatedBlockList> getSortedDisassemblyBlocks() {
        ArrayList<LocatedBlockList> lbListList = new ArrayList<>();

        lbListList.add(getHighPriorityDisassemblyBlocks());
        lbListList.add(getStandardPriorityDisassemblyBlocks());
        lbListList.add(getLowPriorityDisassemblyBlocks());

        return lbListList;
    }

    public LocatedBlockList getHighPriorityAssemblyBlocks() {
        LocatedBlockList thisList = new LocatedBlockList();
        thisList.addAll(this);
        LocatedBlockList lbList = new LocatedBlockList();

        if (!thisList.isEmpty()) {
            for (LocatedBlock lb : thisList) {
                if (MovingWorldLib.INSTANCE.getLocalConfig().getShared().assemblePriorityConfig.getHighPriorityAssembly().contains(lb.getBlockName())) {
                    lbList.add(lb);
                }
            }
        }

        return lbList;
    }

    public LocatedBlockList getHighPriorityDisassemblyBlocks() {
        LocatedBlockList thisList = new LocatedBlockList();
        thisList.addAll(this);
        LocatedBlockList lbList = new LocatedBlockList();

        if (!thisList.isEmpty()) {
            for (LocatedBlock lb : thisList) {
                if (MovingWorldLib.INSTANCE.getLocalConfig().getShared().assemblePriorityConfig.getHighPriorityDisassembly().contains(lb.getBlockName())) {
                    lbList.add(lb);
                }
            }
        }

        return lbList;
    }

    public LocatedBlockList getLowPriorityAssemblyBlocks() {
        LocatedBlockList thisList = new LocatedBlockList();
        thisList.addAll(this);
        LocatedBlockList lbList = new LocatedBlockList();

        if (!thisList.isEmpty()) {
            for (LocatedBlock lb : thisList) {
                if (MovingWorldLib.INSTANCE.getLocalConfig().getShared().assemblePriorityConfig.getLowPriorityAssembly().contains(lb.getBlockName())) {
                    lbList.add(lb);
                }
            }
        }

        return lbList;
    }

    public LocatedBlockList getLowPriorityDisassemblyBlocks() {
        LocatedBlockList thisList = new LocatedBlockList();
        thisList.addAll(this);
        LocatedBlockList lbList = new LocatedBlockList();

        if (!thisList.isEmpty()) {
            for (LocatedBlock lb : thisList) {
                if (MovingWorldLib.INSTANCE.getLocalConfig().getShared().assemblePriorityConfig.getLowPriorityDisassembly().contains(lb.getBlockName())) {
                    lbList.add(lb);
                }
            }
        }

        return lbList;
    }

    public LocatedBlockList getStandardPriorityAssemblyBlocks() {
        LocatedBlockList thisList = new LocatedBlockList();
        thisList.addAll(this);
        LocatedBlockList lbList = new LocatedBlockList();

        if (!thisList.isEmpty()) {
            for (LocatedBlock lb : thisList) {
                if (!MovingWorldLib.INSTANCE.getLocalConfig().getShared().assemblePriorityConfig.getHighPriorityAssembly().contains(lb.getBlockName())
                        && !MovingWorldLib.INSTANCE.getLocalConfig().getShared().assemblePriorityConfig.getLowPriorityAssembly().contains(lb.getBlockName())) {
                    lbList.add(lb);
                }
            }
        }

        return lbList;
    }

    public LocatedBlockList getStandardPriorityDisassemblyBlocks() {
        LocatedBlockList thisList = new LocatedBlockList();
        thisList.addAll(this);
        LocatedBlockList lbList = new LocatedBlockList();

        if (!thisList.isEmpty()) {
            for (LocatedBlock lb : thisList) {
                if (!MovingWorldLib.INSTANCE.getLocalConfig().getShared().assemblePriorityConfig.getHighPriorityDisassembly().contains(lb.getBlockName())
                        && !MovingWorldLib.INSTANCE.getLocalConfig().getShared().assemblePriorityConfig.getLowPriorityDisassembly().contains(lb.getBlockName())) {
                    lbList.add(lb);
                }
            }
        }

        return lbList;
    }

    public LocatedBlock getLBOfPos(BlockPos pos) {
        return posMap.get(pos);
    }

    public boolean containsLBOfPos(BlockPos pos) {
        return posMap.containsKey(pos);
    }

}
