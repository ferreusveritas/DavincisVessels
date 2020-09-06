package com.elytradev.davincisvessels.movingworld.common.network;

import com.elytradev.davincisvessels.concrete.network.NetworkContext;
import com.elytradev.davincisvessels.movingworld.common.network.message.FarInteractMessage;
import com.elytradev.davincisvessels.movingworld.common.network.message.MovingWorldBlockChangeMessage;
import com.elytradev.davincisvessels.movingworld.common.network.message.MovingWorldClientActionMessage;
import com.elytradev.davincisvessels.movingworld.common.network.message.MovingWorldDataRequestMessage;
import com.elytradev.davincisvessels.movingworld.common.network.message.MovingWorldTileChangeMessage;

public class MovingWorldNetworking {

	public static NetworkContext NETWORK;

	public static void setupNetwork() {
		//Init net code with builder.

		//DavincisVesselsMod.LOG.info("Setting up network...");
		MovingWorldNetworking.NETWORK = registerPackets();
		//DavincisVesselsMod.LOG.info("Setup network! " + MovingWorldNetworking.NETWORK.toString());
	}

	private static NetworkContext registerPackets() {
		NetworkContext context = NetworkContext.forChannel("MovingWorld");

		context.register(FarInteractMessage.class);
		context.register(MovingWorldBlockChangeMessage.class);
		context.register(MovingWorldTileChangeMessage.class);
		context.register(MovingWorldDataRequestMessage.class);
		context.register(MovingWorldClientActionMessage.class);

		return context;
	}
}
