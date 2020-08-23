package com.elytradev.davincisvessels.common.network.message;

import com.elytradev.davincisvessels.concrete.network.Message;
import com.elytradev.davincisvessels.concrete.network.NetworkContext;
import com.elytradev.davincisvessels.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.davincisvessels.concrete.network.annotation.type.ReceivedOn;
import com.elytradev.davincisvessels.common.entity.EntityShip;
import com.elytradev.davincisvessels.common.network.DavincisVesselsNetworking;
import com.elytradev.davincisvessels.movingworld.common.network.marshallers.EntityMarshaller;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

@ReceivedOn(Side.SERVER)
public class ControlInputMessage extends Message {

    @MarshalledAs(EntityMarshaller.MARSHALLER_NAME)
    public EntityShip ship;
    @MarshalledAs("i8")
    public int control;

    public ControlInputMessage(EntityShip ship, int control) {
        super(DavincisVesselsNetworking.NETWORK);
        this.ship = ship;
        this.control = control;
    }

    public ControlInputMessage(NetworkContext ctx) {
        super(ctx);
    }

    @Override
    protected void handle(EntityPlayer sender) {
        if (ship == null) {
            return;
        }

        ship.getController().updateControl(ship, sender, control);
    }
}
