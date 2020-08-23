package com.elytradev.davincisvessels.movingworld.common.network.message;

import com.elytradev.davincisvessels.concrete.network.Message;
import com.elytradev.davincisvessels.concrete.network.NetworkContext;
import com.elytradev.davincisvessels.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.davincisvessels.concrete.network.annotation.type.ReceivedOn;
import com.elytradev.davincisvessels.movingworld.common.entity.EntityMovingWorld;
import com.elytradev.davincisvessels.movingworld.common.network.MovingWorldNetworking;
import com.elytradev.davincisvessels.movingworld.common.network.marshallers.EntityMarshaller;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;

@ReceivedOn(Side.SERVER)
public class FarInteractMessage extends Message {

    @MarshalledAs(EntityMarshaller.MARSHALLER_NAME)
    public EntityMovingWorld movingWorld;
    public EnumHand hand;

    public FarInteractMessage(NetworkContext ctx) {
        super(ctx);
    }

    public FarInteractMessage(EntityMovingWorld movingWorld, EnumHand hand) {
        super(MovingWorldNetworking.NETWORK);

        this.movingWorld = movingWorld;
        this.hand = hand;
    }

    @Override
    protected void handle(EntityPlayer entityPlayer) {
        entityPlayer.interactOn(movingWorld, hand);
    }
}
