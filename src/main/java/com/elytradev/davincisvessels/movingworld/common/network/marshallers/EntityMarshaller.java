package com.elytradev.davincisvessels.movingworld.common.network.marshallers;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.concrete.network.Marshaller;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityMarshaller implements Marshaller<Entity> {

    public static final String MARSHALLER_NAME = "com.elytradev.davincisvessels.movingworld.common.network.marshallers.EntityMarshaller";
    public static final EntityMarshaller INSTANCE = new EntityMarshaller();

    @Override
    public Entity unmarshal(ByteBuf in) {
        if (in.readBoolean()) {
            int dimID = in.readInt();
            int entityID = in.readInt();
            World world = DavincisVesselsMod.INSTANCE.getWorld(dimID);
            return world.getEntityByID(entityID);
        } else {
            return null;
        }
    }

    @Override
    public void marshal(ByteBuf out, Entity entity) {
        if (entity != null) {
            out.writeBoolean(true);
            out.writeInt(entity.world.provider.getDimension());
            out.writeInt(entity.getEntityId());
        } else {
            out.writeBoolean(false);
        }
    }
}
