package com.elytradev.davincisvessels.common.handler;

import com.elytradev.davincisvessels.DavincisVesselsMod;
import com.elytradev.davincisvessels.common.entity.EntityParachute;
import com.elytradev.davincisvessels.common.entity.EntitySeat;
import com.elytradev.davincisvessels.common.entity.EntityShip;
import com.elytradev.davincisvessels.common.network.message.ConfigMessage;
import com.elytradev.davincisvessels.common.tileentity.TileEntitySecuredBed;
import com.elytradev.davincisvessels.movingworld.common.util.Vec3dMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.HashMap;
import java.util.UUID;

public class ConnectionHandler {

    public static HashMap<UUID, TileEntitySecuredBed> playerBedMap = new HashMap<>();

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.isCanceled())
            return;
        if (event.player != null && event.player.world != null && !event.player.world.isRemote) {
            handleParachuteLogout(event);
            handleConfigDesync(event);

            if (event.player.getRidingEntity() != null && event.player.getRidingEntity() instanceof EntityShip
                    && !event.player.world.getMinecraftServer().isSinglePlayer()) {
                ((EntityShip) event.player.getRidingEntity()).disassemble(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.isCanceled())
            return;
        if (event.player != null && event.player.world != null && !event.player.world.isRemote) {
            handleParachuteLogin(event);
            handleBedLogin(event);
            handlerConfigSync(event);
        }
    }

    private void handlerConfigSync(PlayerEvent.PlayerLoggedInEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            NBTTagCompound tagCompound = DavincisVesselsMod.INSTANCE.getLocalConfig().getShared().serialize();
            tagCompound.setBoolean("restore", false);

            new ConfigMessage(tagCompound).sendTo(event.player);
        }
    }

    private void handleConfigDesync(PlayerEvent.PlayerLoggedOutEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
        }
    }

    private void handleBedLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (playerBedMap.containsKey(event.player.getGameProfile().getId())) {
            TileEntitySecuredBed bed = playerBedMap.get(event.player.getGameProfile().getId());
            bed.setPlayer(event.player);
            bed.moveBed(bed.getPos());
        }
    }

    private void handleParachuteLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        World worldObj = player.world;
        if (player.getEntityData().getBoolean("reqParachute") == true) {
            NBTTagCompound nbt = player.getEntityData().getCompoundTag("parachuteInfo");

            double vecX = nbt.getDouble("vecX");
            double vecY = nbt.getDouble("vecY");
            double vecZ = nbt.getDouble("vecZ");
            double shipX = nbt.getDouble("shipX");
            double shipY = nbt.getDouble("shipY");
            double shipZ = nbt.getDouble("shipZ");
            double motionX = nbt.getDouble("motionX");
            double motionY = nbt.getDouble("motionY");
            double motionZ = nbt.getDouble("motionZ");
            Vec3dMod vec = new Vec3dMod(vecX, vecY, vecZ);
            Vec3dMod shipVec = new Vec3dMod(shipX, shipY, shipZ);
            Vec3dMod motionVec = new Vec3dMod(motionX, motionY, motionZ);

            EntityParachute parachute = new EntityParachute(worldObj, player, vec, shipVec, motionVec);
            worldObj.spawnEntity(parachute);

            player.getEntityData().removeTag("parachuteInfo");
            player.getEntityData().setBoolean("reqParachute", false);
        }
    }

    private void handleParachuteLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player.getRidingEntity() != null && event.player.getRidingEntity() instanceof EntitySeat) {
            EntityPlayer player = event.player;
            EntitySeat seat = (EntitySeat) player.getRidingEntity();
            EntityShip ship = seat.getShip();

            player.dismountRidingEntity();
            if (ship != null && seat.getChunkPos() != null) {
                NBTTagCompound nbt = new NBTTagCompound();

                Vec3dMod vec = new Vec3dMod(seat.getChunkPos().getX() - ship.getMobileChunk().getCenterX(),
                        seat.getChunkPos().getY() - ship.getMobileChunk().minY(),
                        seat.getChunkPos().getZ() - ship.getMobileChunk().getCenterZ());
                vec = vec.rotateAroundY((float) Math.toRadians(ship.rotationYaw));

                nbt.setDouble("vecX", vec.x);
                nbt.setDouble("vecY", vec.y);
                nbt.setDouble("vecZ", vec.z);
                nbt.setDouble("shipX", ship.posX);
                nbt.setDouble("shipY", ship.posY);
                nbt.setDouble("shipZ", ship.posZ);
                nbt.setDouble("motionX", ship.motionX);
                nbt.setDouble("motionY", ship.motionY);
                nbt.setDouble("motionZ", ship.motionZ);
                player.getEntityData().setTag("parachuteInfo", nbt);
                player.getEntityData().setBoolean("reqParachute", true);
            }
        }
    }

}
