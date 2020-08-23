package com.elytradev.davincisvessels.common.network.message;

import com.elytradev.davincisvessels.concrete.network.Message;
import com.elytradev.davincisvessels.concrete.network.NetworkContext;
import com.elytradev.davincisvessels.concrete.network.annotation.type.ReceivedOn;
import com.elytradev.davincisvessels.common.network.DavincisVesselsNetworking;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by darkevilmac on 2/3/2017.
 */
@ReceivedOn(Side.CLIENT)
public class TranslatedChatMessage extends Message {

    public String message;

    public TranslatedChatMessage(String message) {
        super(DavincisVesselsNetworking.NETWORK);
        this.message = message;
    }

    public TranslatedChatMessage(NetworkContext ctx) {
        super(ctx);
    }

    @Override
    protected void handle(EntityPlayer sender) {
        if (sender == null)
            return;

        String[] split = message.split("~");
        TextComponentString text = new TextComponentString("");

        if (split.length > 0) {
            for (String string : split) {
                if (string.startsWith("TR:")) {
                    text.appendSibling(new TextComponentTranslation(string.substring(3)));
                } else {
                    text.appendSibling(new TextComponentString(string));
                }
            }
        }

        sender.sendMessage(text);
    }
}
